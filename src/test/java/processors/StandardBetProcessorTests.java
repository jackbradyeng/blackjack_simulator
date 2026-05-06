package processors;

import model.actors.Player;
import model.Table.hands.PlayerHand;
import model.Table.positions.PlayerPosition;
import model.Table.processors.standard_bet_processors.StandardBetProcessorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StandardBetProcessorTests {

    private StandardBetProcessorImpl processor;
    private Player player;
    private PlayerPosition position;

    @BeforeEach
    public void setUp() {
        processor = new StandardBetProcessorImpl();
        player = new Player(500, null);
        position = new PlayerPosition(1, player);
        position.getHands().add(new PlayerHand(position));
    }

    @Test
    public void process_assignsBetToHand() {
        processor.process(player, position, 50);
        assertFalse(position.getHands().getFirst().getPairs().isEmpty());
    }

    @Test
    public void process_storesBetWithCorrectAmount() {
        processor.process(player, position, 50);
        double betAmount = position.getHands().getFirst().getPairs().getFirst().getValue().getAmount();
        assertEquals(50, betAmount);
    }

    @Test
    public void process_deductsChipsFromPlayer() {
        processor.process(player, position, 50);
        assertEquals(450, player.getChips());
    }

    @Test
    public void process_storesBetMappedToCorrectPlayer() {
        processor.process(player, position, 50);
        Player bettingPlayer = position.getHands().getFirst().getPairs().getFirst().getKey();
        assertEquals(player, bettingPlayer);
    }

    @Test
    public void process_deductsExactBetAmount_whenBetEqualsEntireStack() {
        Player allInPlayer = new Player(50, null);
        processor.process(allInPlayer, position, 50);
        assertEquals(0, allInPlayer.getChips());
    }
}
