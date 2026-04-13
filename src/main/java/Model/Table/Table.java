package Model.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import Exceptions.PlayerCountException;
import Model.Actors.Dealer;
import Model.Actors.Player;
import Model.Deck.Deck;
import Model.Deck.ShuffleStrategies.FisherYatesStrategy;
import Model.Observers.TablePrinter;
import Model.Observers.TableStats;
import Model.Strategies.dealer_strategies.DefaultDealerStrategy;
import Model.Strategies.player_strategies.OptimalNoCountingStrategy;
import Model.Table.Hands.DealerHand;
import Model.Table.Hands.Hand;
import Model.Table.Hands.PlayerHand;
import Model.Table.PayoutServices.InsurancePayoutService;
import Model.Table.PayoutServices.StandardPayoutService;
import Model.Table.Positions.DealerPosition;
import Model.Table.Positions.PlayerPosition;
import Model.Table.Processors.DoubleBetProcessor;
import Model.Table.Processors.InsuranceBetProcessor;
import Model.Table.Processors.SplitBetProcessor;
import Model.Table.Processors.StandardBetProcessor;
import static Model.Constants.*;
import lombok.Getter;

public class Table {

    /// instance variables
    @Getter private boolean isSimulation;
    @Getter private Deck deck;
    @Getter private Dealer dealer;
    @Getter private final DealerPosition dealerPosition;
    @Getter private ArrayList<Player> players;
    @Getter private final ArrayList<PlayerPosition> playerPositionsIterable;
    @Getter private ArrayList<PlayerHand> activeHands;
    @Getter private HashMap<Player, Double> playerBalances;
    @Getter private Double houseBalance;
    @Getter private StandardPayoutService standardPayoutService;
    @Getter private InsurancePayoutService insurancePayoutService;
    @Getter private TablePrinter tablePrinter;
    @Getter private TableStats tableStats;

    /// default constructor
    public Table(int playerCount, int deckCount, boolean isSimulation) {
        this.isSimulation = isSimulation;
        this.deck = new Deck(deckCount, new FisherYatesStrategy());
        this.dealer = new Dealer(new DefaultDealerStrategy(), DEFAULT_DEALER_STARTING_CHIPS);
        this.players = new ArrayList<>();
        this.dealerPosition = new DealerPosition();
        this.playerPositionsIterable = new ArrayList<>();
        this.activeHands = new ArrayList<>();
        this.playerBalances = new HashMap<>();
        this.tablePrinter = new TablePrinter(this);
        this.tableStats = new TableStats();
        this.standardPayoutService = new StandardPayoutService(tableStats);
        this.insurancePayoutService = new InsurancePayoutService();
        initPlayers(playerCount);
        initPlayerPositions();
        assignDefaultPlayerPositions(players);
        assignDealerPosition(dealer);
    }

    /** initializes the game state for a new round of Blackjack.
     * <p> Actions: checks if the deck requires a top-up.
     * creates empty player hands at each position.
     * creates an empty dealer hand at the dealer position.</p> */
    public void startupRoutine() {
        tablePrinter.printNewRoundMessage();
        logPlayerBalances();
        logHouseBalance();
        checkDeck();
        createPlayerHands();
        createDealerHand();
    }

    /** Actions: deals each player two initial cards, computes the hand values for all active hands, outputs the results. */
    public void drawRoutine() {
        determineActingPlayers();
        dealOpeningCards();
        setActiveHands();
        calculateHandValues();
        tablePrinter.printActivePlayerHands();
        tablePrinter.printDealerFirstCard();
    }

    /** Actions: handles regular payouts, handles insurance payouts, and resets the game state in preparation for a new
     * round. */
    public void windDownRoutine() {
        standardPayoutService.process(activeHands, dealerPosition.getHand(), dealer);
        insurancePayoutService.process(activeHands, dealerPosition.getHand(), dealer);
        tablePrinter.printHandResults();
        clearActiveHands();
        clearPlayerHands();
        clearDealerHand();
    }

    /** initializes each of the players at the table. Throws an exception if more players are allocated than the
     * table allows. */
    private void initPlayers(int playerCount) throws PlayerCountException {
        if(playerCount > DEFAULT_TABLE_POSITIONS) {
            throw new PlayerCountException("Insufficient table positions for this many players. The default number" +
                    "of table positions is " + DEFAULT_TABLE_POSITIONS + ".");
        } else {
            for (int i = 0; i < playerCount; i++) {
                Player player = new Player(DEFAULT_PLAYER_STARTING_CHIPS, new OptimalNoCountingStrategy());
                players.add(player);
            }
        }
    }

    /** initializes each of the player position instances and places them in the iterable arraylist. */
    private void initPlayerPositions() {
        for(int i = 1; i < DEFAULT_TABLE_POSITIONS + 1; i++) {
            PlayerPosition p = new PlayerPosition(i);
            playerPositionsIterable.add(p);
        }
    }

