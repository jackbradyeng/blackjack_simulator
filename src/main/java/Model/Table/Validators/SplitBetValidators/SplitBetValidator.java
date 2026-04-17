package Model.Table.Validators.SplitBetValidators;

import Model.Actors.Player;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;
import java.util.ArrayList;

public interface SplitBetValidator {

    boolean isValid(Player player,
                    ArrayList<Player> players,
                    PlayerPosition playerPosition,
                    ArrayList<PlayerPosition> playerPositions,
                    PlayerHand playerHand,
                    boolean isSimulation);
}
