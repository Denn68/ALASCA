package fr.sorbonne_u.generator;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
    	String xml = Paths.get("ALASCA-etape-1-bis/HEM-2025-etape1-30092025-src/hem-adapter/kettleci-descriptor.xml").toAbsolutePath().toString();
        String rnc = Paths.get("ALASCA-etape-1-bis/HEM-2025-etape1-30092025-src/hem-adapter/control-adapter.rnc").toAbsolutePath().toString();
        String out = Paths.get("generated").toAbsolutePath().toString();

        String[] heaterArgs = new String[] {
            "--xml=" + xml,
            "--rnc=" + rnc, // VALIDATION RNC OBLIGATOIRE
            "--super=fr.sorbonne_u.components.connectors.AbstractConnector",
            "--connector-class=fr.sorbonne_u.components.hem2025e1.equipments.hem.KettleConnector",
            "--out=" + out
            // NOTE: plus besoin d'--equipment-class pour compiler les bodies
        };

        System.out.println("[Main] Génération KettleConnector…");
        ControlAdapterGenerator.main(heaterArgs);
        System.out.println("[Main] OK: KettleConnector généré dans " + out);
    }
}
