package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil;

import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.WashingMachineOperationI;

public interface			SIL_WashingMachineOperationI
extends		WashingMachineOperationI
{
	// Pas d'opérations supplémentaires par rapport au MIL pour l'instant,
	// sauf si on veut setter la puissance instantanée directement.
	// On garde la structure pour la cohérence de type.
}