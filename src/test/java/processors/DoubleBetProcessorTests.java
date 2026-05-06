package processors;

import model.actors.Player;
import model.Table.bets.Bet;
import model.Table.bets.DoubleBet;
import model.Table.hands.PlayerHand;
import model.Table.positions.PlayerPosition;
import model.Table.processors.double_bet_processors.DoubleBetProcessorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class DoubleBetProcessorTests {

    private DoubleBetProcessorImpl processor;
    private Player player;
    private PlayerPosition position;
    private PlayerHand hand;

    @BeforeEach
    public void setUp() {
        processor = new DoubleBetProcessorImpl();
        player = new Player(500, null);
        position = new PlayerPosition(1, player);
        hand = new PlayerHand(position);
        position.getHands().add(hand);
        hand.getPairs().add(Map.entry(player, new Bet(50)));
    }

    @Test
    public void process_addsDoubleBetToHand() {
        processor.process(player, position, hand);
        long doubleBetCount = hand.getPairs().stream()
                .filter(p -> p.getValue() instanceof DoubleBet)
                .count();
        assertEquals(1, doubleBetCount);
    }

    @Test
    public void process_doubleBetAmountMatchesOriginalBet() {
        processor.process(player, position, hand);
        double doubleBetAmount = hand.getPairs().stream()
                .filter(p -> p.getValue() instanceof DoubleBet)
                .findFirst()
                .orElseThrow()
                .getValue()
                .getAmount();
        assertEquals(50, doubleBetAmount);
    }

    @Test
    public void process_deductsOriginalBetAmountFromChips() {
        processor.process(player, position, hand);
        assertEquals(450, player.getChips());
    }

    @Test
    public void process_handRetainsBothOriginalAndDoubleBet() {
        processor.process(player, position, hand);
        assertEquals(2, hand.getPairs().size());
    }
}