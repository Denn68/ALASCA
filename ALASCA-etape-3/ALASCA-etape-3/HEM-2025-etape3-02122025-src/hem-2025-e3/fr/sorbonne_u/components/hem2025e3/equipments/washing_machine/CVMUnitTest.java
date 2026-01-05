package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil.Local_SIL_SimulationArchitectures;

public class			CVMUnitTest
extends		AbstractCVM
{
	public				CVMUnitTest() throws Exception {
		super();
	}

	@Override
	public void			deploy() throws Exception {
		String wmInboundURI = "WM_INBOUND_URI";
		
		// Déploiement du composant CyPhy WashingMachine
		AbstractComponent.createComponent(
				WashingMachineCyPhy.class.getCanonicalName(),
				new Object[]{
						wmInboundURI,
						Local_SIL_SimulationArchitectures.WM_SIL_URI,
						"SECONDS"
				});

		// Déploiement du Tester
		AbstractComponent.createComponent(
				WashingMachineTesterCyPhy.class.getCanonicalName(),
				new Object[]{wmInboundURI});

		super.deploy();
	}

	public static void	main(String[] args) {
		try {
			CVMUnitTest cvm = new CVMUnitTest();
			cvm.startStandardLifeCycle(20000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}