package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.DeckCardRepository;
import com.example.demo.repository.DeckRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
            cardRepository.save(Card.builder().cardId(cardId).nickName(nickName).chineseName(chineseName).englishName(englishName).type(type).rare(rare).build());
        }
        deckCardRepository.save(DeckCard.builder().deckCardId(DeckCardId.builder().deckId(deckId).cardId(cardId).build()).quantity(quantity).build());

    }
}
