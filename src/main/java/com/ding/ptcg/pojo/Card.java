package com.ding.ptcg.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Card {
    private String name;
    private String type;
    private String setCode;
    private String cardIndex;
}
