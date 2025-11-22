package fr.sorbonne_u.components.hem2025e1.equipments.fan;

public interface		FanUserI
extends		FanExternalControlI
{
	public static enum FanSpeed {
		OFF,
		LOW,
		HIGH
	}

	/**
	 * return true if the fan is currently running (LOW or HIGH).
	 */
	public boolean		on() throws Exception;

	/**
	 * switch on the fan (starts at LOW speed by default).
	 */
	public void			switchOn() throws Exception;
	
	/**
	 * switch off the fan.
	 */
	public void			switchOff() throws Exception;

	/**
	 * set the fan to LOW speed.
	 */
	public void			setLowSpeed() throws Exception;

	/**
	 * set the fan to HIGH speed.
	 */
	public void			setHighSpeed() throws Exception;

	/**
	 * return the current speed of the fan.
	 */
	public FanSpeed		getSpeed() throws Exception;
}