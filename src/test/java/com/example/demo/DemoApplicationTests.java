package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.DeckCardRepository;
import com.example.demo.repository.DeckRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@SpringBootTest
class DemoApplicationTests {
    @Autowired
    CardRepository cardRepository;
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    DeckCardRepository deckCardRepository;

    @Test
    void deckExport() {
        List<Deck> deckList = deckRepository.findAll();
        for (Deck deck : deckList) {
            StringBuilder pokemon = new StringBuilder("Pokémon").append("\n");
            StringBuilder trainer = new StringBuilder("Trainer").append("\n");
            StringBuilder energy = new StringBuilder("Energy").append("\n");
            List<DeckCard> deckCardList = deckCardRepository.findDeckCardByDeckCardId_DeckId(deck.getId());
            for (DeckCard deckcard : deckCardList) {
                CardId cardId = deckcard.getDeckCardId().getCardId();
                Card card = cardRepository.findByCardId(cardId);
                String cardName = card.getNickName();
                if (card.getChineseName() != null && !card.getChineseName().isBlank()) cardName = card.getChineseName();
                switch (card.getType()) {
                    case "Pokémon" ->
                            pokemon.append(deckcard.getQuantity()).append(" ").append(cardName).append(" ").append(cardId.getSetName()).append(" ").append(cardId.getSetNumber()).append("\n");
                    case "Trainer" ->
                            trainer.append(deckcard.getQuantity()).append(" ").append(cardName).append(" ").append(cardId.getSetName()).append(" ").append(cardId.getSetNumber()).append("\n");
                    case "Energy" ->
                            energy.append(deckcard.getQuantity()).append(" ").append(cardName).append(" ").append(cardId.getSetName()).append(" ").append(cardId.getSetNumber()).append("\n");
                }
            }
            File file = new File("deck/" + deck.getDeckName());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // 将三个StringBuilder的内容按顺序写入文件
                writer.write(pokemon.toString());
                writer.write(trainer.toString());
                writer.write(energy.toString());
                System.out.println("所有StringBuilder的内容已成功写入文件。");
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
    @Test
    void deckExportCSV() {
        List<Deck> deckList = deckRepository.findAll();
        for (Deck deck : deckList) {
            StringBuilder sb = new StringBuilder("QTY,Name,Type,URL");
            List<DeckCard> deckCardList = deckCardRepository.findDeckCardByDeckCardId_DeckId(deck.getId());
            for (DeckCard deckcard : deckCardList) {
                CardId cardId = deckcard.getDeckCardId().getCardId();
                Card card = cardRepository.findByCardId(cardId);
                String cardName = card.getNickName();
                if (card.getChineseName() != null && !card.getChineseName().isBlank()) cardName = card.getChineseName();
                sb.append("\n").append(deckcard.getQuantity()).append(",").append(cardName).append(",").append(card.getType()).append(",").append("http://localhost:4000/src/img/en/").append(cardId.getSetName()).append("/").append(cardId.getSetName()).append("_").append(cardId.getSetNumber()).append("_R_EN.png");
            }
            File file = new File("deck/" + deck.getDeckName()+".csv");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // 将三个StringBuilder的内容按顺序写入文件
                writer.write(sb.toString());
                System.out.println("所有StringBuilder的内容已成功写入文件。");
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Test
    void deckInsert() {
        Long deckId = 2L;
        Long quantity = 1L;
        String setName = "SMP";
        String setNumber = "191";
        String nickName = "耿鬼TT";
        String chineseName = "耿鬼&谜拟丘GX";
        String englishName = "Gengar & Mimikyu-GX";
        String japaneseName = "ゲンガー&ミミッキュGX";
        String type = "Pokémon";
        String rare = "1";
        String zone = "tpci";
        CardId cardId = CardId.builder().setName(setName).setNumber(setNumber).build();
        Card card = cardRepository.findByCardId(cardId);
        if (card == null) {
            cardRepository.save(Card.builder().cardId(cardId).nickName(nickName).chineseName(chineseName).englishName(englishName).japaneseName(japaneseName).type(type).rare(rare).zone(zone).build());
        }
        deckCardRepository.save(DeckCard.builder().deckCardId(DeckCardId.builder().deckId(deckId).cardId(cardId).build()).quantity(quantity).build());

    }
}
