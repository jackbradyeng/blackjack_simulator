# Blackjack Simulator

A Monte Carlo blackjack simulator and interactive CLI game written in Java. Runs large-scale simulations to measure strategy performance and house edge, or lets you play interactively against a dealer.

## Features

- **Two modes**: interactive play or automated Monte Carlo simulation (default: 100,000 iterations)
- **Optimal strategy**: mathematically correct play without card counting, using per-action lookup tables keyed on the dealer up-card
- **Full rule set**: splitting, doubling down, insurance, back-betting, and multi-hand support
- **Modular actor_strategies**: swap player or dealer actor_strategies independently
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

All constants live in `src/main/java/model/Constants.java`.

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
├── controller/                    # Game flow coordination
├── exceptions/                    # DeckCountException, PlayerCountException
└── model/
    ├── Constants.java             # All configuration values
    ├── actors/                    # Player and Dealer
    ├── cards/                     # Card and Ace types
    ├── deck/                      # Shoe management
    │   └── shuffle_strategies/    # Fisher-Yates shuffle implementations
    ├── observers/                 # Stats tracking and console output
    ├── orchestrators/             # Game mode orchestration (interactive vs simulation)
    │   └── actor_strategy_orchestrators/  # Dealer and player strategy orchestrators
    ├── strategies/
    │   ├── dealer_strategies/     # Dealer strategy interface and default implementation
    │   └── player_strategies/     # Optimal, copy-dealer, and insurance actor_strategies
    └── Table/                     # Game orchestration
        ├── betting_services/      # Bet booking and validation
        ├── bets/                  # Bet type definitions
        ├── deal_services/         # Card dealing
        ├── hand_services/         # Hand lifecycle management
        ├── hands/                 # Player and dealer hand representations
        ├── payout_services/       # Win/loss/push payouts
        ├── positions/             # Table seat management
        ├── position_services/     # Position service interface and implementation
        ├── processors/            # Per-bet-type processing
        │   ├── double_bet_processors/
        │   ├── insurance_bet_processors/
        │   ├── split_bet_processors/
        │   └── standard_bet_processors/
        └── validators/            # Bet validation rules
            ├── double_bet_validators/
            ├── insurance_bet_validators/
            ├── split_bet_validators/
            └── standard_bet_validators/
```

## Running Tests

```bash
mvn test
```

Tests are organised by domain under `src/test/java/`:

| Package | Coverage |
|---|---|
| `actor_strategies` | Optimal, insurance, copy-dealer, and default dealer strategies |
| `deal_services` | Card dealing |
| `deck` | Deck/shoe management |
| `hand_services` | Hand lifecycle |
| `hands` | Hand and player-hand logic |
| `payout_services` | Standard and insurance payouts |
| `position_services` | Table position management |
| `processors` | Double, insurance, split, and standard bet processing |
| `validators` | Double, insurance, split, and standard bet validation |

## Simulation Results

  With optimal non-counting strategy, 4 decks, 3:2 blackjack payout, and a dealer that stands on hard 17:

- House edge: approximately 0.5–2%
- Expected value: roughly -0.50 to -2.00 per $100 wagered

## To Be Completed

- [x] controller refactor
- [x] Table printing refactor
- [x] Testing suite refactor
- [ ] Parallelized simulations
- [ ] Player card counting actor_strategies
