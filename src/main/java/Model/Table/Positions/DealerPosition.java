package Model.Table.Positions;

import Model.Table.Hands.DealerHand;
import lombok.Getter;
import lombok.Setter;

public class DealerPosition {

    @Getter
    @Setter
    private DealerHand hand;

    public DealerPosition() {
        this.hand = new DealerHand();
    }

    public void clearHand() {
        hand.getCards().clear();
    }
}
