package fr.sorbonne_u.generator;

import fr.sorbonne_u.generator.ControlAdapterModel.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ControlAdapterParser {

    // Namespace du XML fourni
    private static final String NS = "http://www.sorbonne-universite.fr/alasca/control-adapter";

    public static ControlAdapterModel parse(File xmlFile) throws Exception {
        if (xmlFile == null || !xmlFile.exists()) {
            throw new IllegalArgumentException("XML introuvable : " + xmlFile);
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringComments(true);
        dbf.setNamespaceAware(true);
        dbf.setIgnoringElementContentWhitespace(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(xmlFile);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        if (!"control-adapter".equals(local(root))) {
            throw new IllegalArgumentException("Racine inattendue: " + local(root));
        }

        ControlAdapterModel model = new ControlAdapterModel();

        // Attributs du root
        String uid = requireAttr(root, "uid");
        String offered = requireAttr(root, "offered");
        model.setUid(uid);
        model.setOfferedInterface(offered);

        // <consumption .../>
        Element consumptionEl = firstChild(root, "consumption");
        if (consumptionEl != null) {
            Double min = attrDoubleOrNull(consumptionEl, "min");
            Double nom = attrDoubleOrNull(consumptionEl, "nominal");
            Double max = attrDoubleOrNull(consumptionEl, "max");
            if (nom == null) throw new IllegalArgumentException("<consumption> nominal manquant");
            model.setConsumption(new Consumption(min, nom, max));
        }

        // <required>...</required>*
        for (Element reqEl : children(root, "required")) {
            String fqcn = text(reqEl);
            if (fqcn != null && !fqcn.isEmpty()) {
                model.getRequiredClasses().add(fqcn);
            }
        }

        // <instance-var ... />*
        for (Element ivEl : children(root, "instance-var")) {
            String modifiers = requireAttr(ivEl, "modifiers");
            String type      = requireAttr(ivEl, "type");
            String name      = requireAttr(ivEl, "name");
            String staticInit= ivEl.hasAttribute("static-init") ? ivEl.getAttribute("static-init") : null;
            model.getInstanceVars().add(new InstanceVar(modifiers, type, name, staticInit));
        }

        // <internal ...> ... </internal>*
        for (Element inEl : children(root, "internal")) {
            String modifiers = requireAttr(inEl, "modifiers");
            String rtype     = requireAttr(inEl, "type");
            String name      = requireAttr(inEl, "name");

            List<Parameter> params = new ArrayList<>();
            for (Element pEl : children(inEl, "parameter")) {
                String ptype = pEl.hasAttribute("type") ? pEl.getAttribute("type") : null;
                String pname = requireAttr(pEl, "name");
                params.add(new Parameter(ptype, pname));
            }

            List<String> thrown = new ArrayList<>();
            for (Element tEl : children(inEl, "thrown")) {
                String fqn = text(tEl);
                if (fqn != null && !fqn.isEmpty()) thrown.add(fqn);
            }

            Body body = parseBody(inEl);   // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< présent ici
            model.getInternalMethods().add(new InternalMethod(modifiers, rtype, name, params, thrown, body));
        }

        // Opérations connues
        List<String> ops = Arrays.asList(
                "maxMode","upMode","downMode","setMode","currentMode",
                "getModeConsumption","suspended","suspend","resume","emergency"
        );
        for (String op : ops) {
            Element opEl = firstChild(root, op);
            if (opEl == null) continue;

            Parameter param = null;
            Element pEl = firstChild(opEl, "parameter");
            if (pEl != null) {
                String ptype = pEl.hasAttribute("type") ? pEl.getAttribute("type") : null;
                String pname = pEl.hasAttribute("name") ? pEl.getAttribute("name") : "modeIndex";
                param = new Parameter(ptype, pname);
            }
            Body body = parseBody(opEl);   // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< présent ici
            model.getOperations().put(op, new Operation(op, param, body));
        }

        return model;
    }

    /* ======================== Helpers DOM ======================== */

    private static Body parseBody(Element parent) {
        // Récupère le premier <body> (namespace-aware)
        NodeList nl = parent.getElementsByTagNameNS(NS, "body");
        if (nl.getLength() == 0) {
            // tolérance : si l’éditeur a enlevé le NS, on tente sans NS
            nl = parent.getElementsByTagName("body");
        }
        if (nl.getLength() == 0) {
            throw new IllegalArgumentException("<body> manquant dans <" + local(parent) + ">");
        }
        Element b = (Element) nl.item(0);
        String eq = b.hasAttribute("equipmentRef") ? b.getAttribute("equipmentRef") : null;
        String code = text(b);
        return new Body((eq != null && eq.trim().isEmpty()) ? null : eq, code == null ? "" : code);
    }

    private static String requireAttr(Element e, String a) {
        String v = e.getAttribute(a);
        if (v == null || v.isEmpty()) {
            throw new IllegalArgumentException("Attribut manquant: " + a + " dans <" + local(e) + ">");
        }
        return v;
    }

    private static Double attrDoubleOrNull(Element e, String a) {
        if (!e.hasAttribute(a)) return null;
        String v = e.getAttribute(a);
        if (v == null || v.trim().isEmpty()) return null;
        return Double.valueOf(v.trim());
    }

    private static String text(Element e) {
        String t = e.getTextContent();
        return t == null ? null : t.trim();
    }

    private static String local(Element e) {
        return e.getLocalName() != null ? e.getLocalName() : e.getNodeName();
    }

    private static Element firstChild(Element parent, String localName) {
        NodeList nl = parent.getElementsByTagNameNS(NS, localName);
        if (nl.getLength() == 0) return null;
        return (Element) nl.item(0);
    }

    private static List<Element> children(Element parent, String localName) {
        List<Element> list = new ArrayList<>();
        NodeList nl = parent.getElementsByTagNameNS(NS, localName);
        for (int i = 0; i < nl.getLength(); i++) {
            list.add((Element) nl.item(i));
        }
        return list;
    }
}
