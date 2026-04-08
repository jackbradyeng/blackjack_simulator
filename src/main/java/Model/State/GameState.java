package Model.State;

import Model.Table.Table;

public interface GameState {

    void transitionState(Table table);
}
