package processors;

import model.actors.Player;
import model.table.bets.InsuranceBet;
import model.table.hands.PlayerHand;
import model.table.positions.PlayerPosition;
import model.table.processors.insurance_bet_processors.InsuranceBetProcessorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InsuranceBetProcessorTests {

    private InsuranceBetProcessorImpl processor;
    private Player player;
    private PlayerPosition position;

    @BeforeEach
    public void setUp() {
        processor = new InsuranceBetProcessorImpl();
        player = new Player(500, null);
        position = new PlayerPosition(1, player);
        position.getHands().add(new PlayerHand(position));
    }

    @Test
    public void process_addsInsuranceBetToHand() {
        processor.process(player, position, 25);
        long count = position.getHands().getFirst().getPairs().stream()
                .filter(pair -> pair.getValue() instanceof InsuranceBet)
                .count();
        assertEquals(1, count);
    }

    @Test
    public void process_storesCorrectInsuranceAmount() {
        processor.process(player, position, 25);
        double amount = position.getHands().getFirst().getPairs().getFirst().getValue().getAmount();
        assertEquals(25, amount);
    }

    @Test
    public void process_deductsChipsFromPlayer() {
        processor.process(player, position, 25);
        assertEquals(475, player.getChips());
    }

    @Test
    public void process_storesBetMappedToCorrectPlayer() {
        processor.process(player, position, 25);
        Player bettingPlayer = position.getHands().getFirst().getPairs().getFirst().getKey();
        assertEquals(player, bettingPlayer);
    }
}
