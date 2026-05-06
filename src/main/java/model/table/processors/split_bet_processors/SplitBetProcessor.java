package model.table.processors.split_bet_processors;

import model.actors.Player;
import model.table.hands.PlayerHand;
import model.table.positions.PlayerPosition;
import java.util.ArrayList;

public interface SplitBetProcessor {

    void process(Player player,
                        PlayerPosition playerPosition,
                        PlayerHand playerHand,
                        ArrayList<PlayerHand> activeHands);
}
