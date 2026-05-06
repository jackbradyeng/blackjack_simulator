package model.Table.validators.insurance_bet_validators;

import model.actors.Player;
import model.Table.hands.PlayerHand;

public interface InsuranceBetValidator {

    boolean isValid(Player player, PlayerHand playerHand, double amount);
}
