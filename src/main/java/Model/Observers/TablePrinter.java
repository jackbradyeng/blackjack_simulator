package Model.Observers;

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
        System.out.println("********************************");

        System.out.println("***** WELCOME TO BLACKJACK *****");

        System.out.println("********************************");
    }

    public void printNewRoundMessage() {
        System.out.print("\n");
        System.out.println("---- NEW ROUND ----");
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
}
