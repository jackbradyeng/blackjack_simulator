package Model.Table.HandServices;

import Model.Observers.TableStats;
import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;
import Model.Table.Positions.DealerPosition;
import Model.Table.Positions.PlayerPosition;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class HandServiceImpl implements HandService {

    private TableStats tableStats;

    @Override
    public void createPlayerHands(List<PlayerPosition> playerPositions) {
        for (PlayerPosition position : playerPositions) {
            PlayerHand emptyHand = new PlayerHand(position);
            position.getHands().add(emptyHand);
        }
    }

    @Override
    public void createDealerHand(DealerPosition dealerPosition) {
        DealerHand dealerHand = new DealerHand();
        dealerPosition.setHand(dealerHand);
    }

    @Override
    public ArrayList<PlayerHand> setActiveHands(List<PlayerPosition> playerPositions) {
        ArrayList<PlayerHand> activeHands = new ArrayList<>();
        for (PlayerPosition position : playerPositions) {
            for (PlayerHand hand : position.getHands()) {
                if (hand.hasBet()) {
                    activeHands.add(hand);
                    tableStats.incrementHandCount();
                }
            }
        }
        return activeHands;
    }

    /** sets the acting player for each hand at the table. This should usually be the default player. But if the
     * default player has not bet on their own position, then the acting player is simply the first to have bet on that
     * position. */
    @Override
    public void setActingPlayers(List<PlayerPosition> playerPositions) {
        for (PlayerPosition position : playerPositions) {
            for (PlayerHand hand : position.getHands()) {
                if (!hand.hasBet())
                    continue;
                if (position.isDefaultPlayerInHand()) {
                    hand.setActingPlayer(position.getDefaultPlayer());
                } else {
                    hand.setActingPlayer(hand.getPairs().getFirst().getKey());
                }
            }
        }
    }

    @Override
    public void clearActiveHands(List<PlayerHand> activeHands) {
        activeHands.clear();
    }

    /** initializes an empty hand for each position at the table. Required before dealing cards. */
    @Override
    public void clearPlayerHands(List<PlayerPosition> playerPositions) {
        for (PlayerPosition position : playerPositions) {
            position.clearHands();
        }
    }

    /** initializes an empty hand for the dealer. */
    @Override
    public void clearDealerHand(DealerPosition dealerPosition) {
        dealerPosition.clearHand();
    }
}
