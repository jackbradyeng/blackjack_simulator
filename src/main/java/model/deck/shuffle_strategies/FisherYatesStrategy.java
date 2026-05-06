package model.deck.shuffle_strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import model.cards.Card;
import static model.Constants.NUMBER_OF_CARDS_PER_DECK;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FisherYatesStrategy implements ShuffleStrategy {

    /** uses the Fisher-Yates shuffling algorithm for the whole deck stack. */
    public void shuffle(ArrayList<Card> deck, Random random, int copies) {

        for (int i = 0; i < copies * NUMBER_OF_CARDS_PER_DECK; i++) {
            int lower = 0;
            int upper = copies * NUMBER_OF_CARDS_PER_DECK;
            int pivot = random.nextInt(lower, upper);
            Collections.swap(deck, pivot, i);
        }
    }
}
