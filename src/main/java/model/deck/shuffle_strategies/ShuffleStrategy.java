package model.deck.shuffle_strategies;

import model.cards.Card;
import java.util.ArrayList;
import java.util.Random;

public interface ShuffleStrategy {

    void shuffle(ArrayList<Card> deck, Random random, int copies);
}
