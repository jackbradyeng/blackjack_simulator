package Model.Table;

import java.util.ArrayList;
import java.util.HashMap;
import Exceptions.PlayerCountException;
import Model.Actors.Dealer;
import Model.Actors.Player;
import Model.Deck.Deck;
import Model.Deck.ShuffleStrategies.FisherYatesStrategy;
import Model.Observers.ChipBalanceObserver;
import Model.Observers.ChipBalanceObserverImpl;
import Model.Observers.TablePrinter;
import Model.Observers.TableStats;
import Model.Strategies.dealer_strategies.DefaultDealerStrategy;
import Model.Strategies.player_strategies.OptimalNoCountingStrategy;
import Model.Table.ActionServices.ActionService;
import Model.Table.ActionServices.ActionServicePlayerImpl;
import Model.Table.BettingServices.BettingService;
import Model.Table.BettingServices.BettingServiceImpl;
import Model.Table.DealServices.DealServiceImpl;
import Model.Table.HandServices.HandServiceImpl;
import Model.Table.Hands.PlayerHand;
import Model.Table.PayoutServices.InsurancePayoutService;
import Model.Table.PayoutServices.StandardPayoutService;
import Model.Table.PositionService.PositionService;
import Model.Table.PositionService.PositionServiceImpl;
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

    @Getter private boolean isSimulation;
    @Getter private Deck deck;
    @Getter private Dealer dealer;
    @Getter private final DealerPosition dealerPosition;
    @Getter private ArrayList<Player> players;
    @Getter private final ArrayList<PlayerPosition> playerPositions;
    @Getter private ArrayList<PlayerHand> activeHands;
    @Getter private HashMap<Player, Double> playerBalances;
    @Getter private Double houseBalance;
    @Getter private ChipBalanceObserver chipBalanceObserver;
    @Getter private DealServiceImpl dealService;
    @Getter private HandServiceImpl handService;
    @Getter private StandardPayoutService standardPayoutService;
    @Getter private InsurancePayoutService insurancePayoutService;
    @Getter private ActionService actionService;
    @Getter private PositionService positionService;
    @Getter private BettingService bettingService;
    @Getter private TablePrinter tablePrinter;
    @Getter private TableStats tableStats;

    public Table(int playerCount, int deckCount, boolean isSimulation, TableStats tableStats) {
        this.isSimulation = isSimulation;
        this.tableStats = tableStats;
        this.deck = new Deck(deckCount, new FisherYatesStrategy());
        this.dealer = new Dealer(new DefaultDealerStrategy(), DEFAULT_DEALER_STARTING_CHIPS);
        this.players = new ArrayList<>();
        this.dealerPosition = new DealerPosition();
        this.playerPositions = new ArrayList<>();
        this.activeHands = new ArrayList<>();
        this.playerBalances = new HashMap<>();
        this.chipBalanceObserver = new ChipBalanceObserverImpl();
        this.tablePrinter = new TablePrinter(this);
        this.dealService = new DealServiceImpl();
        this.handService = new HandServiceImpl(tableStats);
        this.standardPayoutService = new StandardPayoutService(tableStats);
        this.insurancePayoutService = new InsurancePayoutService();
        this.actionService = new ActionServicePlayerImpl();
        this.positionService = new PositionServiceImpl();
        initPlayers(playerCount);
        positionService.createPlayerPositions(this.playerPositions);
        positionService.assignDefaultPlayerPositions(this.players, this.playerPositions);
        positionService.assignDealerPosition(this.dealer, this.dealerPosition);
        initBettingService();
    }

    /** Actions: checks if the deck requires a top-up, creates empty player hands at each position, creates an empty
     * dealer hand at the dealer position. */
    public void startupRoutine() {
        tablePrinter.printNewRoundMessage();
        this.houseBalance = chipBalanceObserver.logHouseBalance(dealer);
        this.playerBalances = chipBalanceObserver.logPlayerBalances(players);
        dealService.checkDeck(deck);
        handService.createPlayerHands(playerPositions);
        handService.createDealerHand(dealerPosition);
    }

    /** Actions: deals each player two initial cards, computes the hand values for all active hands, outputs the results. */
    public void drawRoutine() {
        handService.setActingPlayers(playerPositions);
        dealService.dealOpeningCards(deck, dealerPosition, playerPositions);
        this.activeHands = handService.setActiveHands(playerPositions);
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
        handService.clearPlayerHands(playerPositions);
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

    private void initBettingService() {
        this.bettingService = new BettingServiceImpl(isSimulation, players, playerPositions,
                new DoubleBetProcessorImpl(),
                new DoubleBetValidatorImpl(),
                new InsuranceBetProcessorImpl(),
                new InsuranceBetValidatorImpl(),
                new StandardBetProcessorImpl(),
                new StandardBetValidatorImpl(),
                new SplitBetProcessorImpl(),
                new SplitBetValidatorImpl());
    }

    public void handleDealerAction(String action) {
        if (action.equals(HIT)) {
            actionService.hit(deck, dealer.getPosition().getHand());
        }
    }

    public void handlePlayerAction(Player player, PlayerHand hand, String action) {
        switch (action) {
            case HIT:
                actionService.hit(deck, hand);
                break;
            case SPLIT:
                bettingService.splitHand(player, hand.getPosition(), hand, activeHands);
                tableStats.incrementHandCount();
                tableStats.incrementSplitCount();
                break;
            case DOUBLE:
                bettingService.bookDoubleDownBet(player, hand.getPosition(), hand);
                actionService.hit(deck, hand);
                break;
            case INSURANCE:
                bettingService.bookInsuranceBet(player, hand.getPosition(), hand, DEFAULT_PLAYER_INSURANCE_BET);
                break;
            case STAND: {}
        }
    }
}
