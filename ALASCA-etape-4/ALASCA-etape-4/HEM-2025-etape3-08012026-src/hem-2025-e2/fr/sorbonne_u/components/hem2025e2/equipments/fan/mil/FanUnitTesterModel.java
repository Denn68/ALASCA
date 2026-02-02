package fr.sorbonne_u.components.hem2025e2.equipments.fan.mil;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.cyphy.utils.tests.AbstractTestScenarioBasedAtomicModel;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanUnitTesterModel</code> defines a model that is used
 * to test the models defining the fan simulator.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events:
 * {@code SwitchOnFan},
 * {@code SwitchOffFan},
 * {@code SetLowSpeedFan},
 * {@code SetHighSpeedFan}</li>
 * </ul>
 * *
 * <p>
 * <strong>Implementation Invariants</strong>
 * </p>
 * *
 * 
 * <pre>
 * invariant	{@code
 * step >= 0
 * }
 * </pre>
 * 
 * *
 * <p>
 * <strong>Invariants</strong>
 * </p>
 * *
 * 
 * <pre>
 * invariant	{@code
 * URI != null && !URI.isEmpty()
 * }
 * </pre>
 * 
 * * @author Team DeMoh
 */
@ModelExternalEvents(exported = { SwitchOnFan.class,
		SwitchOffFan.class,
		SetLowSpeedFan.class,
		SetHighSpeedFan.class })
// -----------------------------------------------------------------------------
public class FanUnitTesterModel
		extends AbstractTestScenarioBasedAtomicModel {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created. */
	public static final String URI = FanUnitTesterModel.class.getSimpleName();
	public static boolean VERBOSE = true;
	public static boolean DEBUG = false;
	public static final String TEST_SCENARIO_RP_NAME = "TEST_SCENARIO";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public FanUnitTesterModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			AtomicSimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	@Override
	public void setSimulationRunParameters(
			Map<String, Object> simParams) throws MissingRunParameterException {
		String testScenarioName = ModelI.createRunParameterName(this.getURI(),
				TEST_SCENARIO_RP_NAME);

		assert simParams != null : new MissingRunParameterException("simParams != null");
		assert simParams.containsKey(testScenarioName) : new MissingRunParameterException(testScenarioName);

		this.setTestScenario((TestScenarioWithSimulation) simParams.get(testScenarioName));
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	@Override
	public SimulationReportI getFinalReport() {
		return null;
	}
}
// -----------------------------------------------------------------------------