package Model.Orchestrators;

import Model.Actors.Player;
import Model.Observers.TablePrinter;
import Model.Observers.TableStats;
import Model.Orchestrators.actor_strategy_orchestrators.DealerStrategyOrchestrator;
import Model.Orchestrators.actor_strategy_orchestrators.PlayerStrategyOrchestrator;
import Model.Table.Table;
import java.time.Duration;
import java.time.Instant;
import static Model.Constants.DEFAULT_NUMBER_OF_ITERATIONS;
import static Model.Constants.DEFAULT_PLAYER_BET_AMOUNT;

public class SimulationModeOrchestrator implements GameModeOrchestrator {

    private final PlayerStrategyOrchestrator playerStrategyOrchestrator;
    private final DealerStrategyOrchestrator dealerStrategyOrchestrator;

    public SimulationModeOrchestrator() {
        this.playerStrategyOrchestrator = new PlayerStrategyOrchestrator();
        this.dealerStrategyOrchestrator = new DealerStrategyOrchestrator();
    }

    @Override
    public void runGame(Table table, TablePrinter tablePrinter, TableStats tableStats) {
        Instant start = Instant.now();
        Player mainPlayer = table.getPlayers().getFirst();

        for (int i = 0; i < DEFAULT_NUMBER_OF_ITERATIONS; i++) {
            table.startupRoutine();
            table.getBettingService().bookStandardBet(mainPlayer, mainPlayer.getDefaultPosition(), DEFAULT_PLAYER_BET_AMOUNT);
            table.drawRoutine();
            playerStrategyOrchestrator.executePlayerStrategyForAll(table);
            dealerStrategyOrchestrator.executeDealerStrategy(table, tablePrinter, true);
            table.windDownRoutine();
            tableStats.setRunningProfit(mainPlayer.getChips());
            tableStats.setProfitPerHand(i + 1);
            tableStats.setExpectedValuePerHand();
            if ((i + 1) % 1000 == 0) { tablePrinter.printStatistics(i + 1, tableStats); }
        }
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        long seconds = timeElapsed.getSeconds();
        tablePrinter.printProcessingTime(seconds);
    }
}
