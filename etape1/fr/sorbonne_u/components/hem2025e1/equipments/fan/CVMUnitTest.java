package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.AbstractComponent;

// -----------------------------------------------------------------------------

public class			CVMUnitTest
extends		AbstractCVM
{
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				CVMUnitTest() throws Exception
	{
		FanUnitTester.VERBOSE = true;
		FanUnitTester.X_RELATIVE_POSITION = 0;
		FanUnitTester.Y_RELATIVE_POSITION = 0;
		Fan.VERBOSE = true;
		Fan.X_RELATIVE_POSITION = 1;
		Fan.Y_RELATIVE_POSITION = 0;
	}

	// -------------------------------------------------------------------------
	// CVM life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
				Fan.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				FanUnitTester.class.getCanonicalName(),
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
