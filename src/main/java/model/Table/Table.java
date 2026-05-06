package model.Table;

import java.util.ArrayList;
import java.util.HashMap;
import model.actors.Dealer;
import model.actors.Player;
import model.deck.Deck;
import model.deck.shuffle_strategies.FisherYatesStrategy;
import model.observers.ChipBalanceObserver;
import model.observers.ChipBalanceObserverImpl;
import model.observers.TablePrinter;
import model.observers.TableStats;
import model.strategies.dealer_strategies.DefaultDealerStrategy;
import model.Table.betting_services.BettingService;
import model.Table.betting_services.BettingServiceImpl;
import model.Table.deal_services.DealServiceImpl;
import model.Table.hand_services.HandServiceImpl;
import model.Table.hands.PlayerHand;
import model.Table.payout_services.InsurancePayoutService;
import model.Table.payout_services.StandardPayoutService;
import model.Table.position_services.PositionService;
import model.Table.position_services.PositionServiceImpl;
import model.Table.positions.DealerPosition;
import model.Table.positions.PlayerPosition;
import static model.Constants.*;
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
    @Getter private PositionService positionService;
    @Getter private BettingService bettingService;
    @Getter private TablePrinter tablePrinter;
    @Getter private TableStats tableStats;

    public Table(ArrayList<Player> players, int deckCount, boolean isSimulation,
                 TablePrinter tablePrinter, TableStats tableStats) {

        this.isSimulation = isSimulation;
        this.tableStats = tableStats;
        this.tablePrinter = tablePrinter;
        this.players = players;
        this.deck = new Deck(deckCount, new FisherYatesStrategy());
        this.dealer = new Dealer(new DefaultDealerStrategy(), DEFAULT_DEALER_STARTING_CHIPS);
        this.dealerPosition = new DealerPosition();
        this.playerPositions = new ArrayList<>();
        this.activeHands = new ArrayList<>();
        this.playerBalances = new HashMap<>();
        this.chipBalanceObserver = new ChipBalanceObserverImpl();
        this.dealService = new DealServiceImpl();
        this.handService = new HandServiceImpl(tableStats);
        this.standardPayoutService = new StandardPayoutService(tableStats);
        this.insurancePayoutService = new InsurancePayoutService();
        this.positionService = new PositionServiceImpl();
        this.bettingService = new BettingServiceImpl(isSimulation, players, playerPositions);
        positionService.createPlayerPositions(this.playerPositions);
        positionService.assignDefaultPlayerPositions(this.players, this.playerPositions);
        positionService.assignDealerPosition(this.dealer, this.dealerPosition);
    }

    /** Actions: checks if the deck requires a top-up, creates empty player hands at each position, creates an empty
     * dealer hand at the dealer position. */
    public void startupRoutine() {
        if (!isSimulation) tablePrinter.printNewRoundMessage();
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
        if (!isSimulation) tablePrinter.printActivePlayerHands(this);
        if (!isSimulation) tablePrinter.printDealerFirstCard(this);
    }

    /** Actions: handles regular payouts, handles insurance payouts, and resets the game state in preparation for a new
     * round. */
    public void windDownRoutine() {
        standardPayoutService.process(activeHands, dealerPosition.getHand(), dealer);
        insurancePayoutService.process(activeHands, dealerPosition.getHand(), dealer);
        if (!isSimulation) tablePrinter.printHandResults(this);
        handService.clearActiveHands(activeHands);
        handService.clearPlayerHands(playerPositions);
        handService.clearDealerHand(dealerPosition);
    }

    public void handleDealerAction(String action) {
        if (action.equals(HIT)) {
            dealService.hit(deck, dealer.getPosition().getHand());
        }
    }

    public void handlePlayerAction(Player player, PlayerHand hand, String action) {
        switch (action) {
            case HIT:
                dealService.hit(deck, hand);
                break;
            case SPLIT:
                bettingService.splitHand(player, hand.getPosition(), hand, activeHands);
                tableStats.incrementHandCount();
                tableStats.incrementSplitCount();
                break;
            case DOUBLE:
                bettingService.bookDoubleDownBet(player, hand.getPosition(), hand);
                dealService.hit(deck, hand);
                break;
            case INSURANCE:
                bettingService.bookInsuranceBet(player, hand.getPosition(), hand, DEFAULT_PLAYER_INSURANCE_BET);
                break;
            case STAND: {}
        }
    }
}
