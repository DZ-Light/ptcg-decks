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
import org.springframework.data.domain.Sort;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@SpringBootTest
class DeckExportCsvTest {
    @Autowired
    CardRepository cardRepository;
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    DeckCardRepository deckCardRepository;

    @Test
    void deckExportCSV() {
        List<Deck> deckList = deckRepository.findAll();
        for (Deck deck : deckList) {
            StringBuilder sb = new StringBuilder("QTY,Name,Type,URL");
            List<DeckCard> deckCardList = deckCardRepository.findDeckCardByDeckCardId_DeckId(deck.getId(), Sort.by(Sort.Direction.DESC, "quantity"));
            for (DeckCard deckcard : deckCardList) {
                CardId cardId = deckcard.getDeckCardId().getCardId();
                Card card = cardRepository.findByCardId(cardId);
                String cardName = card.getNickName();
                if (card.getChineseName() != null && !card.getChineseName().isBlank()) {
                    cardName = card.getChineseName();
                }
                List<Card> rareCard = cardRepository.findByNickNameAndRare(card.getNickName(), "1");
                if (rareCard.size() == 1) {
                    cardId = rareCard.get(0).getCardId();
                }
                sb.append("\n").append(deckcard.getQuantity()).append(",").append(cardName).append(",").append(card.getType()).append(",").append("http://localhost:4000/src/img/en/").append(cardId.getSetName()).append("/").append(cardId.getSetName()).append("_").append(cardId.getSetNumber()).append("_R_EN.png");
            }
            File file = new File("deck/" + deck.getDeckName() + ".csv");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // 将三个StringBuilder的内容按顺序写入文件
                writer.write(sb.toString());
                System.out.println("所有StringBuilder的内容已成功写入文件。");
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
