package model.deck.shuffle_strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import model.cards.Card;
import static model.Constants.NUMBER_OF_CARDS_PER_DECK;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ModifiedFisherYatesStrategy implements ShuffleStrategy {

    /** uses a modified version of the Fisher-Yates shuffling algorithm. Fisher-Yates involves iterating over a set of
     * integers, and for each integer, selecting a random index in the iteration range, and swapping it with the
     * current integer. Essentially, this method performs Fisher-Yates but for each of the decks one by one, maintaining
     * their respective boundaries. This is how Blackjack decks are designed to be shuffled, since it minimizes the
     * chance of duplicate cards being dealt in a single hand. */
    public void shuffle(ArrayList<Card> deck, Random random, int copies) {

        for (int i = copies; i > 0; i--) {
            for (int j = NUMBER_OF_CARDS_PER_DECK; j > 0; j--) {
                int upper = (i * NUMBER_OF_CARDS_PER_DECK) - 1;
                int lower = ((i - 1) * NUMBER_OF_CARDS_PER_DECK);
                int index = random.nextInt(lower, upper);
                Collections.swap(deck, index, (i * j) - 1);
            }
        }
    }
}
