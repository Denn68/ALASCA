package fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner;

public interface		VacuumCleanerImplementationI
{
	public static enum	VacuumCleanerState
	{
		/** vacuum cleaner is on.												*/
		ON,
		/** vacuum cleaner is off.												*/
		OFF
	}
	
	public static enum	VacuumCleanerMode
	{
		/** low mode is when the vacuum sucks gently.				       */
		LOW,			
		
		/** medium is the default mode.				       					*/
		MEDIUM,
		
		/** high mode is when the vacuum sucks strongly.						*/
		HIGH,
	}
	
	public VacuumCleanerState	getState() throws Exception;

	public VacuumCleanerMode	getMode() throws Exception;
	
	public void			turnOn() throws Exception;
	
	public void			turnOff() throws Exception;
	
	public void			setHigh() throws Exception;
	
	public void 		setMedium() throws Exception;
	
	public void			setLow() throws Exception;
}
