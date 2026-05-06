package model.Table.positions;

import model.actors.Dealer;
import model.Table.hands.DealerHand;
import lombok.Getter;
import lombok.Setter;

public class DealerPosition {

    @Getter
    @Setter
    private DealerHand hand;

    @Getter
    @Setter
    private Dealer dealer;

    public DealerPosition() {
        this.hand = new DealerHand();
    }

    public void clearHand() {
        hand.getCards().clear();
    }
}
