package Model.Table.DealServices;

import java.util.List;
import Model.Deck.Deck;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.DealerPosition;
import Model.Table.Positions.PlayerPosition;

public interface DealService {

    void dealOpeningCards(Deck deck, DealerPosition dealerPosition, List<PlayerPosition> activePositions);

    void dealToDealer(Deck deck, DealerPosition dealerPosition);

    void dealToActivePositions(Deck deck, List<PlayerPosition> activePositions);

    void calculateHandValues(List<PlayerHand> activeHands, DealerPosition dealerPosition);
}
