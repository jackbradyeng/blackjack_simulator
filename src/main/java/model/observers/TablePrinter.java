package model.observers;

import model.actors.Player;
import model.table.hands.PlayerHand;
import model.table.Table;
import java.util.List;
import static model.Constants.DEFAULT_COUNTDOWN_TIME;
import static model.Constants.DEFAULT_MIN_BET_SIZE;

public class TablePrinter {

    public void printWelcomeMessage() {
        String message =
                """
                ********************************
                ***** WELCOME TO BLACKJACK *****
                ********************************
                """;
        System.out.println(message);
    }

    public void printNewRoundMessage() {
        System.out.println("\n---- NEW ROUND ----");
    }

    public void printExitMessage() { System.out.println("Thanks for playing!"); }

    public void printBettingPrompt() { System.out.println("Would you like to place a bet? Enter Y/N."); }

    public void printFollowUpBettingPrompt() { System.out.println("Would you like to place another bet?"); }

    public void printInvalidInputPrompt() { System.out.println("Please enter a valid response."); }

    public void printBetSizePrompt(double chipCount) {
        System.out.println("Specify your bet size. You have %d chips. The minimum bet size is %d chips."
                .formatted((int) chipCount, DEFAULT_MIN_BET_SIZE));
    }

    public void printInsuranceBetSizePrompt(double chipCount, double maxInsuranceBet) {
        System.out.println("Specify your bet size. You have %d chips. The maximum bet size is %d chips."
                .formatted((int) chipCount, (int) maxInsuranceBet));
    }

    public void printBetPositionPrompt(int positionCount) {
        System.out.println("Which position would you like to bet on? There are %d total positions."
                .formatted(positionCount));
    }

    public void printInsuranceBetPrompt() { System.out.println("Would you like to buy insurance? Y/N"); }

    public void printPositionNumber(int positionNumber) {
        System.out.println("Position no. %d".formatted(positionNumber));
    }

    public void printActingPlayerPrompt(Player player) {
        System.out.println("Player %s to act. Select an action:".formatted(player.toString()));
    }

    public void printAvailableActions(List<String> actions) {
        System.out.println(String.join(" | ", actions));
    }

    public void printActivePlayerHands(Table table) {
        for (PlayerHand hand : table.getActiveHands()) {
            String output = """
                    Position: %d
                    ---- Hand: %s Hand Value: %s.
                    """.formatted(hand.getPosition().getPositionNumber(), hand, hand.getHandValue());
            System.out.println(output);
            if (hand.isBust()) {
                System.out.println("BUST!");
            }
        }
    }

    public void printDealerFirstCard(Table table) {
        var firstCard = table.getDealerPosition().getHand().getCards().getFirst();
        String output = """
                Position: 0 (Dealer)
                ---- Hand: %s Hand Value: %s.
                """.formatted(firstCard, firstCard.getValue());
        System.out.println(output);
    }

    public void printDealerHand(Table table) {
        var dealerHand = table.getDealerPosition().getHand();
        String output = """
                Position: 0 (Dealer)
                ---- Hand: %s Hand Value: %s.
                """.formatted(dealerHand, dealerHand.getHandValue());
        System.out.println(output);
        if (dealerHand.isBust()) {
            System.out.println("BUST!");
        }
    }

    public void printPlayerResults(Table table) {
        for (Player player : table.getPlayers()) {
            Double openingBalance = table.getPlayerBalances().get(player);
            String result = """
                    Player: %s
                    Starting Balance: %s
                    Closing Balance: %s
                    Profit (Loss): %s
                    """.formatted(player,
                    openingBalance.intValue(),
                    (int) player.getChips(),
                    (int) (player.getChips() - openingBalance));
            System.out.println(result);
        }
    }

    public void printHouseResults(Table table) {
        String houseResults = """
                Player: House
                Starting Balance: %s
                Closing Balance: %s
                Profit (Loss): %s
                """.formatted(table.getHouseBalance().intValue(),
                (int) table.getDealer().getChips(),
                (int) (table.getDealer().getChips() - table.getHouseBalance()));
        System.out.println(houseResults);
    }

    public void printHandResults(Table table) {
        System.out.println("---- RESULTS ----");
        printPlayerResults(table);
        printHouseResults(table);
        System.out.println("---- END OF ROUND ----");
    }

    /** prints summary statistics following a round of blackjack, including average profit per hand and the expected
     * value percentage. */
    public void printStatistics(int handNumber,
                                 TableStats tableStats) {

        int handCount = tableStats.getHandCount();

        String statsOverview = """
                
                ---- SUMMARY STATISTICS ----
                Hand No. : %s
                Blackjack Count : %s
                Blackjack Percentage : %s
                Win Count : %s
                Win Percentage : %s
                Loss Count : %s
                Loss Percentage : %s
                Push Count : %s
                Push Percentage : %s
                Split Count : %s
                Split Percentage : %s
                Running Profit (Loss) : %s
                Average Profit Per Hand : %s
                Expected Value Per Hand : %s
                """.formatted(handNumber,
                tableStats.getBlackjackCount(),
                ((double) tableStats.getBlackjackCount() / (double) handCount) * 100,
                tableStats.getPlayerWinCount(),
                ((double) tableStats.getPlayerWinCount() / (double) handCount) * 100,
                tableStats.getPlayerLossCount(),
                ((double) tableStats.getPlayerLossCount() / (double) handCount) * 100,
                tableStats.getPushCount(),
                ((double) tableStats.getPushCount() / (double) handCount) * 100,
                tableStats.getSplitCount(),
                ((double) tableStats.getSplitCount() /(double) handCount) * 100,
                tableStats.getRunningProfit(),
                tableStats.getProfitPerHand(),
                tableStats.getExpectedValuePerHand() * 100);

        System.out.println(statsOverview);
    }

    public void gamePause(String output) {
        System.out.println(output);
        threadSleep();
        System.out.println("3...");
        threadSleep();
        System.out.println("2...");
        threadSleep();
        System.out.println("1...");
    }

    private void threadSleep() {
        try {Thread.sleep(DEFAULT_COUNTDOWN_TIME);}
        catch (InterruptedException i) {Thread.currentThread().interrupt();}
    }

    /// SIMULATION MODE METHODS

    public void printProcessingTime(long seconds) {
        System.out.println("Total processing time: %d seconds."
                .formatted(seconds));
    }

    public void printPlayerStrategy(String playerStrategy) {
        System.out.println("---- PLAYER STRATEGY IS: %s ----"
                .formatted(playerStrategy));
    }
}
