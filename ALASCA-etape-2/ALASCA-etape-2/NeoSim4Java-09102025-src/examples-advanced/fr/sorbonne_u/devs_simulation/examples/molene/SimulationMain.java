package fr.sorbonne_u.devs_simulation.examples.molene;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a new
// implementation of the DEVS simulation <i>de facto</i> standard for Java.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.examples.molene.bsm.BatteryLevel;
import fr.sorbonne_u.devs_simulation.examples.molene.bsm.BatterySensorModel;
import fr.sorbonne_u.devs_simulation.examples.molene.controllers.ControllerModel;
import fr.sorbonne_u.devs_simulation.examples.molene.nm.NetworkModel;
import fr.sorbonne_u.devs_simulation.examples.molene.pcm.PortableComputerModel;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.Compressing;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.LowBattery;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.NotCompressing;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.PortableComputerStateModel;
import fr.sorbonne_u.devs_simulation.examples.molene.sm.ServerModel;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicModel;
import fr.sorbonne_u.devs_simulation.examples.molene.wbm.WiFiBandwidthModel;
import fr.sorbonne_u.devs_simulation.examples.molene.wbsm.WiFiBandwidthReading;
import fr.sorbonne_u.devs_simulation.examples.molene.wbsm.WiFiBandwidthSensorModel;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.InterruptionEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.ResumptionEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.WiFiDisconnectionModel;
import fr.sorbonne_u.devs_simulation.examples.molene.wm.WiFiModel;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.plotters.PlotterDescription;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

