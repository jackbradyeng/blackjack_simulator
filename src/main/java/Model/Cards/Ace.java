package Model.Cards;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Ace extends Card {

    public Ace(String name, int value) {
        super(name, value);
    }
}
