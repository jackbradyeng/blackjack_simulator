package model.orchestrators;

import model.actors.Player;
import model.observers.TablePrinter;
import model.observers.TableStats;
import model.orchestrators.actor_strategy_orchestrators.DealerStrategyOrchestrator;
import model.Table.hands.DealerHand;
import model.Table.hands.PlayerHand;
import model.Table.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import exceptions.QuitException;
import static model.Constants.*;

public class InteractiveModeOrchestrator implements GameModeOrchestrator {

    private boolean isRunning = true;
    private final Scanner scanner;
    private final DealerStrategyOrchestrator dealerStrategyOrchestrator;

    public InteractiveModeOrchestrator() {
        this(new Scanner(System.in), new DealerStrategyOrchestrator());
    }

    // package private constructor for testing only
    InteractiveModeOrchestrator(Scanner scanner, DealerStrategyOrchestrator dealerStrategyOrchestrator) {
        this.scanner = scanner;
        this.dealerStrategyOrchestrator = dealerStrategyOrchestrator;
    }

    @Override
    public void runGame(Table table, TablePrinter tablePrinter, TableStats tableStats) {
        tablePrinter.printWelcomeMessage();

        try {
            while (isRunning) {
                table.startupRoutine();
                initialWager(table, tablePrinter);
                followUpWager(table, tablePrinter);
                table.drawRoutine();
                playerActions(table, tablePrinter);
                tablePrinter.gamePause("Dealer drawing in...");
                tablePrinter.printDealerHand(table);
                dealerStrategyOrchestrator.executeDealerStrategy(table, tablePrinter, false);
                tablePrinter.gamePause("Printing results in...");
                table.windDownRoutine();
            }
        } catch (QuitException ignored) {}
        tablePrinter.printExitMessage();
    }

    private String readInput() {
        String input = scanner.next();
        if (input.equalsIgnoreCase(QUIT)) {
            isRunning = false;
            throw new QuitException();
        }
        return input;
    }

    private double readDouble() { return Double.parseDouble(readInput()); }

    private int readInt() { return Integer.parseInt(readInput()); }

    private void initialWager(Table table, TablePrinter tablePrinter) {
        boolean initialWager = true;
        while (initialWager) {
            for (Player player : table.getPlayers()) {
                try {
                    tablePrinter.printBettingPrompt();
                    String response = readInput();
                    if (processStandardBet(table, tablePrinter, player, response)) {
                        initialWager = false;
                    }
                } catch (QuitException e) {
                    throw e;
                } catch (RuntimeException e) {
                    tablePrinter.printInvalidInputPrompt();
                }
            }
        }
    }

    private void followUpWager(Table table, TablePrinter tablePrinter) {
        boolean followUpWager = true;
        while (followUpWager) {
            for (Player player : table.getPlayers()) {
                tablePrinter.printFollowUpBettingPrompt();
                try {
                    String response = readInput();
                    if (response.equalsIgnoreCase(NO_RESPONSE)) {
                        followUpWager = false;
                    } else {
                        processStandardBet(table, tablePrinter, player, response);
                    }
                } catch (QuitException e) {
                    throw e;
                } catch (RuntimeException e) {
                    tablePrinter.printInvalidInputPrompt();
                }
            }
        }
    }

    /** processes a standard bet during the initial wager phase. */
    private boolean processStandardBet(Table table, TablePrinter tablePrinter, Player player, String action) {
        if (action.equalsIgnoreCase(YES_RESPONSE)) {
            tablePrinter.printBetSizePrompt(player.getChips());
            double playerBet = readDouble();
            tablePrinter.printBetPositionPrompt(table.getPlayerPositions().size());
            int position = readInt();
            return table.getBettingService().bookStandardBet(player, table.getPlayerPositions().get(position - 1), playerBet);
        } else return action.equalsIgnoreCase(NO_RESPONSE);
    }

    /** handles cases where the player has a natural blackjack in non-simulation games. */
    private boolean blackjackHandleInsuranceOption(Table table, TablePrinter tablePrinter, PlayerHand hand) {
        if (!hand.hasInsuranceOption(table.getDealerPosition().getHand())) {
            return false;
        } else {
            tablePrinter.printInsuranceBetPrompt();
            try {
                String playerAction = readInput();
                if (playerAction.equalsIgnoreCase(YES_RESPONSE)) {
                    handleInsuranceCase(table, tablePrinter, hand);
                    return false;
                } else return !playerAction.equalsIgnoreCase(NO_RESPONSE);
            } catch (QuitException e) {
                throw e;
            } catch (RuntimeException e) {
                tablePrinter.printInvalidInputPrompt();
                return true;
            }
        }
    }

    /** handles cases where the player orders insurance in non-simulation games. */
    private void handleInsuranceCase(Table table, TablePrinter tablePrinter, PlayerHand hand) {
        while (isRunning) {
            tablePrinter.printInsuranceBetSizePrompt(
                    hand.getActingPlayer().getChips(),
                    hand.getPairs().getFirst().getValue().getAmount()
            );
            try {
                double insuranceBet = readDouble();
                table.getBettingService()
                        .bookInsuranceBet(hand.getActingPlayer(), hand.getPosition(), hand, insuranceBet);
                break;
            } catch (QuitException e) {
                throw e;
            } catch (RuntimeException e) {
                tablePrinter.printInvalidInputPrompt();
            }
        }
    }

    // core gameplay loop involving hitting, standing, splitting, and/or buying insurance
    private void playerActions(Table table, TablePrinter tablePrinter) {
        for (int i = 0; i < table.getActiveHands().size(); i++) {
            PlayerHand hand = table.getActiveHands().get(i);
            tablePrinter.printPositionNumber(hand.getPosition().getPositionNumber());
            boolean playerCanAct = true;

            while (playerCanAct) {
                if (hand.isBust()) { break; }
                if (hand.isBlackjack()) {
                    playerCanAct = blackjackHandleInsuranceOption(table, tablePrinter, hand);
                    continue;
                }
                determinePlayerOptions(table, tablePrinter, hand);
                try {
                    String playerAction = readInput().toUpperCase();
                    if (playerAction.equalsIgnoreCase(INSURANCE)) {
                        handleInsuranceCase(table, tablePrinter, hand);
                    } else {
                        table.handlePlayerAction(hand.getActingPlayer(), hand, playerAction);
                        tablePrinter.printActivePlayerHands(table);
                        tablePrinter.printDealerFirstCard(table);
                    }
                    if ((playerAction.equalsIgnoreCase(STAND)) || (playerAction.equalsIgnoreCase(DOUBLE))) {
                        playerCanAct = false;
                    }
                } catch (QuitException e) {
                    throw e;
                } catch (RuntimeException e) {
                    tablePrinter.printInvalidInputPrompt();
                }
            }
        }
    }

    /** determines and prints the player's options given the table and hand states. */
    private void determinePlayerOptions(Table table, TablePrinter tablePrinter, PlayerHand hand) {
        tablePrinter.printActingPlayerPrompt(hand.getActingPlayer());
        DealerHand dealerHand = table.getDealerPosition().getHand();
        List<String> actions = new ArrayList<>(List.of(HIT, STAND));
        if (!hand.isHasHit() && !hand.hasInsuranceOption(dealerHand)) actions.add(DOUBLE);
        if (hand.hasSplitOption()) actions.add(SPLIT);
        if (hand.hasInsuranceOption(dealerHand)) actions.add(INSURANCE);
        tablePrinter.printAvailableActions(actions);
    }
}
