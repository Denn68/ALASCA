package fr.sorbonne_u.components.hem2025e1.generated;


import javassist.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.util.*;

public final class HeaterConnectorGenerator {

	 // Cibles DU FRAMEWORK (adaptées à ton projet)
	 private static final String SUPER_CONNECTOR =
	     "fr.sorbonne_u.components.connectors.AbstractConnector";
	 private static final String ADJUSTABLE_CI =
	     "fr.sorbonne_u.components.hem2025.bases.AdjustableCI";
	
	 // Signature map des méthodes AdjustableCI
	 private static final Map<String, MethodSig> ADJ_SIG = new LinkedHashMap<>();
	 static {
	     ADJ_SIG.put("maxMode",             new MethodSig("maxMode",             "int"   ));
	     ADJ_SIG.put("upMode",              new MethodSig("upMode",              "boolean"));
	     ADJ_SIG.put("downMode",            new MethodSig("downMode",            "boolean"));
	     ADJ_SIG.put("setMode",             new MethodSig("setMode",             "boolean", new Param("int","modeIndex")));
	     ADJ_SIG.put("currentMode",         new MethodSig("currentMode",         "int"   ));
	     ADJ_SIG.put("getModeConsumption",  new MethodSig("getModeConsumption",  "double", new Param("int","modeIndex")));
	     ADJ_SIG.put("suspended",           new MethodSig("suspended",           "boolean"));
	     ADJ_SIG.put("suspend",             new MethodSig("suspend",             "boolean"));
	     ADJ_SIG.put("resume",              new MethodSig("resume",              "boolean"));
	     ADJ_SIG.put("emergency",           new MethodSig("emergency",           "double"));
	 }
	
	 // Construit une classe à partir du XML; retourne le Class<?> chargée
	 public Class<?> generateFromXml(ClassLoader loader, InputStream xml, String targetFqcn) throws Exception {
	     // Parse XML
	     DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
	     f.setNamespaceAware(true);
	     DocumentBuilder b = f.newDocumentBuilder();
	     Document doc = b.parse(xml);
	     Element root = doc.getDocumentElement();
	     String offeredFqcn = getAttr(root, "offered"); // ex: HeaterExternalControlJava4CI
	
	     // Javassist setup
	     ClassPool pool = ClassPool.getDefault();
	     pool.appendClassPath(new LoaderClassPath(loader));
	
	     // Si la classe existe déjà, la retourner
	     try {
	         return Class.forName(targetFqcn, false, loader);
	     } catch (ClassNotFoundException ignore) {}
	
	     CtClass superCls = pool.get(SUPER_CONNECTOR);
	     CtClass iface     = pool.get(ADJUSTABLE_CI);
	
	     CtClass cc = pool.makeClass(targetFqcn);
	     cc.setSuperclass(superCls);
	     cc.addInterface(iface);
	
	     // === 1) instance-var
	     NodeList vars = root.getElementsByTagName("instance-var");
	     for (int i = 0; i < vars.getLength(); i++) {
	         Element v = (Element) vars.item(i);
	         String modifiers = getAttr(v, "modifiers"); // e.g., "protected static" | "protected"
	         String type      = getAttr(v, "type");
	         String name      = getAttr(v, "name");
	         String init      = getAttr(v, "static-init"); // valeur initiale
	         String code = modifiers + " " + type + " " + name +
	                       (init != null && !init.isEmpty() ? " = " + init : "") + ";";
	         cc.addField(CtField.make(code, cc));
	     }
	
	     // === 2) internal (méthodes utilitaires)
	     NodeList internals = root.getElementsByTagName("internal");
	     for (int i = 0; i < internals.getLength(); i++) {
	         Element in = (Element) internals.item(i);
	         String modifiers = getAttr(in, "modifiers");
	         String retType   = getAttr(in, "type");
	         String name      = getAttr(in, "name");
	
	         List<Param> params = new ArrayList<>();
	         NodeList paramNodes = in.getElementsByTagName("parameter");
	         for (int p = 0; p < paramNodes.getLength(); p++) {
	             Element pe = (Element) paramNodes.item(p);
	             String pt = getAttr(pe, "type");
	             String pn = getAttr(pe, "name");
	             if (pt != null && pn != null) params.add(new Param(pt, pn));
	         }
	
	         List<String> thrown = new ArrayList<>();
	         NodeList thrownNodes = in.getElementsByTagName("thrown");
	         for (int t = 0; t < thrownNodes.getLength(); t++) {
	             thrown.add(thrownNodes.item(t).getTextContent().trim());
	         }
	
	         // body (un seul <body>)
	         Element bodyEl = firstChildEl(in, "body");
	         String body = bodyWithEquipmentRef(bodyEl, offeredFqcn);
	
	         String sig = buildSignature(modifiers, retType, name, params, thrown);
	         String methodSrc = sig + " { " + body + " }";
	         cc.addMethod(CtNewMethod.make(methodSrc, cc));
	     }
	
	     // === 3) méthodes AdjustableCI (depuis balises dédiées)
	     for (Map.Entry<String, MethodSig> e : ADJ_SIG.entrySet()) {
	         String tag = e.getKey();
	         NodeList nl = root.getElementsByTagName(tag);
	         if (nl.getLength() == 0) continue; // si absente, on l’ignore
	         Element el = (Element) nl.item(0);
	
	         MethodSig sig = e.getValue();
	
	         // Certains tags déclarent <parameter name="..."> : garder la signature connue (types via map)
	         List<Param> params = new ArrayList<>();
	         if (sig.params != null) params.addAll(Arrays.asList(sig.params));
	
	         // Corps
	         Element bodyEl = firstChildEl(el, "body");
	         String body = bodyWithEquipmentRef(bodyEl, offeredFqcn);
	
	         String msrc = buildSignature("public", sig.returnType, sig.name, params, Collections.singletonList("java.lang.Exception"))
	                     + " { " + body + " }";
	         cc.addMethod(CtNewMethod.make(msrc, cc));
	     }
	
	     // === 4) ctor & bind
	     // public ctor (super();) — si besoin d’inits supplémentaires, ajoute-les ici
	     cc.addConstructor(CtNewConstructor.make(
	         "public " + cc.getSimpleName() + "() { super(); }", cc));
	
	     // public void _bind(OfferedFQCN offering) { this.offering = offering; }
	     cc.addMethod(CtNewMethod.make(
	         "public void _bind(" + offeredFqcn + " offering) { this.offering = offering; }", cc));
	
	     // Générer/charger
	     return cc.toClass(loader, null);
	 }
	
