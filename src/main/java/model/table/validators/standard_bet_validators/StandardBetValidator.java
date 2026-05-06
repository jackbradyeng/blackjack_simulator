package model.table.validators.standard_bet_validators;

import model.actors.Player;
import model.table.positions.PlayerPosition;
import java.util.ArrayList;

public interface StandardBetValidator {

    boolean isValid(Player player,
                    ArrayList<Player> players,
                    PlayerPosition playerPosition,
                    ArrayList<PlayerPosition> playerPositions,
                    double amount,
                    boolean isSimulation);
}
