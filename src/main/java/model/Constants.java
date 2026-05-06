package model;

public final class Constants {

    public Constants() {}

    // ACTOR CONSTANTS
    public static final int DEFAULT_NUMBER_OF_PLAYERS = 1;
    public static final int DEFAULT_PLAYER_STARTING_CHIPS = 500;
    public static final int DEFAULT_DEALER_STARTING_CHIPS = 15000;
    public static final int DEFAULT_DEALER_DRAW_VALUE = 17;

    // STRATEGY CONSTANTS
    public static final double DEFAULT_PLAYER_BET_AMOUNT = 25;
    public static final double DEFAULT_PLAYER_INSURANCE_BET = 10;

    // PLAYER COMMANDS
    public static final String QUIT = "QUIT";
    public static final String HIT = "HIT";
    public static final String STAND = "STAND";
    public static final String DOUBLE = "DOUBLE";
    public static final String SPLIT = "SPLIT";
    public static final String NO_SPLIT = "NO SPLIT";
    public static final String INSURANCE = "INSURANCE";
    public static final String YES_RESPONSE = "Y";
    public static final String NO_RESPONSE = "N";

    // DECK CONSTANTS
    public static final int DEFAULT_NUMBER_OF_DECKS = 4;
    public static final int BLACKJACK_CONSTANT = 21;
    public static final int NUMBER_OF_SUITS = 4;
    public static final int NUMBER_OF_CARDS_PER_SUIT = 13;
    public static final int NUMBER_OF_CARDS_PER_DECK = 52;
    public static final int NEW_DECK_THRESHOLD = 52;
    public static final String ACE = "Ace";

    // CARD VALUE CONSTANTS
    public static final int ACE_LOWER_VALUE = 1;
    public static final int ACE_UPPER_VALUE = 11;

    // TABLE CONSTANTS
    public static final int DEFAULT_TABLE_POSITIONS = 5; // excludes the dealer
    public static final int DEFAULT_MIN_BET_SIZE = 25;
    public static final int DEFAULT_PAYOUT_RATIO = 1;
    public static final int DEFAULT_BLACKJACK_PAYOUT_DENOMINATOR = 3;
    public static final int DEFAULT_BLACKJACK_PAYOUT_NUMERATOR = 2;
    public static final int DEFAULT_INSURANCE_RATIO = 3;

    // SIMULATION CONSTANTS
    public static final int DEFAULT_NUMBER_OF_ITERATIONS = 500_000;
    public static final int DEFAULT_COUNTDOWN_TIME = 1_000;
}
