package Model.Table.Hands;

import java.util.ArrayList;
import static Model.Constants.*;
import Model.Cards.Ace;
import Model.Cards.Card;
import lombok.Getter;
import lombok.Setter;

public class Hand {

    // stores the cards allocated to the hand
    @Getter
    protected ArrayList<Card> cards;

    // stores the numerical value of the hand
    @Getter
    protected int handValue;

    // stores whether the hand has been hit or not
    @Getter
    @Setter
    protected boolean hasHit = false;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(Card card : cards) {
            builder.append(card.getName());
            builder.append(" ");
        }
        return builder.toString();
    }

    /** adds a card to the hand. */
    public void receiveCard(Card card) {
        cards.add(card);
    }

    /** returns whether the hand is bust or not.*/
    public boolean isBust() {
        return handValue > BLACKJACK_CONSTANT;
    }

    /** returns whether the hand is a blackjack or not. */
    public boolean isBlackjack() {
        return handValue == BLACKJACK_CONSTANT;
    }

    public void setHandValue() {
        this.handValue = calculateHandValue();
    }

    /** returns whether the hand has an ace in it or not. */
    public boolean hasAce() {
        for (Card card : cards) {
            if (card instanceof Ace) {
                return true;
            }
        } return false;
    }

    /** calculates the final hand value. Sums the non-ace cards before considering aces. */
    private int calculateHandValue() {
        int handValue = 0;
        int aceCount = 0;

        // iterate over the cards in the hand and sum their values
        for (Card card : cards) {
            if (card instanceof Ace) {
                aceCount++;
                handValue += ACE_LOWER_VALUE; // count the ace using its lower value by default
            } else {
                handValue += card.getValue();
            }
        }

        // see if the ace can be "upgraded" to its upper value
        if (aceCount > 0 && handValue + (ACE_UPPER_VALUE - ACE_LOWER_VALUE) <= BLACKJACK_CONSTANT) {
            handValue += ACE_UPPER_VALUE - ACE_LOWER_VALUE;
        }
        return handValue;
    }
}
