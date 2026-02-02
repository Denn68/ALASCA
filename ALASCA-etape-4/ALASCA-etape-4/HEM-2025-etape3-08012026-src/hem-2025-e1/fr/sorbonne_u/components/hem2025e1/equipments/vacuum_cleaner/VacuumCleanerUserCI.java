package fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

// -----------------------------------------------------------------------------
/**
 * The component interface <code>VacuumCleanerUserCI</code> defines the
 * signatures of services that the vacuum cleaner component offers to user
 * components.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * The vacuum cleaner is an <b>uncontrollable</b> appliance, hence it does not
 * connect with the household energy manager. This is the only interface
 * offered by the vacuum cleaner.
 * </p>
 * 
 * @author Team
 */
public interface VacuumCleanerUserCI
		extends OfferedCI,
		RequiredCI,
		VacuumCleanerImplementationI {
	@Override
	public VacuumCleanerState getState() throws Exception;

	@Override
	public VacuumCleanerMode getMode() throws Exception;

	@Override
	public void turnOn() throws Exception;

	@Override
	public void turnOff() throws Exception;

	@Override
	public void setHigh() throws Exception;

	@Override
	public void setLow() throws Exception;
}
// -----------------------------------------------------------------------------
