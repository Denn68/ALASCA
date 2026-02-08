package fr.sorbonne_u.components.hem2025e3.equipments.vacuum_cleaner.sil;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a basic
// household management systems as an example of a cyber-physical system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleaner;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI.VacuumCleanerMode;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI.VacuumCleanerState;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.VacuumCleanerSimulationConfigurationI;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.AbstractVacuumCleanerEvent;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetHighVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetLowVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOffVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOnVacuumCleaner;
import fr.sorbonne_u.components.hem2025e3.equipments.vacuum_cleaner.VacuumCleanerCyPhy;
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
 * The class <code>VacuumCleanerStateSILModel</code> defines a simulation model
 * tracking the state changes on a vacuum cleaner.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model receives event from the vacuum cleaner component (corresponding to
 * calls to operations on the vacuum cleaner in this component), keeps track of
 * the current state of the vacuum cleaner in the simulation and then emits the
 * received events again towards another model simulating the electricity
 * consumption of the vacuum cleaner given its current operating state (switched
 * on/off, high/low mode).
 * </p>
 * <p>
 * This model becomes necessary in a SIL simulation of the household energy
 * management system because the electricity model must be put in the electric
 * meter component to share variables with other electricity models so this
 * state model will serve as a bridge between the models put in the vacuum
 * cleaner component and its electricity model put in the electric meter
 * component.
 * </p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code SwitchOnVacuumCleaner},
 *   {@code SwitchOffVacuumCleaner},
 *   {@code SetLowVacuumCleaner},
 *   {@code SetHighVacuumCleaner}</li>
 * <li>Exported events:
 *   {@code SwitchOnVacuumCleaner},
 *   {@code SwitchOffVacuumCleaner},
 *   {@code SetLowVacuumCleaner},
 *   {@code SetHighVacuumCleaner}</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables: none</li>
 * </ul>
 * 
 * <p>Created on : 2026-06-06</p>
 * 
 * @author	Team
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(
	imported = {SwitchOnVacuumCleaner.class, SwitchOffVacuumCleaner.class,
				SetLowVacuumCleaner.class, SetHighVacuumCleaner.class},
	exported = {SwitchOnVacuumCleaner.class, SwitchOffVacuumCleaner.class,
				SetLowVacuumCleaner.class, SetHighVacuumCleaner.class}
	)
// -----------------------------------------------------------------------------
public class			VacuumCleanerStateSILModel
extends		AtomicModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** when true, leaves a trace of the execution of the model.			*/
	public static boolean			VERBOSE = true;
	/** when true, leaves a debugging trace of the execution of the model.	*/
	public static boolean			DEBUG = false;

	/** URI for an instance model; works as long as only one instance is
	 *  created.															*/
	public static final String		URI = VacuumCleanerStateSILModel.class.
																getSimpleName();

	/** current state (OFF, ON) of the vacuum cleaner.						*/
	protected VacuumCleanerState		currentState = VacuumCleanerState.OFF;
	/** current mode (LOW, HIGH) of the vacuum cleaner.						*/
	protected VacuumCleanerMode			currentMode = VacuumCleanerMode.LOW;
	/** last received event or null if none.								*/
	protected AbstractVacuumCleanerEvent	lastReceived;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the static invariants are observed, false otherwise.
	 */
	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= VacuumCleanerCyPhy.staticInvariants();
		ret &= VacuumCleanerSimulationConfigurationI.staticInvariants();
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				VacuumCleanerStateSILModel.class,
				"URI != null && !URI.isEmpty()");
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(
		VacuumCleanerStateSILModel instance
		)
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

	public				VacuumCleanerStateSILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		if (VERBOSE || DEBUG) {
			this.getSimulationEngine().setLogger(new StandardLogger());
		}

		assert	VacuumCleanerStateSILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"VacuumCleanerStateSILModel."
						+ "implementationInvariants(this)");
		assert	VacuumCleanerStateSILModel.invariants(this) :
				new NeoSim4JavaException(
						"VacuumCleanerStateSILModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public void			turnOn()
	{
		if (this.currentState == VacuumCleanerState.OFF) {
			this.currentState = VacuumCleanerState.ON;
			this.currentMode = VacuumCleaner.INITIAL_MODE;
		}
	}

	public void			turnOff()
	{
		if (this.currentState != VacuumCleanerState.OFF) {
			this.currentState = VacuumCleanerState.OFF;
			this.currentMode = VacuumCleaner.INITIAL_MODE;
		}
	}

	public void			setHigh()
	{
		if (this.currentState == VacuumCleanerState.ON) {
			if (this.currentMode != VacuumCleanerMode.HIGH) {
				this.currentMode = VacuumCleanerMode.HIGH;
			}
		}
	}

	public void			setLow()
	{
		if (this.currentState == VacuumCleanerState.ON) {
			if (this.currentMode != VacuumCleanerMode.LOW) {
				this.currentMode = VacuumCleanerMode.LOW;
			}
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
		this.currentState = VacuumCleanerState.OFF;
		this.currentMode = VacuumCleanerMode.LOW;

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

		this.lastReceived =
				(AbstractVacuumCleanerEvent) currentEvents.get(0);

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
