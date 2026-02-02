package fr.sorbonne_u.components.hem2025e1.equipments.fan.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanUserOutboundPort</code> implements an outbound port for the
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
public class			FanUserOutboundPort
extends		AbstractOutboundPort
implements	FanUserCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				FanUserOutboundPort(ComponentI owner)
	throws Exception
	{
		super(FanUserCI.class, owner);
	}

	public				FanUserOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, FanUserCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public boolean		on() throws Exception
	{
		return ((FanUserCI)this.getConnector()).on();
	}

	@Override
	public void			switchOn() throws Exception
	{
		((FanUserCI)this.getConnector()).switchOn();
	}

	@Override
	public void			switchOff() throws Exception
	{
		((FanUserCI)this.getConnector()).switchOff();
	}

	@Override
	public void			setLowSpeed() throws Exception
	{
		((FanUserCI)this.getConnector()).setLowSpeed();
	}

	@Override
	public void			setHighSpeed() throws Exception
	{
		((FanUserCI)this.getConnector()).setHighSpeed();
	}

	@Override
	public FanSpeed		getSpeed() throws Exception
	{
		return ((FanUserCI)this.getConnector()).getSpeed();
	}

	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		return ((FanUserCI)this.getConnector()).getMaxPowerLevel();
	}

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		((FanUserCI)this.getConnector()).setCurrentPowerLevel(powerLevel);
	}

	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		return ((FanUserCI)this.getConnector()).getCurrentPowerLevel();
	}
}
// -----------------------------------------------------------------------------