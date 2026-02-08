package fr.sorbonne_u.components.hem2025e3.equipments.fan.sil;

// Copyright Jacques Malenfant, Sorbonne Universite.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI.FanSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanSimulationConfigurationI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.FanEventI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanCyPhy;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanStateSILModel</code> defines a simulation model
 * tracking the state changes on a fan.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model receives events from the fan component, keeps track of
 * the current state of the fan and then emits the received events
 * again towards another model simulating the electricity consumption.
 * </p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code SwitchOnFan}, {@code SwitchOffFan},
 *   {@code SetLowSpeedFan}, {@code SetHighSpeedFan}</li>
 * <li>Exported events:
 *   {@code SwitchOnFan}, {@code SwitchOffFan},
 *   {@code SetLowSpeedFan}, {@code SetHighSpeedFan}</li>
 * </ul>
 * 
 * <p>Created on : 2026-06-06</p>
 * @author	Team
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(
	imported = {SwitchOnFan.class, SwitchOffFan.class,
				SetLowSpeedFan.class, SetHighSpeedFan.class},
	exported = {SwitchOnFan.class, SwitchOffFan.class,
				SetLowSpeedFan.class, SetHighSpeedFan.class}
	)
// -----------------------------------------------------------------------------
public class			FanStateSILModel
extends		AtomicModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	public static boolean			VERBOSE = true;
	public static boolean			DEBUG = false;

	public static final String		URI = FanStateSILModel.class.
																getSimpleName();

	/** current speed (OFF, LOW, HIGH) of the fan.							*/
	protected FanSpeed				currentSpeed = FanSpeed.OFF;
	/** last received event or null if none.								*/
	protected EventI				lastReceived;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= FanCyPhy.staticInvariants();
		ret &= FanSimulationConfigurationI.staticInvariants();
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				FanStateSILModel.class,
				"URI != null && !URI.isEmpty()");
		return ret;
	}

	protected static boolean	invariants(FanStateSILModel instance)
	{
		assert	instance != null :
				new NeoSim4JavaException(
						"Precondition violation: instance != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				FanStateSILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		if (VERBOSE || DEBUG) {
			this.getSimulationEngine().setLogger(new StandardLogger());
		}

		assert	FanStateSILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanStateSILModel.implementationInvariants(this)");
		assert	FanStateSILModel.invariants(this) :
				new NeoSim4JavaException(
						"FanStateSILModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public void			switchOn()
	{
		if (this.currentSpeed == FanSpeed.OFF) {
			this.currentSpeed = FanSpeed.LOW;
		}
	}

	public void			switchOff()
	{
		if (this.currentSpeed != FanSpeed.OFF) {
			this.currentSpeed = FanSpeed.OFF;
		}
	}

	public void			setHighSpeed()
	{
		if (this.currentSpeed != FanSpeed.OFF) {
			this.currentSpeed = FanSpeed.HIGH;
		}
	}

	public void			setLowSpeed()
	{
		if (this.currentSpeed != FanSpeed.OFF) {
			this.currentSpeed = FanSpeed.LOW;
		}
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.lastReceived = null;
		this.currentSpeed = FanSpeed.OFF;

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}
	}

	@Override
	public ArrayList<EventI>	output()
	{
		assert	this.lastReceived != null :
				new NeoSim4JavaException("lastReceived != null");

		ArrayList<EventI> ret = new ArrayList<EventI>();
		ret.add(this.lastReceived);
		this.lastReceived = null;
		return ret;
	}

	@Override
	public Duration		timeAdvance()
	{
		if (this.lastReceived != null) {
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}

	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert	currentEvents != null && currentEvents.size() == 1 :
				new NeoSim4JavaException(
						"currentEvents != null && currentEvents.size() == 1");

		this.lastReceived = currentEvents.get(0);

		if (VERBOSE) {
			StringBuffer message = new StringBuffer(this.uri);
			message.append(" executes the external event ");
			message.append(this.lastReceived);
			this.logMessage(message.toString());
		}
	}

	@Override
	public void			endSimulation(Time endTime)
	{
		if (VERBOSE) {
			this.logMessage("simulation ends.");
		}
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		if (simParams.containsKey(
						AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
			this.getSimulationEngine().setLogger(
						AtomicSimulatorPlugin.createComponentLogger(simParams));
		}
	}
}
// -----------------------------------------------------------------------------
