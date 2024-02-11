package com.example.demo.controller;

import com.example.demo.entity.Deck;
import com.example.demo.repository.DeckCardRepository;
import com.example.demo.repository.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {
    @Autowired
    DeckRepository deckRepository;
    @GetMapping("/")
    public String index(Model model) {
        List<Deck> decks = deckRepository.findAll();
        model.addAttribute("decks", decks);
        return "index";
    }
}
