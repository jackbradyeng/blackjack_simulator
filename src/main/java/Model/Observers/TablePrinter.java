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

    // prints all active player hands at the table
    public void printActivePlayerHands() {
        for(PlayerHand hand : table.getActiveHands()) {
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
        System.out.println("----" + " Hand: " + table.getDealerHand().getCards().getFirst() + " Hand Value: " +
                table.getDealerHand().getCards().getFirst().getValue() + "." + "\n");
    }

    // print the dealer's hand
    public void printDealerHand() {
        System.out.println("Position: 0 (Dealer)");
        System.out.println("----" + " Hand: " + table.getDealerHand().toString() + " Hand Value: " +
                table.getDealerHand().getHandValue()
                + "." + "\n");
        if(table.getDealerHand().isBust()) {
            System.out.println("BUST!");
        }
    }

    public void printPlayerResults() {
        for(Player player : table.getPlayers()) {
            System.out.println("Player: " + player);
            System.out.println("Starting Balance: " + table.getPlayerBalances().get(player).intValue()
                    + " Closing Balance: " + (int) player.getChips());
            System.out.println("Profit (Loss): " + (int) (player.getChips() - table.getPlayerBalances().get(player))
                    + "\n");
        }
    }

    public void printHouseResults() {
        System.out.println("Player: House");
        System.out.println("Starting Balance: " + table.getHouseBalance().intValue() + " Closing Balance: "
                + (int) table.getDealer().getChips());
        System.out.println("Profit (Loss): " + (int) (table.getDealer().getChips() - table.getHouseBalance()) + "\n");
    }

    // prints results
    public void printHandResults() {
        System.out.println("---- RESULTS ----");
        printPlayerResults();
        printHouseResults();
        System.out.println("---- END OF ROUND ----");
    }
}
