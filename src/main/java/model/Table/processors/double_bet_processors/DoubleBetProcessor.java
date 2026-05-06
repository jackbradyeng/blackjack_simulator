package model.Table.processors.double_bet_processors;

import model.actors.Player;
import model.Table.hands.PlayerHand;
import model.Table.positions.PlayerPosition;

public interface DoubleBetProcessor {

    void process(Player player, PlayerPosition playerPosition, PlayerHand playerHand);
}
