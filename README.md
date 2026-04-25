# Blackjack Simulator

A Monte Carlo blackjack simulator and interactive CLI game written in Java. Runs large-scale simulations to measure strategy performance and house edge, or lets you play interactively against a dealer.

## Features

- **Two modes**: interactive play or automated Monte Carlo simulation (default: 100,000 iterations)
- **Optimal strategy**: mathematically correct play without card counting, using per-action lookup tables keyed on the dealer up-card
- **Full rule set**: splitting, doubling down, insurance, back-betting, and multi-hand support
- **Modular strategies**: swap player or dealer strategies independently
- **Configurable table**: deck count, player count, bet sizes, payout ratios, and more
- **Live statistics**: tracks win/loss/push/split rates, running profit, and expected value per hand

## Getting Started

**Prerequisites:** Java 11+, Maven

```bash
# Build
mvn clean install

# Run
java -cp target/classes Launcher
```

By default, the launcher runs in simulation mode. To switch to interactive mode, set `isSimulation = false` in `Launcher.java`.

## Configuration

All constants live in `src/main/java/Model/Constants.java`.

| Constant | Default | Description |
|---|---|---|
| `DEFAULT_NUMBER_OF_DECKS` | 4 | Decks in the shoe |
| `DEFAULT_NUMBER_OF_PLAYERS` | 1 | Players at the table |
| `DEFAULT_MIN_BET_SIZE` | 25 | Minimum bet |
| `DEFAULT_PLAYER_STARTING_CHIPS` | 500 | Starting chip balance |
| `DEFAULT_DEALER_STARTING_CHIPS` | 15,000 | House bankroll |
| `DEFAULT_BLACKJACK_PAYOUT_NUMERATOR/DENOMINATOR` | 3:2 | Blackjack payout ratio |
| `DEFAULT_INSURANCE_RATIO` | 3:1 | Insurance payout ratio |
| `DEFAULT_NUMBER_OF_ITERATIONS` | 100,000 | Simulation iterations |
| `DEFAULT_DEALER_DRAW_VALUE` | 17 | Dealer stands at this value |

For statistically reliable results, 500,000–1,000,000 iterations are recommended.

## Player Strategies

| Strategy | Description |
|---|---|
| `OptimalNoCountingStrategy` | Mathematically optimal play (default) |
| `OptimalNoCountingInsuranceStrategy` | Optimal play including insurance decisions |
| `CopyDealerStrategy` | Mirrors the dealer (hit < 17, stand >= 17) |

Strategies implement a common interface and are injected into the `Player`, making them easy to swap or extend.

## Project Structure

```
src/main/java/
├── Launcher.java                  # Entry point
├── Controller/                    # Game flow coordination
├── Exceptions/                    # DeckCountException, PlayerCountException
└── Model/
    ├── Constants.java             # All configuration values
    ├── Actors/                    # Player and Dealer
    ├── Cards/                     # Card and Ace types
    ├── Deck/                      # Shoe management
    │   └── ShuffleStrategies/     # Fisher-Yates shuffle implementations
    ├── Observers/                 # Stats tracking and console output
    ├── Orchestrators/             # Game mode orchestration (interactive vs simulation)
    ├── Strategies/
    │   ├── dealer_strategies/     # Dealer strategy interface and default implementation
    │   └── player_strategies/     # Optimal, copy-dealer, and insurance strategies
    └── Table/                     # Game orchestration
        ├── ActionServices/        # Hit, stand, split, double
        ├── BettingServices/       # Bet booking and validation
        ├── Bets/                  # Bet type definitions
        ├── DealServices/          # Card dealing
        ├── HandServices/          # Hand lifecycle management
        ├── Hands/                 # Player and dealer hand representations
        ├── PayoutServices/        # Win/loss/push payouts
        ├── Positions/             # Table seat management
        ├── PositionService/       # Position service interface and implementation
        ├── Processors/            # Per-bet-type processing
        │   ├── DoubleBetProcessors/
        │   ├── InsuranceBetProcessors/
        │   ├── SplitBetProcessors/
        │   └── StandardBetProcessors/
        └── Validators/            # Bet validation rules
            ├── DoubleBetValidators/
            ├── InsuranceBetValidators/
            ├── SplitBetValidators/
            └── StandardBetValidators/
```

## Running Tests

```bash
mvn test
```

## Simulation Results

  With optimal non-counting strategy, 4 decks, 3:2 blackjack payout, and a dealer that stands on hard 17:

- House edge: approximately 0.5–2%
- Expected value: roughly -0.50 to -2.00 per $100 wagered

## To Be Completed

- [x] Controller refactor
- [ ] Table printing refactor
- [ ] Testing suite refactor
- [ ] Parallelized simulations
- [ ] Player card counting strategies
