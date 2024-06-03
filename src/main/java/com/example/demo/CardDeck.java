package com.example.demo;

import lombok.Data;

import java.util.Map;

@Data
public class CardDeck {
    String name;
    Float usageRates;
    Map<String, Float> winRates;
}
