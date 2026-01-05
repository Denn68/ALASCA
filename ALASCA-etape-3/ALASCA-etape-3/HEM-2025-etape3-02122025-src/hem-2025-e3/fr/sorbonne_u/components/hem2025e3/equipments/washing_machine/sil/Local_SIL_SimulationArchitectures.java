package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>Local_SIL_SimulationArchitectures</code> defines the
 * local SIL simulation architecture for the washing machine component.
 *
 * <p><strong>Description</strong></p>
 * Defines a single atomic RT model architecture.
 *
 * @author	Team DeMoh
 */
public class			Local_SIL_SimulationArchitectures
{
	/** URI of the simulation architecture and the root model. */
	public static final String WM_SIL_URI = "washing-machine-sil";

	/**
	 * Create the local SIL simulation architecture for the washing machine.
	 * * @param architectureURI		URI to be given to the created architecture.
	 * @param rootModelURI			URI to be given to the root model.
	 * @param simulatedTimeUnit		time unit used for the simulation time.
	 * @param accelerationFactor	acceleration factor for the real time simulation.
	 * @return						the local SIL simulation architecture.
	 * @throws Exception			if an error occurs during creation.
	 */
	public static Architecture	createWashingMachineSILArchitecture(
		String architectureURI,
		String rootModelURI,
		TimeUnit simulatedTimeUnit,
		double accelerationFactor
		) throws Exception
	{
		assert	architectureURI != null && !architectureURI.isEmpty() :
				new PreconditionException(
						"architectureURI != null && !architectureURI.isEmpty()");
		assert	rootModelURI != null && !rootModelURI.isEmpty() :
				new PreconditionException(
						"rootModelURI != null && !rootModelURI.isEmpty()");
		assert	simulatedTimeUnit != null :
				new PreconditionException("simulatedTimeUnit != null");
		assert	accelerationFactor > 0.0 :
				new PreconditionException("accelerationFactor > 0.0");

		// 1. Descripteurs Atomiques
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		// On utilise le modèle SIL créé juste au-dessus
		atomicModelDescriptors.put(
				rootModelURI,
				RTAtomicModelDescriptor.create(
						WashingMachineElectricitySILModel.class,
						rootModelURI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		// 2. Descripteurs Couplés (Vide car modèle unique)
		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		// 3. Architecture Temps Réel
		RTArchitecture architecture =
				new RTArchitecture(
						architectureURI,
						rootModelURI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						simulatedTimeUnit,
						accelerationFactor);

		return architecture;
	}
}
// -----------------------------------------------------------------------------