package model.Table.processors.split_bet_processors;

import model.actors.Player;
import model.Table.hands.PlayerHand;
import model.Table.positions.PlayerPosition;
import java.util.ArrayList;

public interface SplitBetProcessor {

    void process(Player player,
                        PlayerPosition playerPosition,
                        PlayerHand playerHand,
                        ArrayList<PlayerHand> activeHands);
}
