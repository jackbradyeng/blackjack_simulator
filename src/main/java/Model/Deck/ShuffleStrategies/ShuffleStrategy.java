package Model.Deck.ShuffleStrategies;

import Model.Cards.Card;
import java.util.ArrayList;
import java.util.Random;

public interface ShuffleStrategy {

    void shuffle(ArrayList<Card> deck, Random random, int copies);
}
