package validators;

import Model.Actors.Player;
import Model.Table.Positions.PlayerPosition;
import Model.Table.Validators.StandardBetValidators.StandardBetValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static Model.Constants.DEFAULT_MIN_BET_SIZE;
import static org.junit.jupiter.api.Assertions.*;

public class StandardBetValidatorTests {

    private StandardBetValidatorImpl validator;
    private Player player;
    private PlayerPosition position;
    private ArrayList<Player> players;
    private ArrayList<PlayerPosition> positions;

    @BeforeEach
    public void setUp() {
        validator = new StandardBetValidatorImpl();
        player = new Player(500, null);
        position = new PlayerPosition(1, player);
        player.setDefaultPosition(position);  // makes this instance distinguishable by equals
        players = new ArrayList<>();
        players.add(player);
        positions = new ArrayList<>();
        positions.add(position);
    }

    @Test
    public void isValid_returnsTrue_whenAllConditionsMet() {
        assertTrue(validator.isValid(player, players, position, positions, DEFAULT_MIN_BET_SIZE, false));
    }

    @Test
    public void isValid_returnsTrue_whenBetExactlyAtMinimum() {
        assertTrue(validator.isValid(player, players, position, positions, DEFAULT_MIN_BET_SIZE, false));
    }

    @Test
    public void isValid_returnsFalse_whenBetBelowMinimum() {
        assertFalse(validator.isValid(player, players, position, positions, DEFAULT_MIN_BET_SIZE - 1, false));
    }

    @Test
    public void isValid_returnsFalse_whenInsufficientChips() {
        Player brokePlayer = new Player(10, null);
        ArrayList<Player> brokeList = new ArrayList<>();
        brokeList.add(brokePlayer);
        assertFalse(validator.isValid(brokePlayer, brokeList, position, positions, DEFAULT_MIN_BET_SIZE, false));
    }

    @Test
    public void isValid_returnsTrue_whenSimulationAllowsOverdraw() {
        Player brokePlayer = new Player(10, null);
        ArrayList<Player> brokeList = new ArrayList<>();
        brokeList.add(brokePlayer);
        assertTrue(validator.isValid(brokePlayer, brokeList, position, positions, DEFAULT_MIN_BET_SIZE, true));
    }

    @Test
    public void isValid_returnsFalse_whenPlayerNotRegistered() {
        Player unregisteredPlayer = new Player(500, null);
        assertFalse(validator.isValid(unregisteredPlayer, players, position, positions, DEFAULT_MIN_BET_SIZE, false));
    }

    @Test
    public void isValid_returnsFalse_whenPositionNotRegistered() {
        PlayerPosition unregisteredPosition = new PlayerPosition(2);
        assertFalse(validator.isValid(player, players, unregisteredPosition, positions, DEFAULT_MIN_BET_SIZE, false));
    }
}