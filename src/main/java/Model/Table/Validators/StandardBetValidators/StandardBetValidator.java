package Model.Table.Validators.StandardBetValidators;

import Model.Actors.Player;
import Model.Table.Positions.PlayerPosition;
import java.util.ArrayList;

public interface StandardBetValidator {

    boolean isValid(Player player,
                    ArrayList<Player> players,
                    PlayerPosition playerPosition,
                    ArrayList<PlayerPosition> playerPositions,
                    double amount,
                    boolean isSimulation);
}
