package com.example.demo.src.townActivity.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor



public class GetTownActivityRes {

    private int townActivityIdx;
    private String userRegionName;
    private String topicName;
    private String image;
    private String content;
    private String nickName;
    private String regionName;
    private String createAt;
    private int commentCount;
    private int sympathyCount;

    //private List<String> imageList;
}

