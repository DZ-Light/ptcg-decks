package com.example.demo;

import com.example.demo.entity.Card;
import com.example.demo.entity.CardId;
import com.example.demo.entity.Deck;
import com.example.demo.entity.DeckCard;
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

@Slf4j
@SpringBootTest
class DeckExportTxtTest {
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
                if (card.getChineseName() != null && !card.getChineseName().isBlank()) {
                    cardName = card.getChineseName();
                    List<Card> rareCard = cardRepository.findByChineseNameAndRare(card.getChineseName(), "1");
                    if (rareCard.size() == 1) {
                        cardId = rareCard.get(0).getCardId();
                    }
                }
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
}
