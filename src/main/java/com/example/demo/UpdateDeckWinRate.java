package com.example.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UpdateDeckWinRate {
    public static void main(String[] args) {
        DeckDatabase deckDatabase = new DeckDatabase();
        deckDatabase.updateWinRates("汇流梦幻","白马", 0.1F, 0.7F);
    }
}
