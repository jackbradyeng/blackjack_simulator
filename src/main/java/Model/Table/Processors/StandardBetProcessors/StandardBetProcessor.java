package Model.Table.Processors.StandardBetProcessors;

import Model.Actors.Player;
import Model.Table.Positions.PlayerPosition;

public interface StandardBetProcessor {

    void process(Player player, PlayerPosition playerPosition, double amount);
}
