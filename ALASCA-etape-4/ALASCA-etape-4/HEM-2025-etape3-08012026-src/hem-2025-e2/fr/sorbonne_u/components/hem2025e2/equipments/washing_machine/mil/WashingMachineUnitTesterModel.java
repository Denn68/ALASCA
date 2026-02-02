package fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.utils.tests.AbstractTestScenarioBasedAtomicModel;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.*;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

@ModelExternalEvents(exported = { 
    SwitchOnWashingMachine.class, 
    SwitchOffWashingMachine.class,
    StartWashing.class, 
    SetDelayedStart.class,
    SuspendWashing.class, 
    ResumeWashing.class,
    SetPowerWashingMachine.class
})
public class WashingMachineUnitTesterModel 
extends AbstractTestScenarioBasedAtomicModel 
{
    private static final long serialVersionUID = 1L;
    public static final String URI = WashingMachineUnitTesterModel.class.getSimpleName();
	/** when true, leaves a trace of the execution of the model.			*/
	public static boolean		VERBOSE = true;
	/** when true, leaves a debugging trace of the execution of the model.	*/
	public static boolean		DEBUG = false;
    public static final String TEST_SCENARIO_RP_NAME = "TEST_SCENARIO";

    public WashingMachineUnitTesterModel(
        String uri, 
        TimeUnit simulatedTimeUnit, 
        AtomicSimulatorI simulationEngine) throws Exception 
    {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.getSimulationEngine().setLogger(new StandardLogger());
    }

    @Override
    public void setSimulationRunParameters(Map<String, Object> simParams) throws MissingRunParameterException {
        String testScenarioName = ModelI.createRunParameterName(this.getURI(), TEST_SCENARIO_RP_NAME);

        assert simParams != null : new MissingRunParameterException("simParams != null");
        assert simParams.containsKey(testScenarioName) : new MissingRunParameterException(testScenarioName);

        this.setTestScenario((TestScenarioWithSimulation) simParams.get(testScenarioName));
    }

    @Override
    public SimulationReportI getFinalReport() {
        return null;
    }
}