package Model.Observers;

import lombok.Data;
import lombok.NoArgsConstructor;
import static Model.Constants.DEFAULT_PLAYER_BET_AMOUNT;
import static Model.Constants.DEFAULT_PLAYER_STARTING_CHIPS;

@Data
@NoArgsConstructor
public class TableStats {

    private int handCount = 0;
    private int splitCount = 0;
    private int blackjackCount = 0;
    private int playerWinCount = 0;
    private int playerLossCount = 0;
    private int pushCount = 0;
    private double runningProfit = 0;
    private double profitPerHand = 0;
    private double expectedValuePerHand = 0;

    public void incrementHandCount() { handCount++; }
    public void incrementSplitCount() { splitCount++; }
    public void incrementBlackjackCount() { blackjackCount++; }
    public void incrementPlayerWinCount() { playerWinCount++; }
    public void incrementPlayerLossCount() { playerLossCount++; }
    public void incrementPushCount() { pushCount++; }
    public void setRunningProfit(double playerChips) { this.runningProfit = playerChips - DEFAULT_PLAYER_STARTING_CHIPS; }
    public void setProfitPerHand(int handNumber) {this.profitPerHand = this.runningProfit / ((double) handNumber); }
    public void setExpectedValuePerHand() {this.expectedValuePerHand = this.profitPerHand / DEFAULT_PLAYER_BET_AMOUNT; }
}
