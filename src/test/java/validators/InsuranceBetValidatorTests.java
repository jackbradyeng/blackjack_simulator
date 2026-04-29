package validators;

import Model.Actors.Player;
import Model.Strategies.player_strategies.OptimalNoCountingStrategy;
import Model.Table.Bets.Bet;
import Model.Table.Bets.InsuranceBet;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;
import Model.Table.Validators.InsuranceBetValidators.InsuranceBetValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class InsuranceBetValidatorTests {

    private InsuranceBetValidatorImpl validator;
    private Player player;
    private PlayerPosition position;
    private PlayerHand hand;

    @BeforeEach
    public void setUp() {
        validator = new InsuranceBetValidatorImpl();
        player = new Player(500, null);
        position = new PlayerPosition(1, player);
        hand = new PlayerHand(position);
        hand.getPairs().add(Map.entry(player, new Bet(100)));
    }

    @Test
    public void isValid_returnsTrue_whenBetExactlyHalfOriginal() {
        assertTrue(validator.isValid(player, hand, 50));
    }

    @Test
    public void isValid_returnsTrue_whenBetBelowHalfOriginal() {
        assertTrue(validator.isValid(player, hand, 25));
    }

    @Test
    public void isValid_returnsFalse_whenBetExceedsHalfOriginal() {
        assertFalse(validator.isValid(player, hand, 51));
    }

    @Test
    public void isValid_returnsFalse_whenNoExistingBet() {
        PlayerHand emptyHand = new PlayerHand(position);
        assertFalse(validator.isValid(player, emptyHand, 25));
    }

    @Test
    public void isValid_returnsFalse_whenInsuranceBetAlreadyPlaced() {
        hand.getPairs().add(Map.entry(player, new InsuranceBet(50)));
        assertFalse(validator.isValid(player, hand, 25));
    }

    @Test
    public void isValid_returnsFalse_whenDifferentPlayerHasNoBetOnHand() {
        Player backbetPlayer = new Player(500, new OptimalNoCountingStrategy());
        assertFalse(validator.isValid(backbetPlayer, hand, 25));
    }
}