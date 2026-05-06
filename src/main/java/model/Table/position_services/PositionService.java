package model.Table.position_services;

import java.util.List;
import model.actors.Dealer;
import model.actors.Player;
import model.Table.positions.DealerPosition;
import model.Table.positions.PlayerPosition;

public interface PositionService {

    void createPlayerPositions(List<PlayerPosition> playerPositions);
    void assignDefaultPlayerPositions(List<Player> players, List<PlayerPosition> playerPositions);
    void assignDealerPosition(Dealer dealer, DealerPosition dealerPosition);
}
