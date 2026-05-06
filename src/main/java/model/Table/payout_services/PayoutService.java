package model.Table.payout_services;

import model.actors.Dealer;
import model.Table.hands.DealerHand;
import model.Table.hands.PlayerHand;
import java.util.List;

public interface PayoutService {

    void process(List<PlayerHand> activeHands, DealerHand dealerHand, Dealer dealer);
}
