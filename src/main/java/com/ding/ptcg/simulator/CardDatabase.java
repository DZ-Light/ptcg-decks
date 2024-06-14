package com.ding.ptcg.simulator;

import com.ding.ptcg.pojo.Card;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CardDatabase {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path jsonDirectory = Paths.get("db/");
    private List<Card> cards;

    // 初始化ObjectMapper，设置缩进等
    public CardDatabase() {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            cards = readAll();
        } catch (IOException e) {
            cards = new ArrayList<>();
        }
    }

    public void addCard(Card card) {
        for (Card c : cards) {
            if (c.equals(card)) return;
        }
        cards.add(card);
        writeAll(cards);
    }

    // 根据mark分组并排序后写入不同的JSON文件
    public void writeAll(List<Card> cards) {
        // 根据mark分组
        Map<String, List<Card>> cardsByMark = cards.stream()
                .collect(Collectors.groupingBy(Card::getSetCode));

        // 遍历每个分组，按number排序并写入对应的文件
        for (Map.Entry<String, List<Card>> entry : cardsByMark.entrySet()) {
            String mark = entry.getKey();
            List<Card> sortedCards = entry.getValue().stream()
                    .sorted(Comparator.comparing(Card::getCardIndex))
                    .collect(Collectors.toList());

            String filePath = jsonDirectory + "/" + mark + ".json";
            try {
                objectMapper.writeValue(new File(filePath), sortedCards);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Wrote " + sortedCards.size() + " cards of mark " + mark + " to " + filePath);
        }
    }

    // 读取指定mark的JSON文件
    public List<Card> readCardByMark(String mark) throws IOException {
        String filePath = jsonDirectory + "/" + mark + ".json";
        return objectMapper.readValue(new File(filePath), new TypeReference<List<Card>>() {
        });
    }

    public List<Card> readAll() throws IOException {
        List<Card> cards = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(jsonDirectory)) {
            for (Path path : directoryStream) {
                if (path.toString().endsWith(".json")) {
                    List<Card> cardsFromFile = readUsersFromFile(path);
                    cards.addAll(cardsFromFile);
                }
            }
        }
        return cards;
    }

    private List<Card> readUsersFromFile(Path filePath) throws IOException {
        return objectMapper.readValue(filePath.toFile(), new TypeReference<List<Card>>() {
        });
    }
}
