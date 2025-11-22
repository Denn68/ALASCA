package fr.sorbonne_u.components.hem2025e2.equipments.fan.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanUnitTesterModel</code> defines a model that is used
 * to test the models defining the fan simulator.
 *
 * <p><strong>Description</strong></p>
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events:
 * {@code SwitchOnFan},
 * {@code SwitchOffFan},
 * {@code SetLowSpeedFan},
 * {@code SetHighSpeedFan}</li>
 * </ul>
 * * <p><strong>Implementation Invariants</strong></p>
 * * <pre>
 * invariant	{@code step >= 0}
 * </pre>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code URI != null && !URI.isEmpty()}
 * </pre>
 *
 * * @author	Team DeMoh
 */
@ModelExternalEvents(exported = {SwitchOnFan.class,
								 SwitchOffFan.class,
								 SetLowSpeedFan.class,
								 SetHighSpeedFan.class})
// -----------------------------------------------------------------------------
public class			FanUnitTesterModel
extends		AtomicModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String	URI = FanUnitTesterModel.class.getSimpleName();

	/** current step in the test scenario.									*/
	protected int	step;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				FanUnitTesterModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);
		this.step = 1;
		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.");
	}

	@Override
	public ArrayList<EventI>	output()
	{
		// Simple scenario for Fan testing:
		// Step 1: ON (Low)
		// Step 2: HIGH
		// Step 3: LOW
		// Step 4: OFF
		
		if (this.step > 0 && this.step < 5) {
			ArrayList<EventI> ret = new ArrayList<EventI>();
			switch (this.step) {
			case 1:
				ret.add(new SwitchOnFan(this.getTimeOfNextEvent()));
				break;
			case 2:
				ret.add(new SetHighSpeedFan(this.getTimeOfNextEvent()));
				break;
			case 3:
				ret.add(new SetLowSpeedFan(this.getTimeOfNextEvent()));
				break;
			case 4:
				ret.add(new SwitchOffFan(this.getTimeOfNextEvent()));
				break;
			}
			return ret;
		} else {
			return null;
		}
	}

	@Override
	public Duration		timeAdvance()
	{
		if (this.step < 5) {
			return new Duration(1.0, this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}

	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);
		this.step++;
	}

	@Override
	public void			endSimulation(Time endTime)
	{
		this.logMessage("simulation ends.");
		super.endSimulation(endTime);
	}

	@Override
	public SimulationReportI	getFinalReport()
	{
		return null;
	}
}
// -----------------------------------------------------------------------------