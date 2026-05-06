package model.table.processors.standard_bet_processors;

import model.actors.Player;
import model.table.positions.PlayerPosition;

public interface StandardBetProcessor {

    void process(Player player, PlayerPosition playerPosition, double amount);
}
