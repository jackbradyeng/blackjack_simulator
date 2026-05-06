package model.Table.deal_services;

import java.util.List;
import model.deck.Deck;
import model.Table.hands.Hand;
import model.Table.hands.PlayerHand;
import model.Table.positions.DealerPosition;
import model.Table.positions.PlayerPosition;
import static model.Constants.DEFAULT_NUMBER_OF_DECKS;
import static model.Constants.NEW_DECK_THRESHOLD;

public class DealServiceImpl implements DealService {

    /** deals first two cards to all active positions including the dealer. */
    @Override
    public void dealOpeningCards(Deck deck, DealerPosition dealerPosition, List<PlayerPosition> activePositions) {
        dealToActivePositions(deck, activePositions);
        dealToDealer(deck, dealerPosition);
        dealToActivePositions(deck, activePositions);
        dealToDealer(deck, dealerPosition);
    }

    @Override
    public void dealToDealer(Deck deck, DealerPosition dealerPosition) {
        deck.deal().ifPresent(deal -> dealerPosition.getHand().receiveCard(deal));
    }

    @Override
    public void dealToActivePositions(Deck deck, List<PlayerPosition> activePositions) {
        for (PlayerPosition position : activePositions) {
            for (PlayerHand hand : position.getHands()) {
                if (hand.hasBet()) {
                    deck.deal().ifPresent(hand::receiveCard);
                }
            }
        }
    }

    /** calculates the hand value for all active hands at the table. */
    @Override
    public void calculateHandValues(List<PlayerHand> activeHands, DealerPosition dealerPosition) {
        for (PlayerHand hand : activeHands) {
            hand.setHandValue();
        }
        dealerPosition.getHand().setHandValue();
    }

    /** checks to see how many cards remain in the deck and creates a new deck instance if the number is too low. */
    @Override
    public void checkDeck(Deck deck) {
        if(deck.getDeck().size() < NEW_DECK_THRESHOLD) {
            deck.createNewDeck(DEFAULT_NUMBER_OF_DECKS);
        }
    }

    @Override
    public boolean hit(Deck deck, Hand hand) {

        if (hand.isBust()) {
            return false;
        } else {
            deck.deal().ifPresent(deal -> {
                hand.receiveCard(deal);
                hand.setHandValue();
                hand.setHasHit(true);
            });
            return true;
        }
    }
}
