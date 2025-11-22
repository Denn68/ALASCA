package fr.sorbonne_u.components.hem2025e1.equipments.fan.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserCI;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanUserConnector</code> implements a connector for the
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
public class			FanUserConnector
extends		AbstractConnector
implements	FanUserCI
{
	@Override
	public boolean		on() throws Exception
	{
		return ((FanUserCI)this.offering).on();
	}

	@Override
	public void			switchOn() throws Exception
	{
		((FanUserCI)this.offering).switchOn();
	}
	
	@Override
	public void			switchOff() throws Exception
	{
		((FanUserCI)this.offering).switchOff();
	}

	@Override
	public void			setLowSpeed() throws Exception
	{
		((FanUserCI)this.offering).setLowSpeed();
	}

	@Override
	public void			setHighSpeed() throws Exception
	{
		((FanUserCI)this.offering).setHighSpeed();
	}

	@Override
	public FanSpeed		getSpeed() throws Exception
	{
		return ((FanUserCI)this.offering).getSpeed();
	}

	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		return ((FanUserCI)this.offering).getMaxPowerLevel();
	}

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		((FanUserCI)this.offering).setCurrentPowerLevel(powerLevel);
	}
	
	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		return ((FanUserCI)this.offering).getCurrentPowerLevel();
	}
}
// -----------------------------------------------------------------------------