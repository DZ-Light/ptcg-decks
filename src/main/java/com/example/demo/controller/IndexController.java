package com.example.demo.controller;

import com.example.demo.entity.Card;
import com.example.demo.entity.CardId;
import com.example.demo.entity.Deck;
import com.example.demo.entity.DeckCard;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.DeckCardRepository;
import com.example.demo.repository.DeckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
public class IndexController {
    @Autowired
    CardRepository cardRepository;
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    DeckCardRepository deckCardRepository;

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false) String deckId) {
        List<Deck> decks = deckRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        if (deckId != null && !deckId.isBlank()) {
            StringBuilder pokemon = new StringBuilder("Pokémon ()").append("\n");
            StringBuilder trainer = new StringBuilder("Trainer ()").append("\n");
            StringBuilder energy = new StringBuilder("Energy ()").append("\n");
            Long pokemonCount = 0L;
            Long trainerCount = 0L;
            Long energyCount = 0L;
            Optional<Deck> deck = deckRepository.findById(Long.valueOf(deckId));
            if (deck.isPresent()) {
                List<DeckCard> deckCardList = deckCardRepository.findDeckCardByDeckCardId_DeckId(Long.valueOf(deckId), Sort.by(Sort.Direction.DESC, "quantity"));
                for (DeckCard deckcard : deckCardList) {
                    CardId cardId = deckcard.getDeckCardId().getCardId();
                    Card card = cardRepository.findByCardId(cardId);
                    String cardName = card.getNickName();
                    if (card.getChineseName() != null && !card.getChineseName().isBlank()) {
                        cardName = card.getChineseName();
                    }
                    List<Card> rareCard = cardRepository.findByNickNameAndRare(card.getNickName(), "1");
                    if (!rareCard.isEmpty()) {
                        cardId = rareCard.get(0).getCardId();
                    } else {
                        List<Card> commonCard = cardRepository.findByNickNameAndRare(card.getNickName(), "0");
                        cardId = commonCard.get(0).getCardId();
                    }
                    switch (card.getType()) {
                        case "Pokémon" -> {
                            pokemonCount += deckcard.getQuantity();
                            pokemon.append(deckcard.getQuantity()).append(" ").append(cardName).append(" ").append(cardId.getSetName()).append(" ").append(cardId.getSetNumber()).append("\n");
                        }
                        case "Trainer" -> {
                            trainerCount += deckcard.getQuantity();
                            trainer.append(deckcard.getQuantity()).append(" ").append(cardName).append(" ").append(cardId.getSetName()).append(" ").append(cardId.getSetNumber()).append("\n");
                        }
                        case "Energy" -> {
                            energyCount += deckcard.getQuantity();
                            energy.append(deckcard.getQuantity()).append(" ").append(cardName).append(" ").append(cardId.getSetName()).append(" ").append(cardId.getSetNumber()).append("\n");
                        }
                        default -> log.info("卡牌未设置分类: {}", cardId);
                    }
                }
                energy.append("总计: ").append(pokemonCount + trainerCount + energyCount);
                pokemon.insert(9, pokemonCount);
                trainer.insert(9, trainerCount);
                energy.insert(8, energyCount);
                // 将三个StringBuilder的内容按顺序写入文件
                String sb = String.valueOf(pokemon) + trainer + energy;
                // 创建返回的Map
                model.addAttribute("deckId", deckId);
                model.addAttribute("deckName", deck.get().getDeckName());
                model.addAttribute("deckCode", sb);
            }
        }
        model.addAttribute("decks", decks);
        return "index";
    }
}
