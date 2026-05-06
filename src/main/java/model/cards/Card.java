package model.cards;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Card {

    private String name;
    private int value;

    @Override
    public String toString() {
        return name;
    }
}
