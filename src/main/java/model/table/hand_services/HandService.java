package model.table.hand_services;

import model.table.hands.PlayerHand;
import model.table.positions.DealerPosition;
import model.table.positions.PlayerPosition;
import java.util.List;

public interface HandService {

    void createPlayerHands(List<PlayerPosition> playerPositions);

    void createDealerHand(DealerPosition dealerPosition);

    List<PlayerHand> setActiveHands(List<PlayerPosition> playerPositions);

    void setActingPlayers(List<PlayerPosition> playerPositions);

    void clearActiveHands(List<PlayerHand> activeHands);

    void clearPlayerHands(List<PlayerPosition> playerPositions);

    void clearDealerHand(DealerPosition dealerPosition);
}
