package fr.sorbonne_u.components.hem2025e1.equipments.generator.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

// -----------------------------------------------------------------------------
/**
 * The class <code>GeneratorConnector</code> implements a connector for
 * the {@code GeneratorCI} component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-09-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			GeneratorConnector
extends		AbstractConnector
implements	GeneratorCI
{
	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorCI#isRunning()
	 */
	@Override
	public boolean		isRunning() throws Exception
	{
		return ((GeneratorCI)this.offering).isRunning();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorImplementationI#nominalOutputTension()
	 */
	@Override
	public Measure<Double>	nominalOutputTension() throws Exception
	{
		return ((GeneratorCI)this.offering).nominalOutputTension();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorImplementationI#tankCapacity()
	 */
	@Override
	public Measure<Double>	tankCapacity() throws Exception
	{
		return ((GeneratorCI)this.offering).tankCapacity();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorCI#currentTankLevel()
	 */
	@Override
	public SignalData<Double>	currentTankLevel() throws Exception
	{
		return ((GeneratorCI)this.offering).currentTankLevel();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorCI#maxPowerProductionCapacity()
	 */
	@Override
	public Measure<Double>	maxPowerProductionCapacity() throws Exception
	{
		return ((GeneratorCI)this.offering).maxPowerProductionCapacity();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorCI#currentPowerProduction()
	 */
	@Override
	public SignalData<Double>	currentPowerProduction() throws Exception
	{
		return ((GeneratorCI)this.offering).currentPowerProduction();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorImplementationI#minFuelConsumption()
	 */
	@Override
	public Measure<Double>	minFuelConsumption() throws Exception
	{
		return ((GeneratorCI)this.offering).minFuelConsumption();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorImplementationI#maxFuelConsumption()
	 */
	@Override
	public Measure<Double>	maxFuelConsumption() throws Exception
	{
		return ((GeneratorCI)this.offering).maxFuelConsumption();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorImplementationI#currentFuelConsumption()
	 */
	@Override
	public SignalData<Double>	currentFuelConsumption() throws Exception
	{
		return ((GeneratorCI)this.offering).currentFuelConsumption();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorCI#startGenerator()
	 */
	@Override
	public void			startGenerator() throws Exception
	{
		((GeneratorCI)this.offering).startGenerator();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.generator.GeneratorCI#stopGenerator()
	 */
	@Override
	public void			stopGenerator() throws Exception
	{
		((GeneratorCI)this.offering).stopGenerator();
	}
}
// -----------------------------------------------------------------------------
