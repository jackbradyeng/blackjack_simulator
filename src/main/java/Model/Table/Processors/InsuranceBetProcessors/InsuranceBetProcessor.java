package Model.Table.Processors.InsuranceBetProcessors;

import Model.Actors.Player;
import Model.Table.Positions.PlayerPosition;

public interface InsuranceBetProcessor {

    void process(Player player, PlayerPosition playerPosition, double amount);
}
