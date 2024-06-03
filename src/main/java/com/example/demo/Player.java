package com.example.demo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Player {
    Deck deck;
    int id;
    int score;
    List<Player> opponent;
    public Player(int id, Deck deck, int score){
        this.id = id;
        this.deck = deck;
        this.score = score;
        this.opponent = new ArrayList<>();
    }
}
