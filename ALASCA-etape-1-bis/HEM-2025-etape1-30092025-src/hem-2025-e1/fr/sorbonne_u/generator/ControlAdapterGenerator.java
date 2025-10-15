package fr.sorbonne_u.generator;

import javassist.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Générateur de connecteur (forwarder) basé sur un ControlAdapterModel.
 * - Génère le .class via Javassist
 * - Génère aussi un .java pour visualiser le code
 *
 * Java 8 compatible.
 */
public class ControlAdapterGenerator {

    /** Dossier par défaut pour écrire les .class/.java générés. */
    public static final String DEFAULT_OUTPUT_DIR =
            System.getProperty("user.dir") + File.separator + "src-gen";

    /**
     * Génère un connecteur forwarder à partir du modèle :
     * - Chaque méthode de l'interface requise appelle la méthode mappée de l'interface offerte via this.offering.
     * - Si le mapping est vide, on tente un mapping 1–1 (même nom & signature).
     * - Écrit le .class et un .java lisible.
     */
    public static Class<?> generateConnectorFromModel(
            ControlAdapterModel model,
            String connectorCanonicalClassName,
            Class<?> connectorSuperclass,
            Class<?> requiredInterface,
            Map<String, String> methodNamesMap,
            String outputDir
    ) throws Exception {

        if (model == null) throw new IllegalArgumentException("model == null");
        if (connectorCanonicalClassName == null || connectorCanonicalClassName.trim().isEmpty())
            throw new IllegalArgumentException("connectorCanonicalClassName manquant");
        if (connectorSuperclass == null) throw new IllegalArgumentException("connectorSuperclass manquant");
        if (requiredInterface == null) throw new IllegalArgumentException("requiredInterface manquant");

        // offeredInterface depuis le modèle
        String offeredFqn = model.getOfferedInterface();
        if (offeredFqn == null || offeredFqn.trim().isEmpty()) {
            throw new IllegalArgumentException("Le modèle ne contient pas le FQN de l'interface offered.");
        }
        Class<?> offeredInterface = Class.forName(offeredFqn);

        // mapping effectif
        Map<String, String> effectiveMap = new LinkedHashMap<>();
        if (methodNamesMap != null) effectiveMap.putAll(methodNamesMap);
        if (effectiveMap.isEmpty()) {
            System.out.println("[GEN] methodNamesMap vide → tentative d'auto-déduction 1–1.");
            effectiveMap.putAll(buildOneToOneMap(requiredInterface, offeredInterface));
        }

        // logs
        System.out.println("[GEN] connectorCanonicalClassName = " + connectorCanonicalClassName);
        System.out.println("[GEN] connectorSuperclass         = " + connectorSuperclass.getCanonicalName());
        System.out.println("[GEN] requiredInterface           = " + requiredInterface.getCanonicalName());
        System.out.println("[GEN] offeredInterface            = " + offeredInterface.getCanonicalName());
        System.out.println("[GEN] methodNamesMap              = " + effectiveMap);
        System.out.println("[GEN] outputDir                   = " + outputDir);

        Files.createDirectories(Paths.get(outputDir));

        // 1) Génération .class via Javassist
        Class<?> generated = makeConnectorClassJavassist(
                connectorCanonicalClassName,
                connectorSuperclass,
                requiredInterface,
                offeredInterface,
                new LinkedHashMap<>(effectiveMap) // HashMap requis par la signature
        );

        // 2) Écriture .class sur disque (utile si on veut garder la classe)
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass cc = pool.get(connectorCanonicalClassName);
            cc.writeFile(outputDir);
            System.out.println("[GEN] .class écrit dans : " + outputDir);
        } catch (Throwable t) {
            System.err.println("[GEN] Écriture .class échouée (non bloquant) : " + t);
        }

        // 3) Génération du .java pour visualiser
        writeJavaSourceForwarder(
                connectorCanonicalClassName,
                connectorSuperclass,
                requiredInterface,
                offeredInterface,
                effectiveMap,
                outputDir
        );

