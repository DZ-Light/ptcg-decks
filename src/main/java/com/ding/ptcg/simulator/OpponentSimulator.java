package com.ding.ptcg.simulator;

import com.ding.ptcg.pojo.Deck;
import com.ding.ptcg.pojo.Player;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class OpponentSimulator {
    public static void main(String[] args) throws IOException {
        int initRounds = Integer.parseInt(args[0]);
        int totalPlayers = 1500; // 总人数
        int rounds = (int) Math.ceil(Math.log(totalPlayers) / Math.log(2));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        DeckDatabase deckDatabase = new DeckDatabase();
        List<Player> players = new ArrayList<>();
        players.add(new Player("1", "汇流三神", 0));
        for (int i = 2; i <= totalPlayers; i++) {
            // 根据使用率随机选择卡组
            String deck = selectDeckBasedOnUsageRate(deckDatabase.getDecks());
            players.add(new Player(Integer.toString(i), deck, 0));
        }

        if (initRounds - 1 > 0) {
            players = objectMapper.readValue(new File("player_" + (initRounds - 1) + ".json"), new TypeReference<List<Player>>() {
            });
        }


        for (int round = initRounds; round <= rounds; round++) {
            // 进行配对和模拟比赛
            Map<Player, Player> matchMaps = pairPlayers(round, players);
            for (Map.Entry<Player, Player> entry : matchMaps.entrySet()) {
                if (entry.getKey().getId().equals("1") || entry.getValue().getId().equals("1")) {
                    System.out.println("第" + round + "轮：" + entry.getKey().getId() + "-" + entry.getKey().getDeck() + " VS " + entry.getValue().getId() + "-" + entry.getValue().getDeck());
                    boolean flag = true;
                    do {
                        Scanner scanner = new Scanner(System.in);
                        String result = scanner.nextLine();
                        switch (result) {
                            case "1":
                                entry.getKey().win(round, entry.getValue());
                                entry.getValue().loss(round, entry.getKey());
                                flag = false;
                                break;
                            case "2":
                                entry.getValue().win(round, entry.getKey());
                                entry.getKey().loss(round, entry.getValue());
                                flag = false;
                                break;
                            case "0":
                                entry.getKey().dLoss(round, entry.getValue());
                                entry.getValue().dLoss(round, entry.getKey());
                                flag = false;
                            default:
                                System.out.println("请重新输入对局结果");
                        }
                    } while (flag);
                } else {
                    simulateMatch(round, entry.getKey(), entry.getValue(), deckDatabase.getDecks());
                }
            }
            //更新小分
            updateSubSource(players);
            players.sort(new PlayerComparator());
            try {
                objectMapper.writeValue(new File("player_" + round + ".json"), players);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getId().equals("1")) {
                    System.out.println("目前排名：" + (i + 1) + "名");
                    System.out.println("目前战绩：" + players.get(i).getWin() + " - " + players.get(i).getLoss());
                    System.out.println("目前积分：" + players.get(i).getScore());
                    System.out.println("目前小分：" + players.get(i).getSubScore());
                }
            }
        }
        for (int i = 0; i < players.size(); i++) {
            System.out.println("第" + (i + 1) + "名：" + players.get(i).getDeck());
            System.out.println("战绩：" + players.get(i).getWin() + " - " + players.get(i).getLoss());
            System.out.println("积分：" + players.get(i).getScore());
            System.out.println("小分：" + players.get(i).getSubScore());
            System.out.println("晋级之路");
            for (String s : players.get(i).getBattleLog()) {
                System.out.println(s);
            }
            if (players.get(i).getId().equals("1")) {
                break;
            }
        }


    }

    private static String selectDeckBasedOnUsageRate(List<Deck> decks) {
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

    private static Map<Player, Player> pairPlayers(int round, List<Player> players) {
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
                players.subList(startIndex, startIndex + samePlayers.size()).clear();
                players.addAll(startIndex, samePlayers);
            }
        }
        Map<Player, Player> matchMaps = new HashMap<>();
        int size = players.size();
        for (int i = 0; i < size; i += 2) {
            if (i + 1 < size) {
                // 将当前元素和它的下一个元素作为键值对添加到map中
                matchMaps.put(players.get(i), players.get(i + 1));
            } else {
                Player player = players.get(i);
                player.setScore(player.getScore() + 3);
                player.getBattleLog().add("第" + round + "轮：轮空");
            }
        }
        return matchMaps;
    }

    private static void simulateMatch(int round, Player player1, Player player2, List<Deck> decks) {
        // 根据卡组的胜率和随机性模拟比赛结果
        float randomPoint = new Random().nextFloat();
        float currentRate = 0;

        for (Deck deck : decks) {
            if (deck.getName().equals(player1.getDeck())) {
                currentRate += deck.getWinRates().getOrDefault(player2.getDeck(), 0.8F);
                if (currentRate >= randomPoint) {
                    player1.win(round, player2);
                    player2.loss(round, player1);
                    return;
                }
            }
            if (deck.getName().equals(player2.getDeck())) {
                currentRate += deck.getWinRates().getOrDefault(player1.getDeck(), 0.8F);
                if (currentRate >= randomPoint) {
                    player2.win(round, player1);
                    player1.loss(round, player2);
                    return;
                }
            }
        }
        if (player1.getDeck().equals("其他")) {
            currentRate += 0.5F;
            if (currentRate >= randomPoint) {
                player1.win(round, player2);
                player2.loss(round, player1);
                return;
            }
        }
        if (player2.getDeck().equals("其他")) {
            currentRate += 0.5F;
            if (currentRate >= randomPoint) {
                player2.win(round, player1);
                player1.loss(round, player2);
                return;
            }
        }
        player1.dLoss(round, player2);
        player2.dLoss(round, player1);
    }

    private static void updateSubSource(List<Player> players) {
        for (Player player : players) {
            int subSource = 0;
            for (Player opponent : players) {
                if (player.getOpponent().contains(opponent.getId())) {
                    subSource += opponent.getScore();
                }
            }
            player.setSubScore(subSource);
        }
    }
}
