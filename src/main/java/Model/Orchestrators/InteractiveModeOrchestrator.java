package Model.Orchestrators;

import Model.Actors.Player;
import Model.Observers.TablePrinter;
import Model.Observers.TableStats;
import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;
import Model.Table.Table;
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

    /** initializes the first round of betting. This is a non-parameterized method for regular command line
     * interactions. */
    private void initialWager(Table table, TablePrinter tablePrinter) {

        for (Player player : table.getPlayers()) {
            while (isRunning) {
                tablePrinter.printBettingPrompt();
                try {
                    String response = readInput();
                    if (processStandardBet(table, tablePrinter, player, response)) {break; }
                } catch (RuntimeException e) {
                    tablePrinter.printInvalidInputPrompt();
                }
            }

            while (isRunning) {
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
                    handleInsuranceCase(table, hand);
                    return false;
                } else return !playerAction.equalsIgnoreCase("N");
            } catch (RuntimeException e) {
                tablePrinter.printInvalidInputPrompt();
                return true;
            }
        }
    }

    /** handles cases where the player orders insurance in non-simulation games. */
    private void handleInsuranceCase(Table table, PlayerHand hand) {
        while (isRunning) {
            System.out.println("How much would you like to bet on insurance? The maximum size is the value of your" +
                    "initial bet.");
            try {
                double insuranceBet = scanner.nextDouble();
                table.getBettingService()
                        .bookInsuranceBet(hand.getActingPlayer(), hand.getPosition(), hand, insuranceBet);
                break;
            } catch (RuntimeException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    // core gameplay loop involving hitting, standing, splitting, and/or buying insurance
    private void playerActions(Table table, TablePrinter tablePrinter) {
        for (int i = 0; i < table.getActiveHands().size(); i++) {
            PlayerHand hand = table.getActiveHands().get(i);
            System.out.println("Position no. " + hand.getPosition().getPositionNumber());
            boolean playerCanAct = true;

            while (playerCanAct) {
                if (hand.isBust()) {
                    playerCanAct = false;
                } else if (hand.isBlackjack()) {
                    playerCanAct = handleBlackjackCase(table, tablePrinter, hand);
                } else {
                    System.out.println("Player " + hand.getActingPlayer() + " to act. Select an action:");
                    DealerHand dealerHand = table.getDealerPosition().getHand();
                    if (hand.hasSplitOption() && hand.hasInsuranceOption(dealerHand) && hand.isHasHit()) {
                        System.out.println("HIT | STAND | DOUBLE | SPLIT | INSURANCE");
                    } else if(!hand.hasSplitOption() && hand.hasInsuranceOption(dealerHand) && hand.isHasHit()) {
                        System.out.println("HIT | STAND | INSURANCE");
                    } else if (hand.hasSplitOption() && !hand.hasInsuranceOption(dealerHand) && hand.isHasHit()) {
                        System.out.println("HIT | STAND | DOUBLE | SPLIT");
                    } else if (!hand.hasSplitOption() && !hand.hasInsuranceOption(dealerHand) && hand.isHasHit()) {
                        System.out.println("HIT | STAND | DOUBLE");
                    } else if (hand.hasSplitOption() && hand.hasInsuranceOption(dealerHand)) {
                        System.out.println("HIT | STAND | SPLIT | INSURANCE");
                    } else if (hand.hasSplitOption() && !hand.hasInsuranceOption(dealerHand)) {
                        System.out.println("HIT | STAND | SPLIT");
                    } else if (!hand.hasSplitOption() && hand.hasInsuranceOption(dealerHand)) {
                        System.out.println("HIT | STAND | INSURANCE");
                    } else {
                        System.out.println("HIT | STAND");
                    }
                    String playerAction;
                    try {
                        playerAction = readInput().toUpperCase();

                        if (playerAction.equalsIgnoreCase(INSURANCE)) {
                            handleInsuranceCase(table, hand);
                        } else {
                            table.handlePlayerAction(hand.getActingPlayer(), hand, playerAction);
                            tablePrinter.printActivePlayerHands();
                            tablePrinter.printDealerFirstCard();
                        }
                        if (playerAction.equalsIgnoreCase(STAND)) {
                            playerCanAct = false;
                        } else if (playerAction.equalsIgnoreCase(DOUBLE)) {
                            playerCanAct = false;
                        }
                    } catch (RuntimeException e) {
                        System.out.println("Invalid input.");
                    }
                }
            }
        }
    }
}
