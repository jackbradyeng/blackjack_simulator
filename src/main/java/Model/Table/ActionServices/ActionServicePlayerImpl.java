package Model.Table.ActionServices;

import Model.Deck.Deck;
import Model.Table.Hands.Hand;

public class ActionServicePlayerImpl implements ActionService {

    @Override
    public boolean hit(Deck deck, Hand hand) {

        if (hand.isBust()) {
            return false;
        } else {
            deck.deal().ifPresent(deal -> {
                hand.receiveCard(deal);
                hand.setHandValue();
                hand.setHasHit(true);
            });
            return true;
        }
    }
}
