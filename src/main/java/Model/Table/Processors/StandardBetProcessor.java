package Model.Table.Processors;

import Model.Actors.Player;
import Model.Table.Positions.PlayerPosition;

public interface StandardBetProcessor {

    void process(Player player, PlayerPosition playerPosition, double amount);
}
