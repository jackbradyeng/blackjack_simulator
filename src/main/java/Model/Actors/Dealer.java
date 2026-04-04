package Model.Actors;

import Model.Actors.Strategies.DealerStrategy;
import Model.Table.Positions.DealerPosition;

public class Dealer extends Actor {

    /** both the dealer and the players store a reference to their allocated position as well as a strategy object.
     * This strategy class determines how the actor will behave in certain conditions. */
    private DealerPosition position;
    private DealerStrategy strategy;

    public Dealer(double startingChips) {
        super(startingChips);
        this.strategy = new DealerStrategy();
    }

    public int getHandValue() {
        return position.getHand().getHandValue();
    }

    public DealerPosition getPosition() {
        return position;
    }

    public void setPosition(DealerPosition position) {
        this.position = position;
    }

    /** executes the dealer's assigned strategy from within the dealer class. */
    public String executeStrategy() {
        return getStrategy().executeStrategy(position.getHand());
    }

    public DealerStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(DealerStrategy strategy) {
        this.strategy = strategy;
    }
}
