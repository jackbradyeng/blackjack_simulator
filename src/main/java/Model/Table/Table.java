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
import Model.Table.BettingServices.BettingService;
import Model.Table.BettingServices.BettingServiceImpl;
import Model.Table.DealServices.DealServiceImpl;
import Model.Table.HandServices.HandServiceImpl;
import Model.Table.Hands.DealerHand;
import Model.Table.Hands.Hand;
import Model.Table.Hands.PlayerHand;
import Model.Table.PayoutServices.InsurancePayoutService;
import Model.Table.PayoutServices.StandardPayoutService;
import Model.Table.Positions.DealerPosition;
import Model.Table.Positions.PlayerPosition;
import Model.Table.Processors.DoubleBetProcessors.DoubleBetProcessorImpl;
import Model.Table.Processors.InsuranceBetProcessors.InsuranceBetProcessorImpl;
import Model.Table.Processors.SplitBetProcessors.SplitBetProcessorImpl;
import Model.Table.Processors.StandardBetProcessors.StandardBetProcessorImpl;
import static Model.Constants.*;
import Model.Table.Validators.DoubleBetValidators.DoubleBetValidatorImpl;
import Model.Table.Validators.InsuranceBetValidators.InsuranceBetValidatorImpl;
import Model.Table.Validators.SplitBetValidators.SplitBetValidatorImpl;
import Model.Table.Validators.StandardBetValidators.StandardBetValidatorImpl;
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
    @Getter private DealServiceImpl dealService;
    @Getter private HandServiceImpl handService;
    @Getter private StandardPayoutService standardPayoutService;
    @Getter private InsurancePayoutService insurancePayoutService;
    @Getter private BettingService bettingService;
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
        this.dealService = new DealServiceImpl();
        this.handService = new HandServiceImpl(tableStats);
        this.standardPayoutService = new StandardPayoutService(tableStats);
        this.insurancePayoutService = new InsurancePayoutService();
        initPlayers(playerCount);
        initPlayerPositions();
        assignDefaultPlayerPositions(players);
        assignDealerPosition(dealer);
        initBettingService();
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
        handService.createPlayerHands(playerPositionsIterable);
        handService.createDealerHand(dealerPosition);
    }

    /** Actions: deals each player two initial cards, computes the hand values for all active hands, outputs the results. */
    public void drawRoutine() {
        handService.setActingPlayers(playerPositionsIterable);
        dealService.dealOpeningCards(deck, dealerPosition, playerPositionsIterable);
        this.activeHands = handService.setActiveHands(playerPositionsIterable);
        dealService.calculateHandValues(activeHands, dealerPosition);
        tablePrinter.printActivePlayerHands();
        tablePrinter.printDealerFirstCard();
    }

    /** Actions: handles regular payouts, handles insurance payouts, and resets the game state in preparation for a new
     * round. */
    public void windDownRoutine() {
        standardPayoutService.process(activeHands, dealerPosition.getHand(), dealer);
        insurancePayoutService.process(activeHands, dealerPosition.getHand(), dealer);
        tablePrinter.printHandResults();
        handService.clearActiveHands(activeHands);
        handService.clearPlayerHands(playerPositionsIterable);
        handService.clearDealerHand(dealerPosition);
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

    private void initBettingService() {
        this.bettingService = new BettingServiceImpl(isSimulation, players, playerPositionsIterable,
                new DoubleBetProcessorImpl(),
                new DoubleBetValidatorImpl(),
                new InsuranceBetProcessorImpl(),
                new InsuranceBetValidatorImpl(),
                new StandardBetProcessorImpl(),
                new StandardBetValidatorImpl(),
                new SplitBetProcessorImpl(),
                new SplitBetValidatorImpl());
    }

    /** logs the house's opening balance. */
    private void logHouseBalance() {
        this.houseBalance = dealer.getChips();
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

        StandardBetValidatorImpl standardBetValidatorImpl = new StandardBetValidatorImpl();
        StandardBetProcessorImpl standardBetProcessorImpl = new StandardBetProcessorImpl();

        if (standardBetValidatorImpl.isValid(player, players, position, playerPositionsIterable, amount, isSimulation)) {
            standardBetProcessorImpl.process(player, position, amount);
        }
    }

    /** books an insurance bet for a player on a given position for a given amount. To be called AFTER the cards are
     * dealt. */
    public void bookInsuranceBet(Player player, PlayerPosition position, PlayerHand hand, double amount) {

        InsuranceBetValidatorImpl insuranceBetValidatorImpl = new InsuranceBetValidatorImpl();
        InsuranceBetProcessorImpl insuranceBetProcessorImpl = new InsuranceBetProcessorImpl();

        if (insuranceBetValidatorImpl.isValid(player, hand, amount)) {
            insuranceBetProcessorImpl.process(player, position, amount);
        }
    }

    /** doubles the player's existing bet at a given position for that amount. Players can only double down once and if
     * they do, they can only hit one more time. If the player has already hit, they cannot double down. Also, if the
     * player has already made a natural blackjack, they cannot double down. */
    public void bookDoubleDownBet(Player player, PlayerPosition position, PlayerHand hand) {

        DoubleBetValidatorImpl doubleBetValidatorImpl = new DoubleBetValidatorImpl();
        DoubleBetProcessorImpl doubleBetProcessorImpl = new DoubleBetProcessorImpl();

        if (doubleBetValidatorImpl.isValid(player, players, position, playerPositionsIterable, hand, isSimulation)) {
            doubleBetProcessorImpl.process(player, position, hand);
        }
    }

    /** if the player's first and second cards are equal in value and if the player has chips remaining equal to the
     * size of their original bet, the hand is "split". Meaning that the second card is allocated to a new hand and
     * the player's new bet is associated with this hand. */
    public void splitHand(Player player, PlayerPosition position, PlayerHand hand) {

        SplitBetProcessorImpl splitBetProcessorImpl = new SplitBetProcessorImpl();
        SplitBetValidatorImpl splitBetValidatorImpl = new SplitBetValidatorImpl();

        if (splitBetValidatorImpl.isValid(player, players, position, playerPositionsIterable, hand, isSimulation)) {
            splitBetProcessorImpl.process(player, position, hand, activeHands);
        }
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
                bettingService.splitHand(player, hand.getPosition(), hand, activeHands);
                tableStats.incrementHandCount();
                tableStats.incrementSplitCount();
                break;
            case DOUBLE:
                // book double down bet
                bettingService.bookDoubleDownBet(player, hand.getPosition(), hand);
                hit(hand);
                break;
            case INSURANCE:
                // book insurance bet
                bettingService.bookInsuranceBet(player, hand.getPosition(), hand, DEFAULT_PLAYER_INSURANCE_BET);
                break;
            case STAND: {}
                // do nothing
        }
    }
}
