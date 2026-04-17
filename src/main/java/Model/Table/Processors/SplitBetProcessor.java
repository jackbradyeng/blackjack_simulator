package Model.Table.Processors;

import Model.Actors.Player;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;
import java.util.ArrayList;

public interface SplitBetProcessor {

    void process(Player player,
                        PlayerPosition playerPosition,
                        PlayerHand playerHand,
                        ArrayList<PlayerHand> activeHands);
}
