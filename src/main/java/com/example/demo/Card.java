package com.example.demo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Card {
    private String name;
    private String type;
    private String mark;
    private String number;
}
