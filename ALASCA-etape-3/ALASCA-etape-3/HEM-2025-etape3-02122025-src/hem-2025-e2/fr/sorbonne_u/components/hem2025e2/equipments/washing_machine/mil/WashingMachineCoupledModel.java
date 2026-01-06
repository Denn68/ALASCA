package fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;

/**
 * The class <code>WashingMachineCoupledModel</code> defines a coupled model
 * used to assemble the washing machine simulation models.
 *
 * <p><strong>Description</strong></p>
 *
 * <p>
 * This coupled model assembles:
 * - WashingMachineElectricityModel: manages state and electricity consumption
 * - WashingMachineTemperatureModel: simulates water temperature evolution
 * - WashingMachineUnitTesterModel: generates test events
 * </p>
 */
public class WashingMachineCoupledModel
extends CoupledModel
{
    private static final long serialVersionUID = 1L;
    public static final String URI = WashingMachineCoupledModel.class.getSimpleName();

    /**
     * Constructor for event exchanges only.
     */
    public WashingMachineCoupledModel(
        String uri,
        TimeUnit simulatedTimeUnit,
        CoordinatorI simulationEngine,
        ModelI[] submodels,
        Map<Class<? extends EventI>, EventSink[]> imported,
        Map<Class<? extends EventI>, ReexportedEvent> reexported,
        Map<EventSource, EventSink[]> connections) throws Exception
    {
        super(uri, simulatedTimeUnit, simulationEngine, submodels,
              imported, reexported, connections);
    }

    /**
     * Constructor for event and variable exchanges.
     *
     * @param uri               URI of the coupled model.
     * @param simulatedTimeUnit time unit used in the simulation.
     * @param simulationEngine  simulation engine enacting the model.
     * @param submodels         array of submodels.
     * @param imported          map of imported events.
     * @param reexported        map of reexported events.
     * @param connections       map of event connections.
     * @param importedVars      variables imported by the coupled model.
     * @param reexportedVars    variables reexported by the coupled model.
     * @param bindings          bindings between exported and imported variables.
     * @throws Exception        if an error occurs.
     */
    public WashingMachineCoupledModel(
        String uri,
        TimeUnit simulatedTimeUnit,
        CoordinatorI simulationEngine,
        ModelI[] submodels,
        Map<Class<? extends EventI>, EventSink[]> imported,
        Map<Class<? extends EventI>, ReexportedEvent> reexported,
        Map<EventSource, EventSink[]> connections,
        Map<StaticVariableDescriptor, VariableSink[]> importedVars,
        Map<VariableSource, StaticVariableDescriptor> reexportedVars,
        Map<VariableSource, VariableSink[]> bindings
    ) throws Exception
    {
        super(uri, simulatedTimeUnit, simulationEngine, submodels,
              imported, reexported, connections,
              importedVars, reexportedVars, bindings);
    }
}