package model.table.betting_services;

import java.util.ArrayList;
import model.actors.Player;
import model.table.hands.PlayerHand;
import model.table.positions.PlayerPosition;

public interface BettingService {
    boolean bookStandardBet(Player player, PlayerPosition position, double amount);
    boolean bookInsuranceBet(Player player, PlayerPosition position, PlayerHand hand, double amount);
    boolean bookDoubleDownBet(Player player, PlayerPosition position, PlayerHand hand);
    boolean splitHand(Player player, PlayerPosition position, PlayerHand hand, ArrayList<PlayerHand> activeHands);
}
