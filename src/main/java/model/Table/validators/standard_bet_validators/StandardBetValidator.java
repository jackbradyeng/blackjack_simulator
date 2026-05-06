package model.Table.validators.standard_bet_validators;

import model.actors.Player;
import model.Table.positions.PlayerPosition;
import java.util.ArrayList;

public interface StandardBetValidator {

    boolean isValid(Player player,
                    ArrayList<Player> players,
                    PlayerPosition playerPosition,
                    ArrayList<PlayerPosition> playerPositions,
                    double amount,
                    boolean isSimulation);
}
