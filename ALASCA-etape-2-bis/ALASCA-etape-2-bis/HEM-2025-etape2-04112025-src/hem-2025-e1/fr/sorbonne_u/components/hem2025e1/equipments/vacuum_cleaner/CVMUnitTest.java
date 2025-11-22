package fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.BCMException;

public class			CVMUnitTest
extends		AbstractCVM
{

	public				CVMUnitTest() throws Exception
	{
		VacuumCleanerTester.VERBOSE = true;
		VacuumCleanerTester.X_RELATIVE_POSITION = 0;
		VacuumCleanerTester.Y_RELATIVE_POSITION = 0;
		VacuumCleaner.VERBOSE = true;
		VacuumCleaner.X_RELATIVE_POSITION = 1;
		VacuumCleaner.Y_RELATIVE_POSITION = 0;
	}
	
	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
					VacuumCleaner.class.getCanonicalName(),
					new Object[]{});

		AbstractComponent.createComponent(
					VacuumCleanerTester.class.getCanonicalName(),
					new Object[]{true});

		super.deploy();
	}

	public static void		main(String[] args)
	{
		BCMException.VERBOSE = true;
		try {
			CVMUnitTest cvm = new CVMUnitTest();
			cvm.startStandardLifeCycle(10000L);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
