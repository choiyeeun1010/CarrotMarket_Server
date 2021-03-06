package com.example.demo.src.region;

import com.example.demo.src.region.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class RegionDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public int createRegion(int userIdx, PostRegion postRegion){
        String regionResetQuery = "update Region set nowStatus = 0, authStatus = 0 where status = 1 and userIdx = ? ";
        Object[] regionResetParams = new Object[]{userIdx};
        this.jdbcTemplate.update(regionResetQuery,regionResetParams);

        String createRegionQuery = "insert into Region (regionName, latitude, longitude, keywordAlertStatus, userIdx) values (?, ?, ?, ?, ?) ";
        Object[] createRegionParams = new Object[]{postRegion.getRegionName(), postRegion.getLatitude(), postRegion.getLongitude(), postRegion.getKeywordAlertStatus(), userIdx};
        this.jdbcTemplate.update(createRegionQuery, createRegionParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    @Transactional
    public List<GetRegion> getRegion(int userIdx){
        String getRegionQuery = "select regionIdx, regionName, nowStatus from Region where status = 1 and userIdx = ? ";
        int getRegionParams = userIdx;
        return this.jdbcTemplate.query(getRegionQuery,
                (rs, rowNum) -> new GetRegion(
                        rs.getInt("regionIdx"),
                        rs.getString("regionName"),
                        rs.getInt("nowStatus")
                ),
                getRegionParams);
    }

    @Transactional
    public int patchRegionStatus(int idx, int userIdx){
        String patchRegionStatusQuery = "update Region set status = 0 where status = 1 and regionIdx = ? and userIdx = ?";
        Object[] patchRegionStatusParams = new Object[]{idx, userIdx};
        return this.jdbcTemplate.update(patchRegionStatusQuery,patchRegionStatusParams);
    }

    @Transactional
    public int patchRegionAuth(int idx, int userIdx){
        String patchRegionAuthStatusQuery = "update Region set authStatus = 1, authCount = authCount + 1 where nowStatus = 1 and status = 1 and regionIdx = ? and userIdx = ? ";
        Object[] patchRegionAuthStatusParams = new Object[]{idx, userIdx};
        return this.jdbcTemplate.update(patchRegionAuthStatusQuery,patchRegionAuthStatusParams);
    }

    @Transactional
    public int patchRegionNow(int idx, int userIdx){
        String patchRegionNowResetQuery = "update Region set nowStatus = 0, authStatus = 0 where status = 1 and userIdx = ? ";
        Object[] patchRegionNowResetParams = new Object[]{userIdx};
        this.jdbcTemplate.update(patchRegionNowResetQuery,patchRegionNowResetParams);

        String patchRegionNowQuery = "update Region set nowStatus = 1 where nowStatus = 0 and status = 1 and regionIdx = ? and userIdx = ? ";
        Object[] patchRegionNowParams = new Object[]{idx, userIdx};
        return this.jdbcTemplate.update(patchRegionNowQuery,patchRegionNowParams);
    }

    @Transactional
    public int checkCount(int userIdx){
        String checkCountQuery = "select count(userIdx) as count from Region where userIdx = ? and status = 1";
        return this.jdbcTemplate.queryForObject(checkCountQuery, int.class, userIdx);
    }

    @Transactional
    public int checkRegion(int userIdx, String regionName){
        String checkRegionQuery = "select exists (select regionIdx from Region where userIdx= ? and regionName = ? and status = 1) as exist ";
        return this.jdbcTemplate.queryForObject(checkRegionQuery, int.class, userIdx, regionName);
    }

    @Transactional
    public int checkRegionIdx(int regionIdx){
        String checkRegionQuery = "select exists (select regionIdx from Region where regionIdx = ? and status = 1) as exist ";
        return this.jdbcTemplate.queryForObject(checkRegionQuery, int.class, regionIdx);
    }

    @Transactional
    public int checkRegionAccess(int regionIdx, int userIdx){
        String checkRegionQuery = "select exists (select regionIdx from Region where userIdx= ? and regionIdx = ? and status = 1) as exist ";
        return this.jdbcTemplate.queryForObject(checkRegionQuery, int.class, userIdx, regionIdx);
    }

    @Transactional
    public int checkRegionNow(int regionIdx, int userIdx){
        String checkRegionQuery = "select exists (select regionIdx from Region where userIdx= ? and regionIdx = ? and status = 1 and nowStatus = 1) as exist ";
        return this.jdbcTemplate.queryForObject(checkRegionQuery, int.class, userIdx, regionIdx);
    }
}
