package Model.Table.PositionService;

import java.util.List;
import Model.Actors.Dealer;
import Model.Actors.Player;
import Model.Table.Positions.DealerPosition;
import Model.Table.Positions.PlayerPosition;

public interface PositionService {

    void createPlayerPositions(List<PlayerPosition> playerPositions);
    void assignDefaultPlayerPositions(List<Player> players, List<PlayerPosition> playerPositions);
    void assignDealerPosition(Dealer dealer, DealerPosition dealerPosition);
}