    /** assigns players to their default positions around the table. <strong> Note: </strong> This method assumes
     * that the player count is less than or equal to the total number of positions available. */
    private void assignDefaultPlayerPositions(ArrayList<Player> players) {
        // if singleplayer, place the player directly across from the dealer
        if(players.size() == 1) {
            Player singlePlayer = players.getFirst();
            int middlePosition = DEFAULT_TABLE_POSITIONS / 2 + 1;
            PlayerPosition defaultPosition = playerPositionsIterable.get(middlePosition);
            // stores a default position reference in both the position and the player classes
            singlePlayer.setDefaultPosition(defaultPosition);
            defaultPosition.setDefaultPlayer(singlePlayer);
        } else {
            // if multiplayer, allocate the players to seats at the table from right to left
            for(int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                PlayerPosition position = playerPositionsIterable.get(i);
                player.setDefaultPosition(position);
                position.setDefaultPlayer(player);
            }
        }
    }

    /** assigns the dealer to his/her default position. */
    private void assignDealerPosition(Dealer dealer) {
        dealer.setPosition(dealerPosition);
    }

    /** logs each of the player's opening balances, storing them as key-value pairs. */
    private void logPlayerBalances() {
        for(Player player : players) {
            playerBalances.put(player, player.getChips());
        }
    }

    /** logs the house's opening balance. */
    private void logHouseBalance() {
        this.houseBalance = dealer.getChips();
    }

    /** initializes an empty hand for each position at the table. Required before dealing cards. */
    private void createPlayerHands() {
        for(PlayerPosition position : playerPositionsIterable) {
            PlayerHand emptyHand = new PlayerHand(position);
            position.getHands().add(emptyHand);
        }
    }

    /** initializes an empty hand for the dealer. */
    private void createDealerHand() {
        DealerHand dealerHand = new DealerHand();
        dealerPosition.setHand(dealerHand);
    }

    /** checks to see how many cards remain in the deck and creates a new deck instance if the number is too low. */
    private void checkDeck() {
        if(deck.getDeck().size() < NEW_DECK_THRESHOLD) {
            deck.createNewDeck(DEFAULT_NUMBER_OF_DECKS);
        }
    }

    /// BETTING LOGIC - TO BE SPUN OFF INTO SEPARATE SERVICES

    /** books a standard bet for a player on a given position for a given amount. To be called before the cards are
     * dealt. */
    public void bookStandardBet(Player player, PlayerPosition position, double amount) {
        StandardBetProcessor processor = new StandardBetProcessor(isSimulation, players, playerPositionsIterable,
                player, position, amount);
        processor.process();
    }

    /** books an insurance bet for a player on a given position for a given amount. To be called AFTER the cards are
     * dealt. */
    public void bookInsuranceBet(Player player, PlayerPosition position, PlayerHand hand, double amount) {
        InsuranceBetProcessor processor = new InsuranceBetProcessor(isSimulation, players, playerPositionsIterable,
                player, position, hand, amount);
        processor.process();
    }

    /** doubles the player's existing bet at a given position for that amount. Players can only double down once and if
     * they do, they can only hit one more time. If the player has already hit, they cannot double down. Also, if the
     * player has already made a natural blackjack, they cannot double down. */
    public void bookDoubleDownBet(Player player, PlayerPosition position, PlayerHand hand) {
        DoubleBetProcessor processor = new DoubleBetProcessor(isSimulation, players, playerPositionsIterable,
                player, position, hand);
        processor.process();
    }

    /** if the player's first and second cards are equal in value and if the player has chips remaining equal to the
     * size of their original bet, the hand is "split". Meaning that the second card is allocated to a new hand and
     * the player's new bet is associated with this hand. */
    public void splitHand(Player player, PlayerPosition position, PlayerHand hand) {
        SplitBetProcessor processor = new SplitBetProcessor(isSimulation, players, playerPositionsIterable, activeHands,
                player, position, hand);
        processor.process();
        this.activeHands = processor.refreshActiveHands();
    }

    /// DEAL LOGIC - TO BE REFACTORED

    /** returns a list of the active hands at the table. */
    public void setActiveHands() {
        ArrayList<PlayerHand> activeHands = new ArrayList<>();
        for(PlayerPosition position : playerPositionsIterable) {
            for(PlayerHand hand : position.getHands()) {
                if(hand.hasBet()) {
                    activeHands.add(hand);
                    tableStats.incrementHandCount();
                }
            }
        }
        this.activeHands =  activeHands;
    }

    public void clearActiveHands() {
        activeHands.clear();
    }

    /** private helper method. Clears all player hands at the table. */
    private void clearPlayerHands() {
        for(PlayerPosition position : playerPositionsIterable) {
            position.clearHands();
        }
    }

