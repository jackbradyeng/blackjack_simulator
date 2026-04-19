package Model.Table.BettingServices;

import java.util.ArrayList;
import Model.Actors.Player;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;

public interface BettingService {
    void bookStandardBet(Player player, PlayerPosition position, double amount);
    void bookInsuranceBet(Player player, PlayerPosition position, PlayerHand hand, double amount);
    void bookDoubleDownBet(Player player, PlayerPosition position, PlayerHand hand);
    void splitHand(Player player, PlayerPosition position, PlayerHand hand, ArrayList<PlayerHand> activeHands);
}
