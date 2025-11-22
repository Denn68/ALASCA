package fr.sorbonne_u.components.hem2025e1.equipments.kettle;

public interface		KettleUserJava4CI
extends		KettleUserCI
{

	public void			setTargetTemperatureJava4(double target) throws Exception;
	
	public double		getMaxPowerLevelJava4() throws Exception;

	public void			setCurrentPowerLevelJava4(double powerLevel)
	throws Exception;

	public double		getCurrentPowerLevelJava4() throws Exception;

	public double		getTargetTemperatureJava4() throws Exception ;

	public double		getCurrentTemperatureJava4() throws Exception;
}
// -----------------------------------------------------------------------------
