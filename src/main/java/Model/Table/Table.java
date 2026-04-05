package Model.Table;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import Exceptions.PlayerCountException;
import Model.Actors.Dealer;
import Model.Actors.Player;
import Model.Deck.Deck;
import Model.Deck.ShuffleStrategies.FisherYatesStrategy;
import Model.Table.Bets.Bet;
import Model.Table.Bets.InsuranceBet;
import Model.Table.Hands.DealerHand;
import Model.Table.Hands.Hand;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.DealerPosition;
import Model.Table.Positions.PlayerPosition;
import Model.Table.Processors.DoubleBetProcessor;
import Model.Table.Processors.InsuranceBetProcessor;
import Model.Table.Processors.SplitBetProcessor;
import Model.Table.Processors.StandardBetProcessor;
import static Model.Constants.*;

public class Table {

    /// instance variables
    private boolean isSimulation;
    private Deck deck;
    private Dealer dealer;
    private ArrayList<Player> players;
    private final DealerPosition dealerPosition;
    private final ArrayList<PlayerPosition> playerPositionsIterable;
    private ArrayList<PlayerHand> activeHands;
    private HashMap<Player, Double> playerBalances;
    private Double houseBalance;

    /// table stats
    public int handCount = 0;
    public int splitCount = 0;
    public int blackjackCount = 0;
    public int playerWinCount = 0;
    public int playerLossCount = 0;
    public int pushCount = 0;

    /// default constructor
    public Table(int playerCount, int deckCount, boolean isSimulation) {
        this.isSimulation = isSimulation;
        this.deck = new Deck(deckCount, new FisherYatesStrategy());
        this.dealer = new Dealer(DEFAULT_DEALER_STARTING_CHIPS);
        this.players = new ArrayList<>();
        this.dealerPosition = new DealerPosition();
        this.playerPositionsIterable = new ArrayList<>();
        this.activeHands = new ArrayList<>();
        this.playerBalances = new HashMap<>();
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
        printNewRoundMessage();
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
        printActivePlayerHands();
        printDealerFirstCard();
    }

    /** Actions: handles regular payouts, handles insurance payouts, and resets the game state in preparation for a new
     * round. */
    public void windDownRoutine() {
        handleRegularPayouts();
        handleInsurancePayouts();
        printHandResults();
        clearActiveHands();
        clearAllHands();
    }

