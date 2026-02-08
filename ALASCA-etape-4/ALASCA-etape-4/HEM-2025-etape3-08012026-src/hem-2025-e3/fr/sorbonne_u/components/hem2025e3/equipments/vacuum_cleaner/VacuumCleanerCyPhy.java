package fr.sorbonne_u.components.hem2025e3.equipments.vacuum_cleaner;

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

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.annotations.LocalArchitecture;
import fr.sorbonne_u.components.cyphy.annotations.SIL_Simulation_Architectures;
import fr.sorbonne_u.components.cyphy.interfaces.CyPhyReflectionCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e3.equipments.vacuum_cleaner.sil.VacuumCleanerStateSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.vacuum_cleaner.sil.Local_SIL_SimulationArchitectures;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerUserCI;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerInboundPort;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetHighVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetLowVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOffVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOnVacuumCleaner;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Supplier;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.AbstractPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>VacuumCleanerCyPhy</code> implements the cyber-physical
 * component version of the vacuum cleaner.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * The vacuum cleaner is an uncontrollable appliance, hence it does not connect
 * with the household energy manager. However, it will connect later to the
 * electric panel to take its (simulated) electricity consumption into account.
 * </p>
 * 
 * <p>
 * <strong>Implementation Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * INITIAL_STATE != null
 * }
 * invariant	{@code
 * INITIAL_MODE != null
 * }
 * invariant	{@code
 * currentState != null
 * }
 * invariant	{@code
 * currentMode != null
 * }
 * invariant	{@code
 * NUMBER_OF_STANDARD_THREADS >= 0
 * }
 * invariant	{@code
 * NUMBER_OF_SCHEDULABLE_THREADS >= 0
 * }
 * invariant	{@code
 * localArchitectureURI == null || !localArchitectureURI.isEmpty() && accelerationFactor > 0.0
 * }
 * invariant	{@code
 * asp == null || localArchitectureURI != null
 * }
 * </pre>
 * 
 * <p>
 * <strong>Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * REFLECTION_INBOUND_PORT_URI != null && !REFLECTION_INBOUND_PORT_URI.isEmpty()
 * }
 * invariant	{@code
 * INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty()
 * }
 * invariant	{@code
 * UNIT_TEST_ARCHITECTURE_URI != null && !UNIT_TEST_ARCHITECTURE_URI.isEmpty()
 * }
 * invariant	{@code
 * INTEGRATION_TEST_ARCHITECTURE_URI != null && !INTEGRATION_TEST_ARCHITECTURE_URI.isEmpty()
 * }
 * invariant	{@code
 * HIGH_POWER != null && HIGH_POWER.getData() > 0.0 && HIGH_POWER.getMeasurementUnit().equals(POWER_UNIT)
 * }
 * invariant	{@code
 * LOW_POWER != null && LOW_POWER.getData() > 0.0 && LOW_POWER.getMeasurementUnit().equals(POWER_UNIT)
 * }
 * invariant	{@code
 * TENSION != null && (TENSION.getData() == 110.0 || TENSION.getData() == 220.0)
 *         && TENSION.getMeasurementUnit().equals(TENSION_UNIT)
 * }
 * invariant	{@code
 * INITIAL_STATE != null && INITIAL_MODE != null
 * }
 * invariant	{@code
 * X_RELATIVE_POSITION >= 0
 * }
 * invariant	{@code
 * Y_RELATIVE_POSITION >= 0
 * }
 * </pre>
 * 
 * <p>
 * Created on : 2026-06-06
 * </p>
 * 
 * @author Team
 */