    /** private helper method. Clears the dealer's hand. */
    private void clearDealerHand() {
        dealerPosition.clearHand();
    }

    /** deals first two cards to all active positions including the dealer. */
    private void dealOpeningCards() {
        dealToActivePositions();
        dealToDealer();
        dealToActivePositions();
        dealToDealer();
    }

    /** deals a single card to the dealer's hand. */
    private void dealToDealer() {
        deck.deal().ifPresent(deal -> dealerPosition.getHand().receiveCard(deal));
    }

    /** deals a single card to each position that has a bet. */
    private void dealToActivePositions() {
        for(PlayerPosition position : playerPositionsIterable) {
            for(PlayerHand hand : position.getHands()) {
                if(hand.hasBet()) {
                    deck.deal().ifPresent(hand::receiveCard);
                }
            }
        }
    }

    /** sets the acting player for each hand at the table. This should usually be the default player. But if the
     * default player has not bet on their own position, then the acting player is simply the first to have bet on that
     * position. */
    public void determineActingPlayers() {
        for(PlayerPosition position : playerPositionsIterable) {
            for(PlayerHand hand : position.getHands()) {
                if(hand.hasBet()) {
                    if(position.isDefaultPlayerInHand()) {
                        hand.setActingPlayer(position.getDefaultPlayer());
                    } else {
                        hand.setActingPlayer(hand.getPairs().getFirst().getKey());
                    }
                }
            }
        }
    }

    /** deals a card to a hand before setting its value. */
    public void hit(Hand hand) {
        if(!hand.isBust()) {
            deck.deal().ifPresent(deal -> {
                hand.receiveCard(deal);
                hand.setHandValue();
                hand.setHasHit(true);
            });
        } else {
            System.out.println("BUST!");
        }
    }

    /** calculates the hand value for all active hands at the table. */
    private void calculateHandValues() {
        for(PlayerHand hand : getActiveHands()) {
            hand.setHandValue();
        }
        dealerPosition.getHand().setHandValue();
    }

    /// STRATEGY LOGIC - TO BE REFACTORED

    /** executes the dealer's strategy. */
    public void executeDealerStrategy() {
        while(!Objects.equals(dealer.executeStrategy(), STAND)) {
            handleDealerAction(dealer.executeStrategy());
        }
        tablePrinter.printDealerHand();
    }

    /** executes the player's strategy. */
    public void executePlayerStrategy(PlayerHand playerHand, DealerHand dealerHand) {
        // defines the acting player in the hand
        Player actingPlayer = playerHand.getActingPlayer();
        // need to check that the hand is not bust to prevent null pointer exceptions in the strategy class
        while(!playerHand.isBust()) {
            System.out.println("---- PLAYER STRATEGY IS: " + actingPlayer.executeStrategy(playerHand, dealerHand)
                    + " ----");
            // players are only permitted to hit once after doubling down so the loop should terminate after doing so
            if(actingPlayer.executeStrategy(playerHand, dealerHand).equals(DOUBLE)) {
                handlePlayerAction(actingPlayer, playerHand, actingPlayer.executeStrategy(playerHand, dealerHand));
                break;
            } else if(actingPlayer.executeStrategy(playerHand, dealerHand).equals(STAND)) {
                break;
            } else {
                handlePlayerAction(actingPlayer, playerHand, actingPlayer.executeStrategy(playerHand, dealerHand));
            }
        }
        tablePrinter.printActivePlayerHands();
    }

    /** executes the player strategy for all active hands at the table. */
    public void executePlayerStrategyForAll() {
        // switched to an iterative for loop to avoid ConcurrentModificationExceptions...
        for(int i = 0; i < getActiveHands().size(); i++) {
            PlayerHand hand = getActiveHands().get(i);
            executePlayerStrategy(hand, dealer.getPosition().getHand());
        }
    }

    /// ACTION LOGIC - TO BE REFACTORED

    public void handleDealerAction(String action) {
        if(action.equals(HIT)) {
            hit(dealer.getPosition().getHand());
        }
    }

    public void handlePlayerAction(Player player, PlayerHand hand, String action) {
        switch (action) {
            case HIT:
                hit(hand);
                break;
            case SPLIT:
                // partition hands and book additional bet
                splitHand(player, hand.getPosition(), hand);
                tableStats.incrementHandCount();
                tableStats.incrementSplitCount();
                break;
            case DOUBLE:
                // book double down bet
                bookDoubleDownBet(player, hand.getPosition(), hand);
                hit(hand);
                break;
            case INSURANCE:
                // book insurance bet
                System.out.println("----INSURANCE BET BOOKED!----");
                bookInsuranceBet(player, hand.getPosition(), hand, DEFAULT_PLAYER_INSURANCE_BET);
                break;
            case STAND: {}
                // do nothing
        }
    }
}
