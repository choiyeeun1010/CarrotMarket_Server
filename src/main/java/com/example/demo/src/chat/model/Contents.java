package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Contents {
    private int senderIdx;
    private String content;
    private int receiverIdx;
    private String time;
}
