package com.ding.ptcg.simulator;

import com.ding.ptcg.pojo.Player;

import java.util.Comparator;

public class PlayerComparator implements Comparator<Player> {
    @Override
    public int compare(Player p1, Player p2) {
        // 首先比较 Score
        int ScoreComparison = Integer.compare(p1.getScore(), p2.getScore());
        if (ScoreComparison != 0) {
            return -ScoreComparison;
        }

        // 如果 Score 相同，比较 SubScore
        int SubScoreComparison = Integer.compare(p1.getSubScore(), p2.getSubScore());
        if (SubScoreComparison != 0) {
            return -SubScoreComparison;
        }

        // 如果两个字段都相同，返回0，表示这两个对象相等
        return 0;
    }
}
