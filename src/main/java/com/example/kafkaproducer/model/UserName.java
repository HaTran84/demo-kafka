package com.example.kafkaproducer.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserName {
    private String userName;
    private Integer loginCount;
}
