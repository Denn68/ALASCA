package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine;

import fr.sorbonne_u.components.cvm.AbstractCVM;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;

public class			CVMUnitTest
extends		AbstractCVM
{
	public static final String CLOCK_URI = "hem-clock";
    public static final long DELAY_TO_START_IN_MILLIS = 3000L;
    public static final double ACCELERATION_FACTOR = 60.0;

	public				CVMUnitTest() throws Exception
	{
		WashingMachineUnitTester.VERBOSE = true;
		WashingMachineUnitTester.X_RELATIVE_POSITION = 1;
		WashingMachineUnitTester.Y_RELATIVE_POSITION = 1;
		WashingMachine.VERBOSE = true;
		WashingMachine.X_RELATIVE_POSITION = 1;
		WashingMachine.Y_RELATIVE_POSITION = 0;
	}

	@Override
	public void			deploy() throws Exception
	{
long unixEpochStartTimeInMillis = System.currentTimeMillis() + DELAY_TO_START_IN_MILLIS;
        
        Instant startInstant = Instant.now();

        AbstractComponent.createComponent(
                ClocksServer.class.getCanonicalName(),
                new Object[]{
                        CLOCK_URI,
                        TimeUnit.MILLISECONDS.toNanos(unixEpochStartTimeInMillis),
                        startInstant,
                        ACCELERATION_FACTOR
                });
        
		AbstractComponent.createComponent(
				WashingMachine.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				WashingMachineUnitTester.class.getCanonicalName(),
				new Object[]{true});	// is unit test

		super.deploy();
	}

	// Dans CVMUnitTest.java
	public static void main(String[] args)
	{
	    try {
	        CVMUnitTest cvm = new CVMUnitTest();
	        cvm.startStandardLifeCycle(20000L); 
	        
	        Thread.sleep(5000L);
	        System.exit(0);
	    } catch (Throwable e) {
	        e.printStackTrace();
	    }
	}
}
// -----------------------------------------------------------------------------
