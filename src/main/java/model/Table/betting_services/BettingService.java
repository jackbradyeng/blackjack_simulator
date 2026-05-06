package model.Table.betting_services;

import java.util.ArrayList;
import model.actors.Player;
import model.Table.hands.PlayerHand;
import model.Table.positions.PlayerPosition;

public interface BettingService {
    boolean bookStandardBet(Player player, PlayerPosition position, double amount);
    boolean bookInsuranceBet(Player player, PlayerPosition position, PlayerHand hand, double amount);
    boolean bookDoubleDownBet(Player player, PlayerPosition position, PlayerHand hand);
    boolean splitHand(Player player, PlayerPosition position, PlayerHand hand, ArrayList<PlayerHand> activeHands);
}
