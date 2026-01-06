package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;

/**
 * Tester component for washing machine in step 3 (CyPhy).
 * We will align its exact behaviour after you send the remaining Heater step-3 files.
 */
public class WashingMachineTesterCyPhy extends AbstractCyPhyComponent
{
	public static boolean VERBOSE = false;
	public static int X_RELATIVE_POSITION = 0;
	public static int Y_RELATIVE_POSITION = 0;

	public static final String REFLECTION_INBOUND_PORT_URI =
			"WashingMachineTester-RIP-URI";

	protected WashingMachineTesterCyPhy() throws Exception {
		super(1, 0);
	}
}
