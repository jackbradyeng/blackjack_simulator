package model.observers;

import model.actors.Dealer;
import model.actors.Player;
import java.util.ArrayList;
import java.util.HashMap;

public interface ChipBalanceObserver {

    HashMap<Player, Double> logPlayerBalances(ArrayList<Player> players);
    Double logHouseBalance(Dealer dealer);
}
