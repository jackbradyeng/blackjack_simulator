package Model.Table.Positions;

import Model.Actors.Dealer;
import Model.Table.Hands.DealerHand;
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
