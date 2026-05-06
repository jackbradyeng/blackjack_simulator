import java.util.ArrayList;
import java.util.Map;
import controller.Controller;
import exceptions.DeckCountException;
import exceptions.PlayerCountException;
import model.actors.Player;
import model.observers.TablePrinter;
import model.observers.TableStats;
import model.strategies.player_strategies.OptimalNoCountingStrategy;
import model.Table.hands.PlayerHand;
import model.Table.positions.PlayerPosition;
import model.Table.Table;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static model.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TableTesting {

    private Table table;
    private TableStats tableStats;
    private TablePrinter tablePrinter;
    private ArrayList<Player> players;
    private PlayerPosition defaultPlayerPosition;
    private final int PLAYER_COUNT = 3;

    public TableTesting() {
        this.tableStats = new TableStats();
        this.tablePrinter = new TablePrinter();
        this.players = createPlayers(PLAYER_COUNT);
        this.table = new Table(players, DEFAULT_NUMBER_OF_DECKS, false, tablePrinter, tableStats);
        this.defaultPlayerPosition = table.getPlayerPositions().get(DEFAULT_TABLE_POSITIONS / 2 + 1);
    }

    private ArrayList<Player> createPlayers(int count) {
        this.players = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            players.add(new Player(DEFAULT_PLAYER_STARTING_CHIPS, new OptimalNoCountingStrategy()));
        }
        return players;
    }

    // private helper method
    private Map.Entry<Player, PlayerHand> betOnDefaultPosition() {
        table.startupRoutine();
        Player singlePlayer = players.getFirst();
        singlePlayer.setDefaultPosition(defaultPlayerPosition);
        table.getBettingService().bookStandardBet(singlePlayer, singlePlayer.getDefaultPosition(), 100);
        PlayerHand hand = players.getFirst().getDefaultPosition().getHands().getFirst();
        return Map.entry(singlePlayer, hand);
    }

    /** tests that a playerCountException is thrown when a table is assigned too many players. */
    @Order(1)
    @Test
    public void testPlayerCountException() {
        assertThrows(PlayerCountException.class, () ->
                new Controller(DEFAULT_TABLE_POSITIONS + 1, DEFAULT_NUMBER_OF_DECKS, false));
    }

    @Order(2)
    @Test
    public void testDeckCountException() {
        assertThrows(DeckCountException.class, () ->
                new Table(createPlayers(PLAYER_COUNT), 0, false, tablePrinter, tableStats));
    }

    /** tests that the player array size returns as expected in a single-player game. */
    @Order(3)
    @Test
    public void testSinglePlayerCount() {assertEquals(PLAYER_COUNT, table.getPlayers().size());}

    /** tests that the default number of table positions are instantiated and stored in the iterable list. **/
    @Order(4)
    @Test
    public void testPlayerPositions() {assertEquals(DEFAULT_TABLE_POSITIONS,
            table.getPlayerPositions().size());}

    /** tests that in a multi-player game, each player is assigned to their respective position. */
    @Order(5)
    @Test
    public void testPlayerDefaultPositions() {
        for(int i = 0; i < table.getPlayers().size(); i++) {
            assertEquals(table.getPlayers().get(i).getDefaultPosition(), table.getPlayerPositions().get(i));
        }
    }

    /** tests that each position has a single (empty) hand at the beginning of the game. */
    @Order(6)
    @Test
    public void testPositionHandCount() {
        table.startupRoutine();
        for(PlayerPosition position : table.getPlayerPositions()) {
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
        Player singlePlayer = players.getFirst();
        singlePlayer.setDefaultPosition(table.getPlayerPositions().get(DEFAULT_TABLE_POSITIONS / 2 + 1));
        table.startupRoutine();
        table.getBettingService().bookStandardBet(singlePlayer, singlePlayer.getDefaultPosition(), 100);

        // a standard bet should be allocated to the first hand at a given position
        PlayerHand hand = singlePlayer.getDefaultPosition().getHands().getFirst();

        // the key in the set of pairs should be the player object while the value should be the corresponding bet
        assertTrue(hand.getPairs().getFirst().getKey().equals(singlePlayer)
                && hand.getPairs().getFirst().getValue().getAmount() == 100);
    }

    /** tests that the table's active hands list contains the correct number and instances of hands. */
    @Order(9)
    @Test
    public void testActiveHandCount() {
        Player singlePlayer = players.getFirst();
        table.startupRoutine();
        table.getBettingService().bookStandardBet(singlePlayer, singlePlayer.getDefaultPosition(), 100);
        table.drawRoutine();
        assertTrue(table.getActiveHands().size() == 1);
        assertTrue(table.getActiveHands().getFirst().equals(singlePlayer.getDefaultPosition().getHands().getFirst()));
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
        table.getHandService().setActingPlayers(table.getPlayerPositions());
        assertEquals(player, hand.getActingPlayer());
    }

    /** tests that a given player becomes the acting player on a non-default position when the default player
     * does not place a bet. */
    @Order(13)
    @Test
    public void testActivePlayerNonDefault() {
        table.startupRoutine();
        Player singlePlayer = players.getFirst();
        table.getBettingService().bookStandardBet(singlePlayer, table.getPlayerPositions().get(1), 100);
        PlayerHand hand = table.getPlayerPositions().get(1).getHands().getFirst();
        table.getHandService().setActingPlayers(table.getPlayerPositions());
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
        table.getDealService().hit(table.getDeck(), hand);
        assertEquals(3, hand.getCards().size());
    }

    /** tests that after a hand is bust, it cannot receive additional cards from the deck. */
    @Order(17)
    @Test
    public void testHitAfterBust() {
        PlayerHand hand = betOnDefaultPosition().getValue();
        table.drawRoutine();
        while(!hand.isBust()) {
            table.getDealService().hit(table.getDeck(), hand);
        }
        int size = hand.getCards().size();
        table.getDealService().hit(table.getDeck(), hand);
        assertEquals(size, hand.getCards().size());
    }
}
