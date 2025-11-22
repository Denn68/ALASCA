package fr.sorbonne_u.components.hem2025e1.equipments.kettle;


import fr.sorbonne_u.alasca.physical_data.Measure;

public interface		KettleUserI
extends		KettleExternalControlI
{

	public boolean		on() throws Exception;

	public void			switchOn() throws Exception;

	public void			switchOff() throws Exception;

	public void			setTargetTemperature(Measure<Double> target)
	throws Exception;
}
// -----------------------------------------------------------------------------
