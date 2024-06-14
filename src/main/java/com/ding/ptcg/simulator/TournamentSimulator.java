package com.ding.ptcg.simulator;

import com.ding.ptcg.pojo.Player;

import java.util.*;

public class TournamentSimulator {
    public static void main(String[] args) {
        int totalPlayers = 1500; // 总人数
        int simulations = 100; // 模拟次数

        try {
            Map<String, Integer> map = new HashMap<>();
            DeckDatabase deckDatabase = new DeckDatabase();
            for (int i = 0; i < simulations; i++) {
                SwissTournament swissTournament = new SwissTournament(totalPlayers, deckDatabase);
                List<Player> qualifiers = swissTournament.simulate(deckDatabase.getDecks());
                System.out.println("模拟第" + (i + 1) + "次：");
                for (int j = 0; j < qualifiers.size(); j++) {
                    System.out.println("第" + (j + 1) + "名：" + qualifiers.get(j).getDeck());
                    System.out.println("战绩：" + qualifiers.get(j).getWin() + " - " + qualifiers.get(j).getLoss());
                    System.out.println("积分：" + qualifiers.get(j).getScore());
                    System.out.println("小分：" + qualifiers.get(j).getSubScore());
                    System.out.println("晋级之路");
                    for (String s : qualifiers.get(j).getBattleLog()) {
                        System.out.println(s);
                    }
                    map.put(qualifiers.get(j).getDeck(), map.getOrDefault(qualifiers.get(j).getDeck(), 0) + 1);
                }
            }

            Map<String, Integer> sortedMap = sortMapByValueDescending(map);

            // 打印排序后的map
            for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValueDescending(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        // 根据value进行倒序排序
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        // 转换回LinkedHashMap以保持倒序
        Map<K, V> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
