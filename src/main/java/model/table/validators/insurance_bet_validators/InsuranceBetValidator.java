package model.table.validators.insurance_bet_validators;

import model.actors.Player;
import model.table.hands.PlayerHand;

public interface InsuranceBetValidator {

    boolean isValid(Player player, PlayerHand playerHand, double amount);
}