// -----------------------------------------------------------------------------
@SIL_Simulation_Architectures({
        @LocalArchitecture(uri = "silUnitTests", rootModelURI = "VacuumCleanerCoupledModel", simulatedTimeUnit = TimeUnit.HOURS, externalEvents = @ModelExternalEvents()),
        @LocalArchitecture(uri = "silIntegrationTests", rootModelURI = "VacuumCleanerStateSILModel", simulatedTimeUnit = TimeUnit.HOURS, externalEvents = @ModelExternalEvents(imported = {}, exported = {
                SwitchOnVacuumCleaner.class,
                SwitchOffVacuumCleaner.class,
                SetHighVacuumCleaner.class,
                SetLowVacuumCleaner.class }))
})
// -----------------------------------------------------------------------------
@OfferedInterfaces(offered = { VacuumCleanerUserCI.class })
// -----------------------------------------------------------------------------
public class VacuumCleanerCyPhy
        extends AbstractCyPhyComponent
        implements VacuumCleanerImplementationI {
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    /** standard URI of the vacuum cleaner reflection inbound port. */
    public static final String REFLECTION_INBOUND_PORT_URI = "VACUUM-CLEANER-RIP-URI";
    /** URI of the vacuum cleaner inbound port used in tests. */
    public static final String INBOUND_PORT_URI = "VACUUM-CLEANER-INBOUND-PORT-URI";
    /** URI of the local simulation architecture for SIL unit tests. */
    public static final String UNIT_TEST_ARCHITECTURE_URI = "silUnitTests";
    /** URI of the local simulation architecture for SIL integration tests. */
    public static final String INTEGRATION_TEST_ARCHITECTURE_URI = "silIntegrationTests";
    // Configuration

    /** power consumption when in mode HIGH. */
    public static final Measure<Double> HIGH_POWER = new Measure<Double>(
            2000.0,
            POWER_UNIT);
    /** power consumption when in mode LOW. */
    public static final Measure<Double> LOW_POWER = new Measure<Double>(
            650.0,
            POWER_UNIT);
    /** tension required by the vacuum cleaner. */
    public static final Measure<Double> TENSION = new Measure<Double>(
            220.0,
            TENSION_UNIT);

    // Internal component state variables

    /** initial state of the vacuum cleaner. */
    public static final VacuumCleanerState INITIAL_STATE = VacuumCleanerState.OFF;
    /** initial mode of the vacuum cleaner. */
    public static final VacuumCleanerMode INITIAL_MODE = VacuumCleanerMode.LOW;

    /** current state (on, off) of the vacuum cleaner. */
    protected VacuumCleanerState currentState;
    /** current mode of operation (low, high) of the vacuum cleaner. */
    protected VacuumCleanerMode currentMode;

    /** inbound port offering the <code>VacuumCleanerUserCI</code> interface. */
    protected VacuumCleanerInboundPort vcip;

    // Execution/Simulation

    /** when true, methods trace their actions. */
    public static boolean VERBOSE = false;
    /** when true, methods provides debugging traces of their actions. */
    public static boolean DEBUG = false;
    /** when tracing, x coordinate of the window relative position. */
    public static int X_RELATIVE_POSITION = 0;
    /** when tracing, y coordinate of the window relative position. */
    public static int Y_RELATIVE_POSITION = 0;

    /**
     * one thread for the method execute, which starts the local SIL
     * simulator, and one to answer the calls to the component services.
     */
    protected static int NUMBER_OF_STANDARD_THREADS = 2;
    /** no need for statically defined schedulable threads. */
    protected static int NUMBER_OF_SCHEDULABLE_THREADS = 0;

    /** plug-in holding the local simulation architecture and simulators. */
    protected AtomicSimulatorPlugin asp;
    /**
     * URI of the local simulation architecture used to compose the global
     * simulation architecture or the empty string if the component does
     * not execute as a simulation.
     */
    protected final String localArchitectureURI;
    /**
     * acceleration factor to be used when running the real time
     * simulation.
     */
    protected final double accelerationFactor;

    // -------------------------------------------------------------------------
    // Invariants
    // -------------------------------------------------------------------------

    /**
     * return true if the static implementation invariants are observed, false
     * otherwise.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * post	{@code
     * true
     * }	// no postcondition.
     * </pre>
     *
     * @return true if the static invariants are observed, false otherwise.
     */
    public static boolean staticImplementationInvariants() {
        boolean ret = true;
        ret &= AssertionChecking.checkStaticImplementationInvariant(
                INITIAL_STATE != null,
                VacuumCleanerCyPhy.class,
                "INITIAL_STATE != null");
        ret &= AssertionChecking.checkStaticImplementationInvariant(
                INITIAL_MODE != null,
                VacuumCleanerCyPhy.class,
                "INITIAL_MODE != null");
        ret &= AssertionChecking.checkStaticImplementationInvariant(
                NUMBER_OF_STANDARD_THREADS >= 0,
                VacuumCleanerCyPhy.class,
                "NUMBER_OF_STANDARD_THREADS >= 0");
        ret &= AssertionChecking.checkStaticImplementationInvariant(
                NUMBER_OF_SCHEDULABLE_THREADS >= 0,
                VacuumCleanerCyPhy.class,
                "NUMBER_OF_SCHEDULABLE_THREADS");
        return ret;
    }

    /**
     * return true if the implementation invariants are observed, false otherwise.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * vc != null
     * }
     * post	{@code
     * true
     * }	// no postcondition.
     * </pre>
     *
     * @param vc instance to be tested.
     * @return true if the implementation invariants are observed, false otherwise.
     */
    protected static boolean implementationInvariants(VacuumCleanerCyPhy vc) {
        assert vc != null : new PreconditionException("vc != null");

        boolean ret = true;
        ret &= staticImplementationInvariants();
        ret &= AssertionChecking.checkInvariant(
                vc.currentState != null,
                VacuumCleanerCyPhy.class, vc,
                "currentState != null");
        ret &= AssertionChecking.checkInvariant(
                vc.currentMode != null,
                VacuumCleanerCyPhy.class, vc,
                "currentMode != null");
        ret &= AssertionChecking.checkInvariant(
                vc.localArchitectureURI == null ||
                        !vc.localArchitectureURI.isEmpty() &&
                                vc.accelerationFactor > 0.0,
                VacuumCleanerCyPhy.class, vc,
                "localArchitectureURI == null || !localArchitectureURI.isEmpty()"
                        + " && accelerationFactor > 0.0");
        ret &= AssertionChecking.checkInvariant(
                vc.asp == null || vc.localArchitectureURI != null,
                VacuumCleanerCyPhy.class, vc,
                "asp == null || localArchitectureURI != null");
        return ret;
    }

    /**
     * return true if the static invariants are observed, false otherwise.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * post	{@code
     * true
     * }	// no postcondition.
     * </pre>
     *
     * @return true if the static invariants are observed, false otherwise.
     */
    public static boolean staticInvariants() {
        boolean ret = true;
        ret &= AssertionChecking.checkStaticInvariant(
                REFLECTION_INBOUND_PORT_URI != null &&
                        !REFLECTION_INBOUND_PORT_URI.isEmpty(),
                VacuumCleanerCyPhy.class,
                "REFLECTION_INBOUND_PORT_URI != null && "
                        + "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
        ret &= AssertionChecking.checkStaticInvariant(
                INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty(),
                VacuumCleanerCyPhy.class,
                "INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty()");
        ret &= AssertionChecking.checkStaticInvariant(
                UNIT_TEST_ARCHITECTURE_URI != null &&
                        !UNIT_TEST_ARCHITECTURE_URI.isEmpty(),
                VacuumCleanerCyPhy.class,
                "UNIT_TEST_ARCHITECTURE_URI != null && "
                        + "!UNIT_TEST_ARCHITECTURE_URI.isEmpty()");
        ret &= AssertionChecking.checkStaticInvariant(
                INTEGRATION_TEST_ARCHITECTURE_URI != null &&
                        !INTEGRATION_TEST_ARCHITECTURE_URI.isEmpty(),
                VacuumCleanerCyPhy.class,
                "INTEGRATION_TEST_ARCHITECTURE_URI != null && "
                        + "!INTEGRATION_TEST_ARCHITECTURE_URI.isEmpty()");
        ret &= AssertionChecking.checkStaticInvariant(
                HIGH_POWER != null &&
                        HIGH_POWER.getData() > 0.0 &&
                        HIGH_POWER.getMeasurementUnit().equals(POWER_UNIT),
                VacuumCleanerCyPhy.class,
                "HIGH_POWER != null && HIGH_POWER.getData()"
                        + " > 0.0 && HIGH_POWER.getMeasurementUnit().equals("
                        + "POWER_UNIT)");
        ret &= AssertionChecking.checkStaticInvariant(
                LOW_POWER != null &&
                        LOW_POWER.getData() > 0.0 &&
                        LOW_POWER.getMeasurementUnit().equals(POWER_UNIT),
                VacuumCleanerCyPhy.class,
                "LOW_POWER != null && LOW_POWER.getData() >"
                        + " 0.0 && LOW_POWER.getMeasurementUnit().equals("
                        + "POWER_UNIT)");
        ret &= AssertionChecking.checkStaticInvariant(
                TENSION != null &&
                        (TENSION.getData() == 110.0 || TENSION.getData() == 220.0) &&
                        TENSION.getMeasurementUnit().equals(TENSION_UNIT),
                VacuumCleanerCyPhy.class,
                "TENSION != null && (TENSION.getData() == 110.0 || TENSION."
                        + "getData() == 220.0) && TENSION.getMeasurementUnit().equals("
                        + "TENSION_UNIT)");
        ret &= AssertionChecking.checkStaticInvariant(
                INITIAL_STATE != null && INITIAL_MODE != null,
                VacuumCleanerCyPhy.class,
                "INITIAL_STATE != null && INITIAL_MODE != null");
        ret &= AssertionChecking.checkStaticInvariant(
                X_RELATIVE_POSITION >= 0,
                VacuumCleanerCyPhy.class,
                "X_RELATIVE_POSITION >= 0");
        ret &= AssertionChecking.checkStaticInvariant(
                Y_RELATIVE_POSITION >= 0,
                VacuumCleanerCyPhy.class,
                "Y_RELATIVE_POSITION >= 0");
        return ret;
    }

    /**
     * return true if the invariants are observed, false otherwise.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * vc != null
     * }
     * post	{@code
     * true
     * }	// no postcondition.
     * </pre>
     *
     * @param vc instance to be tested.
     * @return true if the invariants are observed, false otherwise.
     */
    protected static boolean invariants(VacuumCleanerCyPhy vc) {
        assert vc != null : new PreconditionException("vc != null");

        boolean ret = true;
        ret &= staticInvariants();
        return ret;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    // Standard execution

    /**
     * create a vacuum cleaner component for standard execution.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * post	{@code
     * getState() == INITIAL_STATE
     * }
     * post	{@code
     * getMode() == INITIAL_MODE
     * }
     * post	{@code
     * getExecutionMode().isStandard()
     * }
     * </pre>
     * 
     * @throws Exception <i>to do</i>.
     */
    protected VacuumCleanerCyPhy() throws Exception {
        this(INBOUND_PORT_URI);
    }

    /**
     * create a vacuum cleaner component for standard execution with the given
     * inbound port URI.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * pre	{@code
     * vacuumCleanerInboundPortURI != null && !vacuumCleanerInboundPortURI.isEmpty()
     * }
     * post	{@code
     * getState() == INITIAL_STATE
     * }
     * post	{@code
     * getMode() == INITIAL_MODE
     * }
     * post	{@code
     * getExecutionMode().isStandard()
     * }
     * </pre>
     * 
     * @param vacuumCleanerInboundPortURI URI of the vacuum cleaner inbound port.
     * @throws Exception <i>to do</i>.
     */
    protected VacuumCleanerCyPhy(String vacuumCleanerInboundPortURI)
            throws Exception {
        this(AbstractPort.generatePortURI(CyPhyReflectionCI.class),
                vacuumCleanerInboundPortURI);
    }

    /**
     * create a vacuum cleaner component for standard execution with the given
     * reflection inbound port URI and inbound port URI.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * pre	{@code
     * reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * vacuumCleanerInboundPortURI != null && !vacuumCleanerInboundPortURI.isEmpty()
     * }
     * post	{@code
     * getState() == INITIAL_STATE
     * }
     * post	{@code
     * getMode() == INITIAL_MODE
     * }
     * post	{@code
     * getExecutionMode().isStandard()
     * }
     * </pre>
     *
     * @param reflectionInboundPortURI    URI of the reflection inbound port of the
     *                                    component.
     * @param vacuumCleanerInboundPortURI URI of the vacuum cleaner inbound port.
     * @throws Exception <i>to do</i>.
     */
    protected VacuumCleanerCyPhy(
            String reflectionInboundPortURI,
            String vacuumCleanerInboundPortURI) throws Exception {
        super(reflectionInboundPortURI,
                NUMBER_OF_STANDARD_THREADS,
                NUMBER_OF_SCHEDULABLE_THREADS);

        this.localArchitectureURI = null;
        this.accelerationFactor = 0.0;

        this.initialise(vacuumCleanerInboundPortURI);

        assert VacuumCleanerCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "VacuumCleanerCyPhy.implementationInvariants(this)");
        assert VacuumCleanerCyPhy.invariants(this) : new InvariantException("VacuumCleanerCyPhy.invariants(this)");
    }

    // Tests without simulation execution

    /**
     * create a vacuum cleaner component for test executions without simulation.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * pre	{@code
     * executionMode != null && executionMode.isTestWithoutSimulation()
     * }
     * post	{@code
     * getState() == INITIAL_STATE
     * }
     * post	{@code
     * getMode() == INITIAL_MODE
     * }
     * post	{@code
     * getExecutionMode().equals(executionMode)
     * }
     * </pre>
     * 
     * @param executionMode execution mode for the next run.
     * @throws Exception <i>to do</i>.
     */
    protected VacuumCleanerCyPhy(
            ExecutionMode executionMode) throws Exception {
        this(REFLECTION_INBOUND_PORT_URI, INBOUND_PORT_URI,
                AssertionChecking.assertTrueAndReturnOrThrow(
                        executionMode != null
                                && executionMode.isTestWithoutSimulation(),
                        executionMode,
                        () -> {
                            return new PreconditionException(
                                    "executionMode != null && "
                                            + "executionMode."
                                            + "isTestWithoutSimulation()");
                        }));
    }

    /**
     * create a vacuum cleaner component for test executions without simulation
     * with the given inbound port URI.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * pre	{@code
     * vacuumCleanerInboundPortURI != null && !vacuumCleanerInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * executionMode != null && executionMode.isTestWithoutSimulation()
     * }
     * post	{@code
     * getState() == INITIAL_STATE
     * }
     * post	{@code
     * getMode() == INITIAL_MODE
     * }
     * post	{@code
     * getExecutionMode().equals(executionMode)
     * }
     * </pre>
     * 
     * @param vacuumCleanerInboundPortURI URI of the vacuum cleaner inbound port.
     * @param executionMode               execution mode for the next run.
     * @throws Exception <i>to do</i>.
     */
    protected VacuumCleanerCyPhy(
            String vacuumCleanerInboundPortURI,
            ExecutionMode executionMode) throws Exception {
        this(REFLECTION_INBOUND_PORT_URI, vacuumCleanerInboundPortURI,
                AssertionChecking.assertTrueAndReturnOrThrow(
                        executionMode != null
                                && executionMode.isTestWithoutSimulation(),
                        executionMode,
                        () -> {
                            return new PreconditionException(
                                    "executionType != null && "
                                            + "executionType."
                                            + "isTestWithoutSimulation()");
                        }));
    }

    /**
     * create a vacuum cleaner component for test executions without simulation
     * with the given reflection inbound port URI and inbound port URI.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * pre	{@code
     * reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * vacuumCleanerInboundPortURI != null && !vacuumCleanerInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * executionMode != null && executionMode.isTestWithoutSimulation()
     * }
     * post	{@code
     * getState() == INITIAL_STATE
     * }
     * post	{@code
     * getMode() == INITIAL_MODE
     * }
     * post	{@code
     * getExecutionMode().equals(executionMode)
     * }
     * </pre>
     *
     * @param reflectionInboundPortURI    URI of the reflection inbound port of the
     *                                    component.
     * @param vacuumCleanerInboundPortURI URI of the vacuum cleaner inbound port.
     * @param executionMode               execution mode for the next run.
     * @throws Exception <i>to do</i>.
     */
    protected VacuumCleanerCyPhy(
            String reflectionInboundPortURI,
            String vacuumCleanerInboundPortURI,
            ExecutionMode executionMode) throws Exception {
        super(reflectionInboundPortURI,
                NUMBER_OF_STANDARD_THREADS,
                NUMBER_OF_SCHEDULABLE_THREADS,
                executionMode,
                "fake-clock" // passive component, do not need a clock
        );

        assert executionMode != null &&
                executionMode.isTestWithoutSimulation()
                : new PreconditionException(
                        "executionMode != null && executionMode."
                                + "isTestWithoutSimulation()");

        this.localArchitectureURI = null;
        this.accelerationFactor = 0.0;

        this.initialise(vacuumCleanerInboundPortURI);

        assert VacuumCleanerCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "VacuumCleanerCyPhy.implementationInvariants(this)");
        assert VacuumCleanerCyPhy.invariants(this) : new InvariantException("VacuumCleanerCyPhy.invariants(this)");
    }

    // Tests with simulation

    /**
     * create a vacuum cleaner component for test executions with simulation.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * pre	{@code
     * reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * vacuumCleanerInboundPortURI != null && !vacuumCleanerInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * executionMode != null && executionMode.isSimulationTest()
     * }
     * pre	{@code
     * testScenario != null
     * }
     * pre	{@code
     * localArchitectureURI != null && !localArchitectureURI.isEmpty()
     * }
     * pre	{@code
     * accelerationFactor > 0.0
     * }
     * post	{@code
     * getState() == INITIAL_STATE
     * }
     * post	{@code
     * getMode() == INITIAL_MODE
     * }
     * post	{@code
     * getExecutionMode().equals(executionMode)
     * }
     * </pre>
     *
     * @param reflectionInboundPortURI    URI of the reflection inbound port of the
     *                                    component.
     * @param vacuumCleanerInboundPortURI URI of the vacuum cleaner inbound port.
     * @param executionMode               execution type for the next run.
     * @param testScenario                test scenario to be executed with this
     *                                    component.
     * @param localArchitectureURI        URI of the local simulation architecture.
     * @param accelerationFactor          acceleration factor for the simulation.
     * @throws Exception <i>to do</i>.
     */
    protected VacuumCleanerCyPhy(
            String reflectionInboundPortURI,
            String vacuumCleanerInboundPortURI,
            ExecutionMode executionMode,
            TestScenario testScenario,
            String localArchitectureURI,
            double accelerationFactor) throws Exception {
        super(reflectionInboundPortURI,
                NUMBER_OF_STANDARD_THREADS,
                NUMBER_OF_SCHEDULABLE_THREADS,
                executionMode,
                AssertionChecking.assertTrueAndReturnOrThrow(
                        testScenario != null,
                        testScenario.getClockURI(),
                        () -> new PreconditionException("testScenario != null")),
                testScenario,
                ((Supplier<Set<String>>) () -> {
                    HashSet<String> hs = new HashSet<>();
                    hs.add(UNIT_TEST_ARCHITECTURE_URI);
                    hs.add(INTEGRATION_TEST_ARCHITECTURE_URI);
                    return hs;
                }).get(),
                accelerationFactor);

        assert vacuumCleanerInboundPortURI != null &&
                !vacuumCleanerInboundPortURI.isEmpty()
                : new PreconditionException(
                        "vacuumCleanerInboundPortURI != null && "
                                + "!vacuumCleanerInboundPortURI.isEmpty()");

        this.localArchitectureURI = localArchitectureURI;
        this.accelerationFactor = accelerationFactor;

        this.initialise(vacuumCleanerInboundPortURI);

        if (DEBUG) {
            this.logMessage("VacuumCleanerCyPhy local simulation architectures: "
                    + this.localSimulationArchitectures);
        }

        assert VacuumCleanerCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "VacuumCleanerCyPhy.implementationInvariants(this)");
        assert VacuumCleanerCyPhy.invariants(this) : new InvariantException("VacuumCleanerCyPhy.invariants(this)");
    }

    // -------------------------------------------------------------------------
    // Initialisation methods
    // -------------------------------------------------------------------------

    /**
     * initialise the vacuum cleaner component.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * vacuumCleanerInboundPortURI != null && !vacuumCleanerInboundPortURI.isEmpty()
     * }
     * post	{@code
     * getState() == VacuumCleanerState.OFF
     * }
     * post	{@code
     * getMode() == VacuumCleanerMode.LOW
     * }
     * </pre>
     * 
     * @param vacuumCleanerInboundPortURI URI of the vacuum cleaner inbound port.
     * @throws Exception <i>to do</i>.
     */
    protected void initialise(String vacuumCleanerInboundPortURI)
            throws Exception {
        assert vacuumCleanerInboundPortURI != null : new PreconditionException(
                "vacuumCleanerInboundPortURI != null");
        assert !vacuumCleanerInboundPortURI.isEmpty() : new PreconditionException(
                "!vacuumCleanerInboundPortURI.isEmpty()");

        this.currentState = INITIAL_STATE;
        this.currentMode = INITIAL_MODE;
        this.vcip = new VacuumCleanerInboundPort(vacuumCleanerInboundPortURI,
                this);
        this.vcip.publishPort();

        if (VacuumCleanerCyPhy.VERBOSE || VacuumCleanerCyPhy.DEBUG) {
            this.tracer.get().setTitle("Vacuum cleaner component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
                    Y_RELATIVE_POSITION);
            this.toggleTracing();
        }

        assert VacuumCleanerCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "VacuumCleanerCyPhy.implementationInvariants(this)");
        assert VacuumCleanerCyPhy.invariants(this) : new InvariantException("VacuumCleanerCyPhy.invariants(this)");
    }

    /**
     * @see fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent#createLocalSimulationArchitecture(java.lang.String,
     *      java.lang.String, java.util.concurrent.TimeUnit, double)
     */
    @Override
    protected RTArchitecture createLocalSimulationArchitecture(
            String architectureURI,
            String rootModelURI,
            TimeUnit simulatedTimeUnit,
            double accelerationFactor) throws Exception {
        assert architectureURI != null && !architectureURI.isEmpty() : new PreconditionException(
                "architectureURI != null && !architectureURI.isEmpty()");
        assert rootModelURI != null && !rootModelURI.isEmpty() : new PreconditionException(
                "rootModelURI != null && !rootModelURI.isEmpty()");
        assert simulatedTimeUnit != null : new PreconditionException("simulatedTimeUnit != null");
        assert accelerationFactor > 0.0 : new PreconditionException("accelerationFactor > 0.0");

        RTArchitecture ret = null;
        if (architectureURI.equals(UNIT_TEST_ARCHITECTURE_URI)) {
            ret = Local_SIL_SimulationArchitectures.createVacuumCleanerSIL_Architecture4UnitTest(
                    architectureURI,
                    rootModelURI,
                    simulatedTimeUnit,
                    accelerationFactor);
        } else if (architectureURI.equals(INTEGRATION_TEST_ARCHITECTURE_URI)) {
            ret = Local_SIL_SimulationArchitectures.createVacuumCleanerSIL_Architecture4IntegrationTest(
                    architectureURI,
                    rootModelURI,
                    simulatedTimeUnit,
                    accelerationFactor);
        } else {
            throw new BCMException("Unknown local simulation architecture "
                    + "URI: " + architectureURI);
        }

        return ret;
    }

    // -------------------------------------------------------------------------
    // Component life-cycle
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.components.AbstractComponent#start()
     */
    @Override
    public synchronized void start() throws ComponentStartException {
        super.start();

        assert VacuumCleanerCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "VacuumCleanerCyPhy.implementationInvariants(this)");
        assert VacuumCleanerCyPhy.invariants(this) : new InvariantException("VacuumCleanerCyPhy.invariants(this)");

        try {
            switch (this.getExecutionMode()) {
                case STANDARD:
                case UNIT_TEST:
                case INTEGRATION_TEST:
                    break;
                case UNIT_TEST_WITH_SIL_SIMULATION:
                case INTEGRATION_TEST_WITH_SIL_SIMULATION:
                    RTArchitecture architecture = (RTArchitecture) this.localSimulationArchitectures
                            .get(this.localArchitectureURI);
                    this.asp = new RTAtomicSimulatorPlugin();
                    ((RTAtomicSimulatorPlugin) this.asp).setPluginURI(architecture.getRootModelURI());
                    ((RTAtomicSimulatorPlugin) this.asp).setSimulationArchitecture(architecture);
                    this.installPlugin(this.asp);
                    this.asp.createSimulator();
                    this.asp.setSimulationRunParameters(
                            (TestScenarioWithSimulation) this.testScenario,
                            new HashMap<>());
                    break;
                case UNIT_TEST_WITH_HIL_SIMULATION:
                case INTEGRATION_TEST_WITH_HIL_SIMULATION:
                    throw new BCMException("HIL simulation not implemented yet!");
                default:
            }
        } catch (Exception e) {
            throw new ComponentStartException(e);
        }

        assert VacuumCleanerCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "VacuumCleanerCyPhy.implementationInvariants(this)");
        assert VacuumCleanerCyPhy.invariants(this) : new InvariantException("VacuumCleanerCyPhy.invariants(this)");
    }

    /**
     * @see fr.sorbonne_u.components.AbstractComponent#execute()
     */
    @Override
    public void execute() throws Exception {
        this.traceMessage("Vacuum Cleaner CyPhy executes.\n");

        assert VacuumCleanerCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "VacuumCleanerCyPhy.implementationInvariants(this)");
        assert VacuumCleanerCyPhy.invariants(this) : new InvariantException("VacuumCleanerCyPhy.invariants(this)");

        switch (this.getExecutionMode()) {
            case UNIT_TEST:
            case INTEGRATION_TEST:
                break;
            case UNIT_TEST_WITH_SIL_SIMULATION:
                // First, the component must synchronise with other components
                // to start the execution of the test scenario; we use a
                // time-triggered synchronisation scheme with the accelerated clock
                this.initialiseClock4Simulation(
                        ClocksServerWithSimulation.STANDARD_INBOUNDPORT_URI,
                        this.clockURI);
                this.asp.initialiseSimulation(
                        this.getClock4Simulation().getSimulatedStartTime(),
                        this.getClock4Simulation().getSimulatedDuration());
                // schedule the start of the SIL (real time) simulation
                this.asp.startRTSimulation(
                        TimeUnit.NANOSECONDS.toMillis(
                                this.getClock4Simulation().getStartEpochNanos()),
                        this.getClock4Simulation().getSimulatedStartTime().getSimulatedTime(),
                        this.getClock4Simulation().getSimulatedDuration().getSimulatedDuration());
                // wait until the simulation ends
                this.getClock4Simulation().waitUntilEnd();
                // give some time for the end of simulation catering tasks
                Thread.sleep(200L);
                // get and print the simulation report
                this.logMessage(this.asp.getFinalReport().toString());
                break;
            case INTEGRATION_TEST_WITH_SIL_SIMULATION:
                break;
            case UNIT_TEST_WITH_HIL_SIMULATION:
            case INTEGRATION_TEST_WITH_HIL_SIMULATION:
                throw new BCMException("HIL simulation not implemented yet!");
            case STANDARD:
            default:
        }
    }

    /**
     * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
     */
    @Override
    public synchronized void shutdown() throws ComponentShutdownException {
        try {
            this.vcip.unpublishPort();
        } catch (Throwable e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    // -------------------------------------------------------------------------
    // Component services implementation
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI#getState()
     */
    @Override
    public VacuumCleanerState getState() throws Exception {
        if (VacuumCleanerCyPhy.VERBOSE) {
            this.traceMessage("Vacuum cleaner returns its state : " +
                    this.currentState + ".\n");
        }

        return this.currentState;
    }

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI#getMode()
     */
    @Override
    public VacuumCleanerMode getMode() throws Exception {
        if (VacuumCleanerCyPhy.VERBOSE) {
            this.traceMessage("Vacuum cleaner returns its mode : " +
                    this.currentMode + ".\n");
        }

        return this.currentMode;
    }

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI#turnOn()
     */
    @Override
    public void turnOn() throws Exception {
        if (VacuumCleanerCyPhy.VERBOSE) {
            this.traceMessage("Vacuum cleaner is turned on.\n");
        }

        assert this.getState() == VacuumCleanerState.OFF : new PreconditionException(
                "getState() == VacuumCleanerState.OFF");

        this.currentState = VacuumCleanerState.ON;
        this.currentMode = VacuumCleanerCyPhy.INITIAL_MODE;

        assert this.getState() == VacuumCleanerState.ON : new PostconditionException(
                "getState() == VacuumCleanerState.ON");
        assert this.getMode() == VacuumCleanerCyPhy.INITIAL_MODE : new PostconditionException(
                "getMode() == VacuumCleanerCyPhy.INITIAL_MODE");

        if (this.getExecutionMode().isSILTest()) {
            ((RTAtomicSimulatorPlugin) this.asp).triggerExternalEvent(
                    VacuumCleanerStateSILModel.URI,
                    t -> new SwitchOnVacuumCleaner(t));
        }
    }

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI#turnOff()
     */
    @Override
    public void turnOff() throws Exception {
        if (VacuumCleanerCyPhy.VERBOSE) {
            this.traceMessage("Vacuum cleaner is turned off.\n");
        }

        assert this.getState() == VacuumCleanerState.ON : new PreconditionException(
                "getState() == VacuumCleanerState.ON");

        this.currentState = VacuumCleanerState.OFF;
        this.currentMode = VacuumCleanerCyPhy.INITIAL_MODE;

        assert this.getState() == VacuumCleanerState.OFF : new PostconditionException(
                "getState() == VacuumCleanerState.OFF");
        assert this.getMode() == VacuumCleanerCyPhy.INITIAL_MODE : new PostconditionException(
                "getMode() == VacuumCleanerCyPhy.INITIAL_MODE");

        if (this.getExecutionMode().isSILTest()) {
            ((RTAtomicSimulatorPlugin) this.asp).triggerExternalEvent(
                    VacuumCleanerStateSILModel.URI,
                    t -> new SwitchOffVacuumCleaner(t));
        }
    }

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI#setHigh()
     */
    @Override
    public void setHigh() throws Exception {
        if (VacuumCleanerCyPhy.VERBOSE) {
            this.traceMessage("Vacuum cleaner is set high.\n");
        }

        assert this.getState() == VacuumCleanerState.ON : new PreconditionException(
                "getState() == VacuumCleanerState.ON");
        assert this.getMode() == VacuumCleanerMode.LOW : new PreconditionException(
                "getMode() == VacuumCleanerMode.LOW");

        this.currentMode = VacuumCleanerMode.HIGH;

        assert this.getState() == VacuumCleanerState.ON : new PostconditionException(
                "getState() == VacuumCleanerState.ON");
        assert this.getMode() == VacuumCleanerMode.HIGH : new PostconditionException(
                "getMode() == VacuumCleanerMode.HIGH");

        if (this.getExecutionMode().isSILTest()) {
            ((RTAtomicSimulatorPlugin) this.asp).triggerExternalEvent(
                    VacuumCleanerStateSILModel.URI,
                    t -> new SetHighVacuumCleaner(t));
        }
    }

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI#setLow()
     */
    @Override
    public void setLow() throws Exception {
        if (VacuumCleanerCyPhy.VERBOSE) {
            this.traceMessage("Vacuum cleaner is set low.\n");
        }

        assert this.getState() == VacuumCleanerState.ON : new PreconditionException(
                "getState() == VacuumCleanerState.ON");
        assert this.getMode() == VacuumCleanerMode.HIGH : new PreconditionException(
                "getMode() == VacuumCleanerMode.HIGH");

        this.currentMode = VacuumCleanerMode.LOW;

        assert this.getState() == VacuumCleanerState.ON : new PostconditionException(
                "getState() == VacuumCleanerState.ON");
        assert this.getMode() == VacuumCleanerMode.LOW : new PostconditionException(
                "getMode() == VacuumCleanerMode.LOW");

        if (this.getExecutionMode().isSILTest()) {
            ((RTAtomicSimulatorPlugin) this.asp).triggerExternalEvent(
                    VacuumCleanerStateSILModel.URI,
                    t -> new SetLowVacuumCleaner(t));
        }
    }
}
// -----------------------------------------------------------------------------
