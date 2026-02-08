package fr.sorbonne_u.components.hem2025e3.equipments.vacuum_cleaner.sil;

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

import java.util.concurrent.TimeUnit;
import java.util.Map;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.VacuumCleanerUnitTesterModel;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetHighVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetLowVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOffVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOnVacuumCleaner;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
/**
 * The class <code>VacuumCleanerUnitTesterSILModel</code> implements a unit
 * tester simulation model for the vacuum cleaner which runs test scenarios.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events:
 * {@code SwitchOnVacuumCleaner},
 * {@code SwitchOffVacuumCleaner},
 * {@code SetLowVacuumCleaner},
 * {@code SetHighVacuumCleaner}</li>
 * </ul>
 * 
 * <p>
 * Created on : 2026-06-06
 * </p>
 * 
 * @author Team
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(exported = { SwitchOnVacuumCleaner.class,
        SwitchOffVacuumCleaner.class,
        SetLowVacuumCleaner.class,
        SetHighVacuumCleaner.class })
// -----------------------------------------------------------------------------
public class VacuumCleanerUnitTesterSILModel
        extends VacuumCleanerUnitTesterModel {
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;
    public static boolean VERBOSE = false;
    public static boolean DEBUG = false;
    public static final String URI = VacuumCleanerUnitTesterSILModel.class.getSimpleName();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public VacuumCleanerUnitTesterSILModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            AtomicSimulatorI simulationEngine) {
        super(uri, simulatedTimeUnit, simulationEngine);

        if (VERBOSE || DEBUG) {
            this.getSimulationEngine().setLogger(new StandardLogger());
        }

        assert VacuumCleanerUnitTesterSILModel.implementationInvariants(this) : new NeoSim4JavaException(
                "Implementation Invariants violation: "
                        + "VacuumCleanerUnitTesterSILModel."
                        + "implementationInvariants(this)");
        assert VacuumCleanerUnitTesterSILModel.invariants(this) : new NeoSim4JavaException(
                "Invariants violation: VacuumCleanerUnitTesterSILModel."
                        + "invariants(this)");
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(java.util.Map)
     */
    @Override
    public void setSimulationRunParameters(
            Map<String, Object> simParams) throws MissingRunParameterException {
        if (simParams.containsKey(
                AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
            this.getSimulationEngine().setLogger(
                    AtomicSimulatorPlugin.createComponentLogger(simParams));
        }
        super.setSimulationRunParameters(simParams);
    }
}
// -----------------------------------------------------------------------------
