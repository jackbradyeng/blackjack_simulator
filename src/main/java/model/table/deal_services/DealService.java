package model.table.deal_services;

import java.util.List;
import model.deck.Deck;
import model.table.hands.Hand;
import model.table.hands.PlayerHand;
import model.table.positions.DealerPosition;
import model.table.positions.PlayerPosition;

public interface DealService {

    void dealOpeningCards(Deck deck, DealerPosition dealerPosition, List<PlayerPosition> activePositions);

    void dealToDealer(Deck deck, DealerPosition dealerPosition);

    void dealToActivePositions(Deck deck, List<PlayerPosition> activePositions);

    void calculateHandValues(List<PlayerHand> activeHands, DealerPosition dealerPosition);

    void checkDeck(Deck deck);

    boolean hit(Deck deck, Hand hand);
}
