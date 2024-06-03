package com.example.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Data
public class DeckDatabase {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Deck> decks;
    public DeckDatabase() {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            decks = objectMapper.readValue(new File("deck.json"), new TypeReference<List<Deck>>() {
            });
        } catch (IOException e) {
            decks = new ArrayList<>();
        }
    }
    public void updateDeck(Deck deck){
        for (Deck d:decks){
            if (d.getName().equals(deck.getName())) {
                d.setUsageRates(deck.getUsageRates());
                d.setWinRates(deck.getWinRates());
                writeAll(decks);
                return;
            };
        }
        decks.add(deck);
        writeAll(decks);
    }
    public void writeAll(List<Deck> decks){
        try {
            objectMapper.writeValue(new File("deck.json"),decks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
