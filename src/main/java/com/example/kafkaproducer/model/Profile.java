package com.example.kafkaproducer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class Profile {
    private int userId;
    private String name;
    private String fullName;
    private String phoneNumber;
    private String dob;
    private List<String> emails;
    private UserName userName;

}
