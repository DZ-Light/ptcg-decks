package com.example.demo;

import lombok.Data;

import java.util.Map;

@Data
public class Deck {
    private String name;
    private Float usageRates;
    private Map<String, Float> winRates;
}
