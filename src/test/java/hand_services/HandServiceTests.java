package hand_services;

import Model.Actors.Player;
import Model.Cards.Card;
import Model.Observers.TableStats;
import Model.Table.Bets.Bet;
import Model.Table.HandServices.HandServiceImpl;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.DealerPosition;
import Model.Table.Positions.PlayerPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class HandServiceTests {

    private HandServiceImpl handService;
    private TableStats tableStats;
    private Player defaultPlayer;
    private PlayerPosition position;

    @BeforeEach
    public void setUp() {
        tableStats = new TableStats();
        handService = new HandServiceImpl(tableStats);
        defaultPlayer = new Player(500, null);
        position = new PlayerPosition(1, defaultPlayer);
        // assign defaultPosition so Player.equals() can distinguish it from other players with null fields
        defaultPlayer.setDefaultPosition(position);
    }

    // ================================
    // --- createPlayerHands ---
    // ================================

    @Test
    public void createPlayerHands_addsOneHandPerPosition() {
        handService.createPlayerHands(List.of(position));
        assertEquals(1, position.getHands().size());
    }

    @Test
    public void createPlayerHands_addsHandToEachOfMultiplePositions() {
        Player player2 = new Player(500, null);
        Player player3 = new Player(500, null);
        PlayerPosition pos2 = new PlayerPosition(2, player2);
        PlayerPosition pos3 = new PlayerPosition(3, player3);
        handService.createPlayerHands(List.of(position, pos2, pos3));
        assertEquals(1, position.getHands().size());
        assertEquals(1, pos2.getHands().size());
        assertEquals(1, pos3.getHands().size());
    }

    @Test
    public void createPlayerHands_createdHandStartsWithNoBets() {
        handService.createPlayerHands(List.of(position));
        assertFalse(position.getHands().getFirst().hasBet());
    }

    @Test
    public void createPlayerHands_createdHandStartsWithNoCards() {
        handService.createPlayerHands(List.of(position));
        assertTrue(position.getHands().getFirst().getCards().isEmpty());
    }

    @Test
    public void createPlayerHands_calledTwiceAddsTwoHandsToPosition() {
        handService.createPlayerHands(List.of(position));
        handService.createPlayerHands(List.of(position));
        assertEquals(2, position.getHands().size());
    }

    @Test
    public void createPlayerHands_doesNothingForEmptyList() {
        assertDoesNotThrow(() -> handService.createPlayerHands(List.of()));
    }

    @Test
    public void createPlayerHands_handIsAllocatedToItsPosition() {
        handService.createPlayerHands(List.of(position));
        assertSame(position, position.getHands().getFirst().getPosition());
    }

    // ================================
    // --- createDealerHand ---
    // ================================

    @Test
    public void createDealerHand_setsNonNullHand() {
        DealerPosition dealerPosition = new DealerPosition();
        handService.createDealerHand(dealerPosition);
        assertNotNull(dealerPosition.getHand());
    }

    @Test
    public void createDealerHand_createdHandStartsWithNoCards() {
        DealerPosition dealerPosition = new DealerPosition();
        handService.createDealerHand(dealerPosition);
        assertTrue(dealerPosition.getHand().getCards().isEmpty());
    }

    @Test
    public void createDealerHand_replacesExistingHand() {
        DealerPosition dealerPosition = new DealerPosition();
        var original = dealerPosition.getHand();
        handService.createDealerHand(dealerPosition);
        assertNotSame(original, dealerPosition.getHand());
    }

    // ================================
    // --- setActiveHands ---
    // ================================

    @Test
    public void setActiveHands_returnsEmptyListWhenNoHandsHaveBets() {
        handService.createPlayerHands(List.of(position));
        ArrayList<PlayerHand> active = handService.setActiveHands(List.of(position));
        assertTrue(active.isEmpty());
    }

    @Test
    public void setActiveHands_includesHandWithBet() {
        handService.createPlayerHands(List.of(position));
        PlayerHand hand = position.getHands().getFirst();
        hand.getPairs().add(Map.entry(defaultPlayer, new Bet(50)));
        ArrayList<PlayerHand> active = handService.setActiveHands(List.of(position));
        assertEquals(1, active.size());
        assertSame(hand, active.getFirst());
    }

    @Test
    public void setActiveHands_excludesHandWithNoBet() {
        Player player = new Player(500, null);
        PlayerPosition pos2 = new PlayerPosition(2, player);
        handService.createPlayerHands(List.of(position, pos2));
        position.getHands().getFirst().getPairs().add(Map.entry(defaultPlayer, new Bet(50)));
        ArrayList<PlayerHand> active = handService.setActiveHands(List.of(position, pos2));
        assertEquals(1, active.size());
    }

    @Test
    public void setActiveHands_includesAllBetHandsAcrossMultiplePositions() {
        Player player2 = new Player(500, null);
        Player player3 = new Player(500, null);
        PlayerPosition pos2 = new PlayerPosition(2, player2);
        PlayerPosition pos3 = new PlayerPosition(3, player3);
        List<PlayerPosition> positions = List.of(position, pos2, pos3);
        handService.createPlayerHands(positions);
        position.getHands().getFirst().getPairs().add(Map.entry(defaultPlayer, new Bet(50)));
        pos2.getHands().getFirst().getPairs().add(Map.entry(player2, new Bet(25)));
        pos3.getHands().getFirst().getPairs().add(Map.entry(player3, new Bet(100)));
        ArrayList<PlayerHand> active = handService.setActiveHands(positions);
        assertEquals(3, active.size());
    }

    @Test
    public void setActiveHands_includesAllSplitHandsWhenBothHaveBets() {
        handService.createPlayerHands(List.of(position));
        PlayerHand hand1 = position.getHands().getFirst();
        hand1.getPairs().add(Map.entry(defaultPlayer, new Bet(50)));
        PlayerHand hand2 = new PlayerHand(position);
        hand2.getPairs().add(Map.entry(defaultPlayer, new Bet(50)));
        position.getHands().add(hand2);
        ArrayList<PlayerHand> active = handService.setActiveHands(List.of(position));
        assertEquals(2, active.size());
    }

    @Test
    public void setActiveHands_incrementsHandCountForEachActiveBet() {
        Player player = new Player(500, null);
        PlayerPosition pos2 = new PlayerPosition(2, player);
        List<PlayerPosition> positions = List.of(position, pos2);
        handService.createPlayerHands(positions);
        position.getHands().getFirst().getPairs().add(Map.entry(defaultPlayer, new Bet(50)));
        pos2.getHands().getFirst().getPairs().add(Map.entry(player, new Bet(25)));
        handService.setActiveHands(positions);
        assertEquals(2, tableStats.getHandCount());
    }

    @Test
    public void setActiveHands_doesNotIncrementHandCountForHandsWithNoBets() {
        handService.createPlayerHands(List.of(position));
        handService.setActiveHands(List.of(position));
        assertEquals(0, tableStats.getHandCount());
    }

    @Test
    public void setActiveHands_backBetHandIsIncluded() {
        Player backBetter = new Player(500, null);
        handService.createPlayerHands(List.of(position));
        // only the backBetter places a bet on the default player's position
        position.getHands().getFirst().getPairs().add(Map.entry(backBetter, new Bet(50)));
        ArrayList<PlayerHand> active = handService.setActiveHands(List.of(position));
        assertEquals(1, active.size());
    }

    @Test
    public void setActiveHands_incrementsHandCountOncePerSplitHand() {
        handService.createPlayerHands(List.of(position));
        PlayerHand hand1 = position.getHands().getFirst();
        hand1.getPairs().add(Map.entry(defaultPlayer, new Bet(50)));
        PlayerHand hand2 = new PlayerHand(position);
        hand2.getPairs().add(Map.entry(defaultPlayer, new Bet(50)));
        position.getHands().add(hand2);
        handService.setActiveHands(List.of(position));
        assertEquals(2, tableStats.getHandCount());
    }

    @Test
    public void setActiveHands_multipleBackBettersOnSameHandCountsAsOneActiveHand() {
        Player backBetter1 = new Player(500, null);
        Player backBetter2 = new Player(500, null);
        handService.createPlayerHands(List.of(position));
        PlayerHand hand = position.getHands().getFirst();
        hand.getPairs().add(Map.entry(backBetter1, new Bet(50)));
        hand.getPairs().add(Map.entry(backBetter2, new Bet(25)));
        ArrayList<PlayerHand> active = handService.setActiveHands(List.of(position));
        assertEquals(1, active.size());
        assertEquals(1, tableStats.getHandCount());
    }

    // ================================
    // --- setActingPlayers ---
    // ================================

    @Test
    public void setActingPlayers_defaultPlayerIsActingPlayerWhenTheyHaveBet() {
        handService.createPlayerHands(List.of(position));
        PlayerHand hand = position.getHands().getFirst();
        hand.getPairs().add(Map.entry(defaultPlayer, new Bet(50)));
        handService.setActingPlayers(List.of(position));
        assertSame(defaultPlayer, hand.getActingPlayer());
    }

    @Test
    public void setActingPlayers_firstBackBetterIsActingPlayerWhenDefaultPlayerHasNotBet() {
        Player backBetter1 = new Player(500, null);
        Player backBetter2 = new Player(500, null);
        handService.createPlayerHands(List.of(position));
        PlayerHand hand = position.getHands().getFirst();
        hand.getPairs().add(Map.entry(backBetter1, new Bet(50)));
        hand.getPairs().add(Map.entry(backBetter2, new Bet(25)));
        handService.setActingPlayers(List.of(position));
        assertSame(backBetter1, hand.getActingPlayer());
    }

    @Test
    public void setActingPlayers_skipsHandsWithNoBets() {
        handService.createPlayerHands(List.of(position));
        PlayerHand hand = position.getHands().getFirst();
        handService.setActingPlayers(List.of(position));
        assertNull(hand.getActingPlayer());
    }

    @Test
    public void setActingPlayers_setsCorrectActingPlayerAcrossMultiplePositions() {
        Player player2 = new Player(500, null);
        Player backBetter = new Player(500, null);
        PlayerPosition pos2 = new PlayerPosition(2, player2);
        player2.setDefaultPosition(pos2);
        List<PlayerPosition> positions = List.of(position, pos2);
        handService.createPlayerHands(positions);
        position.getHands().getFirst().getPairs().add(Map.entry(defaultPlayer, new Bet(50)));
        pos2.getHands().getFirst().getPairs().add(Map.entry(backBetter, new Bet(25)));
        handService.setActingPlayers(positions);
        assertSame(defaultPlayer, position.getHands().getFirst().getActingPlayer());
        assertSame(backBetter, pos2.getHands().getFirst().getActingPlayer());
    }

    @Test
    public void setActingPlayers_defaultPlayerIsActingPlayerEvenWhenBackBetterBetsFirst() {
        Player backBetter = new Player(500, null);
        handService.createPlayerHands(List.of(position));
        PlayerHand hand = position.getHands().getFirst();
        // backBetter appears first in pairs, but default player is also present
        hand.getPairs().add(Map.entry(backBetter, new Bet(50)));
        hand.getPairs().add(Map.entry(defaultPlayer, new Bet(100)));
        handService.setActingPlayers(List.of(position));
        assertSame(defaultPlayer, hand.getActingPlayer());
    }

    @Test
    public void setActingPlayers_setsActingPlayerOnAllSplitHands() {
        handService.createPlayerHands(List.of(position));
        PlayerHand hand1 = position.getHands().getFirst();
        hand1.getPairs().add(Map.entry(defaultPlayer, new Bet(50)));
        PlayerHand hand2 = new PlayerHand(position);
        hand2.getPairs().add(Map.entry(defaultPlayer, new Bet(50)));
        position.getHands().add(hand2);
        handService.setActingPlayers(List.of(position));
        assertSame(defaultPlayer, hand1.getActingPlayer());
        assertSame(defaultPlayer, hand2.getActingPlayer());
    }

    @Test
    public void setActingPlayers_backBetterIsActingPlayerOnSplitHandWhenDefaultPlayerAbsent() {
        Player backBetter = new Player(500, null);
        handService.createPlayerHands(List.of(position));
        PlayerHand hand1 = position.getHands().getFirst();
        hand1.getPairs().add(Map.entry(backBetter, new Bet(50)));
        PlayerHand hand2 = new PlayerHand(position);
        hand2.getPairs().add(Map.entry(backBetter, new Bet(50)));
        position.getHands().add(hand2);
        handService.setActingPlayers(List.of(position));
        assertSame(backBetter, hand1.getActingPlayer());
        assertSame(backBetter, hand2.getActingPlayer());
    }

    @Test
    public void setActingPlayers_doesNothingForEmptyPositionList() {
        assertDoesNotThrow(() -> handService.setActingPlayers(List.of()));
    }

    // ================================
    // --- clearActiveHands ---
    // ================================

    @Test
    public void clearActiveHands_emptiesTheList() {
        handService.createPlayerHands(List.of(position));
        position.getHands().getFirst().getPairs().add(Map.entry(defaultPlayer, new Bet(50)));
        ArrayList<PlayerHand> activeHands = handService.setActiveHands(List.of(position));
        handService.clearActiveHands(activeHands);
        assertTrue(activeHands.isEmpty());
    }

    @Test
    public void clearActiveHands_doesNotThrowOnAlreadyEmptyList() {
        assertDoesNotThrow(() -> handService.clearActiveHands(new ArrayList<>()));
    }

    // ================================
    // --- clearPlayerHands ---
    // ================================

    @Test
    public void clearPlayerHands_removesHandsFromPosition() {
        handService.createPlayerHands(List.of(position));
        handService.clearPlayerHands(List.of(position));
        assertTrue(position.getHands().isEmpty());
    }

    @Test
    public void clearPlayerHands_removesHandsFromAllPositions() {
        Player player2 = new Player(500, null);
        PlayerPosition pos2 = new PlayerPosition(2, player2);
        List<PlayerPosition> positions = List.of(position, pos2);
        handService.createPlayerHands(positions);
        handService.clearPlayerHands(positions);
        assertTrue(position.getHands().isEmpty());
        assertTrue(pos2.getHands().isEmpty());
    }

    @Test
    public void clearPlayerHands_removesAllHandsFromPositionWithMultipleHandsAfterSplit() {
        handService.createPlayerHands(List.of(position));
        position.getHands().add(new PlayerHand(position));
        assertEquals(2, position.getHands().size());
        handService.clearPlayerHands(List.of(position));
        assertTrue(position.getHands().isEmpty());
    }

    @Test
    public void clearPlayerHands_doesNothingForEmptyPositionList() {
        assertDoesNotThrow(() -> handService.clearPlayerHands(List.of()));
    }

    @Test
    public void clearPlayerHands_doesNotThrowWhenPositionHasNoHands() {
        assertDoesNotThrow(() -> handService.clearPlayerHands(List.of(position)));
    }

    // ================================
    // --- clearDealerHand ---
    // ================================

    @Test
    public void clearDealerHand_removesCardsFromHand() {
        DealerPosition dealerPosition = new DealerPosition();
        dealerPosition.getHand().getCards().add(new Card("Ace", 1));
        dealerPosition.getHand().getCards().add(new Card("King", 10));
        handService.clearDealerHand(dealerPosition);
        assertTrue(dealerPosition.getHand().getCards().isEmpty());
    }

    @Test
    public void clearDealerHand_doesNotThrowWhenHandIsAlreadyEmpty() {
        DealerPosition dealerPosition = new DealerPosition();
        assertDoesNotThrow(() -> handService.clearDealerHand(dealerPosition));
    }

    @Test
    public void clearDealerHand_leavesHandObjectIntact() {
        DealerPosition dealerPosition = new DealerPosition();
        var hand = dealerPosition.getHand();
        dealerPosition.getHand().getCards().add(new Card("Seven", 7));
        handService.clearDealerHand(dealerPosition);
        assertSame(hand, dealerPosition.getHand());
    }
}