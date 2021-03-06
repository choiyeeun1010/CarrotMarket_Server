package com.example.demo.utils;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class JwtService {

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////    /
    /*
    일반 계정 JWT 발급
    @param userIdx
    @return String
     */
    public String createJwt(int userIdx){  //jwt 생성
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")  //헤더 (type)
                .claim("userIdx",userIdx)      //페이로드 (유저의 idx (primary 키)값)
                .setIssuedAt(now)               //페이로드 (발급 시간)
                .setExpiration(new Date(System.currentTimeMillis()+(1000*60*60*24*365)))  //페이로드 (파기 시간 (ex, 1년).  개발 단계에서는 클라이언트 개발자가 테스트를 원활하게 하기 위해 길게 주는게 좋다)
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)                 //서명 (헤더의 alg인 HS256 알고리즘 사용, 비밀키로 JWT_SECRET_KEY 사용)
                .compact();
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");    //X-ACCESS-TOKEN의 키에 대한 값을 가져옴 (헤더에 넣어주어야 한다)
    }

    /*
    JWT에서 userIdx 추출
    @return int
    @throws BaseException
     */
    public int getUserIdx() throws BaseException{
        //1. JWT 추출
        String accessToken = getJwt();   //
        if(accessToken == null || accessToken.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()  //유효한 토큰인지 확인,  즉 로그인시 부여한 jwt 토큰인지 확인
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(accessToken);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);       //유효한 토큰인지 확인 (만료 여부 알수 있음)
        }

        // 3. userIdx 추출  (위의 과정에서 문제가 없다면 수행)
        return claims.getBody().get("userIdx",Integer.class);
    }



////////////////////////////////////////////////////////////////////
    //토큰 만료 여부 확인 메서드  (만료시간이 지났을 경우 토큰을 만료시킴)
    public int checkJwtTime(String jwt) {  //Claims
        Jws<Claims> claims;
        try {
            claims = Jwts.parser()  //유효한 토큰인지 확인,  즉 로그인시 부여한 jwt 토큰인지 확인
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(jwt);
            return 0;  //토큰이 만료가 되지 않았다면 0 반환
        }
        catch (Exception ignored){
//            throw new BaseException(INVALID_JWT);
            return 1;  //토큰이 만료 되었다면 1 반환
        }

    }










}
