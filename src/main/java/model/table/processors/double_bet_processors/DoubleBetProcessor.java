package model.table.processors.double_bet_processors;

import model.actors.Player;
import model.table.hands.PlayerHand;
import model.table.positions.PlayerPosition;

public interface DoubleBetProcessor {

    void process(Player player, PlayerPosition playerPosition, PlayerHand playerHand);
}
