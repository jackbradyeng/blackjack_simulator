package Model.Cards;

import lombok.Data;
import lombok.Getter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static Model.Constants.ACE_UPPER_VALUE;

@Data
public class CardMapUtility {

    @Getter private static final ArrayList<String> suits = new ArrayList<>();
    @Getter private static final Map<String, Integer> cardValueMap = new HashMap<>();

    static {
        createSuits();
        mapCardValues();
    }

    private CardMapUtility() {}

    /** populates the suits list with each of the major suits. Used to streamline card creation process. */
    private static void createSuits() {
        suits.add("Hearts");
        suits.add("Diamonds");
        suits.add("Clubs");
        suits.add("Spades");
    }

    /** maps cards to their default values. By default, the ace is mapped to its higher value. */
    private static void mapCardValues() {
        for(String suit : suits) {
            CardMapUtility.cardValueMap.put("TwoOf" + suit, 2);
            CardMapUtility.cardValueMap.put("ThreeOf" + suit, 3);
            CardMapUtility.cardValueMap.put("FourOf" + suit, 4);
            CardMapUtility.cardValueMap.put("FiveOf" + suit, 5);
            CardMapUtility.cardValueMap.put("SixOf" + suit, 6);
            CardMapUtility.cardValueMap.put("SevenOf" + suit, 7);
            CardMapUtility.cardValueMap.put("EightOf" + suit, 8);
            CardMapUtility.cardValueMap.put("NineOf" + suit, 9);
            CardMapUtility.cardValueMap.put("TenOf" + suit, 10);
            CardMapUtility.cardValueMap.put("JackOf" + suit, 10);
            CardMapUtility.cardValueMap.put("QueenOf" + suit, 10);
            CardMapUtility.cardValueMap.put("KingOf" + suit, 10);
            CardMapUtility.cardValueMap.put("AceOf" + suit, ACE_UPPER_VALUE);
        }
    }
}
