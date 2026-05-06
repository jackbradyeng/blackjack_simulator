package model.Table.validators.split_bet_validators;

import model.actors.Player;
import model.Table.hands.PlayerHand;
import model.Table.positions.PlayerPosition;
import java.util.ArrayList;

public interface SplitBetValidator {

    boolean isValid(Player player,
                    ArrayList<Player> players,
                    PlayerPosition playerPosition,
                    ArrayList<PlayerPosition> playerPositions,
                    PlayerHand playerHand,
                    boolean isSimulation);
}
