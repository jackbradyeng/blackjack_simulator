package actor_strategies;

import Model.Actors.Player;
import Model.Cards.Card;
import Model.Strategies.player_strategies.CopyDealerStrategy;
import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static Model.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class CopyDealerStrategyTests {

    private CopyDealerStrategy strategy;
    private PlayerPosition position;
    private DealerHand dealerHand;

    @BeforeEach
    public void setUp() {
        strategy = new CopyDealerStrategy();
        position = new PlayerPosition(1, new Player(500, null));
        dealerHand = new DealerHand();
        dealerHand.receiveCard(new Card("Six", 6));
        dealerHand.setHandValue();
    }

    private PlayerHand makePlayerHand(Card... cards) {
        PlayerHand hand = new PlayerHand(position);
        for (Card c : cards) hand.receiveCard(c);
        hand.setHandValue();
        return hand;
    }

    @Test
    public void handValueOfZero_returnsHit() {
        PlayerHand hand = new PlayerHand(position);
        assertEquals(HIT, strategy.executeStrategy(hand, dealerHand));
    }

    @Test
    public void handValueOf16_returnsHit() {
        PlayerHand hand = makePlayerHand(new Card("Seven", 7), new Card("Nine", 9));
        assertEquals(HIT, strategy.executeStrategy(hand, dealerHand));
    }

    @Test
    public void handValueBelow17_returnsHit() {
        PlayerHand hand = makePlayerHand(new Card("Five", 5), new Card("Nine", 9));
        assertEquals(HIT, strategy.executeStrategy(hand, dealerHand));
    }

    @Test
    public void hardHandValueOf17_returnsStand() {
        PlayerHand hand = makePlayerHand(new Card("Eight", 8), new Card("Nine", 9));
        assertEquals(STAND, strategy.executeStrategy(hand, dealerHand));
    }

    @Test
    public void handValueOf18_returnsStand() {
        PlayerHand hand = makePlayerHand(new Card("Nine", 9), new Card("Nine", 9));
        assertEquals(STAND, strategy.executeStrategy(hand, dealerHand));
    }

    @Test
    public void handValueOf21_returnsStand() {
        PlayerHand hand = makePlayerHand(new Card("King", 10), new Card("Eight", 8), new Card("Three", 3));
        assertEquals(STAND, strategy.executeStrategy(hand, dealerHand));
    }

    @Test
    public void bustHand_returnsStand() {
        PlayerHand hand = makePlayerHand(new Card("Eight", 8), new Card("Eight", 8), new Card("Eight", 8));
        assertTrue(hand.isBust());
        assertEquals(STAND, strategy.executeStrategy(hand, dealerHand));
    }

    @Test
    public void dealerHandIsIgnored_sameResultRegardlessOfDealerCard() {
        PlayerHand hand = makePlayerHand(new Card("Seven", 7), new Card("Nine", 9));
        DealerHand lowDealer = new DealerHand();
        lowDealer.receiveCard(new Card("Two", 2));
        lowDealer.setHandValue();
        DealerHand highDealer = new DealerHand();
        highDealer.receiveCard(new Card("King", 10));
        highDealer.setHandValue();
        assertEquals(strategy.executeStrategy(hand, lowDealer), strategy.executeStrategy(hand, highDealer));
    }
}