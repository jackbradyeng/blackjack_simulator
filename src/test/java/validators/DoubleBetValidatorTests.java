package validators;

import Model.Actors.Player;
import Model.Cards.Card;
import Model.Strategies.player_strategies.OptimalNoCountingStrategy;
import Model.Table.Bets.Bet;
import Model.Table.Bets.DoubleBet;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;
import Model.Table.Validators.DoubleBetValidators.DoubleBetValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class DoubleBetValidatorTests {

    private DoubleBetValidatorImpl validator;
    private Player player;
    private PlayerPosition position;
    private ArrayList<Player> players;
    private ArrayList<PlayerPosition> positions;
    private PlayerHand hand;

    @BeforeEach
    public void setUp() {
        validator = new DoubleBetValidatorImpl();
        player = new Player(500, null);
        position = new PlayerPosition(1, player);
        hand = new PlayerHand(position);
        players = new ArrayList<>();
        players.add(player);
        positions = new ArrayList<>();
        positions.add(position);
        hand.getPairs().add(Map.entry(player, new Bet(50)));
        hand.getCards().add(new Card("Eight", 8));
        hand.getCards().add(new Card("Nine", 9));
    }

    @Test
    public void isValid_returnsTrue_whenHandHasNotBeenHit() {
        assertTrue(validator.isValid(player, players, position, positions, hand, false));
    }

    @Test
    public void isValid_returnsFalse_whenHandHasBeenHit() {
        hand.setHasHit(true);
        assertFalse(validator.isValid(player, players, position, positions, hand, false));
    }

    @Test
    public void isValid_returnsFalse_whenPlayerAlreadyDoubled() {
        hand.getPairs().add(Map.entry(player, new DoubleBet(50)));
        assertFalse(validator.isValid(player, players, position, positions, hand, false));
    }

    @Test
    public void isValid_returnsFalse_whenNoExistingBet() {
        PlayerHand noBetHand = new PlayerHand(position);
        noBetHand.getCards().add(new Card("Eight", 8));
        noBetHand.getCards().add(new Card("Nine", 9));
        assertFalse(validator.isValid(player, players, position, positions, noBetHand, false));
    }

    @Test
    public void isValid_returnsFalse_whenInsufficientChipsNonSimulation() {
        Player brokePlayer = new Player(10, null);
        ArrayList<Player> brokeList = new ArrayList<>();
        brokeList.add(brokePlayer);
        PlayerHand brokeHand = new PlayerHand(position);
        brokeHand.getPairs().add(Map.entry(brokePlayer, new Bet(50)));
        brokeHand.getCards().add(new Card("Eight", 8));
        brokeHand.getCards().add(new Card("Nine", 9));
        assertFalse(validator.isValid(brokePlayer, brokeList, position, positions, brokeHand, false));
    }

    @Test
    public void isValid_returnsTrue_whenSimulationAndInsufficientChips() {
        Player brokePlayer = new Player(10, null);
        ArrayList<Player> brokeList = new ArrayList<>();
        brokeList.add(brokePlayer);
        PlayerHand brokeHand = new PlayerHand(position);
        brokeHand.getPairs().add(Map.entry(brokePlayer, new Bet(50)));
        brokeHand.getCards().add(new Card("Eight", 8));
        brokeHand.getCards().add(new Card("Nine", 9));
        assertTrue(validator.isValid(brokePlayer, brokeList, position, positions, brokeHand, true));
    }

    @Test
    public void isValid_returnsFalse_whenPlayerNotRegistered() {
        Player unregisteredPlayer = new Player(500, new OptimalNoCountingStrategy());
        assertFalse(validator.isValid(unregisteredPlayer, players, position, positions, hand, false));
    }
}