package Model.Table.ActionServices;

import Model.Deck.Deck;
import Model.Table.Hands.Hand;

public interface ActionService {

    boolean hit(Deck deck, Hand hand);
}
