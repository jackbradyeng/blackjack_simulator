package position_services;

import Model.Actors.Dealer;
import Model.Actors.Player;
import Model.Table.Positions.DealerPosition;
import Model.Table.Positions.PlayerPosition;
import Model.Table.PositionService.PositionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static Model.Constants.DEFAULT_TABLE_POSITIONS;
import static org.junit.jupiter.api.Assertions.*;

public class PositionServiceTests {

    private PositionServiceImpl positionService;
    private ArrayList<PlayerPosition> playerPositions;

    @BeforeEach
    public void setUp() {
        positionService = new PositionServiceImpl();
        playerPositions = new ArrayList<>();
    }

    // ================================
    // --- createPlayerPositions ---
    // ================================

    @Test
    public void createPlayerPositions_createsExactlyDefaultTablePositions() {
        positionService.createPlayerPositions(playerPositions);
        assertEquals(DEFAULT_TABLE_POSITIONS, playerPositions.size());
    }

    @Test
    public void createPlayerPositions_positionsAreNumberedOneToDefaultTablePositions() {
        positionService.createPlayerPositions(playerPositions);
        for (int i = 0; i < DEFAULT_TABLE_POSITIONS; i++) {
            assertEquals(i + 1, playerPositions.get(i).getPositionNumber());
        }
    }

    @Test
    public void createPlayerPositions_allPositionsStartWithNoDefaultPlayer() {
        positionService.createPlayerPositions(playerPositions);
        for (PlayerPosition p : playerPositions) {
            assertNull(p.getDefaultPlayer());
        }
    }

    @Test
    public void createPlayerPositions_allPositionsStartWithEmptyHandsList() {
        positionService.createPlayerPositions(playerPositions);
        for (PlayerPosition p : playerPositions) {
            assertTrue(p.getHands().isEmpty());
        }
    }

    @Test
    public void createPlayerPositions_appendsToExistingListContents() {
        PlayerPosition existing = new PlayerPosition(99);
        playerPositions.add(existing);
        positionService.createPlayerPositions(playerPositions);
        assertEquals(DEFAULT_TABLE_POSITIONS + 1, playerPositions.size());
        assertSame(existing, playerPositions.get(0));
    }

    @Test
    public void createPlayerPositions_calledTwiceDoublesFill() {
        positionService.createPlayerPositions(playerPositions);
        positionService.createPlayerPositions(playerPositions);
        assertEquals(DEFAULT_TABLE_POSITIONS * 2, playerPositions.size());
    }

    // ================================
    // --- assignDefaultPlayerPositions: single player ---
    // ================================

    @Test
    public void assignDefaultPlayerPositions_singlePlayer_isAssignedToMiddleIndex() {
        positionService.createPlayerPositions(playerPositions);
        Player player = new Player(500, null);
        positionService.assignDefaultPlayerPositions(List.of(player), playerPositions);
        int expectedIndex = DEFAULT_TABLE_POSITIONS / 2 + 1;
        assertSame(playerPositions.get(expectedIndex), player.getDefaultPosition());
    }

    @Test
    public void assignDefaultPlayerPositions_singlePlayer_positionStoresPlayerReference() {
        positionService.createPlayerPositions(playerPositions);
        Player player = new Player(500, null);
        positionService.assignDefaultPlayerPositions(List.of(player), playerPositions);
        int expectedIndex = DEFAULT_TABLE_POSITIONS / 2 + 1;
        assertSame(player, playerPositions.get(expectedIndex).getDefaultPlayer());
    }

    @Test
    public void assignDefaultPlayerPositions_singlePlayer_bidirectionalReferenceIsConsistent() {
        positionService.createPlayerPositions(playerPositions);
        Player player = new Player(500, null);
        positionService.assignDefaultPlayerPositions(List.of(player), playerPositions);
        assertSame(player, player.getDefaultPosition().getDefaultPlayer());
    }

    @Test
    public void assignDefaultPlayerPositions_singlePlayer_otherPositionsHaveNoDefaultPlayer() {
        positionService.createPlayerPositions(playerPositions);
        Player player = new Player(500, null);
        positionService.assignDefaultPlayerPositions(List.of(player), playerPositions);
        int assignedIndex = DEFAULT_TABLE_POSITIONS / 2 + 1;
        for (int i = 0; i < playerPositions.size(); i++) {
            if (i != assignedIndex) {
                assertNull(playerPositions.get(i).getDefaultPlayer());
            }
        }
    }

    // ================================
    // --- assignDefaultPlayerPositions: multiplayer ---
    // ================================

