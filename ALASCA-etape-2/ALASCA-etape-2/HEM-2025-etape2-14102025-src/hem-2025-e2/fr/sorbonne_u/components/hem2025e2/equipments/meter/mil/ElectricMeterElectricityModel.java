package fr.sorbonne_u.components.hem2025e2.equipments.meter.mil;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
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
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.GlobalSimulationConfigurationI;
import fr.sorbonne_u.components.hem2025e2.utils.Electricity;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelImportedVariables;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>ElectricMeterElectricityModel</code> defines the simulation
 * model for the electric meter electricity consumption.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This model is an HIOA model that imports variables, hence shows how this kind
 * of models are programmed.
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: none</li>
 * <li>Imported variables:
 *   name = {@code currentHeaterIntensity}, type = {@code Double}</li>
 * <li>Exported variables:
 *   name = {@code currentHairDryerIntensity}, type = {@code Double}</li>
 * </ul>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code STEP > 0.0}
 * invariant	{@code evaluationStep.getSimulatedDuration() > 0.0}
 * invariant	{@code currentHeaterIntensity == null || !currentHeaterIntensity.isInitialised() || currentHeaterIntensity.getValue() >= 0.0}
 * invariant	{@code currentHairDryerIntensity == null || !currentHairDryerIntensity.isInitialised() || currentHairDryerIntensity.getValue() >= 0.0}
 * invariant	{@code currentIntensity != null && (!currentIntensity.isInitialised() || currentIntensity.getValue() >= 0.0)}
 * invariant	{@code currentConsumption != null && (!currentConsumption.isInitialised() || currentConsumption.getValue() >= 0.0)}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code URI != null && !URI.isEmpty()}
 * </pre>
 * 
 * <p>Created on : 2023-10-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//-----------------------------------------------------------------------------
@ModelImportedVariables(
	{@ModelImportedVariable(name = "currentHeaterIntensity",
							type = Double.class),
	 @ModelImportedVariable(name = "currentHairDryerIntensity",
	 						type = Double.class)})
