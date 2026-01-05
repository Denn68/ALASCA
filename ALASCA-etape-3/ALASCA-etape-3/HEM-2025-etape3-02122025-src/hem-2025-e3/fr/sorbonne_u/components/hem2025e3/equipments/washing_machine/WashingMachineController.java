package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.WashingMachineCyPhy.WashingMachineState;

@RequiredInterfaces(required = {
		WashingMachineActuatorCI.class,
		WashingMachineSensorDataCI.WashingMachineSensorRequiredPullCI.class
})
public class			WashingMachineController
extends		AbstractComponent
implements	WashingMachinePushImplementationI
{
	// Ports (supposés existants ou génériques)
	// protected WashingMachineActuatorOutboundPort actuatorPort;
	// protected WashingMachineSensorDataOutboundPort sensorPort;

	protected			WashingMachineController() throws Exception {
		super(1, 0);
		// Init ports...
	}

	@Override
	public synchronized void	start() throws ComponentStartException {
		super.start();
		// Lancement de la boucle de contrôle ou scénario
		this.runTask(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						controlLoop();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	}

	protected void controlLoop() throws Exception {
		// Exemple simple : Allumer et démarrer
		// actuatorPort.switchOn();
		// actuatorPort.startWashing(10000, new Measure<>(40.0, MeasurementUnit.CELSIUS));
		
		// Polling simple si pas de push
		// WashingMachineStateSensorData data = sensorPort.statePullSensor();
	}

	@Override
	public void processWashingMachineState(WashingMachineState state) {
		this.traceMessage("Controller received state: " + state + "\n");
	}
}