package model.Table.deal_services;

import java.util.List;
import model.deck.Deck;
import model.Table.hands.Hand;
import model.Table.hands.PlayerHand;
import model.Table.positions.DealerPosition;
import model.Table.positions.PlayerPosition;

public interface DealService {

    void dealOpeningCards(Deck deck, DealerPosition dealerPosition, List<PlayerPosition> activePositions);

    void dealToDealer(Deck deck, DealerPosition dealerPosition);

    void dealToActivePositions(Deck deck, List<PlayerPosition> activePositions);

    void calculateHandValues(List<PlayerHand> activeHands, DealerPosition dealerPosition);

    void checkDeck(Deck deck);

    boolean hit(Deck deck, Hand hand);
}
