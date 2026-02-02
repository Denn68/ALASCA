package fr.sorbonne_u.devs_simulation.architectures;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a new
// implementation of the DEVS simulation <i>de facto</i> standard for Java.
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

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.ModelDescriptorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The interface <code>ModelArchitectureI</code> defines the signatures
 * of methods to be handled by a DEVS simulation architecture implementation.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * TODO: complete!
 * <pre>
 * invariant	{@code !isComplete() || (isMonoModel() == isAtomicModel(getRootModelURI()))}
 * invariant	{@code !isComplete() || !isMonoModel() == isCoupledModel(getRootModelURI())}
 * invariant	{@code !isComplete() || isSimulationTimeUnitSet()}
 * </pre>
 * 
 * <p>Created on : 2017-10-31</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ArchitectureI
extends		Serializable
{
	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the black-box invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the black-box invariants are observed, false otherwise.
	 */
	public static boolean	invariants(ArchitectureI instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
					!instance.isComplete() ||
						(instance.isMonoModel() ==
							instance.isAtomicModel(instance.getRootModelURI())),
					ArchitectureI.class,
					instance,
					"!isComplete() || (isMonoModel() == "
					+ "isAtomicModel(getRootModelURI()))");
		ret &= AssertionChecking.checkInvariant(
					!instance.isComplete() ||
						!instance.isMonoModel() ==
							instance.isCoupledModel(instance.getRootModelURI()),
					ArchitectureI.class,
					instance,
					"!isComplete() || !isMonoModel() == "
					+ "isCoupledModel(getRootModelURI())");
		ret &= AssertionChecking.checkInvariant(
					!instance.isComplete() || instance.isSimulationTimeUnitSet(),
					ArchitectureI.class,
					instance,
					"!isComplete() || isSimulationTimeUnitSet()");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * get the URI of the described architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the URI of the described architecture.
	 */
	public String		getArchitectureURI();

	/**
	 * return true if the architecture contains only one (atomic) model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isComplete()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the architecture contains only one (atomic) model.
	 */
	public boolean		isMonoModel();

	/**
	 * return true if the architecture uses the real time mode of execution.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the architecture uses the real time mode of execution.
	 */
	public boolean		isRealTime();

	/**
	 * return true if the given URI is an atomic model URI in the
	 * architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri	URI to be tested.
	 * @return		true if the given URI is an atomic model URI in the architecture.
	 */
	public boolean		isAtomicModel(String uri);

	/**
	 * return true if the given URI is a coupled model URI in the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			URI to be tested.
	 * @return				true if the given URI is a coupled model URI in the architecture.
	 */
	public boolean		isCoupledModel(String uri);

	/**
	 * return true if the given URI is a model URI in the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri	URI to be tested.
	 * @return		true if the given URI is a model URI in the architecture.
	 */
	public boolean		isModel(String uri);

	/**
	 * return the model descriptor associated to the given model URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri	URI of a model in the architecture.
	 * @return		the model descriptor of the given model.
	 */
	public ModelDescriptorI	getModelDescriptor(String uri);

	/**
	 * return true if the simulation time unit has been set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the simulation time unit has been set.
	 */
	public boolean		isSimulationTimeUnitSet();

	/**
	 * return	the simulation time unit associated to the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isSimulationTimeUnitSet()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the simulation time unit associated to the architecture.
	 */
	public TimeUnit		getSimulationTimeUnit();

	/**
	 * return true if the given URI is the root model URI in the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri	URI to be tested.
	 * @return		true if the given URI is the root model URI in the architecture.
	 */
	public boolean		isRootModel(String uri);

	/**
	 * return true if the model <code>childURI</code> is a child of the
	 * model <code>parentURI</code> in the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code childURI != null && !childURI.isEmpty()}
	 * pre	{@code parentURI != null && !parentURI.isEmpty()}
	 * pre	{@code isModel(childURI) && isCoupledModel(parentURI)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param childURI		URI of the possible child model.
	 * @param parentURI		URI of the possible parent model.
	 * @return				true if the model <code>childURI</code> is a child of the model <code>parentURI</code>
	 * @throws Exception	if one of the URI does not exist in the architecture.
	 */
	public boolean		isChildModelOf(
		String childURI,
		String parentURI
		) throws Exception;

	/**
	 * return true if the model <code>descendentURI</code> is a descendent of
	 * the model <code>ancestorURI</code> in the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code descendantURI != null && !descendantURI.isEmpty()}
	 * pre	{@code ancestorURI != null && !ancestorURI.isEmpty()}
	 * pre	{@code isModel(descendantURI) && isCoupledModel(ancestorURI)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param descendantURI	URI of the possible descendent model.
	 * @param ancestorURI	URI of the possible ancestor model.
	 * @return				true if the model <code>descendentURI</code> is a descendent of the model <code>ancestorURI</code> in the architecture.
	 * @throws Exception	if one of the URI does not exist in the architecture.
	 */
	public boolean		isDescendant(
		String descendantURI,
		String ancestorURI
		) throws Exception;

	/**
	 * return the URI of the root model in the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret == null || isRootModel(ret)}
	 * </pre>
	 *
	 * @return	the URI of the root model in the architecture.
	 */
	public String		getRootModelURI();

	/**
	 * get a set of all of the current model URIs.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	a set of all of the current model URIs.
	 */
	public Set<String>	getAllModelURIs();

	/**
	 * return a set of the URIs of all atomic models in this architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	a set of the URIs of all atomic models in this architecture.
	 */
	public Set<String>	getAllAtomicModelsURIs();

	/**
	 * return the URI of the parent model of <code>modelURI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * post	{@code !isComplete() || (ret != null || modelURI.equals(this.getRootModelURI()))}
	 * </pre>
	 *
	 * @param modelURI	URI of the model which parent is looked for.
	 * @return			the URI of the parent model of <code>modelURI</code>.
	 */
	public String		getParentURI(String modelURI);

	/**
	 * return the URIs of the direct children models of the given coupled model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * pre	{@code isCoupledModel(uri)}
	 * post	{@code ret != null}
	 * post	{@code !isComplete() || ret.size() > 0}
	 * </pre>
	 *
	 * @param uri	the URI of a coupled model.
	 * @return		the set of URIs of the direct children models of the given model.
	 */
	public Set<String>	getChildrenModelURIs(String uri);

	/**
	 * return the set of all URIs currently known as descendant models.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * post	{@code ret != null}
	 * post	{@code !isComplete() || ret.size() > 0}
	 * </pre>
	 *
	 * @param uri	uri of a coupled model.
	 * @return		the set of all URIs currently known as descendant models.
	 */
	public Set<String>	getDescendantModels(String uri);

	/**
	 * set the simulation time unit of the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isSimulationTimeUnitSet()}
	 * pre	{@code tu != null}
	 * post	{@code isSimulationTimeUnitSet()}
	 * </pre>
	 *
	 * @param tu	the new simulation time unit.
	 */
	public void			setSimulationTimeUnit(TimeUnit tu);

	/**
	 * add an atomic model to the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code !isModel(modelURI)}
	 * pre	{@code descriptor != null && descriptor.modelURI.equals(modelURI)}
	 * post	{@code isAtomicModel(modelURI)}
	 * </pre>
	 *
	 * @param modelURI		URI of the new model.
	 * @param descriptor	descriptor of the added model.
	 */
	public void			addAtomicModel(
		String modelURI,
		AtomicModelDescriptor descriptor
		);

	/**
	 * add an atomic model to the architecture as its root model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code !isModel(modelURI)}
	 * pre	{@code getAllModels().size() == 0}
	 * pre	{@code descriptor != null && descriptor.modelURI.equals(modelURI)}
	 * post	{@code isAtomicModel(modelURI)}
	 * post	{@code isRootModel(modelURI)}
	 * post	{@code isMonoModel()}
	 * post	{@code isComplete()}
	 * </pre>
	 *
	 * @param modelURI		URI of the new model.
	 * @param descriptor	descriptor of the added model.
	 */
	public void			addAtomicModelAsRoot(
		String modelURI,
		AtomicModelDescriptor descriptor
		);

	/**
	 * add a coupled model to the architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code !isModel(modelURI)}
	 * pre	{@code descriptor != null && descriptor.modelURI.equals(modelURI)}
	 * post	{@code isCoupledModel(modelURI)}
	 * </pre>
	 *
	 * @param modelURI		URI of the model to be added.
	 * @param descriptor	descriptor of the added model.
	 */
	public void			addCoupledModel(
		String modelURI,
		CoupledModelDescriptor descriptor
		);

	/**
	 * add a coupled model to the architecture as its root model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code !isModel(modelURI)}
	 * pre	{@code getRootModel() == null}
	 * pre	{@code descriptor != null && descriptor.modelURI.equals(modelURI)}
	 * post	{@code isCoupledModel(modelURI)}
	 * post	{@code !isMonoModel()}
	 * post	{@code isRootModel(modelURI)}
	 * </pre>
	 *
	 * @param modelURI		URI of the model to be added.
	 * @param descriptor	descriptor of the added model.
	 */
	public void			addCoupledModelAsRoot(
		String modelURI,
		CoupledModelDescriptor descriptor
		);

	/**
	 * return true if the model architecture can be interpreted as complete
	 * i.e., all models mentioned as submodels, sources or sinks of events
	 * have descriptors defined in this model architecture.
	 * 
	 * <p>
	 * Detecting that an architecture is complete is complex. A possibility
	 * would be to have a specific method to be called to inform that the
	 * architecture is complete, but in practice, if the architecture is
	 * constructed by gathering model descriptions from several parallel or
	 * distributed processes, it would be extremely difficult (and probably
	 * repetitive in terms of coding) to check that all models have been added
	 * to the architecture. Hence, the basic idea of the test is to check that
	 * the root model has been defined and that no models mention the URI of
	 * another model that has not been added yet.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the model architecture can be interpreted as complete.
	 */
	public boolean		isComplete();

	/**
	 * return true if the architecture is a tree of models.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isComplete()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the architecture is a tree of models.
	 */
	public boolean		isTree();

	/**
	 * return a topological sort of the models in the architecture
	 * as a list of URIs.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isComplete()}
	 * pre	{@code isTree()}
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	a topological sort of the models in the architecture as a list of URIs.
	 */
	public List<String>	topologicalSort();


	/**
	 * return a topological sort of the models subtree which root is the
	 * given model as a list of URIs; the result is a depth-first traversal
	 * of the architecture seen as a tree.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code isModel(modelURI)}
	 * pre	{@code isComplete()}
	 * pre	{@code isTree()}
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @param modelURI	URI of the model at the root of the subtree to be sorted.
	 * @return			a topological sort of the models subtree which root is the given model as a list of URIs.
	 */
	public List<String>	topologicalSort(String modelURI);

	/**
	 * construct a simulator from a complete architecture returning the root
	 * simulation engine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isComplete()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the simulation engine of the root model in a simulation architecture for this model architecture.
	 */
	public SimulatorI	constructSimulator();

	/**
	 * construct a simulator from a complete subtree of the architecture
	 * returning the simulation engine of the model at the root of this
	 * subtree.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code isModel(modelURI)}
	 * pre	{@code isComplete()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI	URI of the root model.
	 * @return			the simulation engine of the root model in a simulation architecture for this model architecture.
	 */
	public SimulatorI	constructSimulator(String modelURI);
}
// -----------------------------------------------------------------------------
