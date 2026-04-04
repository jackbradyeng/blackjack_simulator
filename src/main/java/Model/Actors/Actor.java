package Model.Actors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class Actor {

    private double chips;

    /** receipts a given number of chips to the actor's total. */
    public void receiveChips(double chips) {
        this.chips += chips;
    }

    /** dispenses a given number of chips from the actor's total. */
    public void dispenseChips(double chips) {
        this.chips -= chips;
    }
}
