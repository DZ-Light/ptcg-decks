package com.example.demo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Player {
    private String deck;
    private String id;
    private int win;
    private int loss;
    private int score;
    private int subScore;
    private List<String> opponent;
    private List<String> battleLog;
    public Player(String id, String deck, int score){
        this.id = id;
        this.deck = deck;
        this.win = 0;
        this.loss = 0;
        this.score = score;
        this.opponent = new ArrayList<>();
        this.battleLog = new ArrayList<>();
    }
    public void win(int round,Player opponent){
        this.win++;
        this.score+=3;
        this.battleLog.add("第" + round + "轮：" + this.deck + " VS " + opponent.getDeck() + " 胜");
    }
    public void loss(int round,Player opponent){
        this.loss++;
        this.battleLog.add("第" + round + "轮：" + this.deck + " VS " + opponent.getDeck() + " 负");
    }
    public void dLoss(int round,Player opponent){
        this.loss++;
        this.battleLog.add("第" + round + "轮：" + this.deck + " VS " + opponent.getDeck() + " 双败");
    }

}
