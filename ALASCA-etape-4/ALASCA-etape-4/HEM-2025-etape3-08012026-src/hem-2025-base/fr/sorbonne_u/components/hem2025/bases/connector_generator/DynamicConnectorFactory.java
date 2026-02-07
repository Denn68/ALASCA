package fr.sorbonne_u.components.hem2025.bases.connector_generator;

import java.util.List;
import java.util.StringJoiner;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;

// -------------------------------------------------------------------------
/**
 * Dynamically fabricates a BCM connector class at runtime using Javassist,
 * based on an {@link AdapterDescriptor} previously parsed from XML.
 *
 * <p>
 * The fabricated class extends
 * {@code fr.sorbonne_u.components.connectors.AbstractConnector} and
 * implements
 * {@code fr.sorbonne_u.components.hem2025.bases.AdjustableCI}, acting as
 * a bridge between the generic HEM control interface and the equipment's
 * own offered interface.
 * </p>
 *
 * <p>
 * Created on : 2026-01-30
 * </p>
 */
public class DynamicConnectorFactory {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** Fully-qualified name of the BCM abstract connector. */
    private static final String CONNECTOR_BASE = "fr.sorbonne_u.components.connectors.AbstractConnector";

    /** Fully-qualified name of the AdjustableCI component interface. */
    private static final String ADJUSTABLE_IFACE = "fr.sorbonne_u.components.hem2025.bases.AdjustableCI";

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Fabricate a new connector class whose name is {@code fqClassName},
     * load it in the current JVM, and return its {@code Class<?>} object.
     *
     * @param descriptor  the parsed adapter descriptor.
     * @param fqClassName fully-qualified name for the connector to create
     *                    (e.g. {@code "generated.KettleConnector"}).
     * @return the freshly loaded {@code Class<?>}.
     * @throws Exception if code generation or compilation fails.
     */
    public static Class<?> fabricate(AdapterDescriptor descriptor,
            String fqClassName) throws Exception {

        ClassPool cp = ClassPool.getDefault();
        cp.insertClassPath(
                new LoaderClassPath(
                        Thread.currentThread().getContextClassLoader()));

        // ---- 1. Create the shell class --------------------------------
        CtClass shell = cp.makeClass(fqClassName);
        shell.setSuperclass(cp.get(CONNECTOR_BASE));
        shell.addInterface(cp.get(ADJUSTABLE_IFACE));

        // ---- 2. Inject fields -----------------------------------------
        injectFields(shell, descriptor.getFields());

        // ---- 3. Inject a no-arg constructor ----------------------------
        injectConstructor(shell, fqClassName, descriptor.getFields());

        // ---- 4. Inject operations (internal helpers + AdjustableCI) ----
        for (AdapterDescriptor.Operation op : descriptor.getOperations()) {
            injectMethod(shell, op);
        }

        // ---- 5. Materialise and return --------------------------------
        Class<?> result = shell.toClass();
        shell.detach();
        System.out.println("[DynamicConnectorFactory] classe fabriquée : "
                + fqClassName);
        return result;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Translates every {@link AdapterDescriptor.Field} into a Javassist
     * {@code CtField} and adds it to {@code target}.
     */
    private static void injectFields(CtClass target,
            List<AdapterDescriptor.Field> fields)
            throws Exception {

        for (AdapterDescriptor.Field f : fields) {
            StringBuilder src = new StringBuilder();
            src.append(f.getVisibility()).append(' ')
                    .append(f.getDataType()).append(' ')
                    .append(f.getIdentifier());
            if (f.hasInitialValue()) {
                src.append(" = ").append(f.getInitialValue());
            }
            src.append(';');
            target.addField(CtField.make(src.toString(), target));
        }
    }

    /**
     * Builds and adds a public no-argument constructor that initialises
     * non-static fields with their declared initial values.
     */
    private static void injectConstructor(CtClass target,
            String simpleClassName,
            List<AdapterDescriptor.Field> fields)
            throws Exception {

        // extract the simple name from the FQCN
        String simple = simpleClassName.contains(".")
                ? simpleClassName.substring(
                        simpleClassName.lastIndexOf('.') + 1)
                : simpleClassName;

        StringBuilder body = new StringBuilder();
        body.append("public ").append(simple).append("() {\n");
        body.append("  super();\n");

        for (AdapterDescriptor.Field f : fields) {
            if (f.isStatic())
                continue;
            if (f.hasInitialValue()) {
                body.append("  this.").append(f.getIdentifier())
                        .append(" = ").append(f.getInitialValue())
                        .append(";\n");
            }
        }
        body.append("}\n");

        target.addConstructor(
                CtNewConstructor.make(body.toString(), target));
    }

    /**
     * Translates a single {@link AdapterDescriptor.Operation} into Java
     * source and compiles it into the target class.
     */
    private static void injectMethod(CtClass target,
            AdapterDescriptor.Operation op)
            throws Exception {

        StringBuilder src = new StringBuilder();

        // signature ----------------------------------------------------------
        src.append(op.getAccess()).append(' ')
                .append(op.getResultType()).append(' ')
                .append(op.getLabel()).append('(');

        StringJoiner paramJoiner = new StringJoiner(", ");
        for (AdapterDescriptor.Argument a : op.getArguments()) {
            paramJoiner.add(a.getDataType() + " " + a.getIdentifier());
        }
        src.append(paramJoiner).append(')');

        // throws clause ------------------------------------------------------
        if (!op.getExceptions().isEmpty()) {
            src.append(" throws ");
            StringJoiner thrownJoiner = new StringJoiner(", ");
            for (String ex : op.getExceptions())
                thrownJoiner.add(ex);
            src.append(thrownJoiner);
        } else {
            src.append(" throws Exception");
        }

        // body ---------------------------------------------------------------
        src.append(" {\n");

        String code = op.getImplementation();
        if (code != null && !code.isEmpty()) {
            src.append(code).append('\n');
        } else {
            // safe defaults so Javassist always has something to return
            switch (op.getResultType()) {
                case "boolean":
                    src.append("return false;\n");
                    break;
                case "int":
                    src.append("return 0;\n");
                    break;
                case "double":
                    src.append("return 0.0;\n");
                    break;
                default:
                    break;
            }
        }
        src.append("}\n");

        // compile & attach ---------------------------------------------------
        try {
            target.addMethod(CtNewMethod.make(src.toString(), target));
        } catch (javassist.CannotCompileException cce) {
            System.err.println("[DynamicConnectorFactory] ERREUR compilation "
                    + op.getLabel() + " : " + cce.getMessage());
            // fallback : stub qui lance une RuntimeException
            String stub = "public "
                    + op.getResultType() + " "
                    + op.getLabel()
                    + "() throws Exception { throw new RuntimeException(\""
                    + op.getLabel() + " : génération échouée\"); }";
            target.addMethod(CtNewMethod.make(stub, target));
        }
    }
}
