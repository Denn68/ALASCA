package fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.utils.tests.AbstractTestScenarioBasedAtomicModel;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.DoNotHeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.HeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SetPowerKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOffKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOnKettle;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.components.utils.tests.TestScenario;

// -----------------------------------------------------------------------------
/**
 * The class <code>KettleUnitTesterModel</code> defines a model that is used
 * to test the models defining the kettle simulator.
 *
 * <p><strong>Description</strong></p>
 * * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events:
 * {@code SwitchOnKettle},
 * {@code SwitchOffKettle},
 * {@code SetPowerKettle},
 * {@code HeatKettle},
 * {@code DoNotHeatKettle}</li>
 * </ul>
 * * <p><strong>Implementation Invariants</strong></p>
 * * <pre>
 * invariant	{@code step >= 0}
 * </pre>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code URI != null && !URI.isEmpty()}
 * </pre>
 * * @author	Team DeMoh
 */
@ModelExternalEvents(exported = {SwitchOnKettle.class,
								 SwitchOffKettle.class,
								 HeatKettle.class,
								 DoNotHeatKettle.class,
								 SetPowerKettle.class})
// -----------------------------------------------------------------------------
public class			KettleUnitTesterModel
extends		AbstractTestScenarioBasedAtomicModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	public static final String	URI = KettleUnitTesterModel.class.getSimpleName();
	public static boolean		VERBOSE = true;
	public static boolean		DEBUG = false;
	public static final String	TEST_SCENARIO_RP_NAME = "TEST_SCENARIO";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				KettleUnitTesterModel(
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
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		String testScenarioName = ModelI.createRunParameterName(this.getURI(),
														TEST_SCENARIO_RP_NAME);

		assert	simParams != null :
				new MissingRunParameterException("simParams != null");
		assert	simParams.containsKey(testScenarioName) :
				new MissingRunParameterException(testScenarioName);

		this.setTestScenario((TestScenarioWithSimulation) simParams.get(testScenarioName));
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	@Override
	public SimulationReportI	getFinalReport()
	{
		return null;
	}
}
// -----------------------------------------------------------------------------