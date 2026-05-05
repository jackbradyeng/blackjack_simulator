package Model.Table.BettingServices;

import java.util.ArrayList;
import Model.Actors.Player;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;

public interface BettingService {
    boolean bookStandardBet(Player player, PlayerPosition position, double amount);
    boolean bookInsuranceBet(Player player, PlayerPosition position, PlayerHand hand, double amount);
    boolean bookDoubleDownBet(Player player, PlayerPosition position, PlayerHand hand);
    boolean splitHand(Player player, PlayerPosition position, PlayerHand hand, ArrayList<PlayerHand> activeHands);
}
