package processors;

import Model.Actors.Player;
import Model.Cards.Card;
import Model.Table.Bets.Bet;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;
import Model.Table.Processors.SplitBetProcessors.SplitBetProcessorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class SplitBetProcessorTests {

    private SplitBetProcessorImpl processor;
    private Player player;
    private PlayerPosition position;
    private PlayerHand hand;
    private ArrayList<PlayerHand> activeHands;

    @BeforeEach
    public void setUp() {
        processor = new SplitBetProcessorImpl();
        player = new Player(500, null);
        position = new PlayerPosition(1, player);
        hand = new PlayerHand(position);
        hand.setActingPlayer(player);
        hand.getPairs().add(Map.entry(player, new Bet(50)));
        hand.getCards().add(new Card("Eight", 8));
        hand.getCards().add(new Card("Eight", 8));
        activeHands = new ArrayList<>();
        activeHands.add(hand);
    }

    @Test
    public void process_addsSplitHandToActiveHands() {
        processor.process(player, position, hand, activeHands);
        assertEquals(2, activeHands.size());
    }

    @Test
    public void process_insertsSplitHandDirectlyAfterOriginal() {
        processor.process(player, position, hand, activeHands);
        assertSame(hand, activeHands.get(0));
        assertNotSame(hand, activeHands.get(1));
    }

    @Test
    public void process_originalHandRetainsOneCard() {
        processor.process(player, position, hand, activeHands);
        assertEquals(1, activeHands.get(0).getCards().size());
    }

    @Test
    public void process_splitHandReceivesOneCard() {
        processor.process(player, position, hand, activeHands);
        assertEquals(1, activeHands.get(1).getCards().size());
    }

    @Test
    public void process_deductsOriginalBetAmountFromChips() {
        processor.process(player, position, hand, activeHands);
        assertEquals(450, player.getChips());
    }

    @Test
    public void process_splitHandHasBetForPlayer() {
        processor.process(player, position, hand, activeHands);
        boolean hasBet = activeHands.get(1).getPairs().stream()
                .anyMatch(p -> p.getKey().equals(player));
        assertTrue(hasBet);
    }

    @Test
    public void process_resetsHasHitOnOriginalHand() {
        hand.setHasHit(true);
        processor.process(player, position, hand, activeHands);
        assertFalse(activeHands.get(0).isHasHit());
    }

    @Test
    public void process_insertsSplitHandAfterOriginalWhenOtherHandsFollow() {
        PlayerHand thirdHand = new PlayerHand(position);
        activeHands.add(thirdHand);
        processor.process(player, position, hand, activeHands);
        // order should be: hand, splitHand, thirdHand
        assertSame(hand, activeHands.get(0));
        assertSame(thirdHand, activeHands.get(2));
        assertEquals(3, activeHands.size());
    }
}