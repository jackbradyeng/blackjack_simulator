import java.util.Map;
import Exceptions.DeckCountException;
import Exceptions.PlayerCountException;
import Model.Actors.Player;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.PlayerPosition;
import Model.Table.Table;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static Model.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TableTesting {

    // testing instance variables
    private Table table;
    private final int PLAYER_COUNT = 3;

    public TableTesting() {
        table = new Table(PLAYER_COUNT, DEFAULT_NUMBER_OF_DECKS, false);
    }

    // private helper method
    private Map.Entry<Player, PlayerHand> betOnDefaultPosition() {
        table.startupRoutine();
        Player singlePlayer = table.getPlayers().getFirst();
        table.getBettingService().bookStandardBet(singlePlayer, singlePlayer.getDefaultPosition(), 100);
        PlayerHand hand = table.getPlayers().getFirst().getDefaultPosition().getHands().getFirst();
        return Map.entry(singlePlayer, hand);
    }

    /** tests that a playerCountException is thrown when a table is assigned too many players. */
    @Order(1)
    @Test
    public void testPlayerCountException() {
        PlayerCountException thrown = assertThrows(PlayerCountException.class, () ->
                new Table(DEFAULT_TABLE_POSITIONS + 1, DEFAULT_NUMBER_OF_DECKS, false));
    }

    @Order(2)
    @Test
    public void testDeckCountException() {
        DeckCountException thrown = assertThrows(DeckCountException.class, () ->
                new Table(PLAYER_COUNT, 0, false));
    }

    /** tests that the player array size returns as expected in a single-player game. */
    @Order(3)
    @Test
    public void testSinglePlayerCount() {assertEquals(PLAYER_COUNT, table.getPlayers().size());}

    /** tests that the default number of table positions are instantiated and stored in the iterable list. **/
    @Order(4)
    @Test
    public void testPlayerPositions() {assertEquals(DEFAULT_TABLE_POSITIONS,
            table.getPlayerPositionsIterable().size());}

    /** tests that in a multi-player game, each player is assigned to their respective position. */
    @Order(5)
    @Test
    public void testPlayerDefaultPositions() {
        for(int i = 0; i < table.getPlayers().size(); i++) {
            assertEquals(table.getPlayers().get(i).getDefaultPosition(), table.getPlayerPositionsIterable().get(i));
        }
    }

    /** tests that each position has a single (empty) hand at the beginning of the game. */
    @Order(6)
    @Test
    public void testPositionHandCount() {
        table.startupRoutine();
        for(PlayerPosition position : table.getPlayerPositionsIterable()) {
            assertEquals(1, position.getHands().size());
        }
    }

    /** tests that each player has the default number of starting chips at the beginning of the game. */
    @Order(7)
    @Test
    public void testPlayerStartingChips() {
        for(Player player : table.getPlayers()) {
            assertEquals(DEFAULT_PLAYER_STARTING_CHIPS, player.getChips());
        }
    }

    /** tests that a player's standard bet is correctly processed on a given position. */
    @Order(8)
    @Test
    public void testPlayerStandardBet() {
        Player player = table.getPlayers().getFirst();
        table.startupRoutine();
        table.getBettingService().bookStandardBet(player, player.getDefaultPosition(), 100);

        // a standard bet should be allocated to the first hand at a given position
        PlayerHand hand = player.getDefaultPosition().getHands().getFirst();

        // the key in the set of pairs should be the player object while the value should be the corresponding bet
        assertTrue(hand.getPairs().getFirst().getKey().equals(player)
                && hand.getPairs().getFirst().getValue().getAmount() == 100);
    }

    /** tests that the table's active hands list contains the correct number and instances of hands. */
    @Order(9)
    @Test
    public void testActiveHandCount() {
        Player singlePlayer = table.getPlayers().getFirst();
        table.startupRoutine();
        table.getBettingService().bookStandardBet(singlePlayer, singlePlayer.getDefaultPosition(), 100);
        table.getHandService().setActiveHands(table.getPlayerPositionsIterable());

        assertTrue(table.getActiveHands().size() == 1 &&
                table.getActiveHands().getFirst().equals(singlePlayer.getDefaultPosition().getHands().getFirst()));
    }

    /** tests that the dealer's position is created. */
    @Order(10)
    @Test
    public void testDealerPositionNotNull() {
        assertNotNull(table.getDealer().getPosition());
    }

    /** tests that the dealer's hand is created. */
    @Order(11)
    @Test
    public void testDealerHasHand() {
        table.startupRoutine();
        assertNotNull(table.getDealerPosition().getHand());
    }

    /** tests that a given player remains the acting player after betting on their default position. */
    @Order(12)
    @Test
    public void testActivePlayerDefault() {
        Player player = betOnDefaultPosition().getKey();
        PlayerHand hand = betOnDefaultPosition().getValue();
        table.getHandService().setActingPlayers(table.getPlayerPositionsIterable());
        assertEquals(player, hand.getActingPlayer());
    }

    /** tests that a given player becomes the acting player on a non-default position when the default player
     * does not place a bet. */
    @Order(13)
    @Test
    public void testActivePlayerNonDefault() {
        table.startupRoutine();
        Player singlePlayer = table.getPlayers().getFirst();
        table.getBettingService().bookStandardBet(singlePlayer, table.getPlayerPositionsIterable().get(1), 100);
        PlayerHand hand = table.getPlayerPositionsIterable().get(1).getHands().getFirst();
        table.getHandService().setActingPlayers(table.getPlayerPositionsIterable());
        assertEquals(singlePlayer, hand.getActingPlayer());
    }

    /** tests that only two cards are dealt to an active position after the draw routine. */
    @Order(14)
    @Test
    public void testHandSize() {
        PlayerHand hand = betOnDefaultPosition().getValue();
        table.drawRoutine();
        assertEquals(2, hand.getCards().size());
    }

    /** tests that the hand value for each active position at the start of the betting round is less than or equal to
     * the blackjack constant. */
    @Order(15)
    @Test
    public void testHandValueLessThanBlackJack() {
        betOnDefaultPosition();
        table.drawRoutine();
        for(PlayerHand hand : table.getActiveHands()) {
            assertTrue(hand.getHandValue() <= BLACKJACK_CONSTANT);
        }
    }

    /** tests that the number of cards after hitting on a fresh hand is equal to three. */
    @Order(16)
    @Test
    public void testHandCountAfterHit() {
        PlayerHand hand = betOnDefaultPosition().getValue();
        table.drawRoutine();
        table.getActionService().hit(table.getDeck(), hand);
        assertEquals(3, hand.getCards().size());
    }

    /** tests that after a hand is bust, it cannot receive additional cards from the deck. */
    @Order(17)
    @Test
    public void testHitAfterBust() {
        PlayerHand hand = betOnDefaultPosition().getValue();
        table.drawRoutine();
        while(!hand.isBust()) {
            table.getActionService().hit(table.getDeck(), hand);
        }
        int size = hand.getCards().size();
        table.getActionService().hit(table.getDeck(), hand);
        assertEquals(size, hand.getCards().size());
    }
}
