package com.example.demo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Player {
    CardDeck deck;
    int id;
    int score;
    List<Player> opponent;
    public Player(int id,CardDeck deck,int score){
        this.id = id;
        this.deck = deck;
        this.score = score;
        this.opponent = new ArrayList<>();
    }
}
