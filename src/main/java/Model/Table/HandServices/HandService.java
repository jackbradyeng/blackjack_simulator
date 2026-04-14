package Model.Table.HandServices;

import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.DealerPosition;
import Model.Table.Positions.PlayerPosition;
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
