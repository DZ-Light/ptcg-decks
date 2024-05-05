package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.model.DeckImport;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.DeckCardRepository;
import com.example.demo.repository.DeckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/deck")
public class DeckController {
    final CardRepository cardRepository;
    final DeckRepository deckRepository;
    final DeckCardRepository deckCardRepository;

    public DeckController(CardRepository cardRepository, DeckRepository deckRepository, DeckCardRepository deckCardRepository) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.deckCardRepository = deckCardRepository;
    }

    @PostMapping(path = "/import")
    public ResponseEntity<String> deckImport(@RequestBody DeckImport deckImport) {
        Deck deck = saveDeck(deckImport);
        if (deck == null) {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        String type = "";
        for (String str : deckImport.getDeckCode().split("\n")) {
            String setName;
            String setNumber;
            String cardName;
            String quantity;
            if (Pattern.compile("Pokémon.*", Pattern.CANON_EQ).matcher(str).matches()) type = "Pokémon";
            else if (Pattern.compile("Trainer.*").matcher(str).matches()) type = "Trainer";
            else if (Pattern.compile("Energy.*").matcher(str).matches()) type = "Energy";
            else if (!str.isBlank()) {
                String string = str;
                //适配Live
                string = matchForPTCGLive(string);
                string = matchForEnergy(string);
                // 创建匹配器
                Matcher regexWithSet = Pattern.compile("(\\d+) (.+?) (\\w{2,3}) ((\\w{2,3})?\\d+[a-zA-Z]?)").matcher(string);
                Matcher regexWithOutSet = Pattern.compile("(\\d+) (.+)").matcher(string);
                // 查找匹配项
                if (regexWithSet.find()) {
                    quantity = regexWithSet.group(1);
                    cardName = regexWithSet.group(2);
                    setName = regexWithSet.group(3);
                    setNumber = regexWithSet.group(4);
                    Card card = Card.builder().cardId(CardId.builder().setName(setName).setNumber(setNumber).build()).nickName(cardName).type(type).build();
                    Optional<Card> temp = cardRepository.findById(CardId.builder().setName(setName).setNumber(setNumber).build());
                    if (temp.isEmpty()) {
                        card.setRare("0");
                        card.setEnglishName(cardName);
                        cardRepository.save(card);
                    }
                    deckCardRepository.save(DeckCard.builder().deckCardId(DeckCardId.builder().deckId(deck.getId()).cardId(CardId.builder().setName(setName).setNumber(setNumber).build()).build()).quantity(Long.valueOf(quantity)).build());

                } else if (regexWithOutSet.matches()) {
                    quantity = regexWithOutSet.group(1);
                    cardName = regexWithOutSet.group(2);
                    CardId cardId = getCardId(cardName);
                    if (cardId != null) {
                        deckCardRepository.save(DeckCard.builder().deckCardId(DeckCardId.builder().deckId(deck.getId()).cardId(cardId).build()).quantity(Long.valueOf(quantity)).build());
                    } else {
                        log.error("{} 未找到匹配的卡牌", cardName);
                    }
                }
            }
        }
        return new ResponseEntity<>(String.valueOf(deck.getId()), HttpStatus.OK);
    }

    @PostMapping("/findCardById")
    public Map<String, String> findCardById(@RequestBody DeckImport deckImport) {
        String deckId = deckImport.getDeckId();
        Map<String, String> result = new HashMap<>();
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
            result.put("deckId", deckId);
            result.put("deckName", deck.get().getDeckName());
            result.put("deckCode", sb);
        }
        return result;
    }

    private CardId getCardId(String cardName) {
        List<Card> cards = cardRepository.findByNickNameLikeAndRare("%" + cardName.replace(" ", "%") + "%", "1");
        if (cards.size() == 1) {
            return cards.get(0).getCardId();
        } else {
            log.info("{} 模糊查找稀有版本 发现{}条", cardName, cards.size());
        }
        cards = cardRepository.findByNickNameLikeAndRare("%" + cardName.replace(" ", "%"), "1");
        if (cards.size() == 1) {
            return cards.get(0).getCardId();
        } else {
            log.info("{} 模糊查找稀有版本 发现{}条", cardName, cards.size());
        }
        cards = cardRepository.findByNickNameLikeAndRare("%" + cardName.replace(" ", "%") + "%", "0");
        if (cards.size() == 1) {
            return cards.get(0).getCardId();
        } else {
            log.info("{} 模糊查找普通版本 发现{}条", cardName, cards.size());
        }
        cards = cardRepository.findByChineseNameAndRare(cardName, "1");
        if (cards.size() == 1) {
            return cards.get(0).getCardId();
        } else {
            log.info("{} 精确查找稀有版本 发现{}条", cardName, cards.size());
        }
        cards = cardRepository.findByChineseNameAndRare(cardName, "0");
        if (!cards.isEmpty()) {
            return cards.get(0).getCardId();
        } else {
            log.info("{} 精确查找普通版本 发现{}条", cardName, 0);
        }
        return null;
    }

    private Deck saveDeck(DeckImport deckImport) {
        log.info("DeckImport: {}", deckImport);
        if (!deckImport.getDeckId().isBlank()) {
            deckRepository.deleteById(Long.valueOf(deckImport.getDeckId()));
            int i = deckCardRepository.deleteAllByDeckId(Long.valueOf(deckImport.getDeckId()));
            log.info("删除了{}条数据", i);
        }
        if (deckImport.getDeckCode().isBlank()) {
            return null;
        }
        Deck deck = deckRepository.save(Deck.builder().deckName(deckImport.getDeckName()).build());
        log.info("Deck: {}", deck);
        return deck;
    }


    /**
     * 适配PTCG live的特殊格式
     *
     * @param string 匹配到到的特殊格式
     * @return 修改后的格式
     */
    private String matchForPTCGLive(String string) {
        string = Pattern.compile("(?!PR-)(\\w{2,3})-(\\w{2,3}) (\\d+)").matcher(string).replaceAll("$1 $2$3");
        string = Pattern.compile("PR-SV").matcher(string).replaceAll("SVP");
        string = Pattern.compile("PR-SW").matcher(string).replaceAll("SSP");
        string = Pattern.compile("PR-SM").matcher(string).replaceAll("SMP");
        string = Pattern.compile("PR-XY").matcher(string).replaceAll("XYP");
        string = Pattern.compile("PR-BLW").matcher(string).replaceAll("BWP");
        string = Pattern.compile("PR-HS").matcher(string).replaceAll("HSP");
        return string;
    }

    /**
     * 适配limitlesstcg的能量
     *
     * @param string 匹配到到的能量
     * @return 修改后的格式
     */
    private String matchForEnergy(String string) {
        string = Pattern.compile("(\\d+) Fire Energy (\\d+)").matcher(string).replaceAll("$1 基本火能量 OBF 230");
        string = Pattern.compile("(\\d+) Darkness Energy (\\d+)").matcher(string).replaceAll("$1 基本恶能量 BUS 168");
        string = Pattern.compile("(\\d+) Fairy Energy (\\d+)").matcher(string).replaceAll("$1 基本妖能量 BUS 169");
        string = Pattern.compile("(\\d+) Grass Energy (\\d+)").matcher(string).replaceAll("$1 基本草能量 PAL 278");
        string = Pattern.compile("(\\d+) Lightning Energy (\\d+)").matcher(string).replaceAll("$1 基本雷能量 SVI 257");
        string = Pattern.compile("(\\d+) Fighting Energy (\\d+)").matcher(string).replaceAll("$1 基本斗能量 SVI 258");
        string = Pattern.compile("(\\d+) Psychic Energy (\\d+)").matcher(string).replaceAll("$1 基本超能量 MEW 207");
        string = Pattern.compile("(\\d+) Metal Energy (\\d+)").matcher(string).replaceAll("$1 基本钢能量 SUM 163");
        string = Pattern.compile("(\\d+) Water Energy (\\d+)").matcher(string).replaceAll("$1 基本水能量 SVI 258");
        return string;
    }


}
