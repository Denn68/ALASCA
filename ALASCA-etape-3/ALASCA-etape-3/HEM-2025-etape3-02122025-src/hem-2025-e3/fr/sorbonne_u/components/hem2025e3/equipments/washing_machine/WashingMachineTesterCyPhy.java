package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;

@RequiredInterfaces(required = { WashingMachineActuatorCI.class })
public class			WashingMachineTesterCyPhy
extends		AbstractComponent
{
	// protected WashingMachineActuatorOutboundPort actuatorPort;
	protected String wmInboundPortURI;

	protected			WashingMachineTesterCyPhy(String wmInboundPortURI) throws Exception {
		super(1, 0);
		this.wmInboundPortURI = wmInboundPortURI;
		// Init actuatorPort...
	}

	@Override
	public synchronized void	execute() throws Exception {
		super.execute();
		
		// Connexion
		// doPortConnection(actuatorPort.getPortURI(), wmInboundPortURI, ...);

		this.traceMessage("Tester: Switch On\n");
		// actuatorPort.switchOn();
		
		Thread.sleep(1000);
		
		this.traceMessage("Tester: Start Washing\n");
		// actuatorPort.startWashing(...);
		
		Thread.sleep(5000); // Wait for cycle
		
		this.traceMessage("Tester: Switch Off\n");
		// actuatorPort.switchOff();
	}
}