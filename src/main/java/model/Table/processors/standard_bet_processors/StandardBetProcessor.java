package model.Table.processors.standard_bet_processors;

import model.actors.Player;
import model.Table.positions.PlayerPosition;

public interface StandardBetProcessor {

    void process(Player player, PlayerPosition playerPosition, double amount);
}
