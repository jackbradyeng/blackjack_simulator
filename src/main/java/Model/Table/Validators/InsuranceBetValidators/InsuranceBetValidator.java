package Model.Table.Validators.InsuranceBetValidators;

import Model.Actors.Player;
import Model.Table.Hands.PlayerHand;

public interface InsuranceBetValidator {

    boolean isValid(Player player, PlayerHand playerHand, double amount);
}
