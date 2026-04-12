package Model.Table.PayoutServices;

import Model.Actors.Dealer;
import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;
import java.util.List;

public interface PayoutService {

    void process(List<PlayerHand> activeHands, DealerHand dealerHand, Dealer dealer);
}