// -----------------------------------------------------------------------------
public class			ElectricMeterElectricityModel
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String		URI = ElectricMeterElectricityModel.class.
																getSimpleName();

	/** evaluation step for the equation (assumed in hours).				*/
	protected static final double	STEP = 60.0/3600.0;	// 60 seconds

	/** evaluation step as a duration, including the time unit.				*/
	protected final Duration		evaluationStep;

	/** final report of the simulation run.									*/
	protected ElectricMeterElectricityReport	finalReport;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** current intensity of the heater in amperes.							*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>			currentHeaterIntensity;
	/** current intensity of the heater in amperes.							*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>			currentHairDryerIntensity;

	/** current total intensity of the house in the power unit defined by
	 *  the electric meter.													*/
	@InternalVariable(type = Double.class)
	protected final Value<Double>	currentIntensity =
												new Value<Double>(this);
	/** current total consumption of the house in kwh.						*/
	@InternalVariable(type = Double.class)
	protected final Value<Double>	currentConsumption =
												new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false
	 * otherwise.
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
	protected static boolean	implementationInvariants(
		ElectricMeterElectricityModel instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				STEP > 0.0,
				ElectricMeterElectricityModel.class,
				instance,
				"STEP > 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.evaluationStep.getSimulatedDuration() > 0.0,
				ElectricMeterElectricityModel.class,
				instance,
				"evaluationStep.getSimulatedDuration() > 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentHeaterIntensity == null ||
					!instance.currentHeaterIntensity.isInitialised() ||
							instance.currentHeaterIntensity.getValue() >= 0.0,
				ElectricMeterElectricityModel.class,
				instance,
				"currentHeaterIntensity == null || "
				+ "!currentHeaterIntensity.isInitialised() || "
				+ "currentHeaterIntensity.getValue() >= 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentHairDryerIntensity == null ||
					!instance.currentHairDryerIntensity.isInitialised() ||
						instance.currentHairDryerIntensity.getValue() >= 0.0,
				ElectricMeterElectricityModel.class,
				instance,
				"currentHairDryerIntensity == null || !i "
				+ "currentHairDryerIntensity.isInitialised() || "
				+ "currentHairDryerIntensity.getValue() >= 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentIntensity != null &&
					(!instance.currentIntensity.isInitialised() ||
								instance.currentIntensity.getValue() >= 0.0),
				ElectricMeterElectricityModel.class,
				instance,
				"currentIntensity != null && "
				+ "(!currentIntensity.isInitialised() || "
				+ "currentIntensity.getValue() >= 0.0)");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentConsumption != null &&
					(!instance.currentConsumption.isInitialised() ||
								instance.currentConsumption.getValue() >= 0.0),
				ElectricMeterElectricityModel.class,
				instance,
				"currentConsumption != null && "
				+ "(!currentConsumption.isInitialised() || "
				+ "currentConsumption.getValue() >= 0.0)");
		return ret;
	}

	/**
	 * return true if the static invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return			true if the invariants are observed, false otherwise.
	 */
	protected static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= GlobalSimulationConfigurationI.staticInvariants();
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				ElectricMeterElectricityModel.class,
				"URI != null && !URI.isEmpty()");
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
	protected static boolean	invariants(
		ElectricMeterElectricityModel instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an <code>ElectricMeterElectricityModel</code> instance.
	 * 
	 * <pre>
	 * pre	{@code uri == null || !uri.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine == null || !simulationEngine.isModelSet()}
	 * pre	{@code simulationEngine == null || simulationEngine instanceof AtomicEngine}
	 * post	{@code !isDebugModeOn()}
	 * post	{@code getURI() != null && !getURI().isEmpty()}
	 * post	{@code uri == null || getURI().equals(uri)}
	 * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
	 * post	{@code getSimulationEngine().equals(simulationEngine)}
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do</i>.
	 */
	public				ElectricMeterElectricityModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.evaluationStep = new Duration(STEP, this.getSimulatedTimeUnit());
		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	ElectricMeterElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"ElectricMeterElectricityModel."
						+ "implementationInvariants(this)");
		assert	ElectricMeterElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"ElectricMeterElectricityModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * update the total electricity consumption in kwh given the current
	 * intensity has been constant for the duration {@code d}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	duration for which the intensity has been maintained.
	 */
	protected void		updateConsumption(Duration d)
	{
		double c = this.currentConsumption.getValue();
		c += Electricity.computeConsumption(
				d,
				ElectricMeter.TENSION.getData()*this.currentIntensity.getValue());
		Time t = this.currentConsumption.getTime().add(d);
		this.currentConsumption.setNewValue(c, t);

		assert	ElectricMeterElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"ElectricMeterElectricityModel."
						+ "implementationInvariants(this)");
		assert	ElectricMeterElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"ElectricMeterElectricityModel.invariants(this)");
	}

	/**
	 * compute the current total intensity.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return the current total intensity of electric consumption.
	 */
	protected double		computeTotalIntensity()
	{
		// simple sum of all incoming intensities
		return this.currentHairDryerIntensity.getValue()
					+ this.currentHeaterIntensity.getValue();
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#useFixpointInitialiseVariables()
	 */
	@Override
	public boolean		useFixpointInitialiseVariables()
	{
		return true;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer, Integer>	fixpointInitialiseVariables()
	{
		int justInitialised = 0;
		int notInitialisedYet = 0;

		if (!this.currentIntensity.isInitialised() &&
							this.currentHairDryerIntensity.isInitialised() &&
							this.currentHeaterIntensity.isInitialised()) {
			double i = this.computeTotalIntensity();
			this.currentIntensity.initialise(i);
			this.currentConsumption.initialise(0.0);
			justInitialised += 2;
		} else if (!this.currentIntensity.isInitialised()) {
			notInitialisedYet += 2;
		}

		assert	ElectricMeterElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"ElectricMeterElectricityModel."
						+ "implementationInvariants(this)");
		assert	ElectricMeterElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"ElectricMeterElectricityModel.invariants(this)");

		return new Pair<>(justInitialised, notInitialisedYet);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		// The model does not export any event.
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		// trigger a new internal transition at each evaluation step duration
		return this.evaluationStep;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		// update the current consumption since the last consumption update.
		// must be done before recomputing the instantaneous intensity.
		this.updateConsumption(elapsedTime);
		// recompute the current total intensity
		double i = this.computeTotalIntensity();
		this.currentIntensity.setNewValue(i, this.getCurrentStateTime());

		// Tracing
		if (this.currentIntensity.isInitialised()) {
			StringBuffer message = new StringBuffer("current total consumption: ");
			message.append(this.currentIntensity.getValue());
			message.append(" at ");
			message.append(this.getCurrentStateTime());
			this.logMessage(message.toString());
		}

		assert	ElectricMeterElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"ElectricMeterElectricityModel."
						+ "implementationInvariants(this)");
		assert	ElectricMeterElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"ElectricMeterElectricityModel.invariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		this.updateConsumption(
						endTime.subtract(this.currentConsumption.getTime()));

		// must capture the current consumption before the finalisation
		// reinitialise the internal model variable.
		this.finalReport = new ElectricMeterElectricityReport(
											URI,
											this.currentConsumption.getValue());

		this.logMessage("simulation ends.");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * The class <code>ElectricMeterElectricityReport</code> implements the
	 * simulation report for the <code>ElectricMeterElectricityModel</code>.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no invariant
	 * </pre>
	 * 
	 * <p>Created on : 2021-10-01</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class		ElectricMeterElectricityReport
	implements	SimulationReportI, GlobalReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption; // in kwh

		public			ElectricMeterElectricityReport(
			String modelURI,
			double totalConsumption
			)
		{
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
		}

		@Override
		public String	getModelURI()
		{
			return this.modelURI;
		}

		@Override
		public String	printout(String indent)
		{
			StringBuffer ret = new StringBuffer(indent);
			ret.append("---\n");
			ret.append(indent);
			ret.append('|');
			ret.append(this.modelURI);
			ret.append(" report\n");
			ret.append(indent);
			ret.append('|');
			ret.append("total consumption in kwh = ");
			ret.append(this.totalConsumption);
			ret.append(".\n");
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}		
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return this.finalReport;
	}
}
// -----------------------------------------------------------------------------
