package Model.Observers;

import Model.Actors.Player;
import Model.Table.Hands.PlayerHand;
import Model.Table.Table;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TablePrinter implements GameEventListener {

    private Table table;

    @Override
    public void onRoundStart() {

    }

    @Override
    public void onHandResult() {

    }

    @Override
    public void onPlayerWin() {

    }

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
        var firstCard = table.getDealerHand().getCards().getFirst();
        String output = """
                Position: 0 (Dealer)
                ---- Hand: %s Hand Value: %s.
                """.formatted(firstCard, firstCard.getValue());
        System.out.println(output);
    }

    public void printDealerHand() {
        var dealerHand = table.getDealerHand();
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
                    """.formatted(player, openingBalance.intValue(), (int) player.getChips(),
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
                (int) table.getDealer().getChips() - table.getHouseBalance());
        System.out.println(houseResults);
    }

    // prints results
    public void printHandResults() {
        System.out.println("---- RESULTS ----");
        printPlayerResults();
        printHouseResults();
        System.out.println("---- END OF ROUND ----");
    }
}
