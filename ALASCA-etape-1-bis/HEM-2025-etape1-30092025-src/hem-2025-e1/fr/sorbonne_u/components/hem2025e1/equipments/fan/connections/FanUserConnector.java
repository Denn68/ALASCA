package fr.sorbonne_u.components.hem2025e1.equipments.fan.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserCI;

// -----------------------------------------------------------------------------
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

	@Override
	public boolean running() throws Exception {
		return ((FanUserCI)this.offering).running();
	}

	@Override
	public void startRunning() throws Exception {
		((FanUserCI)this.offering).startRunning();
	}

	@Override
	public void stopRunning() throws Exception {
		((FanUserCI)this.offering).stopRunning();
	}
}
// -----------------------------------------------------------------------------
