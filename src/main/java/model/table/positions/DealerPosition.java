package model.table.positions;

import model.actors.Dealer;
import model.table.hands.DealerHand;
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
