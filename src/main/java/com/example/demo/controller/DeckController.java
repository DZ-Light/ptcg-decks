package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.model.DeckImport;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.DeckCardRepository;
import com.example.demo.repository.DeckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/deck")
public class DeckController {
    @Autowired
    CardRepository cardRepository;
    @Autowired
    DeckRepository deckRepository;
    @Autowired
    DeckCardRepository deckCardRepository;

    @PostMapping(path = "/import")
    public String deckImport(@RequestBody DeckImport deckImport) {
        log.info("卡组名称: {}", deckImport.getDeckName());
        log.info("卡组代码: {}", deckImport.getDeckCode());
        Deck deck = deckRepository.save(Deck.builder().deckName(deckImport.getDeckName()).build());
        log.info("deckId:{}, deckName:{}", deck.getId(), deck.getDeckName());
        String type = "";
        for (String str : deckImport.getDeckCode().split("\n")) {
            String setName = "";
            String setNumber = "";
            String cardName = "";
            String quantity = "";
            if (str.contains("Pokémon (")) type = "Pokémon";
            else if (str.contains("Trainer (")) type = "Trainer";
            else if (str.contains("Energy (")) type = "Energy";
            else if (!str.isBlank()) {
                String result = Pattern.compile("(?!PR-)(\\w{2,3})-(\\w{2,3}) (\\d+)").matcher(str).replaceAll("$1 $2$3");
                // 创建匹配器
                Matcher matcher = Pattern.compile("(\\d+) (.+?) (\\w{2,3}) ((\\w{2,3})?\\d+[a-zA-Z]?)").matcher(result);
                Matcher matcherEnergy = Pattern.compile("(\\d+) (.+?) Energy (\\d+)").matcher(str);
                if (matcherEnergy.find()) {
                    quantity = matcherEnergy.group(1);
                    String energyType = matcherEnergy.group(2);
                    switch (energyType){
                        case "Fire":
                            setName = "BUS";
                            setNumber = "167";
                            cardName = "基本火能量";
                            break;
                        case "Grass":
                            setName = "GRI";
                            setNumber = "167";
                            cardName = "基本草能量";
                            break;
                        case "Fairy":
                            setName = "BUS";
                            setNumber = "169";
                            cardName = "基本妖能量";
                            break;
                        case "Darkness":
                            setName = "BUS";
                            setNumber = "168";
                            cardName = "基本恶能量";
                            break;
                        case "Lightning":
                            setName = "GRI";
                            setNumber = "168";
                            cardName = "基本雷能量";
                            break;
                        case "Fighting":
                            setName = "GRI";
                            setNumber = "169";
                            cardName = "基本斗能量";
                            break;
                        case "Psychic":
                            setName = "SUM";
                            setNumber = "162";
                            cardName = "基本超能量";
                            break;
                        case "Metal":
                            setName = "SUM";
                            setNumber = "163";
                            cardName = "基本钢能量";
                            break;
                        case "Water":
                            setName = "CIN";
                            setNumber = "124";
                            cardName = "基本水能量";
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + energyType);
                    }
                }
                // 查找匹配项
                else if (matcher.find()) {
                    quantity = matcher.group(1);
                    cardName = matcher.group(2);
                    setName = matcher.group(3);
                    setNumber = matcher.group(4);

                } else {
                    log.info(result);
                }
                Card card = Card.builder().cardId(CardId.builder().setName(setName).setNumber(setNumber).build()).nickName(cardName).type(type).build();
                Optional<Card> temp = cardRepository.findById(CardId.builder().setName(setName).setNumber(setNumber).build());
                if (temp.isEmpty()){
                    cardRepository.save(card);
                }
                deckCardRepository.save(DeckCard.builder().deckCardId(DeckCardId.builder().deckId(deck.getId()).cardId(CardId.builder().setName(setName).setNumber(setNumber).build()).build()).quantity(Long.valueOf(quantity)).build());
            }
        }
        return "Saved";
    }
}
