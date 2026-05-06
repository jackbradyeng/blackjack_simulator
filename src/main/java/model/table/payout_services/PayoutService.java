package model.table.payout_services;

import model.actors.Dealer;
import model.table.hands.DealerHand;
import model.table.hands.PlayerHand;
import java.util.List;

public interface PayoutService {

    void process(List<PlayerHand> activeHands, DealerHand dealerHand, Dealer dealer);
}
