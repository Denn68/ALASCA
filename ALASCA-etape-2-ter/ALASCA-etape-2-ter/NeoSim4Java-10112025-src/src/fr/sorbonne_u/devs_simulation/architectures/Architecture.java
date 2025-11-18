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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.ModelDescriptorI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>Architecture</code> describes a DEVS simulation architecture
 * with a set of the atomic model descriptors, a set of the coupled model
 * descriptors and the URI of the root model.
 *
 * <p><strong>Description</strong></p>
 * 
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code !isComplete() || rootModelURI != null}
 * invariant	{@code modelsParent != null}
 * invariant	{@code !modelsParent.containsKey(rootModelURI)}
 * invariant	{@code !isComplete() || (atomicModelDescriptors.size() > 1 || coupledModelDescriptors.isEmpty())}
 * invariant	{@code !isComplete() || atomicModelDescriptors.entrySet().stream().allMatch(e -> e.getKey().equals(e.getValue().getModelURI()))}
 * invariant	{@code !isComplete() || (atomicModelDescriptors.size() > 1 || atomicModelDescriptors.keySet().contains(rootModelURI))}
 * invariant	{@code !isComplete() || (atomicModelDescriptors.size() == 1 || coupledModelDescriptors.containsKey(rootModelURI))}
 * invariant	{@code !isComplete() || (!(atomicModelDescriptors.containsKey(rootModelURI) && coupledModelDescriptors.containsKey(rootModelURI)))}
 * invariant	{@code !isComplete() || coupledModelDescriptors.entrySet().stream().allMatch(e -> e.getKey().equals(e.getValue().getModelURI()))}
 * invariant	{@code !isComplete() || (coupledModelDescriptors.entrySet().stream().allMatch(e -> e.getValue() != null && e.getValue().submodelURIs.stream().allMatch(childURI -> atomicModelDescriptors.containsKey(childURI) || coupledModelDescriptors.containsKey(childURI))))}
 * invariant	{@code !isComplete() || (allModelsUnique(atomicModelDescriptors.keySet(), coupledModelDescriptors.keySet()))}
 * invariant	{@code !isComplete() || (atomicModelDescriptors.size() == 1 || architectureIsATree(atomicModelDescriptors.keySet(), rootModelURI, coupledModelDescriptors))}
 * invariant	{@code !isComplete() || (modelsParent.size() + 1 == (atomicModelDescriptors.size() + coupledModelDescriptors.size()))}
 * invariant	{@code !isComplete() || coupledModelDescriptors.values().stream().allMatch(cmd -> CoupledModelDescriptor.checkArchitecturalConsistency(cmd, atomicModelDescriptors, coupledModelDescriptors))}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code getAllAtomicModelsURIs().stream().allMatch(uri -> isAtomicModel(uri))}
 * invariant	{@code getAllModelURIs().stream().allMatch(uri -> isAtomicModel(uri) != isCoupledModel(uri))}
 * invariant	{@code !isComplete() || (getAllModelURIs().stream().allMatch(uri -> !isCoupledModel(uri) || getChildrenModelURIs(uri).size() > 0))}
 * </pre>
 * 
 * <p>Created on : 2017-10-30</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Architecture
