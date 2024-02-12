package com.example.demo.repository;

import com.example.demo.entity.Card;
import com.example.demo.entity.CardId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, CardId> {
    Card findByCardId(CardId cardId);

    List<Card> findByNickNameAndRare(String name, String rare);
    @Query("SELECT c FROM Card c WHERE c.nickName LIKE :name and c.rare = :rare")
    List<Card> findByNickNameLikeAndRare(String name, String rare);

    List<Card> findByChineseNameAndRare(String name, String rare);
}
