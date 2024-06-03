package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SwissTournament {
    private List<Player> players;
    private Random random;
    private int rounds;
    private int cutOff; // 晋级人数，根据总人数确定

    public SwissTournament(int totalPlayers, DeckDatabase cardDeckData) {
        this.players = initializePlayers(totalPlayers, cardDeckData);
        this.random = new Random();
        this.rounds = calculateRounds(totalPlayers); // 根据总人数计算轮数
        this.cutOff = calculateCutOff(totalPlayers); // 根据总人数计算晋级人数
    }

    private List<Player> initializePlayers(int totalPlayers, DeckDatabase cardDeckData) {
        List<Player> players = new ArrayList<>();
        // 初始化选手列表，包括分配卡组和初始化胜负记录等
        for (int i = 1; i <= totalPlayers; i++) {
            // 根据使用率随机选择卡组
            Deck deck = selectDeckBasedOnUsageRate(decks);
            players.add(new Player(i,deck,0));
        }
        return players;
    }
    private Deck selectDeckBasedOnUsageRate(List<Deck> decks) {
        // 根据使用率随机选择一个卡组
        float totalUsage = 0;
        for (Deck deck : decks) {
            totalUsage += deck.usageRate;
        }
        float randomPoint = random.nextFloat() * totalUsage;
        float currentUsage = 0;
        for (Deck deck : decks) {
            currentUsage += deck.usageRate;
            if (currentUsage >= randomPoint) {
                return deck;
            }
        }
        // 如果所有卡组的使用率之和都小于randomPoint（理论上不可能发生），则返回最后一个卡组
        return decks.get(decks.size() - 1);
    }
    private int calculateRounds(int totalPlayers) {
        // 根据总人数计算轮数，可以使用公式或近似算法
        return (int) Math.ceil(Math.log(totalPlayers) / Math.log(2));
    }

    private int calculateCutOff(int totalPlayers) {
        // 根据总人数计算晋级人数，可以是前8或前16等
        if (totalPlayers<128)
            return 8;
        return 16;
    }

    public void simulate() {
        for (int round = 1; round <= rounds; round++) {
            // 进行配对和模拟比赛
            Map<Player, Player> matchups = pairPlayers(players);
            for (Map.Entry<Player, Player> entry : matchups.entrySet()) {
                Player winner = simulateMatch(entry.getKey(), entry.getValue(), cardDeckData);
                // 更新选手的胜负记录
            }
        }

        // 根据胜负记录确定晋级选手
        List<Player> qualifiers = determineQualifiers(players, cutOff);
        // 输出晋级选手或进行其他后续处理...
    }

    private Map<Player, Player> pairPlayers(List<Player> players) {
        // 实现瑞士轮制的配对逻辑
    }

    private Player simulateMatch(Player player1, Player player2, DeckDatabase cardDeckData) {
        // 根据卡组的胜率和随机性模拟比赛结果
    }

    private List<Player> determineQualifiers(List<Player> players, int cutOff) {
        // 根据胜负记录确定晋级选手
    }

}
