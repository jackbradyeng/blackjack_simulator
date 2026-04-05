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
        for(Card card : cards) {
            if(card instanceof Ace) {
                return true;
            }
        } return false;
    }

    /** calculates the final hand value. Sums the non-ace cards before considering aces. */
    private int calculateHandValue() {
        int handValue = 0;
        int aceCount = 0;

        // first sweep, sums the total of non-Ace cards
        for(Card card : cards) {
            if(!(card instanceof Ace)) {
                handValue += card.getValue();
            }
        }

        // second sweep, sums the total of Ace cards
        for(Card card : cards) {
            if(card instanceof Ace) {
                if(aceCount > 0) {
                    if(handValue + ACE_UPPER_VALUE < BLACKJACK_CONSTANT) {
                        handValue += ACE_UPPER_VALUE;
                    } else {
                        handValue += ACE_LOWER_VALUE;
                    }
                } else if(handValue < ACE_UPPER_VALUE) {
                    handValue += ACE_UPPER_VALUE;
                } else {
                    handValue += ACE_LOWER_VALUE;
                }
                /* if an ace has already been counted then any subsequent aces need to be assigned their lower value
                rather than their higher value. */
                aceCount++;
            }
        }
        return handValue;
    }
}
