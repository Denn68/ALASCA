package fr.sorbonne_u.generator;

import javassist.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Générateur générique de connecteurs à partir de fichiers control-adapter XML.
 * Exemple d'appel :
 *     java fr.sorbonne_u.generator.ControlAdapterGenerator /path/to/heaterci-descriptor.xml
 */
public class ControlAdapterGenerator {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: java ControlAdapterGenerator <descriptor.xml>");
            System.exit(1);
        }

        File xmlFile = new File(args[0]);
        if (!xmlFile.exists()) {
            System.err.println("Fichier XML introuvable : " + xmlFile.getAbsolutePath());
            System.exit(2);
        }


        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(xmlFile);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        String offered = root.getAttribute("offered"); // ex: fr.sorbonne_u.components.equipments.heater.HeaterExternalControlJava4CI
        String uid = root.getAttribute("uid");

        // Extraire le nom du connecteur à générer
        String baseName = extractBaseName(offered);
        String className = baseName + "Connector";

        System.out.println("Génération du connecteur : " + className + " (UID: " + uid + ")");
        System.out.println("Interface offerte : " + offered);

        // Création de la classe
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass("fr.sorbonne_u.generated." + className);
        cc.setSuperclass(pool.get("fr.sorbonne_u.components.connectors.AbstractConnector"));
        cc.addInterface(pool.get("fr.sorbonne_u.components.hem2025.bases.AdjustableCI"));

        // Variables d'instance
        NodeList vars = doc.getElementsByTagName("instance-var");
        for (int i = 0; i < vars.getLength(); i++) {
            Element var = (Element) vars.item(i);
            String type = var.getAttribute("type");
            String name = var.getAttribute("name");
            String modifiers = var.getAttribute("modifiers");
            String init = var.getAttribute("static-init");

            int mod = getModifiers(modifiers);
            CtField field = new CtField(pool.get(type), name, cc);
            field.setModifiers(mod);
            if (init != null && !init.isEmpty()) {
                cc.addField(field, CtField.Initializer.byExpr(init));
            } else {
                cc.addField(field);
            }
            System.out.println("Variable : " + name + " : " + type);
        }

        // Méthodes internes
        NodeList internals = doc.getElementsByTagName("internal");
        for (int i = 0; i < internals.getLength(); i++) {
            Element mEl = (Element) internals.item(i);
            String name = mEl.getAttribute("name");
            String returnType = mEl.getAttribute("type");

            List<String[]> params = new ArrayList<>();
            NodeList plist = mEl.getElementsByTagName("parameter");
            for (int j = 0; j < plist.getLength(); j++) {
                Element pEl = (Element) plist.item(j);
                params.add(new String[]{pEl.getAttribute("type"), pEl.getAttribute("name")});
            }

            String body = mEl.getElementsByTagName("body").item(0).getTextContent().trim();
            CtClass[] paramTypes = new CtClass[params.size()];
            for (int k = 0; k < params.size(); k++) {
                paramTypes[k] = pool.get(params.get(k)[0]);
            }

            CtMethod method = new CtMethod(pool.get(returnType), name, paramTypes, cc);
            method.setModifiers(Modifier.PROTECTED);
            method.setBody("{ " + body + " }");
            cc.addMethod(method);
            System.out.println("Méthode interne : " + name);
        }

        // Méthodes publiques (toutes les balises directes hors internal, instance-var)
        String[] publicMethodTags = new String[]{
                "maxMode", "upMode", "downMode", "setMode",
                "currentMode", "getModeConsumption", "suspended",
                "suspend", "resume", "emergency"
        };

        for (String tag : publicMethodTags) {
            NodeList list = doc.getElementsByTagName(tag);
            if (list.getLength() > 0) {
                Element mEl = (Element) list.item(0);
                String body = mEl.getElementsByTagName("body").item(0).getTextContent().trim();

                CtMethod method = new CtMethod(pool.get(resolveReturnType(tag)), tag, new CtClass[]{}, cc);
                method.setModifiers(Modifier.PUBLIC);
                method.setBody("{ " + body + " }");
                cc.addMethod(method);
                System.out.println("Méthode publique : " + tag);
            }
        }

        // Sauvegarde
        File outDir = new File("generated_classes");
        outDir.mkdirs();
        cc.writeFile(outDir.getAbsolutePath());

        System.out.println("\nClasse générée : " + outDir.getAbsolutePath() + "/" + className + ".class");
    }

    // Utilitaires
    private static String extractBaseName(String offered) {
        String[] parts = offered.split("\\.");
        String last = parts[parts.length - 1]; // <ClassName>ExternalControlJava4CI
        return last.replace("ExternalControlJava4CI", "");
    }

    private static String resolveReturnType(String methodName) {
        switch (methodName) {
            case "maxMode": return "int";
            case "currentMode": return "int";
            case "getModeConsumption": return "double";
            case "emergency": return "double";
            case "suspended": return "boolean";
            case "upMode": case "downMode": case "setMode": case "suspend": case "resume":
                return "boolean";
            default: return "void";
        }
    }

    private static int getModifiers(String mods) {
        int m = 0;
        if (mods.contains("public")) m |= Modifier.PUBLIC;
        if (mods.contains("protected")) m |= Modifier.PROTECTED;
        if (mods.contains("private")) m |= Modifier.PRIVATE;
        if (mods.contains("static")) m |= Modifier.STATIC;
        return m;
    }
}