        return generated;
    }

    /**
     * Version inspirée de ton snippet : implémente chaque méthode de l'interface requise
     * en appelant la méthode mappée de l'interface offerte via this.offering.
     */
    public static Class<?> makeConnectorClassJavassist(
            String connectorCanonicalClassName,
            Class<?> connectorSuperclass,
            Class<?> requiredInterface,
            Class<?> offeredInterface,
            HashMap<String,String> methodNamesMap
    ) throws Exception
    {
        ClassPool pool = ClassPool.getDefault();
        CtClass cs = pool.get(connectorSuperclass.getCanonicalName());
        CtClass ri = pool.get(requiredInterface.getCanonicalName());
        CtClass oi = pool.get(offeredInterface.getCanonicalName());
        CtClass connectorCtClass = pool.makeClass(connectorCanonicalClassName);
        connectorCtClass.setSuperclass(cs);

        Method[] methodsToImplement = requiredInterface.getDeclaredMethods();
        for (int i = 0 ; i < methodsToImplement.length ; i++) {
            String mapped = methodNamesMap.get(methodsToImplement[i].getName());
            if (mapped == null) {
                System.out.println("[GEN] Pas de mapping pour " + methodsToImplement[i].getName() + " → ignorée.");
                continue;
            }

            String source = "public ";
            source += methodsToImplement[i].getReturnType().getName() + " ";
            source += methodsToImplement[i].getName() + "(";

            Class<?>[] pt = methodsToImplement[i].getParameterTypes();
            String callParam = "";
            for (int j = 0 ; j < pt.length ; j++) {
                String pName = "aaa" + j;
                source += pt[j].getCanonicalName() + " " + pName;
                callParam += pName;
                if (j < pt.length - 1) {
                    source += ", ";
                    callParam += ", ";
                }
            }
            source += ")";

            Class<?>[] et = methodsToImplement[i].getExceptionTypes();
            if (et != null && et.length > 0) {
                source += " throws ";
                for (int z = 0 ; z < et.length ; z++) {
                    source += et[z].getCanonicalName();
                    if (z < et.length - 1) {
                        source += ",";
                    }
                }
            }

            source += "\n{\n";
            if (methodsToImplement[i].getReturnType().equals(void.class)) {
                source += "((" + offeredInterface.getCanonicalName() + ")this.offering).";
                source += mapped + "(" + callParam + ");\n";
            } else {
                source += "return ((" + offeredInterface.getCanonicalName() + ")this.offering).";
                source += mapped + "(" + callParam + ");\n";
            }
            source += "}\n";

            CtMethod theCtMethod = CtMethod.make(source, connectorCtClass);
            connectorCtClass.addMethod(theCtMethod);
        }

        connectorCtClass.setInterfaces(new CtClass[]{ri});
        ri.detach(); cs.detach(); oi.detach();
        Class<?> ret = connectorCtClass.toClass();
        connectorCtClass.detach();
        return ret;
    }

    /**
     * Tente de construire un mapping 1–1 pour les méthodes ayant le même nom et la même signature.
     */
    public static Map<String,String> buildOneToOneMap(Class<?> requiredInterface, Class<?> offeredInterface) {
        Map<String,String> map = new LinkedHashMap<>();
        Method[] reqMethods = requiredInterface.getDeclaredMethods();

        for (Method rm : reqMethods) {
            try {
                Method om = offeredInterface.getMethod(rm.getName(), rm.getParameterTypes());
                map.put(rm.getName(), om.getName());
            } catch (NoSuchMethodException nsme) {
                // ignore
            }
        }
        return map;
    }

    /**
     * Écrit un fichier source .java qui correspond (logiquement) à ce que le bytecode forwarder réalise.
     */
    private static void writeJavaSourceForwarder(
            String connectorCanonicalClassName,
            Class<?> connectorSuperclass,
            Class<?> requiredInterface,
            Class<?> offeredInterface,
            Map<String,String> methodNamesMap,
            String outputDir
    ) throws IOException {

        int lastDot = connectorCanonicalClassName.lastIndexOf('.');
        String pkg = (lastDot > 0) ? connectorCanonicalClassName.substring(0, lastDot) : null;
        String simpleName = (lastDot > 0) ? connectorCanonicalClassName.substring(lastDot + 1) : connectorCanonicalClassName;

        StringBuilder sb = new StringBuilder(16_384);

        // package
        if (pkg != null) {
            sb.append("package ").append(pkg).append(";\n\n");
        }

        // imports (minimaux; on peut aussi tout mettre en FQN pour être safe)
        sb.append("import ").append(connectorSuperclass.getCanonicalName()).append(";\n");
        sb.append("import ").append(requiredInterface.getCanonicalName()).append(";\n");
        sb.append("import ").append(offeredInterface.getCanonicalName()).append(";\n\n");

        // header
        sb.append("/**\n")
          .append(" * Classe générée automatiquement (forwarder).\n")
          .append(" * Chaque méthode de ").append(requiredInterface.getSimpleName())
          .append(" appelle la méthode mappée de ").append(offeredInterface.getSimpleName())
          .append(" via this.offering.\n")
          .append(" */\n");

        sb.append("public class ").append(simpleName)
          .append(" extends ").append(connectorSuperclass.getSimpleName())
          .append(" implements ").append(requiredInterface.getSimpleName())
          .append(" {\n\n");

        // constructeur par défaut
        sb.append("    public ").append(simpleName).append("() {\n")
          .append("        super();\n")
          .append("    }\n\n");

        // méthodes
        Method[] reqMethods = requiredInterface.getDeclaredMethods();
        for (Method m : reqMethods) {
            String mapped = methodNamesMap.get(m.getName());
            if (mapped == null) {
                // on génère quand même une méthode qui throw pour que ce soit visible dans le .java
                sb.append("    @Override\n    public ")
                  .append(m.getReturnType().getCanonicalName()).append(" ")
                  .append(m.getName()).append("(")
                  .append(renderParams(m.getParameterTypes()))
                  .append(")");
                appendThrows(sb, m.getExceptionTypes());
                sb.append(" {\n")
                  .append("        throw new UnsupportedOperationException(\"No mapping provided for method: ")
                  .append(m.getName()).append("\");\n")
                  .append("    }\n\n");
                continue;
            }

            sb.append("    @Override\n    public ")
              .append(m.getReturnType().getCanonicalName()).append(" ")
              .append(m.getName()).append("(")
              .append(renderParams(m.getParameterTypes()))
              .append(")");
            appendThrows(sb, m.getExceptionTypes());
            sb.append(" {\n");

            // appel
            String cast = "((" + offeredInterface.getSimpleName() + ") this.offering)";
            String args = renderArgNames(m.getParameterTypes().length);

            if (m.getReturnType().equals(void.class)) {
                sb.append("        ").append(cast).append(".")
                  .append(mapped).append("(").append(args).append(");\n");
            } else {
                sb.append("        return ").append(cast).append(".")
                  .append(mapped).append("(").append(args).append(");\n");
            }
            sb.append("    }\n\n");
        }

        sb.append("}\n");

        // écriture fichier
        String dir = outputDir + File.separator + (pkg == null ? "" : pkg.replace('.', File.separatorChar));
        Files.createDirectories(Paths.get(dir));
        File javaFile = new File(dir, simpleName + ".java");
        Files.write(javaFile.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8));
        System.out.println("[GEN] .java écrit dans : " + javaFile.getAbsolutePath());
    }

    // --------- helpers rendu source ---------

    private static String renderParams(Class<?>[] pts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pts.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(pts[i].getCanonicalName()).append(" aaa").append(i);
        }
        return sb.toString();
    }

    private static String renderArgNames(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) sb.append(", ");
            sb.append("aaa").append(i);
        }
        return sb.toString();
    }

    private static void appendThrows(StringBuilder sb, Class<?>[] exceptions) {
        if (exceptions != null && exceptions.length > 0) {
            sb.append(" throws ");
            for (int i = 0; i < exceptions.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(exceptions[i].getCanonicalName());
            }
        }
    }

    /* ===================== Exemple d'utilisation (facultatif) =====================
    public static void main(String[] args) throws Exception {
        // 1) Obtenir le ControlAdapterModel (via ton parser)
        ControlAdapterModel model = ...;

        // 2) Paramétrer la génération
        String connectorFQN = "fr.sorbonne_u.components.hem2025e1.equipments.hem.HeaterConnector";
        Class<?> superClass = fr.sorbonne_u.components.connectors.AbstractConnector.class;
        Class<?> requiredCI = fr.sorbonne_u.components.hem2025.bases.AdjustableCI.class;

        // 3) Mappings (ou null/empty pour auto 1–1)
        Map<String,String> map = new LinkedHashMap<>();
        map.put("maxMode", "maxMode");
        map.put("currentMode", "currentMode");
        map.put("getModeConsumption", "getModeConsumption");
        map.put("suspended", "suspended");
        map.put("suspend", "suspend");
        map.put("resume", "resume");
        map.put("emergency", "emergency");

        // 4) Générer
        Class<?> clazz = generateConnectorFromModel(
                model, connectorFQN, superClass, requiredCI, map, DEFAULT_OUTPUT_DIR
        );

        System.out.println("Généré : " + clazz.getCanonicalName());
    }
    ============================================================================= */
}
