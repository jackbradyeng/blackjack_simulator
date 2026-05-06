package model.actors;

import model.strategies.dealer_strategies.DealerStrategy;
import model.table.positions.DealerPosition;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Dealer extends Actor {

    /** both the dealer and the players store a reference to their allocated position as well as a strategy object.
     * This strategy class determines how the actor will behave in certain conditions. */
    private DealerPosition position;
    private DealerStrategy strategy;

    public Dealer(DealerStrategy strategy, double startingChips) {
        super(startingChips);
        this.strategy = strategy;
    }

    /** executes the dealer's assigned strategy from within the dealer class. */
    public String executeStrategy() {
        return getStrategy().executeStrategy(position.getHand());
    }
}
