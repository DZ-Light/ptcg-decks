package com.example.demo.repository;

import com.example.demo.entity.DeckCard;
import com.example.demo.entity.DeckCardId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DeckCardRepository extends JpaRepository<DeckCard, DeckCardId> {
    List<DeckCard> findDeckCardByDeckCardId_DeckId(Long id);

    @Modifying
    @Transactional
    @Query("delete from DeckCard dc where dc.deckCardId.deckId = :deckId")
    int deleteAllByDeckId(Long deckId);
}