    @Test
    public void assignDefaultPlayerPositions_twoPlayers_firstPlayerAssignedToIndexZero() {
        positionService.createPlayerPositions(playerPositions);
        Player player1 = new Player(500, null);
        Player player2 = new Player(500, null);
        positionService.assignDefaultPlayerPositions(List.of(player1, player2), playerPositions);
        assertSame(playerPositions.get(0), player1.getDefaultPosition());
    }

    @Test
    public void assignDefaultPlayerPositions_twoPlayers_secondPlayerAssignedToIndexOne() {
        positionService.createPlayerPositions(playerPositions);
        Player player1 = new Player(500, null);
        Player player2 = new Player(500, null);
        positionService.assignDefaultPlayerPositions(List.of(player1, player2), playerPositions);
        assertSame(playerPositions.get(1), player2.getDefaultPosition());
    }

    @Test
    public void assignDefaultPlayerPositions_twoPlayers_bidirectionalReferencesSetOnBothPositions() {
        positionService.createPlayerPositions(playerPositions);
        Player player1 = new Player(500, null);
        Player player2 = new Player(500, null);
        positionService.assignDefaultPlayerPositions(List.of(player1, player2), playerPositions);
        assertSame(player1, playerPositions.get(0).getDefaultPlayer());
        assertSame(player2, playerPositions.get(1).getDefaultPlayer());
    }

    @Test
    public void assignDefaultPlayerPositions_maxPlayers_allAssignedLeftToRight() {
        positionService.createPlayerPositions(playerPositions);
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < DEFAULT_TABLE_POSITIONS; i++) {
            players.add(new Player(500, null));
        }
        positionService.assignDefaultPlayerPositions(players, playerPositions);
        for (int i = 0; i < DEFAULT_TABLE_POSITIONS; i++) {
            assertSame(playerPositions.get(i), players.get(i).getDefaultPosition());
            assertSame(players.get(i), playerPositions.get(i).getDefaultPlayer());
        }
    }

    @Test
    public void assignDefaultPlayerPositions_twoPlayers_unoccupiedPositionsHaveNoDefaultPlayer() {
        positionService.createPlayerPositions(playerPositions);
        Player player1 = new Player(500, null);
        Player player2 = new Player(500, null);
        positionService.assignDefaultPlayerPositions(List.of(player1, player2), playerPositions);
        for (int i = 2; i < playerPositions.size(); i++) {
            assertNull(playerPositions.get(i).getDefaultPlayer());
        }
    }

    @Test
    public void assignDefaultPlayerPositions_twoPlayers_middlePositionNotUsed() {
        positionService.createPlayerPositions(playerPositions);
        Player player1 = new Player(500, null);
        Player player2 = new Player(500, null);
        positionService.assignDefaultPlayerPositions(List.of(player1, player2), playerPositions);
        int middleIndex = DEFAULT_TABLE_POSITIONS / 2 + 1;
        assertNull(playerPositions.get(middleIndex).getDefaultPlayer());
    }

    // ================================
    // --- assignDealerPosition ---
    // ================================

    @Test
    public void assignDealerPosition_setsPositionOnDealer() {
        Dealer dealer = new Dealer(null, 15000);
        DealerPosition dealerPosition = new DealerPosition();
        positionService.assignDealerPosition(dealer, dealerPosition);
        assertSame(dealerPosition, dealer.getPosition());
    }

    @Test
    public void assignDealerPosition_dealerPositionIsNotNull() {
        Dealer dealer = new Dealer(null, 15000);
        DealerPosition dealerPosition = new DealerPosition();
        positionService.assignDealerPosition(dealer, dealerPosition);
        assertNotNull(dealer.getPosition());
    }

    @Test
    public void assignDealerPosition_dealerStartsWithNoPosition() {
        Dealer dealer = new Dealer(null, 15000);
        assertNull(dealer.getPosition());
    }

    @Test
    public void assignDealerPosition_updatesPositionWhenCalledAgainWithDifferentPosition() {
        Dealer dealer = new Dealer(null, 15000);
        DealerPosition first = new DealerPosition();
        DealerPosition second = new DealerPosition();
        positionService.assignDealerPosition(dealer, first);
        positionService.assignDealerPosition(dealer, second);
        assertSame(second, dealer.getPosition());
    }

    @Test
    public void assignDealerPosition_doesNotModifyDealerHand() {
        Dealer dealer = new Dealer(null, 15000);
        DealerPosition dealerPosition = new DealerPosition();
        var hand = dealerPosition.getHand();
        positionService.assignDealerPosition(dealer, dealerPosition);
        assertSame(hand, dealer.getPosition().getHand());
    }
}