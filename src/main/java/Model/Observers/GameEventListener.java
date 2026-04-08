package Model.Observers;

public interface GameEventListener {

    void onRoundStart();
    void onHandResult();
    void onPlayerWin();
}