    /** initializes each of the players at the table. Throws an exception if more players are allocated than the
     * table allows. */
    private void initPlayers(int playerCount) throws PlayerCountException {
        if(playerCount > DEFAULT_TABLE_POSITIONS) {
            throw new PlayerCountException("Insufficient table positions for this many players. The default number" +
                    "of table positions is " + DEFAULT_TABLE_POSITIONS + ".");
        } else {
            for (int i = 0; i < playerCount; i++) {
                Player player = new Player(DEFAULT_PLAYER_STARTING_CHIPS);
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
        if(getDeck().size() < NEW_DECK_THRESHOLD) {
            deck.createNewDeck();
        }
    }

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

    /** returns a list of the active hands at the table. */
    public void setActiveHands() {
        ArrayList<PlayerHand> activeHands = new ArrayList<>();
        for(PlayerPosition position : playerPositionsIterable) {
            for(PlayerHand hand : position.getHands()) {
                if(hand.hasBet()) {
                    activeHands.add(hand);
                    handCount++;
                }
            }
        }
        this.activeHands =  activeHands;
    }

    public ArrayList<PlayerHand> getActiveHands() {
        return activeHands;
    }

    public void clearActiveHands() {
        activeHands.clear();
    }

    /** clears all hands at the table (along with their associated cards and player-bet pairs). */
    private void clearAllHands() {
        clearPlayerHands();
        clearDealerHand();
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
        getDealerHand().receiveCard(deck.deal());
    }

    /** deals a single card to each position that has a bet. */
    private void dealToActivePositions() {
        for(PlayerPosition position : playerPositionsIterable) {
            for(PlayerHand hand : position.getHands()) {
                if(hand.hasBet()) {
                    hand.receiveCard(deck.deal());
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
            hand.receiveCard(deck.deal());
            hand.setHandValue();
            hand.setHasHit(true);
        } else {
            System.out.println("BUST!");
        }
    }

    /** calculates the hand value for all active hands at the table. */
    private void calculateHandValues() {
        for(PlayerHand hand : getActiveHands()) {
            hand.setHandValue();
        }
        getDealerHand().setHandValue();
    }

    /** executes the dealer's strategy. */
    public void executeDealerStrategy() {
        while(!Objects.equals(dealer.executeStrategy(), STAND)) {
            handleDealerAction(dealer.executeStrategy());
        }
        printDealerHand();
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
        printActivePlayerHands();
    }

    /** executes the player strategy for all active hands at the table. */
    public void executePlayerStrategyForAll() {
        // switched to an iterative for loop to avoid ConcurrentModificationExceptions...
        for(int i = 0; i < getActiveHands().size(); i++) {
            PlayerHand hand = getActiveHands().get(i);
            executePlayerStrategy(hand, dealer.getPosition().getHand());
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
                splitHand(player, hand.getPosition(), hand);
                handCount++;
                splitCount++;
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

    /** processes the non-insurance payouts for each active hand at the table. */
    private void handleRegularPayouts() {
        for(PlayerHand hand : getActiveHands()) {
            for(Map.Entry<Player, Bet> pair : hand.getPairs()) {
                if(handlePlayerWin(hand, pair)) {
                    // avoid double counting bets in single player games
                    if(isStandardBet(pair.getValue()))
                        playerWinCount++;
                }
                else if(handlePlayerPush(hand, pair)) {
                    if(isStandardBet(pair.getValue()))
                        pushCount++;
                }
                else if(handlePlayerLoss(hand, pair)) {
                    if(isStandardBet(pair.getValue())) {
                        playerLossCount++;
                    }
                }
            }
        }
    }

    /** private helper method. Determines whether a given bet is standard or not. */
    private boolean isStandardBet(Bet bet) {
        return bet.getClass().equals(Bet.class);
    }

    /** process a player's bet on a hand if it wins against the dealer. */
    public boolean handlePlayerWin(PlayerHand hand, Map.Entry<Player, Bet> pair) {
        if(!hand.isBust() && (getDealerHand().isBust() || hand.getHandValue() >
                getDealerHand().getHandValue())) {
            if(!(pair.getValue() instanceof InsuranceBet)) {
                double payout;
                // blackjack pays out for natural blackjacks only
                if(hand.getHandValue() == BLACKJACK_CONSTANT && hand.getCards().size() == 2) {
                    payout = pair.getValue().getAmount() * (1 +
                            ((double) DEFAULT_BLACKJACK_PAYOUT_DENOMINATOR / DEFAULT_BLACKJACK_PAYOUT_NUMERATOR));
                    blackjackCount++;
                } else {
                    payout = pair.getValue().getAmount() * (1 + DEFAULT_PAYOUT_RATIO);
                }
                dealer.dispenseChips(payout - pair.getValue().getAmount());
                pair.getKey().receiveChips(payout);
            }
            return true;
        } else {
            return false;
        }
    }

    /** processes a player's bet on a hand if it pushes with the dealer. (i.e. the two are equal in value) */
    public boolean handlePlayerPush(PlayerHand hand, Map.Entry<Player, Bet> pair) {
        if(!hand.isBust() && hand.getHandValue() == getDealerHand().getHandValue()) {
            if(!(pair.getValue() instanceof InsuranceBet)) {
                // refund chips;
                pair.getKey().receiveChips(pair.getValue().getAmount());
            }
            return true;
        } else {
            return false;
        }
    }

    /** processes a player's bet on a hand if it loses against the dealer. */
    public boolean handlePlayerLoss(PlayerHand hand, Map.Entry<Player, Bet> pair) {
        if(hand.isBust() || (!getDealerHand().isBust() && getDealerHand().getHandValue() > hand.getHandValue())) {
            dealer.receiveChips(pair.getValue().getAmount());
            return true;
        } else {
            return false;
        }
    }

    /** processes the insurance payouts for each active hand at the table. */
    private void handleInsurancePayouts() {
        for(PlayerHand hand : getActiveHands()) {
            for(Map.Entry<Player, Bet> pair : hand.getPairs()) {
                if(pair.getValue() instanceof InsuranceBet) {
                    if(getDealerHand().getHandValue() == BLACKJACK_CONSTANT && hand.hasInsuranceOption(getDealerHand())) {
                        double payout = pair.getValue().getAmount() * (1 + DEFAULT_INSURANCE_RATIO);
                        dealer.dispenseChips(payout - pair.getValue().getAmount());
                        pair.getKey().receiveChips(payout);
                    } else {
                        dealer.receiveChips((pair.getValue().getAmount()));
                    }
                }
            }
        }
    }

    public Deck getDeck() {
        return deck;
    }

    public int getNumberOfPositions() {
        return playerPositionsIterable.size();
    }

    public ArrayList<PlayerPosition> getPlayerPositionsIterable() {
        return playerPositionsIterable;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public DealerHand getDealerHand() {
        return dealerPosition.getHand();
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getHandCount() {return handCount; }

    public int getBlackjackCount() {
        return blackjackCount;
    }

    public int getPlayerWinCount() {
        return playerWinCount;
    }

    public int getPlayerLossCount() {
        return playerLossCount;
    }

    public int getPushCount() {
        return pushCount;
    }

    public int getSplitCount() {
        return splitCount;
    }

    /** may be required if a player decides to leave the game. */
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    // prints welcome message
    public void printWelcomeMessage() {
        System.out.println("********************************");

        System.out.println("***** WELCOME TO BLACKJACK *****");

        System.out.println("********************************");
    }

    // prints new round message
    public void printNewRoundMessage() {
        System.out.print("\n");
        System.out.println("---- NEW ROUND ----");
    }

    // prints all active player hands at the table
    public void printActivePlayerHands() {
        for(PlayerHand hand : getActiveHands()) {
            System.out.println("Position: " + hand.getPosition().getPositionNumber());
            System.out.println("----" + " Hand: " + hand + " Hand value: " + hand.getHandValue() + ".");
            if (hand.isBust()) {
                System.out.println("BUST!");
            }
        }
    }

    // prints the dealer's first card and its corresponding value
    public void printDealerFirstCard() {
        System.out.println("Position: 0 (Dealer)");
        System.out.println("----" + " Hand: " + getDealerHand().getCards().getFirst() + " Hand Value: " +
                getDealerHand().getCards().getFirst().getValue() + "." + "\n");
    }

    // print the dealer's hand
    public void printDealerHand() {
        System.out.println("Position: 0 (Dealer)");
        System.out.println("----" + " Hand: " + getDealerHand().toString() + " Hand Value: " +
                getDealerHand().getHandValue()
                + "." + "\n");
        if(getDealerHand().isBust()) {
            System.out.println("BUST!");
        }
    }

    // prints results
    public void printHandResults() {
        System.out.println("---- RESULTS ----");
        printPlayerResults();
        printHouseResults();
        System.out.println("---- END OF ROUND ----");
    }

    public void printPlayerResults() {
        for(Player player : players) {
            System.out.println("Player: " + player);
            System.out.println("Starting Balance: " + playerBalances.get(player).intValue() + " Closing Balance: "
                    + (int) player.getChips());
            System.out.println("Profit (Loss): " + (int) (player.getChips() - playerBalances.get(player)) + "\n");
        }
    }

    public void printHouseResults() {
        System.out.println("Player: House");
        System.out.println("Starting Balance: " + houseBalance.intValue() + " Closing Balance: "
                + (int) dealer.getChips());
        System.out.println("Profit (Loss): " + (int) (dealer.getChips() - houseBalance) + "\n");
    }
}