// -----------------------------------------------------------------------------
/**
 * The class <code>SimulationMain</code> constructs a simulation architecture
 * for the Molene experiment (see Maria-Teresa Segarra Ph.D. thesis).
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
 * invariant	{@code true}
 * </pre>
 * 
 * <p>Created on : 2018-07-18</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			SimulationMain
{
	public static final String	MOLENE_MODEL_URI = "MoleneModel";
	public static int	ORIGIN_X = 100;
	public static int	ORIGIN_Y = 0;
	/** simulated duration, 6000 seconds = 1h40.							*/
	public static final double	SIMULATION_DURATION = 6000.0;

	public static int	getPlotterWidth()
	{
		int ret = Integer.MAX_VALUE;
		GraphicsEnvironment ge =
						GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for (int i = 0; i < gs.length; i++) {
			DisplayMode dm = gs[i].getDisplayMode();
			int width = dm.getWidth();
			if (width < ret) {
				ret = width;
			}
		}
		return (int) (0.25 * ret);
	}

	public static int	getPlotterHeight()
	{
		int ret = Integer.MAX_VALUE;
		GraphicsEnvironment ge =
						GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for (int i = 0; i < gs.length; i++) {
			DisplayMode dm = gs[i].getDisplayMode();
			int height = dm.getHeight();
			if (height < ret) {
				ret = height;
			}
		}
		return (int) (0.2 * ret);
	}

	public static void	main(String[] args)
	{
		try {
			// ----------------------------------------------------------------
			// WiFi TIOA model
			// ----------------------------------------------------------------

			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

			atomicModelDescriptors.put(
				WiFiDisconnectionModel.URI,
				AtomicModelDescriptor.create(
						WiFiDisconnectionModel.class,
						WiFiDisconnectionModel.URI,
						TimeUnit.SECONDS,
						null));

			atomicModelDescriptors.put(
				WiFiBandwidthModel.URI,
				AtomicHIOA_Descriptor.create(
						WiFiBandwidthModel.class,
						WiFiBandwidthModel.URI,
						TimeUnit.SECONDS,
						null));

			atomicModelDescriptors.put(
				TicModel.URI + "-1",
				AtomicModelDescriptor.create(
						TicModel.class,
						TicModel.URI + "-1",
						TimeUnit.SECONDS,
						null));

			atomicModelDescriptors.put(
					WiFiBandwidthSensorModel.URI,
					AtomicHIOA_Descriptor.create(
							WiFiBandwidthSensorModel.class,
							WiFiBandwidthSensorModel.URI,
							TimeUnit.SECONDS,
							null));

			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

			Set<String> submodels1 = new HashSet<String>();
			submodels1.add(WiFiDisconnectionModel.URI);
			submodels1.add(WiFiBandwidthModel.URI);
			submodels1.add(TicModel.URI + "-1");
			submodels1.add(WiFiBandwidthSensorModel.URI);

			Map<Class<? extends EventI>,EventSink[]> imported1 =
					new HashMap<Class<? extends EventI>,EventSink[]>();

			Map<Class<? extends EventI>,ReexportedEvent> reexported1 =
					new HashMap<Class<? extends EventI>,ReexportedEvent>();
			reexported1.put(
					WiFiBandwidthReading.class,
					new ReexportedEvent(WiFiBandwidthSensorModel.URI,
										WiFiBandwidthReading.class));
			reexported1.put(
					InterruptionEvent.class,
					new ReexportedEvent(WiFiDisconnectionModel.URI,
										InterruptionEvent.class));
			reexported1.put(
					ResumptionEvent.class,
					new ReexportedEvent(WiFiDisconnectionModel.URI,
										ResumptionEvent.class));

			Map<EventSource,EventSink[]> connections1 =
					new HashMap<EventSource,EventSink[]>();
			EventSource from11 =
					new EventSource(WiFiDisconnectionModel.URI,
									InterruptionEvent.class);
			EventSink[] to11 =
					new EventSink[] {
						new EventSink(WiFiBandwidthModel.URI,
									  InterruptionEvent.class)};
			connections1.put(from11, to11);
			EventSource from12 =
					new EventSource(WiFiDisconnectionModel.URI,
									ResumptionEvent.class);
			EventSink[] to12 =
					new EventSink[] {
						new EventSink(WiFiBandwidthModel.URI,
									  ResumptionEvent.class)};
			connections1.put(from12, to12);
			EventSource from13 =
					new EventSource(TicModel.URI + "-1",
									TicEvent.class);
			EventSink[] to13 =
					new EventSink[] {
						new EventSink(WiFiBandwidthSensorModel.URI,
									  TicEvent.class)};
			connections1.put(from13, to13);

			Map<VariableSource,VariableSink[]> bindings1 =
						new HashMap<VariableSource,VariableSink[]>();
			VariableSource source11 =
					new VariableSource("bandwidth",
									   Double.class,
									   WiFiBandwidthModel.URI);
			VariableSink[] sinks11 =
					new VariableSink[] {
							new VariableSink("bandwidth",
											 Double.class,
											 WiFiBandwidthSensorModel.URI)};
			bindings1.put(source11, sinks11);

			coupledModelDescriptors.put(
				WiFiModel.URI,
				new CoupledHIOA_Descriptor(
						WiFiModel.class,
						WiFiModel.URI,
						submodels1,
						imported1,
						reexported1,
						connections1,
						null,
						null,
						null,
						bindings1));

			// ----------------------------------------------------------------
			// Portable computer TIOA model
			// ----------------------------------------------------------------

			atomicModelDescriptors.put(
					PortableComputerStateModel.URI,
					AtomicHIOA_Descriptor.create(
							PortableComputerStateModel.class,
							PortableComputerStateModel.URI,
							TimeUnit.SECONDS,
							null));

			atomicModelDescriptors.put(
					BatterySensorModel.URI,
					AtomicHIOA_Descriptor.create(
							BatterySensorModel.class,
							BatterySensorModel.URI,
							TimeUnit.SECONDS,
							null));

			atomicModelDescriptors.put(
					TicModel.URI + "-2",
					AtomicModelDescriptor.create(
							TicModel.class,
							TicModel.URI + "-2",
							TimeUnit.SECONDS,
							null));

			Set<String> submodels2 = new HashSet<String>();
			submodels2.add(PortableComputerStateModel.URI);
			submodels2.add(BatterySensorModel.URI);
			submodels2.add(TicModel.URI + "-2");

			Map<Class<? extends EventI>,EventSink[]> imported2 =
					new HashMap<Class<? extends EventI>,EventSink[]>();
			imported2.put(
				InterruptionEvent.class,
				new EventSink[] {
						new EventSink(PortableComputerStateModel.URI,
									  InterruptionEvent.class)
				});
			imported2.put(
					ResumptionEvent.class,
					new EventSink[] {
						new EventSink(PortableComputerStateModel.URI,
									  ResumptionEvent.class)
					});
			imported2.put(
					Compressing.class,
					new EventSink[] {
						new EventSink(PortableComputerStateModel.URI,
									  Compressing.class)
					});
			imported2.put(
				NotCompressing.class,
				new EventSink[] {
						new EventSink(PortableComputerStateModel.URI,
									  NotCompressing.class)
				});
			imported2.put(
				LowBattery.class,
				new EventSink[] {
						new EventSink(PortableComputerStateModel.URI,
									  LowBattery.class)
				});

			Map<Class<? extends EventI>,ReexportedEvent> reexported2 =
					new HashMap<Class<? extends EventI>,ReexportedEvent>();
			reexported2.put(
					BatteryLevel.class,
					new ReexportedEvent(BatterySensorModel.URI,
										BatteryLevel.class));

			Map<EventSource,EventSink[]> connections2 =
								new HashMap<EventSource,EventSink[]>();
			EventSource from21 =
					new EventSource(TicModel.URI + "-2",
									TicEvent.class);
			EventSink[] to21 =
					new EventSink[] {
						new EventSink(BatterySensorModel.URI,
									  TicEvent.class)};
			connections2.put(from21, to21);

			Map<VariableSource,VariableSink[]> bindings2 =
							new HashMap<VariableSource,VariableSink[]>();
			VariableSource source21 =
				new VariableSource("remainingCapacity",
								   Double.class,
								   PortableComputerStateModel.URI);
			VariableSink[] sinks21 =
				new VariableSink[] {
						new VariableSink("remainingCapacity",
										 Double.class,
										 BatterySensorModel.URI)};
			bindings2.put(source21, sinks21);

			coupledModelDescriptors.put(
					PortableComputerModel.URI,
					new CoupledHIOA_Descriptor(
							PortableComputerModel.class,
							PortableComputerModel.URI,
							submodels2,
							imported2,
							reexported2,
							connections2,
							null,
							null,
							null,
							bindings2));

			// ----------------------------------------------------------------
			// Portable computer controller TIOA model
			// ----------------------------------------------------------------

			atomicModelDescriptors.put(
					ControllerModel.PORTABLE_URI,
					AtomicModelDescriptor.create(
							ControllerModel.class,
							ControllerModel.PORTABLE_URI,
							TimeUnit.SECONDS,
							null));

			// ----------------------------------------------------------------
			// Server controller TIOA model
			// ----------------------------------------------------------------

			atomicModelDescriptors.put(
					ControllerModel.SERVER_URI,
					AtomicModelDescriptor.create(
							ControllerModel.class,
							ControllerModel.SERVER_URI,
							TimeUnit.SECONDS,
							null));

			// ----------------------------------------------------------------
			// Server TIOA model
			// ----------------------------------------------------------------

			atomicModelDescriptors.put(
					ServerModel.URI,
					AtomicModelDescriptor.create(
							ServerModel.class,
							ServerModel.URI,
							TimeUnit.SECONDS,
							null));

			// ----------------------------------------------------------------
			// Network TIOA model
			// ----------------------------------------------------------------

			atomicModelDescriptors.put(
					NetworkModel.URI,
					AtomicModelDescriptor.create(
							NetworkModel.class,
							NetworkModel.URI,
							TimeUnit.SECONDS,
							null));

			// ----------------------------------------------------------------
			// Full architecture and Molene global model
			// ----------------------------------------------------------------

			Set<String> submodels3 = new HashSet<String>();
			submodels3.add(WiFiModel.URI);
			submodels3.add(PortableComputerModel.URI);
			submodels3.add(ControllerModel.PORTABLE_URI);
			submodels3.add(ControllerModel.SERVER_URI);
			submodels3.add(ServerModel.URI);
			submodels3.add(NetworkModel.URI);

			Map<EventSource,EventSink[]> connections3 =
								new HashMap<EventSource,EventSink[]>();

			EventSource from31 =
					new EventSource(
							WiFiModel.URI,
							WiFiBandwidthReading.class);
			EventSink[] to31 =
					new EventSink[] {
							new EventSink(
									ControllerModel.PORTABLE_URI,
									WiFiBandwidthReading.class),
							new EventSink(
									NetworkModel.URI,
									WiFiBandwidthReading.class)};
			connections3.put(from31, to31);
			EventSource from311 =
					new EventSource(
							WiFiModel.URI,
							InterruptionEvent.class);
			EventSink[] to311 =
					new EventSink[] {
							new EventSink(
									PortableComputerModel.URI,
									InterruptionEvent.class)};
			connections3.put(from311, to311);
			EventSource from312 =
					new EventSource(
							WiFiModel.URI,
							ResumptionEvent.class);
			EventSink[] to312 =
					new EventSink[] {
							new EventSink(
									PortableComputerModel.URI,
									ResumptionEvent.class)};
			connections3.put(from312, to312);
			EventSource from32 =
					new EventSource(
							PortableComputerModel.URI,
							BatteryLevel.class);
			EventSink[] to32 =
					new EventSink[] {
							new EventSink(
									ControllerModel.PORTABLE_URI,
									BatteryLevel.class),
							new EventSink(
									NetworkModel.URI,
									BatteryLevel.class)};
			connections3.put(from32, to32);
			EventSource from33 =
					new EventSource(
							ControllerModel.PORTABLE_URI,
							Compressing.class);
			EventSink[] to33 =
					new EventSink[] {
							new EventSink(
									PortableComputerModel.URI,
									Compressing.class)};
			connections3.put(from33, to33);
			EventSource from34 =
					new EventSource(
							ControllerModel.PORTABLE_URI,
							NotCompressing.class);
			EventSink[] to34 =
					new EventSink[] {
							new EventSink(
									PortableComputerModel.URI,
									NotCompressing.class)};
			connections3.put(from34, to34);
			EventSource from35 =
					new EventSource(
							ControllerModel.PORTABLE_URI,
							LowBattery.class);
			EventSink[] to35 =
					new EventSink[] {
							new EventSink(
									PortableComputerModel.URI,
									LowBattery.class)};
			connections3.put(from35, to35);
			EventSource from36 =
					new EventSource(
							NetworkModel.URI,
							WiFiBandwidthReading.class);
			EventSink[] to36 =
					new EventSink[] {
							new EventSink(
									ControllerModel.SERVER_URI,
									WiFiBandwidthReading.class)};
			connections3.put(from36, to36);
			EventSource from37 =
					new EventSource(
							NetworkModel.URI,
							BatteryLevel.class);
			EventSink[] to37 =
					new EventSink[] {
							new EventSink(
									ControllerModel.SERVER_URI,
									BatteryLevel.class)};
			connections3.put(from37, to37);
			EventSource from38 =
					new EventSource(
							ControllerModel.SERVER_URI,
							Compressing.class);
			EventSink[] to38 =
					new EventSink[] {
							new EventSink(
									ServerModel.URI,
									Compressing.class)};
			connections3.put(from38, to38);
			EventSource from39 =
					new EventSource(
							ControllerModel.SERVER_URI,
							NotCompressing.class);
			EventSink[] to39 =
					new EventSink[] {
							new EventSink(
									ServerModel.URI,
									NotCompressing.class)};
			connections3.put(from39, to39);
			EventSource from310 =
					new EventSource(
							ControllerModel.SERVER_URI,
							LowBattery.class);
			EventSink[] to310 =
					new EventSink[] {
							new EventSink(
									ServerModel.URI,
									LowBattery.class)};
			connections3.put(from310, to310);

			coupledModelDescriptors.put(
					MoleneModel.URI,
					new CoupledModelDescriptor(
							MoleneModel.class,
							MoleneModel.URI,
							submodels3,
							null,
							null,
							connections3,
							null));

			ArchitectureI architecture =
					new Architecture(
							MoleneModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.SECONDS);

			// ----------------------------------------------------------------
			// Simulation run
			// ----------------------------------------------------------------

			SimulatorI se = architecture.constructSimulator();
			se.setDebugLevel(0);
//			System.out.println(se.simulatorAsString());

			Map<String,Object> simParams = new HashMap<>();

			String modelURI = TicModel.URI  + "-1";
			simParams.put(ModelI.createRunParameterName(
										modelURI,
										TicModel.DELAY_PARAMETER_NAME),
						  new Duration(10.0, TimeUnit.SECONDS));

			modelURI = TicModel.URI  + "-2";
			simParams.put(
				ModelI.createRunParameterName(modelURI,
											  TicModel.DELAY_PARAMETER_NAME),
				new Duration(50.0, TimeUnit.SECONDS));

			modelURI = WiFiDisconnectionModel.URI;
			simParams.put(
				ModelI.createRunParameterName(modelURI,
											  WiFiDisconnectionModel.MTBI),
				200.0);
			simParams.put(
				ModelI.createRunParameterName(modelURI,
											  WiFiDisconnectionModel.MID),
				10.0);
			simParams.put(
				ModelI.createRunParameterName(
								modelURI,
								PlotterDescription.PLOTTING_PARAM_NAME),
				new PlotterDescription(
						"WiFi Disconnection Model",
						"Time (sec)",
						"Connected/Interrupted",
						SimulationMain.ORIGIN_X,
						SimulationMain.ORIGIN_Y,
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight()));

			modelURI = WiFiBandwidthModel.URI;
			simParams.put(
				ModelI.createRunParameterName(modelURI,
											  WiFiBandwidthModel.MAX_BANDWIDTH),
				50.0);
			simParams.put(
				ModelI.createRunParameterName(modelURI,
											  WiFiBandwidthModel.BAAR),
				1.75);
			simParams.put(
				ModelI.createRunParameterName(modelURI,
											  WiFiBandwidthModel.BBAR), 
				1.75);
			simParams.put(
				ModelI.createRunParameterName(modelURI,
											  WiFiBandwidthModel.BMSF),
				0.5);
			simParams.put(
				ModelI.createRunParameterName(modelURI,
											  WiFiBandwidthModel.BIS), 
				0.5);
			simParams.put(
				ModelI.createRunParameterName(
									modelURI,
									PlotterDescription.PLOTTING_PARAM_NAME),
				new PlotterDescription(
						"WiFi Bandwidth Model",
						"Time (sec)",
						"Bandwidth (Mbps)",
						SimulationMain.ORIGIN_X,
						SimulationMain.ORIGIN_Y +
						SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight()));

			modelURI = WiFiBandwidthSensorModel.URI;
			simParams.put(
				ModelI.createRunParameterName(
									modelURI,
									PlotterDescription.PLOTTING_PARAM_NAME),
				new PlotterDescription(
						"WiFi Bandwidth Sensor Model",
						"Time (sec)",
						"Bandwidth (Mbps)",
						SimulationMain.ORIGIN_X,
						SimulationMain.ORIGIN_Y +
								2*SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight()));

			modelURI = PortableComputerStateModel.URI;
			simParams.put(
				ModelI.createRunParameterName(
						modelURI,
						PortableComputerStateModel.EVALUATION_STEP_PARAM_NAME),
				1.0);
			simParams.put(
				ModelI.createRunParameterName(
						modelURI,
						PortableComputerStateModel.BATTERY_PLOTTING_PARAM_NAME),
				new PlotterDescription(
						"Portable Computer Model - Battery Level",
						"Time (sec)",
						"Battery level (mAh)",
						SimulationMain.ORIGIN_X +
							SimulationMain.getPlotterWidth(),
						SimulationMain.ORIGIN_Y,
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight()));
			simParams.put(
				ModelI.createRunParameterName(
						modelURI,
						PortableComputerStateModel.STATE_PLOTTING_PARAM_NAME),
				new PlotterDescription(
						"Portable Computer Model - State",
						"Time (sec)",
						"State",
						SimulationMain.ORIGIN_X +
						  		SimulationMain.getPlotterWidth(),
						SimulationMain.ORIGIN_Y +
								2 * SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight()));

			modelURI = BatterySensorModel.URI;
			simParams.put(
				ModelI.createRunParameterName(
									modelURI,
									PlotterDescription.PLOTTING_PARAM_NAME),
				new PlotterDescription(
						"Battery Level Sensor Model",
						"Time (sec)",
						"Battery level (mAh)",
						SimulationMain.ORIGIN_X +
						  		SimulationMain.getPlotterWidth(),
						SimulationMain.ORIGIN_Y +
								SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight()));

			modelURI = ControllerModel.PORTABLE_URI;
			simParams.put(
				ModelI.createRunParameterName(
									modelURI,
									PlotterDescription.PLOTTING_PARAM_NAME),
				new PlotterDescription(
						"Portable Computer Controller Model",
						"Time (sec)",
						"Decision",
						SimulationMain.ORIGIN_X +
						  		SimulationMain.getPlotterWidth(),
						SimulationMain.ORIGIN_Y +
								3 * SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight()));

			modelURI = ServerModel.URI;
			simParams.put(
				ModelI.createRunParameterName(
									modelURI,
									PlotterDescription.PLOTTING_PARAM_NAME),
				new PlotterDescription(
						"Server Model",
						"Time (sec)",
						"State",
						SimulationMain.ORIGIN_X +
						  		2 * SimulationMain.getPlotterWidth(),
						SimulationMain.ORIGIN_Y +
								2 * SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight()));

			modelURI = ControllerModel.SERVER_URI;
			simParams.put(
				ModelI.createRunParameterName(
									modelURI,
									PlotterDescription.PLOTTING_PARAM_NAME),
				new PlotterDescription(
						"Server Controller Model",
						"Time (sec)",
						"Decision",
						SimulationMain.ORIGIN_X +
						  		2 * SimulationMain.getPlotterWidth(),
						SimulationMain.ORIGIN_Y +
								3 * SimulationMain.getPlotterHeight(),
						SimulationMain.getPlotterWidth(),
						SimulationMain.getPlotterHeight()));

			modelURI = NetworkModel.URI;
			simParams.put(
				ModelI.createRunParameterName(
									modelURI,
									NetworkModel.GAMMA_SHAPE_PARAM_NAME),
				11.0);
			simParams.put(
				ModelI.createRunParameterName(
									modelURI,
									NetworkModel.GAMMA_SCALE_PARAM_NAME),
				2.0);

			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
			se.getSimulatedModel().setSimulationRunParameters(simParams);
			long start = System.currentTimeMillis();
			se.doStandAloneSimulation(0.0, SIMULATION_DURATION);
			long end = System.currentTimeMillis();
			System.out.println(se.getFinalReport());
			System.out.println(
					"First simulation run ends; execution duration = "
					+ (end - start));
			Thread.sleep(2000L);
			// Before starting a new run, cleanup the simulators and models.
			se.finaliseSimulation();

			// Second run, for the simplicity of the example, the same run
			// parameters are used again.
			se.setSimulationRunParameters(simParams);
			start = System.currentTimeMillis();
			se.doStandAloneSimulation(0.0, SIMULATION_DURATION);
			end = System.currentTimeMillis();
			System.out.println(se.getFinalReport());
			System.out.println(
					"Second simulation run ends; execution duration = "
					+ (end - start));
			Thread.sleep(100000L);
			// Cleanup the simulators and models.
			se.finaliseSimulation();

			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
// -----------------------------------------------------------------------------
