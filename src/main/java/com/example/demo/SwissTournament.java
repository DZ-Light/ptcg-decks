package com.example.demo;

import java.util.*;

public class SwissTournament {
    private List<Player> players;
    private int rounds;
    private int cutOff; // 晋级人数，根据总人数确定

    public SwissTournament(int totalPlayers, DeckDatabase cardDeckData) {
        this.players = initializePlayers(totalPlayers, cardDeckData);
        this.rounds = calculateRounds(totalPlayers); // 根据总人数计算轮数
        this.cutOff = calculateCutOff(totalPlayers); // 根据总人数计算晋级人数
    }

    private List<Player> initializePlayers(int totalPlayers, DeckDatabase deckDatabase) {
        List<Player> players = new ArrayList<>();
        // 初始化选手列表，包括分配卡组和初始化胜负记录等
        for (int i = 1; i <= totalPlayers; i++) {
            // 根据使用率随机选择卡组
            String deck = selectDeckBasedOnUsageRate(deckDatabase.getDecks());
            players.add(new Player(Integer.toString(i),deck,0));
        }
        return players;
    }
    private String selectDeckBasedOnUsageRate(List<Deck> decks) {
        // 根据使用率随机选择一个卡组
        float randomPoint = new Random().nextFloat();
        float currentUsage = 0;
        for (Deck deck : decks) {
            currentUsage += deck.getUsageRates();
            if (currentUsage >= randomPoint) {
                return deck.getName();
            }
        }
        return "其他";
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

    public List<Player> simulate(List<Deck> decks) {
        for (int round = 1; round <= rounds; round++) {
            // 进行配对和模拟比赛
            Map<Player, Player> matchMaps = pairPlayers(round,players);
            for (Map.Entry<Player, Player> entry : matchMaps.entrySet()) {
                simulateMatch(round,entry.getKey(), entry.getValue(), decks);
            }
            //更新小分
            updateSubSource();
        }

        // 根据胜负记录确定晋级选手
        return determineQualifiers(players, cutOff);
        // 输出晋级选手或进行其他后续处理...
    }

    private Map<Player, Player> pairPlayers(int round,List<Player> players) {
        // 首先根据积分和小分排序
        players.sort(new PlayerComparator());
        // 接下来处理字段都相同的情况，进行随机排序
        for (int i = 0; i < players.size(); ) {
            Player current = players.get(i);
            List<Player> samePlayers = new ArrayList<>();

            // 查找所有字段都相同的对象
            while (i < players.size() &&
                    Objects.equals(current.getScore(), players.get(i).getScore()) &&
                    Objects.equals(current.getSubScore(), players.get(i).getSubScore())) {
                samePlayers.add(players.get(i));
                i++;
            }

            // 如果找到多个相同的对象，则进行随机排序
            if (samePlayers.size() > 1) {
                int startIndex = players.indexOf(samePlayers.get(0));
                Collections.shuffle(samePlayers);
                // 获取原始列表中第一个相同对象的索引
                players.subList(startIndex, startIndex+samePlayers.size()).clear();
                players.addAll(startIndex, samePlayers);
            }
        }
        Map<Player, Player> matchMaps = new HashMap<>();
        int size = players.size();
        for (int i = 0; i < size; i+=2) {
            if (i + 1 < size) {
                // 将当前元素和它的下一个元素作为键值对添加到map中
                matchMaps.put(players.get(i), players.get(i + 1));
                players.get(i).getOpponent().add(players.get(i + 1).getId());
                players.get(i+1).getOpponent().add(players.get(i).getId());
            } else {
                Player player = players.get(i);
                player.setScore(player.getScore()+3);
                player.getBattleLog().add("第"+round+"轮：轮空");
            }
        }
        return matchMaps;
    }

    private void simulateMatch(int round,Player player1, Player player2, List<Deck> decks) {
        // 根据卡组的胜率和随机性模拟比赛结果
        float randomPoint = new Random().nextFloat();
        float currentRate = 0;

        for (Deck deck : decks) {
            if (deck.getName().equals(player1.getDeck())){
                currentRate += deck.getWinRates().getOrDefault(player2.getDeck(),0.8F);
                if (currentRate >= randomPoint) {
                    player1.win(round,player2);
                    player2.loss(round,player1);
                    return;
                }
            }
            if (deck.getName().equals(player2.getDeck())){
                currentRate += deck.getWinRates().getOrDefault(player1.getDeck(),0.8F);
                if (currentRate >= randomPoint) {
                    player2.win(round,player1);
                    player1.loss(round,player2);
                    return;
                }
            }
        }
        if (player1.getDeck().equals("其他")){
            currentRate += 0.5F;
            if (currentRate >= randomPoint) {
                player1.win(round,player2);
                player2.loss(round,player1);
                return;
            }
        }
        if (player2.getDeck().equals("其他")){
            currentRate += 0.5F;
            if (currentRate >= randomPoint) {
                player2.win(round,player1);
                player1.loss(round,player2);
                return;
            }
        }
        player1.dLoss(round,player2);
        player2.dLoss(round,player1);
    }

    private List<Player> determineQualifiers(List<Player> players, int cutOff) {
        List<Player> qualifier = new ArrayList<>();
        // 首先根据积分和小分排序
        players.sort(new PlayerComparator());
        for (int i = 0; i < cutOff; i++) {
            qualifier.add(players.get(i));
        }
        return qualifier;
    }

    private void updateSubSource(){
        for (Player player: players){
            int subSource = 0;
            for (Player opponent: players){
                if (player.getOpponent().contains(opponent.getId())){
                    subSource+=opponent.getSubScore();
                }
            }
            player.setSubScore(subSource);
        }
    }

}
