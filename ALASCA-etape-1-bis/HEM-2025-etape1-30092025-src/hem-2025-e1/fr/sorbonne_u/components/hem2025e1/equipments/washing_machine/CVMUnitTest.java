package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.AbstractComponent;

public class			CVMUnitTest
extends		AbstractCVM
{

	public				CVMUnitTest() throws Exception
	{
		WashingMachineUnitTester.VERBOSE = true;
		WashingMachineUnitTester.X_RELATIVE_POSITION = 0;
		WashingMachineUnitTester.Y_RELATIVE_POSITION = 0;
		WashingMachine.VERBOSE = true;
		WashingMachine.X_RELATIVE_POSITION = 1;
		WashingMachine.Y_RELATIVE_POSITION = 0;
	}

	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
				WashingMachine.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				WashingMachineUnitTester.class.getCanonicalName(),
				new Object[]{true});	// is unit test

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			CVMUnitTest cvm = new CVMUnitTest();
			cvm.startStandardLifeCycle(1000L);
			Thread.sleep(100000L);
			System.exit(0);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
// -----------------------------------------------------------------------------
