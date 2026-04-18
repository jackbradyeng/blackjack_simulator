package Model.Observers;

import Model.Actors.Dealer;
import Model.Actors.Player;
import java.util.ArrayList;
import java.util.HashMap;

public interface ChipBalanceObserver {

    HashMap<Player, Double> logPlayerBalances(ArrayList<Player> players);
    Double logHouseBalance(Dealer dealer);
}
