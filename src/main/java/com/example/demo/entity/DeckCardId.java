package com.example.demo.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class DeckCardId implements Serializable {
    private Long deckId;
    private CardId cardId;
}
