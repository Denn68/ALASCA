package fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>VacuumCleanerCoupledModel</code> defines the DEVS coupled
 * model for the vacuum cleaner simulator.
 *
 * <p><strong>Description</strong></p>
 * * <p><strong>Implementation Invariants</strong></p>
 * * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code URI != null && !URI.isEmpty()}
 * </pre>
 * * @author	Team DeMoh
 */
public class			VacuumCleanerCoupledModel
extends		CoupledModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for an instance model; works as long as only one instance is
	 * created.															*/
	public static final String	URI = VacuumCleanerCoupledModel.class.getSimpleName();

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * creating the coupled model instance.
	 * * <p><strong>Contract</strong></p>
	 * * <pre>
	 * pre	{@code uri == null || !uri.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine != null && !simulationEngine.isModelSet()}
	 * pre	{@code simulationEngine instanceof CoordinationEngine}
	 * pre	{@code submodels != null && submodels.length > 1}
	 * pre	{@code Stream.of(submodels).allMatch(s -> s!= null)}
	 * post	{@code !isDebugModeOn()}
	 * post	{@code getURI() != null && !getURI().isEmpty()}
	 * post	{@code uri == null || getURI().equals(uri)}
	 * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
	 * post	{@code getSimulationEngine().equals(simulationEngine)}
	 * </pre>
	 *
	 * @param uri				URI of the coupled model to be created.
	 * @param simulatedTimeUnit	time unit used in the simulation by the model.
	 * @param simulationEngine	simulation engine enacting the model.
	 * @param submodels			array of submodels of the new coupled model.
	 * @param imported			map from imported event types to submodels consuming them.
	 * @param reexported		map from event types exported by submodels that are reexported by this coupled model.
	 * @param connections		map connecting event sources to arrays of event sinks among submodels.
	 * @throws Exception		<i>to do</i>.
	 */
	public				VacuumCleanerCoupledModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		CoordinatorI simulationEngine,
		ModelI[] submodels,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>,ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine, submodels,
			  imported, reexported, connections);
	}
}
// -----------------------------------------------------------------------------