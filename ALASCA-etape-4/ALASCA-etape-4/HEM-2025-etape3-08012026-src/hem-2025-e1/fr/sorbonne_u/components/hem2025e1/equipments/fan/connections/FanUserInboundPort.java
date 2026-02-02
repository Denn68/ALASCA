package fr.sorbonne_u.components.hem2025e1.equipments.fan.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserCI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanUserInboundPort</code> implements an inbound port for the
 * <code>FanUserCI</code> component interface.
 *
 * <p><strong>Description</strong></p>
 * * <p><strong>Implementation Invariants</strong></p>
 * * <pre>
 * invariant	{@code true}
 * </pre>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code true}
 * </pre>
 * * @author	Team DeMoh
 */
public class			FanUserInboundPort
extends		AbstractInboundPort
implements	FanUserCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				FanUserInboundPort(ComponentI owner) throws Exception
	{
		super(FanUserCI.class, owner);
		assert	owner instanceof FanUserI :
				new PreconditionException("owner instanceof FanUserI");
	}

	public				FanUserInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, FanUserCI.class, owner);
		assert	owner instanceof FanUserI :
				new PreconditionException("owner instanceof FanUserI");
	}
	
	public				FanUserInboundPort(
		Class<? extends OfferedCI> implementedInterface,
		ComponentI owner
		) throws Exception
	{
		super(implementedInterface, owner);
		assert	owner instanceof FanUserI :
				new PreconditionException("owner instanceof FanUserI");
	}

	public				FanUserInboundPort(
		String uri,
		Class<? extends OfferedCI> implementedInterface,
		ComponentI owner
		) throws Exception
	{
		super(uri, implementedInterface, owner);
		assert	owner instanceof FanUserI :
				new PreconditionException("owner instanceof FanUserI");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public boolean		on() throws Exception
	{
		return this.getOwner().handleRequest(o -> ((FanUserI)o).on());
	}

	@Override
	public void			switchOn() throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((FanUserI)o).switchOn();;
									return null;
							});
	}

	@Override
	public void			switchOff() throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((FanUserI)o).switchOff();;
									return null;
							});
	}

	@Override
	public void			setLowSpeed() throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((FanUserI)o).setLowSpeed();
									return null;
							});
	}

	@Override
	public void			setHighSpeed() throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((FanUserI)o).setHighSpeed();
									return null;
							});
	}

	@Override
	public FanSpeed		getSpeed() throws Exception
	{
		return this.getOwner().handleRequest(o -> ((FanUserI)o).getSpeed());
	}

	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((FanExternalControlI)o).getMaxPowerLevel());
	}

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((FanExternalControlI)o).
								setCurrentPowerLevel(powerLevel);
						return null;
				});
	}

	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((FanExternalControlI)o).getCurrentPowerLevel());
	}
}
// -----------------------------------------------------------------------------