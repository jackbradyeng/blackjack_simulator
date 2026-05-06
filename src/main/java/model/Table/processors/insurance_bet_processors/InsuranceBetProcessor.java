package model.Table.processors.insurance_bet_processors;

import model.actors.Player;
import model.Table.positions.PlayerPosition;

public interface InsuranceBetProcessor {

    void process(Player player, PlayerPosition playerPosition, double amount);
}
