package model.table.validators.split_bet_validators;

import model.actors.Player;
import model.table.hands.PlayerHand;
import model.table.positions.PlayerPosition;
import java.util.ArrayList;

public interface SplitBetValidator {

    boolean isValid(Player player,
                    ArrayList<Player> players,
                    PlayerPosition playerPosition,
                    ArrayList<PlayerPosition> playerPositions,
                    PlayerHand playerHand,
                    boolean isSimulation);
}
