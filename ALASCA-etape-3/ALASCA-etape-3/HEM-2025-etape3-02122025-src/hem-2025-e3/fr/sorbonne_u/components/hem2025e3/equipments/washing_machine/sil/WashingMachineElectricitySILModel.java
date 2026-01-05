package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.WashingMachineElectricityModel;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>WashingMachineElectricitySILModel</code> wraps the MIL
 * model to execute within a SIL (Software-In-the-Loop) simulation.
 *
 * <p><strong>Description</strong></p>
 * It mainly overrides setSimulationRunParameters to redirect logging to the
 * owner component's logger.
 * * <p>Created on : 2025-10-20</p>
 * * @author	Team DeMoh
 */
public class			WashingMachineElectricitySILModel
extends		WashingMachineElectricityModel
implements	SIL_WashingMachineOperationI
{
	private static final long serialVersionUID = 1L;

	/**
	 * create a SIL model instance.
	 * * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		if the creation fails.
	 */
	public				WashingMachineElectricitySILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		// Connexion du logger du modèle vers le logger du composant propriétaire
		if (simParams.containsKey(
						AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
			this.getSimulationEngine().setLogger(
						AtomicSimulatorPlugin.createComponentLogger(simParams));
		}
	}
}
// -----------------------------------------------------------------------------