package com.example.demo.repository;

import com.example.demo.entity.DeckCard;
import com.example.demo.entity.DeckCardId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeckCardRepository extends JpaRepository<DeckCard, DeckCardId> {
    List<DeckCard> findDeckCardByDeckCardId_DeckId(Long id);
}
