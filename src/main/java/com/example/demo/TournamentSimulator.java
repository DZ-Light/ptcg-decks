package com.example.demo;

import java.util.Map;

public class TournamentSimulator {
    public static void main(String[] args) {
        int totalPlayers = 128; // 假设总人数为128
        int simulations = 1000; // 模拟次数

        try {
            DeckDatabase deckDatabase = new DeckDatabase();
//            Map<String, Double> deckQualificationProbabilities = simulateTournaments(totalPlayers, simulations, deckDatabase);

            // 输出每种卡组的晋级概率
//            for (Map.Entry<String, Double> entry : deckQualificationProbabilities.entrySet()) {
//                System.out.println("Deck: " + entry.getKey() + ", Qualification Probability: " + entry.getValue());
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
