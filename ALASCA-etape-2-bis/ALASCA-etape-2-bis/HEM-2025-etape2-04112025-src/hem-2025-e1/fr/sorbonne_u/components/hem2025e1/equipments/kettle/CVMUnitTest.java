package fr.sorbonne_u.components.hem2025e1.equipments.kettle;


import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.AbstractComponent;


public class			CVMUnitTest
extends		AbstractCVM
{

	public				CVMUnitTest() throws Exception
	{
		KettleUnitTester.VERBOSE = true;
		KettleUnitTester.X_RELATIVE_POSITION = 0;
		KettleUnitTester.Y_RELATIVE_POSITION = 0;
		Kettle.VERBOSE = true;
		Kettle.X_RELATIVE_POSITION = 1;
		Kettle.Y_RELATIVE_POSITION = 0;
	}

	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
				Kettle.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				KettleUnitTester.class.getCanonicalName(),
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