	 // Remplace les appels "heater.xxx(...)" par "((Offered)this.offering).xxx(...)"
	 private static String bodyWithEquipmentRef(Element bodyEl, String offeredFqcn) {
	     if (bodyEl == null) return "";
	     String ref = getAttr(bodyEl, "equipmentRef"); // ex: "heater"
	     String raw = bodyEl.getTextContent(); // déjà décodé (&lt; &gt; etc.)
	     if (ref == null || ref.isEmpty()) return raw.trim();
	     // Remplacement safe: ref + "." → ((Offered)this.offering).
	     return raw.replace(ref + ".", "((" + offeredFqcn + ")this.offering).").trim();
	 }
	
	 private static String buildSignature(String modifiers, String retType, String name,
	                                      List<Param> params, List<String> thrown) {
	     StringBuilder sb = new StringBuilder();
	     sb.append(modifiers).append(" ").append(retType).append(" ").append(name).append("(");
	     for (int i = 0; i < params.size(); i++) {
	         if (i > 0) sb.append(", ");
	         sb.append(params.get(i).type).append(" ").append(params.get(i).name);
	     }
	     sb.append(")");
	     if (thrown != null && !thrown.isEmpty()) {
	         sb.append(" throws ");
	         for (int i = 0; i < thrown.size(); i++) {
	             if (i > 0) sb.append(", ");
	             sb.append(thrown.get(i));
	         }
	     }
	     return sb.toString();
	 }
	
	 private static Element firstChildEl(Element parent, String tag) {
	     NodeList nl = parent.getElementsByTagName(tag);
	     return nl.getLength() > 0 ? (Element) nl.item(0) : null;
	 }
	
	 private static String getAttr(Element e, String name) {
	     return e.hasAttribute(name) ? e.getAttribute(name).trim() : null;
	 }
	
	 // --- helpers ---
	 private static final class Param {
	     final String type, name;
	     Param(String t, String n) { this.type = t; this.name = n; }
	 }
	 private static final class MethodSig {
	     final String name, returnType;
	     final Param[] params;
	     MethodSig(String n, String r, Param... p) { name = n; returnType = r; params = p; }
	 }
	
	 // =======================
	 // ==== DEMO / USAGE  ====
	 // =======================
	 public static void main(String[] args) throws Exception {
	     // 1) charge ton XML (ex: depuis le classpath ou un fichier)
	     InputStream xml = HeaterConnectorGenerator.class.getResourceAsStream(
	         "/heater-control-adapter.xml" // mets ici ton fichier
	     );
	     if (xml == null) throw new IllegalStateException("XML introuvable sur le classpath");
	
	     // 2) génère la classe à la volée
	     String targetFqcn = "fr.su.generated.HeaterConnectorJA"; // nom que tu veux
	     HeaterConnectorGenerator gen = new HeaterConnectorGenerator();
	     Class<?> clazz = gen.generateFromXml(Thread.currentThread().getContextClassLoader(), xml, targetFqcn);
	
	     // 3) instancie et bind l’offering
	     // HeaterExternalControlJava4CI heaterOffering = ... (ton instance réelle)
	     // Object instance = clazz.getConstructor().newInstance();
	     // clazz.getMethod("_bind",
	     //   Class.forName("fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterExternalControlJava4CI")
	     // ).invoke(instance, heaterOffering);
	
	     // 4) utilise via l’interface
	     // fr.sorbonne_u.components.hem2025.bases.AdjustableCI ci =
	     //     (fr.sorbonne_u.components.hem2025.bases.AdjustableCI) instance;
	     // System.out.println("maxMode=" + ci.maxMode());
	 }
}

