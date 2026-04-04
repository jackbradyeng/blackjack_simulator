package Model.Actors.Strategies.player_strategies;

import Model.Cards.Card;
import Model.Table.Hands.DealerHand;
import Model.Table.Hands.PlayerHand;
import static Model.Constants.*;
import java.util.HashMap;
import java.util.Map;

public class OptimalStrategyUtility {

    // player hard value -> dealer hard value map
    private static final HashMap<Map.Entry<Integer, Integer>, String> hardValuesActionTable = new HashMap<>();
    private static final HashMap<Map.Entry<Integer, Integer>, String> softValuesActionTable = new HashMap<>();
    private static final HashMap<Map.Entry<Map.Entry<Integer, Integer>, Integer>, String> splitActionTable = new HashMap<>();

    static {
        populateHardValueTable();
        populateSoftValueTable();
        populateSplittingTable();
    }

    private OptimalStrategyUtility() {}

    /** returns the player action based on the hard-values strategy table. */
    public static String executeHardValuesStrategy(PlayerHand playerHand, DealerHand dealerHand) {
        int playerHV = playerHand.getHandValue();
        int dealerHV = dealerHand.getCards().getFirst().getValue();
        Map.Entry<Integer, Integer> entry = Map.entry(playerHV, dealerHV);
        return hardValuesActionTable.get(entry);
    }

    /** returns the player action based on the soft-values strategy table. */
    public static String executeSoftValuesStrategy(PlayerHand playerHand, DealerHand dealerHand) {
        int playerSV = playerHand.getHandValue();
        int dealerSV = dealerHand.getCards().getFirst().getValue();
        Map.Entry<Integer, Integer> entry = Map.entry(playerSV, dealerSV);
        return softValuesActionTable.get(entry);
    }

    /** returns the player action based on the splitting strategy table. */
    public static String executeSplittingStrategy(PlayerHand playerHand, DealerHand dealerHand) {
        Card first = playerHand.getCards().get(0);
        Card second = playerHand.getCards().get(1);
        Card dealerFirst = dealerHand.getCards().getFirst();
        return splitActionTable.get(Map.entry(Map.entry(first.getValue(), second.getValue()), dealerFirst.getValue()));
    }

    /** determines player behavior based on hard-value comparisons (i.e. neglecting the flexibility of the Ace). */
    private static void populateHardValueTable() {
        for (int playerHV = 2; playerHV <= BLACKJACK_CONSTANT; playerHV++) {
            for (int dealerHV = 2; dealerHV <= ACE_UPPER_VALUE; dealerHV++) {
                Map.Entry<Integer, Integer> entry = Map.entry(playerHV, dealerHV);
                // always hit if the player's hand value is less than 9
                if (playerHV < 9) {
                    hardValuesActionTable.put(entry, HIT);
                } else if(playerHV == 9) {
                    if(dealerHV == 3 || dealerHV == 4 || dealerHV == 5 || dealerHV == 6) {
                        hardValuesActionTable.put(entry, DOUBLE);
                    } else {
                        hardValuesActionTable.put(entry, HIT);
                    }
                } else if(playerHV == 10) {
                    if(dealerHV < 10) {
                        hardValuesActionTable.put(entry, DOUBLE);
                    } else {
                        hardValuesActionTable.put(entry, HIT);
                    }
                } else if(playerHV == 11) {
                    hardValuesActionTable.put(entry, DOUBLE);
                } else if(playerHV == 12) {
                    if(dealerHV == 4 || dealerHV == 5 || dealerHV == 6) {
                        hardValuesActionTable.put(entry, STAND);
                    } else {
                        hardValuesActionTable.put(entry, HIT);
                    }
                } else if(playerHV < 17) {
                    if(dealerHV < 7) {
                        hardValuesActionTable.put(entry, STAND);
                    } else {
                        hardValuesActionTable.put(entry, HIT);
                    }
                } else {
                    hardValuesActionTable.put(entry, STAND);
                }
            }
        }
    }

    /** determines player behavior based on soft-value comparisons (taking account of the ace card's flexibility). */
    private static void populateSoftValueTable() {
        for (int playerSV = ACE_UPPER_VALUE + 2; playerSV <= ACE_UPPER_VALUE + 10; playerSV++) {
            for (int dealerSV = 2; dealerSV <= ACE_UPPER_VALUE; dealerSV++) {
                Map.Entry<Integer, Integer> entry = Map.entry(playerSV, dealerSV);
                if(playerSV == ACE_UPPER_VALUE + 2 || playerSV == ACE_UPPER_VALUE + 3) {
                    if(dealerSV != 5 && dealerSV != 6) {
                        softValuesActionTable.put(entry, HIT);
                    } else {
                        softValuesActionTable.put(entry, DOUBLE);
                    }
                } else if(playerSV == ACE_UPPER_VALUE + 4 || playerSV == ACE_UPPER_VALUE + 5) {
                    if(dealerSV != 4 && dealerSV != 5 && dealerSV != 6) {
                        softValuesActionTable.put(entry, HIT);
                    } else {
                        softValuesActionTable.put(entry, DOUBLE);
                    }
                } else if(playerSV == ACE_UPPER_VALUE + 6) {
                    if(dealerSV > 7 || dealerSV == 2) {
                        softValuesActionTable.put(entry, HIT);
                    } else {
                        softValuesActionTable.put(entry, DOUBLE);
                    }
                } else if(playerSV == ACE_UPPER_VALUE + 7) {
                    if(dealerSV < 7) {
                        softValuesActionTable.put(entry, DOUBLE);
                    } else if(dealerSV == 7 || dealerSV == 8) {
                        softValuesActionTable.put(entry, STAND);
                    } else {
                        softValuesActionTable.put(entry, HIT);
                    }
                } else {
                    softValuesActionTable.put(entry, STAND);
                }
            }
        }
    }

    /** for pairs where the first card's value equals the second, determines whether the player should split or not
     * based on the value of the dealer's face-up card. Note: This method does specific the action to be taken if the
     * decision is not to split. This must be handled by another method. */
    private static void populateSplittingTable() {
        for (int first = 2; first <= ACE_UPPER_VALUE; first++) {
            for (int dealerValue = 2; dealerValue <= ACE_UPPER_VALUE; dealerValue++) {
                Map.Entry<Map.Entry<Integer, Integer>, Integer> entry = Map.entry(Map.entry(first, first), dealerValue);

                if (first == 2 || first == 3) {
                    if (dealerValue <= 7) {
                        splitActionTable.put(entry, SPLIT);
                    } else {
                        splitActionTable.put(entry, NO_SPLIT);
                    }
                }
                // never split these hands
                else if (first == 4 || first == 5 || first == 10) {
                    splitActionTable.put(entry, NO_SPLIT);
                } else if (first == 6) {
                    if (dealerValue <= 6) {
                        splitActionTable.put(entry, SPLIT);
                    } else {
                        splitActionTable.put(entry, NO_SPLIT);
                    }
                } else if (first == 7) {
                    if (dealerValue <= 7) {
                        splitActionTable.put(entry, SPLIT);
                    } else {
                        splitActionTable.put(entry, NO_SPLIT);
                    }
                }
                // always split these hands
                else if (first == 8 || first == ACE_UPPER_VALUE) {
                    splitActionTable.put(entry, SPLIT);
                } else {
                    if (dealerValue <= 9) {
                        splitActionTable.put(entry, SPLIT);
                    } else {
                        splitActionTable.put(entry, NO_SPLIT);
                    }
                }
            }
        }
    }
}
