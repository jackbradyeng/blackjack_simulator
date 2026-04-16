package Model.Table.Processors.DoubleBetProcessors;

import Model.Actors.Player;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;

public interface DoubleBetProcessor {

    void process(Player player, PlayerPosition playerPosition, PlayerHand playerHand);
}
