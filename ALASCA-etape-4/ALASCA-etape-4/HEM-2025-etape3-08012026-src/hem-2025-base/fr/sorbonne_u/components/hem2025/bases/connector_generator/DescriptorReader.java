package fr.sorbonne_u.components.hem2025.bases.connector_generator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

// -------------------------------------------------------------------------
/**
 * Reads a control-adapter XML descriptor using a <b>SAX</b> event-driven
 * parser and produces an {@link AdapterDescriptor}.
 *
 * <p>
 * Unlike a DOM parser that loads the entire document tree in memory, SAX
 * processes nodes one-by-one as they are encountered, which makes it
 * lighter and structurally different from the DOM approach used in
 * {@code ConnectorAdapterParserXML}.
 * </p>
 *
 * <p>
 * Created on : 2026-01-30
 * </p>
 */
public class DescriptorReader {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** XML namespace of control-adapter descriptors. */
    private static final String ADAPTER_NS = "http://www.sorbonne-universite.fr/alasca/control-adapter";

    /** AdjustableCI operations whose return type can be inferred. */
    private static final Set<String> BOOL_OPS = new HashSet<>(
            Arrays.asList("upMode", "downMode", "setMode",
                    "suspended", "suspend", "resume"));
    private static final Set<String> INT_OPS = new HashSet<>(
            Arrays.asList("maxMode", "currentMode"));
    private static final Set<String> DOUBLE_OPS = new HashSet<>(
            Arrays.asList("getModeConsumption", "emergency"));

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Parse the file at {@code path} and return a filled descriptor.
     *
     * @param path absolute or relative path to the XML file.
     * @return a fully populated {@link AdapterDescriptor}.
     * @throws Exception if the file cannot be read or is malformed.
     */
    public static AdapterDescriptor readFrom(String path) throws Exception {

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser parser = spf.newSAXParser();

        AdapterHandler handler = new AdapterHandler();
        parser.parse(new File(path), handler);

        return handler.toDescriptor();
    }

    // =====================================================================
    // SAX handler — private inner class
    // =====================================================================

    private static class AdapterHandler extends DefaultHandler {

        // builder
        private final AdapterDescriptor.Builder bld = new AdapterDescriptor.Builder();

        // tracking where we are in the XML tree
        private String offeredIface;
        private StringBuilder chars = new StringBuilder();

        // current operation being built (null when outside an op element)
        private String curOpName;
        private String curOpAccess;
        private String curOpResult;
        private List<AdapterDescriptor.Argument> curArgs;
        private List<String> curThrown;
        private String curEquipRef;
        private boolean insideBody;

        // ----------------------------------------------------------------
        // SAX callbacks
        // ----------------------------------------------------------------

        @Override
        public void startElement(String uri, String local,
                String qName, Attributes att) {

            chars.setLength(0); // reset character buffer

            if (!"".equals(uri) && !ADAPTER_NS.equals(uri))
                return;

            switch (local) {

                case "control-adapter":
                    bld.equipmentId(att.getValue("uid"));
                    offeredIface = att.getValue("offered");
                    bld.offeredInterface(offeredIface);
                    break;

                case "instance-var":
                    bld.addField(new AdapterDescriptor.Field(
                            safe(att.getValue("modifiers")),
                            safe(att.getValue("type")),
                            safe(att.getValue("name")),
                            safe(att.getValue("static-init"))));
                    break;

                case "internal":
                    beginOperation(
                            safe(att.getValue("name")),
                            safe(att.getValue("modifiers")),
                            safe(att.getValue("type")));
                    break;

                case "maxMode":
                case "upMode":
                case "downMode":
                case "setMode":
                case "currentMode":
                case "getModeConsumption":
                case "suspended":
                case "suspend":
                case "resume":
                case "emergency":
                    beginOperation(local, "public", guessType(local));
                    break;

                case "parameter":
                    if (curArgs != null) {
                        String ptype = att.getValue("type");
                        curArgs.add(new AdapterDescriptor.Argument(
                                ptype == null || ptype.isEmpty() ? "int" : ptype.trim(),
                                safe(att.getValue("name"))));
                    }
                    break;

                case "thrown":
                    // text content will be captured in characters()
                    break;

                case "body":
                    insideBody = true;
                    curEquipRef = att.getValue("equipmentRef");
                    break;

                default:
                    break;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            chars.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String local, String qName) {

            if (!"".equals(uri) && !ADAPTER_NS.equals(uri))
                return;

            switch (local) {

                case "required":
                    bld.addDependency(chars.toString().trim());
                    break;

                case "thrown":
                    if (curThrown != null)
                        curThrown.add(chars.toString().trim());
                    break;

                case "body":
                    insideBody = false;
                    break;

                case "internal":
                case "maxMode":
                case "upMode":
                case "downMode":
                case "setMode":
                case "currentMode":
                case "getModeConsumption":
                case "suspended":
                case "suspend":
                case "resume":
                case "emergency":
                    finishOperation();
                    break;

                default:
                    break;
            }
        }

        // ----------------------------------------------------------------
        // Helpers
        // ----------------------------------------------------------------

        private void beginOperation(String name, String access,
                String resultType) {
            curOpName = name;
            curOpAccess = access;
            curOpResult = resultType;
            curArgs = new ArrayList<>();
            curThrown = new ArrayList<>();
            curEquipRef = null;
            insideBody = false;
        }

        private void finishOperation() {
            if (curOpName == null)
                return;

            String code = chars.toString().trim();

            // replace equipment alias with Javassist-compatible cast
            if (curEquipRef != null && !curEquipRef.isEmpty()
                    && offeredIface != null) {
                String cast = "(((" + offeredIface
                        + ") this.offering))";
                code = code.replaceAll(
                        "\\b" + curEquipRef + "\\b", cast);
            }

            bld.addOperation(new AdapterDescriptor.Operation(
                    curOpName, curOpAccess, curOpResult,
                    curArgs, curThrown, code, curEquipRef));

            curOpName = null;
        }

        private AdapterDescriptor toDescriptor() {
            return bld.build();
        }

        private static String guessType(String opName) {
            if (BOOL_OPS.contains(opName))
                return "boolean";
            if (INT_OPS.contains(opName))
                return "int";
            if (DOUBLE_OPS.contains(opName))
                return "double";
            return "void";
        }

        private static String safe(String s) {
            return s == null ? "" : s.trim();
        }
    }
}
