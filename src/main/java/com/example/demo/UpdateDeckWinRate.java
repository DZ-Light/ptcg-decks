package com.example.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UpdateDeckWinRate {
    public static void main(String[] args) {
        String filePath = "deck.json";
        String deckName1 = "汇流梦幻";
        
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<CardDeck> decks = objectMapper.readValue(new File(filePath), new TypeReference<List<CardDeck>>() {
            });

            objectMapper.writeValue(new File(filePath), decks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
