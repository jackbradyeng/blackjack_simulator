package model.observers;

import model.actors.Dealer;
import model.actors.Player;
import java.util.ArrayList;
import java.util.HashMap;

public class ChipBalanceObserverImpl implements ChipBalanceObserver {

    @Override
    public HashMap<Player, Double> logPlayerBalances(ArrayList<Player> players) {
        HashMap<Player, Double> playerBalances = new HashMap<>();
        for (Player player : players) {
            playerBalances.put(player, player.getChips());
        }
        return playerBalances;
    }

    @Override
    public Double logHouseBalance(Dealer dealer) {
        return dealer.getChips();
    }
}