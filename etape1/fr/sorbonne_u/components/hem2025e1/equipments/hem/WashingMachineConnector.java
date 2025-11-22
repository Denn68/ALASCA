package fr.sorbonne_u.components.hem2025e1.equipments.hem;


import fr.sorbonne_u.components.hem2025.bases.AdjustableCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineExternalControlJava4CI;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;


public class			WashingMachineConnector
extends		AbstractConnector
implements	AdjustableCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** modes will be defined by five power levels, including a power
	 *  level of 0.0 watts; note that modes go from 1 (0.0 watts) to
	 *  6 (2000.0 watts).													*/
	public static final int		MAX_WASHING_MACHINE_MODE = 6;
	/** the minimum admissible temperature from which the washing machine should
	 *  be resumed in priority after being suspended to save energy.		*/
	public static final double	MIN_WASHING_MACHINE_ADMISSIBLE_TEMP = 12.0;
	/** the maximal admissible difference between the target and the
	 *  current temperature from which the washing machine should be resumed in
	 *  priority after being suspended to save energy.						*/
	public static final double	MAX_WASHING_MACHINE_ADMISSIBLE_DELTA = 10.0;

	/** the current mode of the washing machine.										*/
	protected int		currentMode;
	/** true if the washing machine has been suspended, false otherwise.				*/
	protected boolean	isSuspended;

	public				WashingMachineConnector()
	{
		super();
		this.currentMode = MAX_WASHING_MACHINE_MODE;
		this.isSuspended = false;
	}

	protected double	computePowerLevel(int mode) throws Exception
	{
		assert	mode > 0 || mode <= MAX_WASHING_MACHINE_MODE :
				new PreconditionException("mode > 0 || mode <= MAX_MODE");

		double maxPowerLevel =
				((WashingMachineExternalControlJava4CI)this.offering).
													getMaxPowerLevelJava4();
		return (mode - 1) * maxPowerLevel/(MAX_WASHING_MACHINE_MODE - 1);
	}
	
	protected void		setNewPowerLevel(double newPowerLevel) throws Exception
	{
		assert	newPowerLevel >= 0.0 :
				new PreconditionException("newPowerLevel >= 0.0");

		double maxPowerLevel =
				((WashingMachineExternalControlJava4CI)this.offering).
													getMaxPowerLevelJava4();
		
		if (newPowerLevel > maxPowerLevel) {
			newPowerLevel = maxPowerLevel;
		}
		((WashingMachineExternalControlJava4CI)this.offering).
									setCurrentPowerLevelJava4(newPowerLevel);
	}

	protected void		computeAndSetNewPowerLevel(int newMode) throws Exception
	{
		double newPowerLevel = this.computePowerLevel(newMode);
		this.setNewPowerLevel(newPowerLevel);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public int			maxMode() throws Exception
	{
		return MAX_WASHING_MACHINE_MODE;
	}

	@Override
	public boolean		upMode() throws Exception
	{
		assert	!this.suspended() : new PreconditionException("!suspended()");
		assert	this.currentMode() < MAX_WASHING_MACHINE_MODE :
				new PreconditionException("currentMode() < MAX_MODE");

		try {
			this.computeAndSetNewPowerLevel(this.currentMode + 1);
			this.currentMode++;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean		downMode() throws Exception
	{
		assert	!this.suspended() : new PreconditionException("!suspended()");
		assert	this.currentMode() > 0 :
				new PreconditionException("currentMode() > 0");

		try {
			this.computeAndSetNewPowerLevel(this.currentMode - 1);
			this.currentMode--;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean		setMode(int modeIndex) throws Exception
	{
		assert	!this.suspended() : new PreconditionException("!suspended()");
		assert	modeIndex > 0 && modeIndex <= this.maxMode() :
				new PreconditionException(
						"modeIndex > 0 && modeIndex <= maxMode()");

		try {
			this.computeAndSetNewPowerLevel(modeIndex);
			this.currentMode = modeIndex;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public int			currentMode() throws Exception
	{
		assert	!suspended() : new PreconditionException("!suspended()");

		return this.currentMode;
	}

	@Override
	public double		getModeConsumption(int modeIndex) throws Exception
	{
		assert	modeIndex > 0 && modeIndex <= this.maxMode() :
				new PreconditionException(
						"modeIndex > 0 && modeIndex <= maxMode()");

		return this.computePowerLevel(modeIndex);
	}

	@Override
	public boolean		suspended() throws Exception
	{
		return this.isSuspended;
	}

	@Override
	public boolean		suspend() throws Exception
	{
		assert	!this.suspended() : new PreconditionException("!suspended()");

		try {
			((WashingMachineExternalControlJava4CI)this.offering).
												setCurrentPowerLevelJava4(0.0);
			this.isSuspended = true;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean		resume() throws Exception
	{
		assert	this.suspended() : new PreconditionException("suspended()");

		try {
			this.computeAndSetNewPowerLevel(this.currentMode);
			this.isSuspended = false;
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public double		emergency() throws Exception
	{
		assert	this.suspended() : new PreconditionException("suspended()");

		double currentTemperature =
					((WashingMachineExternalControlJava4CI)this.offering).
												getCurrentTemperatureJava4();
		double targetTemperature =
					((WashingMachineExternalControlJava4CI)this.offering).
												getTargetTemperatureJava4();
		double delta = Math.abs(targetTemperature - currentTemperature);
		double ret = -1.0;
		if (currentTemperature < WashingMachineConnector.MIN_WASHING_MACHINE_ADMISSIBLE_TEMP ||
							delta >= WashingMachineConnector.MAX_WASHING_MACHINE_ADMISSIBLE_DELTA) {
			ret = 1.0;
		} else {
			ret = delta/WashingMachineConnector.MAX_WASHING_MACHINE_ADMISSIBLE_DELTA;
		}

		assert	ret >= 0.0 && ret <= 1.0 :
				new PostconditionException("return >= 0.0 && return <= 1.0");

		return ret;
	}
}
// -----------------------------------------------------------------------------
