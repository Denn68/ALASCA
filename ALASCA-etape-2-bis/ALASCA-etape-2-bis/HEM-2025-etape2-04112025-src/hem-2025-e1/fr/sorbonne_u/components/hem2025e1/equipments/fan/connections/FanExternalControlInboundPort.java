package fr.sorbonne_u.components.hem2025e1.equipments.fan.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlCI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

public class			FanExternalControlInboundPort
extends		AbstractInboundPort
implements	FanExternalControlCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				FanExternalControlInboundPort(ComponentI owner)
	throws Exception
	{
		this(FanExternalControlCI.class, owner);
	}

	public				FanExternalControlInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		this(uri, FanExternalControlCI.class, owner);
	}

	public				FanExternalControlInboundPort(
		Class<? extends OfferedCI> implementedInterface,
		ComponentI owner
		) throws Exception
	{
		super(implementedInterface, owner);

		assert	implementedInterface != null &&
						FanExternalControlCI.class.isAssignableFrom(
														implementedInterface)  :
				new PreconditionException(
						"implementedInterface != null && "
						+ "FanExternalControlCI.class.isAssignableFrom("
						+ "implementedInterface)");
		assert	owner instanceof FanExternalControlI :
				new PreconditionException(
						"owner instanceof FanExternalControlI");
	}

	public				FanExternalControlInboundPort(
		String uri,
		Class<? extends OfferedCI> implementedInterface,
		ComponentI owner
		) throws Exception
	{
		super(uri, implementedInterface, owner);

		assert	implementedInterface != null &&
						FanExternalControlCI.class.isAssignableFrom(
														implementedInterface)  :
				new PreconditionException(
						"implementedInterface != null && "
						+ "FanExternalControlCI.class.isAssignableFrom("
						+ "implementedInterface)");
		assert	owner instanceof FanExternalControlI :
				new PreconditionException(
						"owner instanceof FanExternalControlI");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

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
				o -> {	((FanExternalControlI)o).
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