implements	ArchitectureI
{
	// -------------------------------------------------------------------------
	// Variables and constants
	// -------------------------------------------------------------------------

	private static final long 				serialVersionUID = 1L;
	/** debug flag for all architecture and model creation and composition
	 *  classes.															*/
	public static boolean					DEBUG = false;

	/** URI of the simulation architecture described by the instance.		*/
	protected final String					architectureURI;
	/** the URI of the root model; if the model is composed of only one
	 *  atomic model, its URI is the root model URI, but if the model
	 *  contains at least one coupled model, then the URI of the root
	 *  model is a coupled model URI.										*/
	protected String						rootModelURI;
	/** Creation descriptors for atomic models.						 		*/
	protected final Map<String,AbstractAtomicModelDescriptor>
											atomicModelDescriptors;
	/** Creation descriptors for coupled models.						 	*/
	protected final Map<String,CoupledModelDescriptor>
											coupledModelDescriptors;
	/** Map from model URIs to their parent model URI.						*/
	protected final Map<String,String>		modelsParent;
	/** Simulation time unit used in this simulation model architecture.	*/
	protected TimeUnit						simulationTimeUnit;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean	implementationInvariants(Architecture instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.isComplete() || instance.rootModelURI != null,
				Architecture.class,
				instance,
				"!instance.isComplete() || instance.rootModelURI != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.modelsParent != null,
				Architecture.class,
				instance,
				"instance.modelsParent != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.modelsParent.containsKey(instance.rootModelURI),
				Architecture.class,
				instance,
				"!instance.modelsParent.containsKey(instance.rootModelURI)");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.isComplete() ||
					(instance.atomicModelDescriptors.size() > 1 ||
									instance.coupledModelDescriptors.isEmpty()),
				Architecture.class,
				instance,
				"!instance.isComplete() || "
				+ "(instance.atomicModelDescriptors.size() > 1 || "
				+ "instance.coupledModelDescriptors.isEmpty())");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.isComplete() ||
					instance.atomicModelDescriptors.entrySet().stream().allMatch(
							e -> e.getKey().equals(e.getValue().getModelURI())),
				Architecture.class,
				instance,
				"!isComplete() || atomicModelDescriptors.entrySet().stream()."
				+ "allMatch(e -> e.getKey().equals(e.getValue().getModelURI()))");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.isComplete() ||
						(instance.atomicModelDescriptors.size() > 1 ||
								instance.atomicModelDescriptors.keySet().
											contains(instance.rootModelURI)),
				Architecture.class,
				instance,
				"!instance.isComplete() || "
				+ "(instance.atomicModelDescriptors.size() > 1 || "
				+ "instance.atomicModelDescriptors.keySet().contains("
				+ "instance.rootModelURI))");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.isComplete() || 
						(instance.atomicModelDescriptors.size() == 1 ||
								instance.coupledModelDescriptors.containsKey(
														instance.rootModelURI)),
				Architecture.class,
				instance,
				"!instance.isComplete() || "
				+ "(instance.atomicModelDescriptors.size() == 1 || "
				+ "instance.coupledModelDescriptors.containsKey("
				+ "instance.rootModelURI))");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.isComplete() ||
						(!(instance.atomicModelDescriptors.containsKey(
							instance.rootModelURI) && 
								instance.coupledModelDescriptors.containsKey(
													instance.rootModelURI))),
				Architecture.class,
				instance,
				"!instance.isComplete() || "
				+ "(!(instance.atomicModelDescriptors.containsKey("
				+ "instance.rootModelURI) && instance.coupledModelDescriptors."
				+ "containsKey(instance.rootModelURI)))");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.isComplete() ||
						instance.coupledModelDescriptors.entrySet().stream().
							allMatch(e -> e.getKey().equals(
												e.getValue().getModelURI())),
				Architecture.class,
				instance,
				"!isComplete() || coupledModelDescriptors.entrySet().stream()."
				+ "allMatch(e -> e.getKey().equals(e.getValue().getModelURI()))");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.isComplete() ||
					(instance.coupledModelDescriptors.entrySet().stream().
						allMatch(e ->
							e.getValue() != null && 
							e.getValue().submodelURIs.stream().
								allMatch(
									childURI ->
										instance.atomicModelDescriptors.containsKey(childURI) ||
										instance.coupledModelDescriptors.containsKey(childURI)))),
				Architecture.class,
				instance,
				"!instance.isComplete() || "
				+ "(instance.coupledModelDescriptors.entrySet().stream()."
				+ "allMatch(e -> e.getValue() != null && "
				+ "e.getValue().submodelURIs.stream().allMatch(childURI -> "
				+ "instance.atomicModelDescriptors.containsKey(childURI) || "
				+ "instance.coupledModelDescriptors.containsKey(childURI))))");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.isComplete() ||
					(allModelsUnique(instance.atomicModelDescriptors.keySet(),
									 instance.coupledModelDescriptors.keySet())),
				Architecture.class,
				instance,
				"!instance.isComplete() || "
				+ "(allModelsUnique(instance.atomicModelDescriptors.keySet(), "
				+ "instance.coupledModelDescriptors.keySet()))");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.isComplete() ||
					(instance.atomicModelDescriptors.size() == 1 ||
					architectureIsATree(
							instance.atomicModelDescriptors.keySet(),
							instance.rootModelURI,
							instance.coupledModelDescriptors)),
				Architecture.class,
				instance,
				"!instance.isComplete() || (instance.atomicModelDescriptors."
				+ "size() == 1 || architectureIsATree(instance."
				+ "atomicModelDescriptors.keySet(), instance.rootModelURI, "
				+ "instance.coupledModelDescriptors))");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.isComplete() ||
					(instance.modelsParent.size() + 1 ==
						(instance.atomicModelDescriptors.size() +
								instance.coupledModelDescriptors.size())),
				Architecture.class,
				instance,
				"!instance.isComplete() || (instance.modelsParent.size() + 1 == "
				+ "(instance.atomicModelDescriptors.size() + "
				+ "instance.coupledModelDescriptors.size()))");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.isComplete() ||
					instance.coupledModelDescriptors.values().stream().allMatch(
						cmd -> cmd.checkArchitecturalConsistency(
										instance.atomicModelDescriptors,
										instance.coupledModelDescriptors)),
				Architecture.class,
				instance,
				"!isComplete() || coupledModelDescriptors.values().stream()."
				+ "allMatch(cmd -> CoupledModelDescriptor."
				+ "checkArchitecturalConsistency(cmd, atomicModelDescriptors, "
				+ "coupledModelDescriptors))");
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(Architecture instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				instance.getAllAtomicModelsURIs().stream().allMatch(
											uri -> instance.isAtomicModel(uri)),
				Architecture.class,
				instance,
				"instance.getAllAtomicModelsURIs().stream().allMatch("
				+ "uri -> instance.isAtomicModel(uri))");
		ret &= AssertionChecking.checkInvariant(
				instance.getAllModelURIs().stream().allMatch(
						uri -> instance.isAtomicModel(uri) !=
											instance.isCoupledModel(uri)),
				Architecture.class,
				instance,
				"instance.getAllModelURIs().stream().allMatch("
				+ "uri -> instance.isAtomicModel(uri) != "
				+ "instance.isCoupledModel(uri))");
		ret &= AssertionChecking.checkInvariant(
				!instance.isComplete() ||
					(instance.getAllModelURIs().stream().allMatch(
							uri -> !instance.isCoupledModel(uri) ||
								instance.getChildrenModelURIs(uri).size() > 0)),
				Architecture.class,
				instance,
				"!instance.isComplete() || (instance.getAllModelURIs()."
				+ "stream().allMatch(uri -> !instance.isCoupledModel(uri) || "
				+ "instance.getChildrenModelURIs(uri).size() > 0))");
		ret &= ArchitectureI.invariants(instance);
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a model architectural description given the parameters.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code rootModelURI != null && !rootModelURI.isEmpty()}
	 * pre	{@code atomicModelDescriptors != null}
	 * pre	{@code atomicModelDescriptors.entrySet().stream().allMatch(e -> e.getKey().equals(e.getValue().getModelURI()))}
	 * pre	{@code coupledModelDescriptors != null}
	 * pre	{@code coupledModelDescriptors.entrySet().stream().allMatch(e -> e.getKey().equals(e.getValue().getModelURI()))}
	 * pre	{@code atomicModelDescriptors.containsKey(rootModelURI) || coupledModelDescriptors.containsKey(rootModelURI)}
	 * pre	{@code simulationTimeUnit != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param rootModelURI				URI of the root model in this architecture.
	 * @param atomicModelDescriptors	map from atomic model URIs to their respective descriptor.
	 * @param coupledModelDescriptors	map from coupled model URIs to their respective descriptor.
	 * @param simulationTimeUnit		time unit used by all simulation clocks in the architecture.
	 * @throws Exception				<i>to do</i>.
	 */
	public				Architecture(
		String rootModelURI,
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors,
		TimeUnit simulationTimeUnit
		) throws Exception
	{
		// see http://www.asciiarmor.com/post/33736615/java-util-uuid-mini-faq
		this("devs-architecture-" + java.util.UUID.randomUUID().toString(),
			 rootModelURI, atomicModelDescriptors, coupledModelDescriptors,
			 simulationTimeUnit);
	}

	/**
	 * create a model architectural description given the parameters.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code architectureURI != null && !architectureURI.isEmpty()}
	 * pre	{@code rootModelURI != null && !rootModelURI.isEmpty()}
	 * pre	{@code atomicModelDescriptors != null}
	 * pre	{@code atomicModelDescriptors.entrySet().stream().allMatch(e -> e.getKey().equals(e.getValue().getModelURI()))}
	 * pre	{@code coupledModelDescriptors != null}
	 * pre	{@code coupledModelDescriptors.entrySet().stream().allMatch(e -> e.getKey().equals(e.getValue().getModelURI()))}
	 * pre	{@code atomicModelDescriptors.containsKey(rootModelURI) || coupledModelDescriptors.containsKey(rootModelURI)}
	 * pre	{@code simulationTimeUnit != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI			URI of this simulation architecture.
	 * @param rootModelURI				URI of the root model in this architecture.
	 * @param atomicModelDescriptors	map from atomic model URIs to their respective descriptor.
	 * @param coupledModelDescriptors	map from coupled model URIs to their respective descriptor.
	 * @param simulationTimeUnit		time unit used by all simulation clocks in the architecture.
	 */
	public				Architecture(
		String architectureURI,
		String rootModelURI,
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors,
		TimeUnit simulationTimeUnit
		)
	{
		assert	architectureURI != null && !architectureURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "architectureURI != null && !architectureURI.isEmpty()");
		assert	rootModelURI != null && !rootModelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "rootModelURI != null && !rootModelURI.isEmpty()");
		assert	atomicModelDescriptors != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "atomicModelDescriptors != null");
		assert	atomicModelDescriptors.entrySet().stream().allMatch(
						e -> e.getKey().equals(e.getValue().getModelURI())) :
				new NeoSim4JavaException("Precondition violation: "
						+ "atomicModelDescriptors.entrySet().stream().allMatch("
						+ "e -> e.getKey().equals(e.getValue().getModelURI()))");
		assert	coupledModelDescriptors != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "coupledModelDescriptors != null");
		assert	coupledModelDescriptors.entrySet().stream().allMatch(
						e -> e.getKey().equals(e.getValue().getModelURI())) :
				new NeoSim4JavaException("Precondition violation: "
						+ "coupledModelDescriptors.entrySet().stream().allMatch("
						+ "e -> e.getKey().equals(e.getValue().getModelURI()))");
		assert	atomicModelDescriptors.containsKey(rootModelURI) ||
						coupledModelDescriptors.containsKey(rootModelURI) :
				new NeoSim4JavaException("Precondition violation: "
						+ "atomicModelDescriptors.containsKey(rootModelURI) || "
						+ "coupledModelDescriptors.containsKey(rootModelURI)");
		assert	simulationTimeUnit != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationTimeUnit != null");

		this.architectureURI = architectureURI;
		this.rootModelURI = rootModelURI;
		this.modelsParent = new HashMap<String,String>();
		this.atomicModelDescriptors = atomicModelDescriptors;
		this.coupledModelDescriptors = coupledModelDescriptors;
		for (String modelURI : coupledModelDescriptors.keySet()) {
			for (String childURI : coupledModelDescriptors.
											get(modelURI).submodelURIs) {
				this.modelsParent.put(childURI, modelURI);
			}
		}
		this.simulationTimeUnit = simulationTimeUnit;

		// Invariants checking
		assert	Architecture.implementationInvariants(this) :
				new NeoSim4JavaException(
						"Architecture.implementationInvariants(this)");
		assert	Architecture.invariants(this) :
				new NeoSim4JavaException("Architecture.invariants");
	}

	// -------------------------------------------------------------------------
	// Correctness tests for the representation of the architecture.
	// -------------------------------------------------------------------------

	/**
	 * return true if all of the model URIs are uniquely defined in the
	 * architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code atomicModelsURIs != null}
	 * pre	{@code atomicModelsURIs.stream().allMatch(u -> u != null && !u.isEmpty())}
	 * pre	{@code coupledModelURIs != null}
	 * pre	{@code coupledModelURIs.stream().allMatch(u -> u != null && !u.isEmpty())}
	 * pre	{@code atomicModelsURIs.size() == 1 || coupledModels != null}
	 * pre	{@code atomicModelsURIs.size() > 1 || coupledModels == null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param atomicModelsURIs	set of URIs of the atomic models in the architecture.
	 * @param coupledModelURIs	set of URIs of the coupled models in the architecture.
	 * @return					true if all of the models a unique in the architecture.
	 */
	protected static boolean	allModelsUnique(
		Set<String> atomicModelsURIs,
		Set<String> coupledModelURIs
		)
	{
		assert	atomicModelsURIs != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "atomicModelsURIs != null");
		assert	atomicModelsURIs.stream().allMatch(
										u -> u != null && !u.isEmpty()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "atomicModelsURIs.stream().allMatch("
						+ "u -> u != null && !u.isEmpty())");
		assert	coupledModelURIs != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "coupledModelURIs != null");
		assert	coupledModelURIs.stream().allMatch(
										u -> u != null && !u.isEmpty()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "coupledModelURIs.stream().allMatch("
						+ "u -> u != null && !u.isEmpty())");
		assert	atomicModelsURIs.size() == 1 || !coupledModelURIs.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "atomicModelsURIs.size() == 1 || "
						+ "!coupledModelURIs.isEmpty()");
		assert	atomicModelsURIs.size() > 1 || coupledModelURIs.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "atomicModelsURIs.size() > 1 || "
						+ "coupledModelURIs.isEmpty()");

		boolean allUnique = true;
		if (atomicModelsURIs.size() > 1) {
			for (String uri : atomicModelsURIs) {
				allUnique &= !coupledModelURIs.contains(uri);
			}
			for (String uri : coupledModelURIs) {
				allUnique &= !atomicModelsURIs.contains(uri);
			}
		}
		return allUnique;
	}

	/**
	 * return true if the model architecture is a tree i.e., every model,
	 * atomic or coupled, appears once and only once in the coupled models,
	 * except for the root coupled model. 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * TODO
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param atomicModelsURIs			set of URIs of the atomic models in the architecture.
	 * @param rootModelURI				URI of the root model in the architecture.
	 * @param coupledModelDescriptors	map of the URIs of the coupled models to the set of URIs of their composed models.
	 * @return							true if the architecture is a model tree.
	 */
	public static boolean	architectureIsATree(
//	protected static boolean	architectureIsATree(
		Set<String> atomicModelsURIs,
		String rootModelURI,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		boolean ret = true;	
		if (atomicModelsURIs.size() > 1) {
			int count;
			for (String atomicModelURI : atomicModelsURIs) {
				count = 0;
				for (String coupledModelURI : coupledModelDescriptors.keySet()) {
					if (coupledModelDescriptors.get(coupledModelURI).
									submodelURIs.contains(atomicModelURI)) {
						count++;
					}
				}
				ret &= (count == 1);
			}
			for (String coupledModelURI : coupledModelDescriptors.keySet()) {
				count = 0;
				for (String curi : coupledModelDescriptors.keySet()) {
					if (coupledModelDescriptors.get(curi).
									submodelURIs.contains(coupledModelURI)) {
						count++;
					}
				}
				if (rootModelURI.equals(coupledModelURI)) {
					ret &= (count == 0);
				} else {
					ret &= (count == 1);
				}
			}
		}
		return ret;
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getArchitectureURI()
	 */
	@Override
	public String		getArchitectureURI()
	{
		return this.architectureURI;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isMonoModel()
	 */
	@Override
	public boolean		isMonoModel()
	{
		assert	this.isComplete() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isComplete()");

		return this.atomicModelDescriptors.size() == 1 &&
					this.coupledModelDescriptors.size() == 0 &&
						this.atomicModelDescriptors.keySet().
												contains(this.rootModelURI);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isRealTime()
	 */
	@Override
	public boolean		isRealTime()
	{
		return false;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isAtomicModel(java.lang.String)
	 */
	@Override
	public boolean		isAtomicModel(String uri)
	{
		assert	uri != null && !uri.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
										+ "uri != null && !uri.isEmpty()");

		return this.atomicModelDescriptors.keySet().contains(uri);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isCoupledModel(java.lang.String)
	 */
	@Override
	public boolean		isCoupledModel(String uri)
	{
		assert	uri != null && !uri.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
										+ "uri != null && !uri.isEmpty()");

		return this.coupledModelDescriptors.keySet().contains(uri);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isModel(java.lang.String)
	 */
	@Override
	public boolean		isModel(String uri)
	{
		assert	uri != null && !uri.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
										+ "uri != null && !uri.isEmpty()");

		return this.isAtomicModel(uri) || this.isCoupledModel(uri);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getModelDescriptor(java.lang.String)
	 */
	@Override
	public ModelDescriptorI	getModelDescriptor(String uri)
	{
		assert	uri != null && !uri.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
										+ "uri != null && !uri.isEmpty()");

		if (this.isCoupledModel(uri)) {
			return this.coupledModelDescriptors.get(uri);
		} else {
			return this.atomicModelDescriptors.get(uri);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isSimulationTimeUnitSet()
	 */
	@Override
	public boolean		isSimulationTimeUnitSet()
	{
		return this.simulationTimeUnit != null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getSimulationTimeUnit()
	 */
	@Override
	public TimeUnit		getSimulationTimeUnit()
	{
		return this.simulationTimeUnit;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isRootModel(java.lang.String)
	 */
	@Override
	public boolean		isRootModel(String uri)
	{
		assert	uri != null && !uri.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
										+ "uri != null && !uri.isEmpty()");

		return this.rootModelURI != null && this.rootModelURI.equals(uri);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isChildModelOf(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean		isChildModelOf(
		String childURI,
		String parentURI
		)
	{
		assert	childURI != null && !childURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
							+ "childURI != null && !childURI.isEmpty()");
		assert	parentURI != null && !parentURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
							+ "parentURI != null && !parentURI.isEmpty()");
		assert	this.isModel(childURI) && this.isCoupledModel(parentURI) :
				new NeoSim4JavaException("Precondition violation: "
							+ "isModel(childURI) && isCoupledModel(parentURI)");

		return this.modelsParent.get(childURI).equals(parentURI);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isDescendant(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean		isDescendant(
		String descendantURI,
		String ancestorURI
		) throws Exception
	{
		assert	descendantURI != null && !descendantURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "descendantURI != null && !descendantURI.isEmpty()");
		assert	ancestorURI != null && !ancestorURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "ancestorURI != null && !ancestorURI.isEmpty()");
		assert	isModel(descendantURI) && isCoupledModel(ancestorURI) :
				new NeoSim4JavaException("Precondition violation: "
						+ "isModel(descendantURI) && "
						+ "isCoupledModel(ancestorURI)");

		boolean ret = false;
		String uri = descendantURI;
		while (!ret && uri != null) {
			ret = this.isChildModelOf(uri, ancestorURI);
			if (!ret) {
				uri = this.getParentURI(uri);
			}
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getRootModelURI()
	 */
	@Override
	public String		getRootModelURI()
	{
		return this.rootModelURI;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getAllModelURIs()
	 */
	@Override
	public Set<String>	getAllModelURIs()
	{
		Set<String> ret = new HashSet<String>();
		for (String uri : this.atomicModelDescriptors.keySet()) {
			ret.add(uri);
		}
		for (String uri : this.coupledModelDescriptors.keySet()) {
			ret.add(uri);
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getAllAtomicModelsURIs()
	 */
	@Override
	public Set<String>	getAllAtomicModelsURIs()
	{
		Set<String> ret = new HashSet<String>();
		for (String uri : this.atomicModelDescriptors.keySet()) {
			ret.add(uri);
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getParentURI(java.lang.String)
	 */
	@Override
	public String		getParentURI(String modelURI)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "modelURI != null && !modelURI.isEmpty()");

		String ret = null;
		if (this.modelsParent.containsKey(modelURI)) {
			ret = this.modelsParent.get(modelURI);
		}
		assert	!this.isComplete() ||
					(ret != null || modelURI.equals(this.getRootModelURI()));
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getChildrenModelURIs(java.lang.String)
	 */
	@Override
	public Set<String>	getChildrenModelURIs(String uri)
	{
		assert	uri != null && !uri.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
									+ "uri != null && !uri.isEmpty()");
		assert	isCoupledModel(uri) :
				new NeoSim4JavaException("Precondition violation: "
									+ "isCoupledModel(uri)");

		Set<String> ret = new HashSet<String>();
		if (this.coupledModelDescriptors.containsKey(uri)) {
			for (String child :  this.coupledModelDescriptors.
												get(uri).submodelURIs) {
				ret.add(child);
			}
		}
		return ret;
	}

	
	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#getDescendantModels(java.lang.String)
	 */
	@Override
	public Set<String>	getDescendantModels(String uri)
	{
		assert	uri != null && !uri.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
									+ "uri != null && !uri.isEmpty()");

		HashSet<String> ret = new HashSet<String>();
		if (this.coupledModelDescriptors.containsKey(uri)) {
			for (String childURI : this.getChildrenModelURIs(uri)) {
				ret.add(childURI);
				ret.addAll(this.getDescendantModels(childURI));
			}
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#setSimulationTimeUnit(java.util.concurrent.TimeUnit)
	 */
	@Override
	public void			setSimulationTimeUnit(TimeUnit tu)
	{
		assert	!isSimulationTimeUnitSet() :
				new NeoSim4JavaException("Precondition violation: "
									+ "!isSimulationTimeUnitSet()");
		assert	tu != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "tu != null");

		this.simulationTimeUnit = tu;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#addAtomicModel(java.lang.String, fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor)
	 */
	@Override
	public void			addAtomicModel(
		String modelURI,
		AtomicModelDescriptor descriptor
		)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
								+ "modelURI != null && !modelURI.isEmpty()");
		assert	!isModel(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
						+ "!isModel(modelURI)");
		assert	descriptor != null && descriptor.modelURI.equals(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
								+ "descriptor != null && "
								+ "descriptor.modelURI.equals(modelURI)");

		this.atomicModelDescriptors.put(modelURI, descriptor);

		assert	this.isAtomicModel(modelURI);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#addAtomicModelAsRoot(java.lang.String, fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor)
	 */
	@Override
	public void			addAtomicModelAsRoot(
		String modelURI,
		AtomicModelDescriptor descriptor
		)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
								+ "modelURI != null && !modelURI.isEmpty()");
		assert	!isModel(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
						+ "!isModel(modelURI)");
		assert	this.getAllModelURIs().size() == 0 :
				new NeoSim4JavaException("Precondition violation: "
								+ "getAllModelURIs().size() == 0");
		assert	descriptor != null && descriptor.modelURI.equals(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
								+ "descriptor != null && "
								+ "descriptor.modelURI.equals(modelURI)");

		this.addAtomicModel(modelURI, descriptor);
		this.rootModelURI = modelURI;

		assert	this.isAtomicModel(modelURI);
		assert	this.isRootModel(modelURI);
		assert	this.isMonoModel();
		assert	this.isComplete();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#addCoupledModel(java.lang.String, fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor)
	 */
	@Override
	public void			addCoupledModel(
		String modelURI,
		CoupledModelDescriptor descriptor
		)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
								+ "modelURI != null && !modelURI.isEmpty()");
		assert	!isModel(modelURI) :
				new NeoSim4JavaException("Precondition violation: !isModel(modelURI)");
		assert	descriptor != null && descriptor.modelURI.equals(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
								+ "descriptor != null && "
								+ "descriptor.modelURI.equals(modelURI)");

		this.coupledModelDescriptors.put(modelURI, descriptor);

		assert	this.isCoupledModel(modelURI);
		assert	!this.isMonoModel();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#addCoupledModelAsRoot(java.lang.String, fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor)
	 */
	@Override
	public void			addCoupledModelAsRoot(
		String modelURI,
		CoupledModelDescriptor descriptor
		)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
							+ "modelURI != null && !modelURI.isEmpty()");
		assert	!isModel(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
						+ "!isModel(modelURI)");
		assert	this.getRootModelURI() == null :
				new NeoSim4JavaException("Precondition violation: "
							+ "getRootModelURI() == null");
		assert	descriptor != null && descriptor.modelURI.equals(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
							+ "descriptor != null && "
							+ "descriptor.modelURI.equals(modelURI)");

		this.addCoupledModel(modelURI, descriptor);
		this.rootModelURI = modelURI;

		assert	this.isCoupledModel(modelURI);
		assert	!this.isMonoModel();
		assert	this.isRootModel(modelURI);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isComplete()
	 */
	@Override
	public boolean		isComplete()
	{
		boolean ret = this.rootModelURI != null &&
										this.isSimulationTimeUnitSet();
		if (ret) {
			if (this.atomicModelDescriptors.containsKey(this.rootModelURI)) {
				// implies that the architecture is mono-model
				ret = true;
			} else {
				// implies that the architecture is not mono-model hence the
				// root must appear in the coupled model descriptors and all
				// of it descendant models must be a model in the architecture.
				assert	this.coupledModelDescriptors.containsKey(rootModelURI);
				for (String d : this.getDescendantModels(rootModelURI)) {
					this.isModel(d);
				}
			}
		}
		if (ret) {
			assert	Architecture.architectureIsATree(
										this.atomicModelDescriptors.keySet(),
										this.rootModelURI,
										this.coupledModelDescriptors);
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#isTree()
	 */
	@Override
	public boolean		isTree()
	{
		assert	isComplete() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isComplete()");

		return Architecture.architectureIsATree(
								this.atomicModelDescriptors.keySet(),
								this.rootModelURI,
								this.coupledModelDescriptors);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#topologicalSort()
	 */
	@Override
	public List<String>	topologicalSort()
	{
		assert	isComplete() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isComplete()");
		assert	isTree() :
				new NeoSim4JavaException("Precondition violation: isTree()");
			

		List<String> ret = this.depthFirstTraversal(this.rootModelURI);

		assert	ret.size() == this.atomicModelDescriptors.size() +
								this.coupledModelDescriptors.size();
		assert	ret.containsAll(this.atomicModelDescriptors.keySet());
		assert	ret.containsAll(this.coupledModelDescriptors.keySet());

		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#topologicalSort(java.lang.String)
	 */
	@Override
	public List<String>	topologicalSort(String modelURI)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "modelURI != null && !modelURI.isEmpty()");
		assert	isModel(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
						+ "isModel(modelURI)");
		assert	isComplete() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isComplete()");
		assert	isTree() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isTree()");

		return this.depthFirstTraversal(modelURI);
	}

	/**
	 * return a depth-first traversal of the subtree as a list of model URIs.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code isModel(modelURI)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI	URI of a model root of a subtree in the architecture.
	 * @return			a depth-first traversal of the subtree as a list of model URIs.
	 */
	protected List<String>	depthFirstTraversal(String modelURI)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "modelURI != null && !modelURI.isEmpty()");
		assert	isModel(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
						+ "isModel(modelURI)");

		ArrayList<String> ret = null;
		if (this.atomicModelDescriptors.containsKey(modelURI)) {
			ret = new ArrayList<String>(1);
			ret.add(modelURI);
		} else {
			ret = new ArrayList<String>();
			Stack<String> traversed = new Stack<String>();
			traversed.push(modelURI);
			Stack<List<String>> toBeProcessed = new Stack<List<String>>();
			ArrayList<String> hs = new ArrayList<String>();
			Set<String> children =
				this.coupledModelDescriptors.get(modelURI).submodelURIs;
			hs = new ArrayList<String>(children.size());
			hs.addAll(children);
			toBeProcessed.push(hs);
			while (!toBeProcessed.empty()) {
				List<String> top = toBeProcessed.peek();
				if (top.isEmpty()) {
					toBeProcessed.pop();
					ret.add(traversed.pop());
				} else {
					int last = top.size() - 1;
					String uri = top.remove(last);
					if (this.atomicModelDescriptors.containsKey(uri)) {
						ret.add(uri);
					} else {
						traversed.push(uri);
						children =
							this.coupledModelDescriptors.get(uri).submodelURIs;
						hs = new ArrayList<String>(children.size());
						hs.addAll(children);
						toBeProcessed.push(hs);
					}
				}
			}
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#constructSimulator()
	 */
	@Override
	public SimulatorI	constructSimulator()
	{
		if (DEBUG) {
			System.out.println("ModelArchitectureI#constructSimulator(): "
													+ this.rootModelURI);
		}

		assert	isComplete() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isComplete()");

		Iterator<String> iter = this.topologicalSort().iterator();
		Map<String,ModelI> createdModels = new HashMap<>();
		while (iter.hasNext()) {
			String uri = iter.next();
			ModelI m = null;
			if (this.atomicModelDescriptors.containsKey(uri)) {
				m = this.atomicModelDescriptors.get(uri).createAtomicModel();
			} else {
				assert	this.coupledModelDescriptors.containsKey(uri);
				ModelI[] models =
						new ModelI[this.coupledModelDescriptors.get(uri).
					        							submodelURIs.size()];
				int i = 0;
				for (String childURI :
						this.coupledModelDescriptors.get(uri).submodelURIs) {
					models[i++] = createdModels.get(childURI);
				}
				m = this.coupledModelDescriptors.get(uri).
												createCoupledModel(models);
			}
			createdModels.put(uri, m);
		}
		return createdModels.get(this.rootModelURI).getSimulationEngine();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.ArchitectureI#constructSimulator(java.lang.String)
	 */
	@Override
	public SimulatorI	constructSimulator(String modelURI)
	{
		if (DEBUG) {
			System.out.println(
				"ModelArchitectureI#constructSimulator(String) 1: "
															+ modelURI);
		}

		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
								+ "modelURI != null && !modelURI.isEmpty()");
		assert	isModel(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
						+ "isModel(modelURI)");
		assert	isComplete() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isComplete()");

		Iterator<String> iter = this.topologicalSort(modelURI).iterator();
		Map<String,ModelI> createdModels = new HashMap<String,ModelI>();
		while (iter.hasNext()) {
			String uri = iter.next();
			ModelI m = null;
			if (this.atomicModelDescriptors.containsKey(uri)) {
				m = this.atomicModelDescriptors.get(uri).createAtomicModel();
			} else {
				assert	this.coupledModelDescriptors.containsKey(uri);
				ModelI[] models =
					new ModelI[this.coupledModelDescriptors.get(uri).
					        							submodelURIs.size()];
				int i = 0;
				for (String childURI :
						this.coupledModelDescriptors.get(uri).submodelURIs) {
					models[i++] = createdModels.get(childURI);
				}
				m = this.coupledModelDescriptors.get(uri).
												createCoupledModel(models);
			}
			if (DEBUG) {
				System.out.println(
					"ModelArchitectureI#constructSimulator(String): 2\n" +
													m.modelAsString(""));
			}
			createdModels.put(uri, m);
		}
		return createdModels.get(modelURI).getSimulationEngine();
	}
}
// -----------------------------------------------------------------------------

