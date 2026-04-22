package Model.Observers;

import Model.Actors.Player;
import Model.Table.Hands.PlayerHand;
import Model.Table.Table;
import lombok.AllArgsConstructor;
import static Model.Constants.DEFAULT_COUNTDOWN_TIME;

@AllArgsConstructor
public class TablePrinter {

    private Table table;

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

    public void printInvalidInputPrompt() { System.out.println("Please enter a valid response: Y/N."); }

    public void printActivePlayerHands() {
        for (PlayerHand hand : table.getActiveHands()) {
            System.out.println("Position: " + hand.getPosition().getPositionNumber());
            System.out.println("---- Hand: " + hand + " Hand value: " + hand.getHandValue() + ".");
            if (hand.isBust()) {
                System.out.println("BUST!");
            }
        }
    }

    public void printDealerFirstCard() {
        var firstCard = table.getDealerPosition().getHand().getCards().getFirst();
        String output = """
                Position: 0 (Dealer)
                ---- Hand: %s Hand Value: %s.
                """.formatted(firstCard, firstCard.getValue());
        System.out.println(output);
    }

    public void printDealerHand() {
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

    public void printPlayerResults() {
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

    public void printHouseResults() {
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

    public void printHandResults() {
        System.out.println("---- RESULTS ----");
        printPlayerResults();
        printHouseResults();
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

    public void printProcessingTime(long seconds) {
        System.out.println("Total processing time: " + seconds + " seconds.");
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
}
