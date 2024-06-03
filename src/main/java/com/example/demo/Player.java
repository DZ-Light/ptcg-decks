package com.example.demo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Player {
    private String deck;
    private String id;
    private int score;
    private int subScore;
    private List<String> opponent;
    private List<String> battleLog;
    public Player(String id, String deck, int score){
        this.id = id;
        this.deck = deck;
        this.score = score;
        this.opponent = new ArrayList<>();
        this.battleLog = new ArrayList<>();
    }

}
