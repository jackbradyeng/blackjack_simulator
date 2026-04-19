package Model.Observers;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TableStats {

    public int handCount = 0;
    public int splitCount = 0;
    public int blackjackCount = 0;
    public int playerWinCount = 0;
    public int playerLossCount = 0;
    public int pushCount = 0;

    public void incrementHandCount() { handCount++; }
    public void incrementSplitCount() { splitCount++; }
    public void incrementBlackjackCount() { blackjackCount++; }
    public void incrementPlayerWinCount() { playerWinCount++; }
    public void incrementPlayerLossCount() { playerLossCount++; }
    public void incrementPushCount() { pushCount++; }
}
