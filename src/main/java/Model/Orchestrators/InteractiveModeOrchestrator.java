package Model.Orchestrators;

import Model.Actors.Player;
import Model.Observers.TablePrinter;
import Model.Observers.TableStats;
import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;
import Model.Table.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static Model.Constants.*;

public class InteractiveModeOrchestrator implements GameModeOrchestrator {

    private boolean isRunning = true;
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void runGame(Table table, TablePrinter tablePrinter, TableStats tableStats) {
        tablePrinter.printWelcomeMessage();

        while (isRunning) {
            table.startupRoutine();
            initialWager(table, tablePrinter);
            followUpWager(table, tablePrinter);
            table.drawRoutine();
            playerActions(table, tablePrinter);
            tablePrinter.gamePause("Dealer drawing in...");
            tablePrinter.printDealerHand();
            table.executeDealerStrategy();
            tablePrinter.gamePause("Printing results in...");
            table.windDownRoutine();
        }
        tablePrinter.printExitMessage();
    }

    private String readInput() {
        String input = scanner.next();
        if (input.equalsIgnoreCase(QUIT)) {
            isRunning = false;
        }
        return input;
    }

    private void initialWager(Table table, TablePrinter tablePrinter) {
        while (isRunning) {
            for (Player player : table.getPlayers()) {
                tablePrinter.printBettingPrompt();
                try {
                    String response = readInput();
                    if (processStandardBet(table, tablePrinter, player, response)) {
                        break;
                    }
                } catch (RuntimeException e) {
                    tablePrinter.printInvalidInputPrompt();
                }
            }
        }
    }

    private void followUpWager(Table table, TablePrinter tablePrinter) {
        while (isRunning) {
            for (Player player : table.getPlayers()) {
                tablePrinter.printFollowUpBettingPrompt();
                try {
                    String response = readInput();
                    if (response.equalsIgnoreCase("N")) {
                        break;
                    } else {
                        processStandardBet(table, tablePrinter, player, response);
                    }
                } catch (RuntimeException e) {
                    tablePrinter.printInvalidInputPrompt();
                }
            }
        }
    }

    /** processes a standard bet during the initial wager phase. */
    private boolean processStandardBet(Table table, TablePrinter tablePrinter, Player player, String action) {
        if (action.equalsIgnoreCase("Y")) {
            tablePrinter.printBetSizePrompt(player.getChips());
            double playerBet = scanner.nextDouble();
            tablePrinter.printBetPositionPrompt(table.getPlayerPositions().size());
            int position = scanner.nextInt();
            table.getBettingService().bookStandardBet(player, table.getPlayerPositions().get(position - 1), playerBet);
            return true;
        } else return action.equalsIgnoreCase("N");
    }

    /** handles cases where the player has a natural blackjack in non-simulation games. */
    private boolean handleBlackjackCase(Table table, TablePrinter tablePrinter, PlayerHand hand) {
        if (!hand.hasInsuranceOption(table.getDealerPosition().getHand())) {
            return false;
        } else {
            tablePrinter.printInsuranceBetPrompt();
            try {
                String playerAction = readInput();
                if (playerAction.equalsIgnoreCase("Y")) {
                    handleInsuranceCase(table, tablePrinter, hand);
                    return false;
                } else return !playerAction.equalsIgnoreCase("N");
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
                double insuranceBet = scanner.nextDouble();
                table.getBettingService()
                        .bookInsuranceBet(hand.getActingPlayer(), hand.getPosition(), hand, insuranceBet);
                break;
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
                if (hand.isBust()) {
                    playerCanAct = false;
                }
                if (hand.isBlackjack()) {
                    playerCanAct = handleBlackjackCase(table, tablePrinter, hand);
                }
                determinePlayerOptions(table, tablePrinter, hand);
                String playerAction;
                try {
                    playerAction = readInput().toUpperCase();
                    if (playerAction.equalsIgnoreCase(INSURANCE)) {
                        handleInsuranceCase(table, tablePrinter, hand);
                    } else {
                        table.handlePlayerAction(hand.getActingPlayer(), hand, playerAction);
                        tablePrinter.printActivePlayerHands();
                        tablePrinter.printDealerFirstCard();
                    }
                    if ((playerAction.equalsIgnoreCase(STAND)) || (playerAction.equalsIgnoreCase(DOUBLE))) {
                        playerCanAct = false;
                    }
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
