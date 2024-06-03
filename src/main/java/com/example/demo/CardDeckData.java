package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.Map;
@Data
public class CardDeckData {
    private Map<String, Map<String, Float>> winRates; // 卡组间胜率
    private Map<String, Float> usageRates; // 卡组使用率

    public CardDeckData(String jsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CardDeckDataJson jsonData = objectMapper.readValue(new File(jsonFilePath), CardDeckDataJson.class);
        this.winRates = jsonData.getDecks();
        this.usageRates = jsonData.getUsageRates();
    }

    // 内部类，用于映射JSON文件的结构
    @Data
    private static class CardDeckDataJson {
        private Map<String, Map<String, Float>> decks;
        private Map<String, Float> usageRates;
    }
}
