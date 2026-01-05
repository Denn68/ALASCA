package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.*;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil.Local_SIL_SimulationArchitectures;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.WashingMachineStateSensorData; 
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.exceptions.PreconditionException;

@OfferedInterfaces(offered = {
		WashingMachineActuatorCI.class, 
		WashingMachineSensorDataCI.WashingMachineSensorOfferedPullCI.class
})
public class			WashingMachineCyPhy
extends		AbstractCyPhyComponent
implements	WashingMachineActuatorCI,
			WashingMachineSensorDataCI.WashingMachineSensorOfferedPullCI
{
	// -------------------------------------------------------------------------
	// Constants & States
	// -------------------------------------------------------------------------
	public static enum WashingMachineState { OFF, ON, WASHING } // Simplifié pour l'exemple
	protected static final String WM_MODEL_URI = Local_SIL_SimulationArchitectures.WM_SIL_URI;

	// -------------------------------------------------------------------------
	// Variables
	// -------------------------------------------------------------------------
	protected AtomicSimulatorPlugin simulatorPlugin;
	protected WashingMachineState currentState;
	protected long simulationStartTimeMS;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	protected			WashingMachineCyPhy(
		String wmInboundPortURI,
		String simulationArchitectureURI,
		String simulationTimeUnit
		) throws Exception
	{
		super(1, 0);
		this.currentState = WashingMachineState.OFF;
		
		// Initialisation SIL
		this.initialise(simulationTimeUnit);
		
		// Note: Pour faire propre, il faudrait publier les ports ici (ActuatorInboundPort, SensorInboundPort)
		// Je suppose que tu as les classes génériques ou spécifiques pour ça.
	}

	protected void		initialise(String simulationTimeUnit) throws Exception
	{
		this.simulatorPlugin = new AtomicSimulatorPlugin();
		this.simulatorPlugin.setPluginURI(WM_MODEL_URI);
		
		Architecture localArchitecture = 
			Local_SIL_SimulationArchitectures.createWashingMachineSILArchitecture(
				WM_MODEL_URI,
				WM_MODEL_URI,
				TimeUnit.valueOf(simulationTimeUnit),
				1.0); // Acc Factor default

		this.simulatorPlugin.setSimulationArchitecture(localArchitecture);
		this.installPlugin(this.simulatorPlugin);
	}
	
	@Override
	public synchronized void start() throws fr.sorbonne_u.components.exceptions.ComponentStartException {
		super.start();
		this.simulationStartTimeMS = System.currentTimeMillis();
	}

	// -------------------------------------------------------------------------
	// SIL Helpers
	// -------------------------------------------------------------------------
	protected Time getCurrentSimulationTime() throws Exception {
		long now = System.currentTimeMillis();
		double elapsed = (now - this.simulationStartTimeMS) / 1000.0;
		return new Time(elapsed, this.simulatorPlugin.getSimulatedTimeUnit());
	}

	protected void triggerSimulationEvent(EventI event) throws Exception {
		if (this.simulatorPlugin != null) {
			ArrayList<EventI> events = new ArrayList<>();
			events.add(event);
			this.simulatorPlugin.storeInput(WM_MODEL_URI, events);
		}
	}

	// -------------------------------------------------------------------------
	// Actuator Implementation
	// -------------------------------------------------------------------------
	@Override
	public void switchOn() throws Exception {
		this.currentState = WashingMachineState.ON;
		triggerSimulationEvent(new SwitchOnWashingMachine(getCurrentSimulationTime()));
	}

	@Override
	public void switchOff() throws Exception {
		this.currentState = WashingMachineState.OFF;
		triggerSimulationEvent(new SwitchOffWashingMachine(getCurrentSimulationTime()));
	}

	@Override
	public void startWashing(long washingTimeMS, Measure<Double> target) throws Exception {
		this.currentState = WashingMachineState.WASHING;
		triggerSimulationEvent(new StartWashing(getCurrentSimulationTime()));
		// Note: La logique de fin automatique (scheduler) peut être ajoutée ici comme dans ton code précédent
	}

	@Override
	public void suspendCycle() throws Exception {
		triggerSimulationEvent(new SuspendWashingMachine(getCurrentSimulationTime()));
	}

	@Override
	public void resumeCycle() throws Exception {
		triggerSimulationEvent(new ResumeWashingMachine(getCurrentSimulationTime()));
	}

	// -------------------------------------------------------------------------
	// Sensor Implementation (Pull & Push)
	// -------------------------------------------------------------------------
	@Override
	public WashingMachineStateSensorData statePullSensor() throws Exception {
		// Renvoie l'état courant encapsulé dans l'objet SensorData
		return new WashingMachineStateSensorData(this.currentState); 
	}

	@Override
	public void startStatePushSensor(long controlPeriod, TimeUnit tu) throws Exception {
		// Planifie une tâche répétitive pour envoyer l'état
		// Note: Cela nécessite un OutboundPort connecté au contrôleur pour envoyer les données
		// Voir HeaterCyPhy.temperaturesPushSensorTask
		
		this.scheduleTaskOnComponent(
			new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						// Logique de push ici (envoi via port)
						// pushSensorOutboundPort.send(new WashingMachineStateSensorData(currentState));
						
						// Re-schedule
						startStatePushSensor(controlPeriod, tu);
					} catch (Exception e) { e.printStackTrace(); }
				}
			}, controlPeriod, tu);
	}
}