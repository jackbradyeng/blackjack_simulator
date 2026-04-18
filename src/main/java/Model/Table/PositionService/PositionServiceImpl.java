package Model.Table.PositionService;

import java.util.List;
import Model.Actors.Dealer;
import Model.Actors.Player;
import Model.Table.Positions.DealerPosition;
import Model.Table.Positions.PlayerPosition;
import static Model.Constants.DEFAULT_TABLE_POSITIONS;

public class PositionServiceImpl implements PositionService {

    @Override
    public void createPlayerPositions(List<PlayerPosition> playerPositions) {
        for (int i = 1; i < DEFAULT_TABLE_POSITIONS + 1; i++) {
            PlayerPosition p = new PlayerPosition(i);
            playerPositions.add(p);
        }
    }

    @Override
    public void assignDefaultPlayerPositions(List<Player> players, List<PlayerPosition> playerPositions) {
        if (players.size() == 1) {
            handleSinglePlayerCase(players, playerPositions);
        } else {
            handleMultiplayerCase(players, playerPositions);
        }
    }

    private void handleSinglePlayerCase(List<Player> players, List<PlayerPosition> playerPositions) {
        Player singlePlayer = players.getFirst();
        int middlePosition = DEFAULT_TABLE_POSITIONS / 2 + 1;
        PlayerPosition defaultPosition = playerPositions.get(middlePosition);
        // stores a default position reference in both the position and the player classes
        singlePlayer.setDefaultPosition(defaultPosition);
        defaultPosition.setDefaultPlayer(singlePlayer);
    }

    private void handleMultiplayerCase(List<Player> players, List<PlayerPosition> playerPositions) {
        // if multiplayer, allocate the players to seats at the table from right to left
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            PlayerPosition position = playerPositions.get(i);
            player.setDefaultPosition(position);
            position.setDefaultPlayer(player);
        }
    }

    @Override
    public void assignDealerPosition(Dealer dealer, DealerPosition dealerPosition) {
        dealer.setPosition(dealerPosition);
    }
}
