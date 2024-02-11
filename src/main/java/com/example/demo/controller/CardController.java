package com.example.demo.controller;

import com.example.demo.entity.Card;
import com.example.demo.entity.CardId;
import com.example.demo.repository.CardRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/card")
public class CardController {
	private final CardRepository cardRepository;

	public CardController(CardRepository cardRepository) {
		this.cardRepository = cardRepository;
	}

	@PostMapping(path="/add") // Map ONLY POST Requests
	public @ResponseBody String addNewUser (@RequestParam String set
			, @RequestParam String setNumber,@RequestParam String type) {
		cardRepository.save(Card.builder().cardId(CardId.builder().setName(set).setNumber(setNumber).build()).type(type).build());
		return "Saved";
	}

	@GetMapping(path="/all")
	public @ResponseBody Iterable<Card> getAllUsers() {
		// This returns a JSON or XML with the users
		return cardRepository.findAll();
	}
}
