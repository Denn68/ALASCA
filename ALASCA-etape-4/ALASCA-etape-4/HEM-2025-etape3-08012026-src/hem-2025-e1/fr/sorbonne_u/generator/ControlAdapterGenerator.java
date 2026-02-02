package fr.sorbonne_u.generator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.thaiopensource.relaxng.jaxp.CompactSyntaxSchemaFactory;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;

// -----------------------------------------------------------------------------
/**
 * The class <code>ControlAdapterGenerator</code> implements a generic connector
 * generator based on an XML descriptor "control-adapter".
 *
 * <p><strong>Description</strong></p>
 * * <p>
 * This tool uses Javassist and RELAX NG Compact (JING) validation to dynamically
 * create connector classes bridging the HEM logic and specific equipments.
 * </p>
 * <p>
 * Key features:
 * <ul>
 * <li>Method names are preserved.</li>
 * <li>Calls to {@code <equipmentRef>.m(...)} in bodies are rewritten to
 * {@code ((<offered-interface>)this.offering).m(...)}.</li>
 * <li>Strict Linting: every method call in a body must exist in the offered interface.</li>
 * <li>Supports {@code AdjustableCI} if available on the classpath.</li>
 * </ul>
 * </p>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code true}
 * </pre>
 *
 * * @author	Team DeMoh
 */
public class			ControlAdapterGenerator
{
	// -------------------------------------------------------------------------
	// Inner Classes
	// -------------------------------------------------------------------------

	protected static class Param { 
		CtClass type; 
		String name; 
		Param(CtClass t, String n) { type = t; name = n; } 
	}

	protected static class BodyIssue { 
		String method; 
		String body; 
		BodyIssue(String m, String b) { method = m; body = b; } 
	}

	// -------------------------------------------------------------------------
	// Main / API Methods
	// -------------------------------------------------------------------------

	/**
	 * main entry point for CLI usage.
	 * * <p><strong>Contract</strong></p>
	 * * <pre>
	 * pre	{@code args != null}
	 * post	{@code true}
	 * </pre>
	 *
	 * @param args			command line arguments (xml, rnc, super, etc.).
	 * @throws Exception	if generation fails.
	 */
	public static void	main(String[] args) throws Exception
	{
		Map<String, String> a = parseArgs(args);

		String xmlPath         = require(a, "xml");
		String rncPath         = require(a, "rnc");
		String superClass      = require(a, "super");
		String connectorClass  = require(a, "connector-class");
		String outDir          = a.containsKey("out") ? a.get("out") : "./generated";

		System.out.println("[RNC] Validation en cours...");
		validateWithRnc(xmlPath, rncPath);
		System.out.println("[RNC] OK : " + xmlPath + " | " + rncPath);

		Document doc = readXml(new File(xmlPath));
		Element root = doc.getDocumentElement();

		String offeredInterface = root.getAttribute("offered");
		if (offeredInterface == null || offeredInterface.trim().isEmpty()) {
			throw new IllegalArgumentException("Attribute 'offered' missing in XML.");
		}
		offeredInterface = offeredInterface.trim();

		Set<String> equipmentRefs = new LinkedHashSet<>();
		collectEquipmentRefsRec(root, equipmentRefs);
		if (equipmentRefs.isEmpty()) equipmentRefs.add("equipment");

		ClassPool pool = ClassPool.getDefault();
		pool.insertClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));

		CtClass superCt = pool.get(superClass);
		CtClass ciCt    = pool.get(offeredInterface);

		CtClass adjustableCI = null;
		try {
			adjustableCI = pool.get("fr.sorbonne_u.components.hem2025.bases.AdjustableCI");
		} catch (NotFoundException ignore) {
			// AdjustableCI not present, ignore.
		}

		lintBodiesAgainstOffered(root, ciCt, equipmentRefs);

		CtClass connectorCt = pool.makeClass(connectorClass);
		connectorCt.setSuperclass(superCt);
		connectorCt.addInterface(ciCt);
		if (adjustableCI != null) {
			connectorCt.addInterface(adjustableCI);
		}

		// Instance variables
		for (Element iv : children(root, "instance-var")) {
			String modifiers = iv.getAttribute("modifiers");
			String type      = iv.getAttribute("type");
			String name      = iv.getAttribute("name");
			String init      = iv.getAttribute("static-init");
			
			if (type == null || name == null || type.isEmpty() || name.isEmpty()) {
				throw new IllegalArgumentException("Invalid instance-var (type/name required).");
			}
			int mods = toModifiers(modifiers);
			CtClass t = primitiveOrClass(pool, type);
			CtField fld = new CtField(t, name, connectorCt);
			fld.setModifiers(mods);
			if (init != null && !init.isEmpty()) {
				connectorCt.addField(fld, CtField.Initializer.byExpr(init));
			} else {
				connectorCt.addField(fld);
			}
		}

		// Internal methods
		for (Element in : children(root, "internal")) {
			String modifiers = in.getAttribute("modifiers");
			String retType   = in.getAttribute("type");
			String name      = in.getAttribute("name");
			
			if (retType == null || name == null || retType.isEmpty() || name.isEmpty()) {
				throw new IllegalArgumentException("<internal> must have 'type' and 'name'.");
			}

			List<Param> params = parseParams(pool, in);
			List<String> throwsList = parseThrows(in);
			Element bodyElt = firstChild(in, "body");
			String src = text(bodyElt);

			src = rewriteBodyToOffering(src, offeredInterface, equipmentRefs);

			StringBuilder decl = new StringBuilder();
			decl.append((modifiers == null || modifiers.isEmpty()) ? "public" : modifiers)
				.append(" ").append(retType).append(" ").append(name).append("(");
			for (int i = 0; i < params.size(); i++) {
				if (i > 0) decl.append(", ");
				decl.append(params.get(i).type.getName()).append(" ").append(params.get(i).name);
			}
			decl.append(")");
			if (!throwsList.isEmpty()) {
				decl.append(" throws ");
				for (int i = 0; i < throwsList.size(); i++) {
					if (i > 0) decl.append(", ");
					decl.append(throwsList.get(i));
				}
			}
			decl.append("{\n").append(src).append("\n}");

			CtMethod m = CtNewMethod.make(decl.toString(), connectorCt);
			connectorCt.addMethod(m);
		}

		Map<String, Element> xmlByName = new HashMap<>();
		NodeList kids = root.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++) {
			Node n = kids.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				String t = tag(e);
				if (!"internal".equals(t) && !"instance-var".equals(t)) {
					if (firstChild(e, "body") != null) xmlByName.put(t, e);
				}
			}
		}

		for (CtMethod cim : ciCt.getDeclaredMethods()) {
			addMethodFromInterfaceSignature(connectorCt, cim, xmlByName, offeredInterface, equipmentRefs);
		}

		if (adjustableCI != null) {
			for (CtMethod aim : adjustableCI.getDeclaredMethods()) {
				if (!hasSameSignature(connectorCt, aim)) {
					addMethodFromInterfaceSignature(connectorCt, aim, xmlByName, offeredInterface, equipmentRefs);
				}
			}
		}

		Files.createDirectories(Paths.get(outDir));
		connectorCt.writeFile(outDir);
		System.out.println("[OK] Generated " + connectorClass + " -> " + outDir);
	}

	/**
	 * generate and load the connector class dynamically.
	 * * <p><strong>Contract</strong></p>
	 * * <pre>
	 * pre	{@code xmlPath != null && rncPath != null}
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @param xmlPath			path to the XML descriptor.
	 * @param rncPath			path to the RELAX NG Compact schema.
	 * @param superClass		fully qualified name of the super class.
	 * @param connectorFqcn		fully qualified name of the target connector class.
	 * @param loader			class loader to use.
	 * @return					the generated {@code Class<?>}.
	 * @throws Exception		if generation or loading fails.
	 */
	public static Class<?>	generateAndLoad(
		String xmlPath,
		String rncPath,
		String superClass,
		String connectorFqcn,
		ClassLoader loader
		) throws Exception
	{
		validateWithRnc(xmlPath, rncPath);

		Document doc = readXml(new File(xmlPath));
		Element root = doc.getDocumentElement();
		String offeredInterface = root.getAttribute("offered");
		if (offeredInterface == null || offeredInterface.trim().isEmpty()) {
			throw new IllegalArgumentException("Attribute 'offered' missing in XML.");
		}
		offeredInterface = offeredInterface.trim();

		Set<String> equipmentRefs = new LinkedHashSet<>();
		collectEquipmentRefsRec(root, equipmentRefs);
		if (equipmentRefs.isEmpty()) equipmentRefs.add("equipment");

		ClassPool pool = ClassPool.getDefault();
		pool.insertClassPath(new LoaderClassPath(loader));

		CtClass superCt = pool.get(superClass);
		CtClass ciCt    = pool.get(offeredInterface);

		CtClass adjustableCI = null;
		try {
			adjustableCI = pool.get("fr.sorbonne_u.components.hem2025.bases.AdjustableCI");
		} catch (NotFoundException ignore) {}

		lintBodiesAgainstOffered(root, ciCt, equipmentRefs);

		CtClass connectorCt = pool.makeClass(connectorFqcn);
		connectorCt.setSuperclass(superCt);
		connectorCt.addInterface(ciCt);
		if (adjustableCI != null) {
			connectorCt.addInterface(adjustableCI);
		}

		// Instance variables
		for (Element iv : children(root, "instance-var")) {
			String modifiers = iv.getAttribute("modifiers");
			String type      = iv.getAttribute("type");
			String name      = iv.getAttribute("name");
			String init      = iv.getAttribute("static-init");
			
			if (type == null || name == null || type.isEmpty() || name.isEmpty()) {
				throw new IllegalArgumentException("Invalid instance-var.");
			}
			int mods = toModifiers(modifiers);
			CtClass t = primitiveOrClass(pool, type);
			CtField fld = new CtField(t, name, connectorCt);
			fld.setModifiers(mods);
			if (init != null && !init.isEmpty()) {
				connectorCt.addField(fld, CtField.Initializer.byExpr(init));
			} else {
				connectorCt.addField(fld);
			}
		}

		// Internal methods
		for (Element in : children(root, "internal")) {
			String modifiers = in.getAttribute("modifiers");
			String retType   = in.getAttribute("type");
			String name      = in.getAttribute("name");
			if (retType == null || name == null || retType.isEmpty() || name.isEmpty()) {
				throw new IllegalArgumentException("<internal> must have 'type' and 'name'.");
			}
			List<Param> params = parseParams(pool, in);
			List<String> throwsList = parseThrows(in);
			Element bodyElt = firstChild(in, "body");
			String src = text(bodyElt);
			src = rewriteBodyToOffering(src, offeredInterface, equipmentRefs);

			StringBuilder decl = new StringBuilder();
			decl.append((modifiers == null || modifiers.isEmpty()) ? "public" : modifiers)
				.append(" ").append(retType).append(" ").append(name).append("(");
			for (int i = 0; i < params.size(); i++) {
				if (i > 0) decl.append(", ");
				decl.append(params.get(i).type.getName()).append(" ").append(params.get(i).name);
			}
			decl.append(")");
			if (!throwsList.isEmpty()) {
				decl.append(" throws ");
				for (int i = 0; i < throwsList.size(); i++) {
					if (i > 0) decl.append(", ");
					decl.append(throwsList.get(i));
				}
			}
			decl.append("{\n").append(src).append("\n}");
			CtMethod m = CtNewMethod.make(decl.toString(), connectorCt);
			connectorCt.addMethod(m);
		}

		Map<String, Element> xmlByName = new HashMap<>();
		NodeList kids = root.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++) {
			Node n = kids.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				String t = tag(e);
				if (!"internal".equals(t) && !"instance-var".equals(t)) {
					if (firstChild(e, "body") != null) xmlByName.put(t, e);
				}
			}
		}

		for (CtMethod cim : ciCt.getDeclaredMethods()) {
			addMethodFromInterfaceSignature(connectorCt, cim, xmlByName, offeredInterface, equipmentRefs);
		}

		if (adjustableCI != null) {
			for (CtMethod aim : adjustableCI.getDeclaredMethods()) {
				if (!hasSameSignature(connectorCt, aim)) {
					addMethodFromInterfaceSignature(connectorCt, aim, xmlByName, offeredInterface, equipmentRefs);
				}
			}
		}

		return connectorCt.toClass(loader, null);
	}

	// -------------------------------------------------------------------------
	// Internal Methods (Parsing, Validation, Code Gen)
	// -------------------------------------------------------------------------

	private static Map<String, String> parseArgs(String[] args)
	{
		Map<String, String> m = new LinkedHashMap<>();
		for (String a : args) {
			if (a != null && a.startsWith("--") && a.contains("=")) {
				int i = a.indexOf('=');
				m.put(a.substring(2, i).trim(), a.substring(i + 1).trim());
			}
		}
		return m;
	}

	private static Document		readXml(File f) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(f);
		doc.getDocumentElement().normalize();
		return doc;
	}

	private static String		text(Node n)
	{
		return n == null ? "" : n.getTextContent();
	}

	private static String		tag(Element e)
	{
		String ln = e.getLocalName();
		if (ln != null && !ln.isEmpty()) return ln;
		String tn = e.getTagName();
		int k = tn.indexOf(':');
		return (k >= 0) ? tn.substring(k + 1) : tn;
	}

	private static Element		firstChild(Element root, String localOrName)
	{
		NodeList nl = root.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				if (localOrName.equals(tag(e))) return e;
			}
		}
		return null;
	}

	private static List<Element> children(Element root, String localOrName)
	{
		List<Element> out = new ArrayList<>();
		NodeList nl = root.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				if (localOrName.equals(tag(e))) out.add(e);
			}
		}
		return out;
	}

	private static void			validateWithRnc(String xmlPath, String rncPath)
	throws Exception
	{
		File x = new File(xmlPath);
		File r = new File(rncPath);
		if (!x.isFile()) throw new IllegalArgumentException("XML not found: " + x.getAbsolutePath());
		if (!r.isFile()) throw new IllegalArgumentException("RNC not found: " + r.getAbsolutePath());

		Source schemaSrc = new StreamSource(r);
		Source xmlSrc = new StreamSource(x);

		SchemaFactory sf = new CompactSyntaxSchemaFactory();
		Schema schema = sf.newSchema(schemaSrc);
		javax.xml.validation.Validator v = schema.newValidator();
		v.validate(xmlSrc);
	}

	private static String		require(Map<String, String> a, String key)
	{
		String v = a.get(key);
		if (v == null || v.trim().isEmpty()) {
			throw new IllegalArgumentException("Missing argument: --" + key + "=...");
		}
		return v.trim();
	}

	private static void			collectEquipmentRefsRec(Element elt, Set<String> out)
	{
		if (elt.hasAttribute("equipmentRef")) {
			String ref = elt.getAttribute("equipmentRef").trim();
			if (!ref.isEmpty()) out.add(ref);
		}
		NodeList nl = elt.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) collectEquipmentRefsRec((Element) n, out);
		}
	}

	private static int			toModifiers(String s)
	{
		int mods = 0;
		if (s == null || s.trim().isEmpty()) return Modifier.PUBLIC;
		for (String w : s.trim().split("\\s+")) {
			if ("public".equals(w))    mods |= Modifier.PUBLIC;
			else if ("protected".equals(w)) mods |= Modifier.PROTECTED;
			else if ("private".equals(w))   mods |= Modifier.PRIVATE;
			else if ("static".equals(w))    mods |= Modifier.STATIC;
			else if ("final".equals(w))     mods |= Modifier.FINAL;
			else if ("abstract".equals(w))  mods |= Modifier.ABSTRACT;
		}
		if ((mods & (Modifier.PUBLIC|Modifier.PROTECTED|Modifier.PRIVATE)) == 0) mods |= Modifier.PUBLIC;
		return mods;
	}

	private static CtClass		primitiveOrClass(ClassPool pool, String type)
	throws NotFoundException
	{
		if ("void".equals(type))    return CtClass.voidType;
		if ("boolean".equals(type)) return CtClass.booleanType;
		if ("byte".equals(type))    return CtClass.byteType;
		if ("char".equals(type))    return CtClass.charType;
		if ("short".equals(type))   return CtClass.shortType;
		if ("int".equals(type))     return CtClass.intType;
		if ("long".equals(type))    return CtClass.longType;
		if ("float".equals(type))   return CtClass.floatType;
		if ("double".equals(type))  return CtClass.doubleType;
		return pool.get(type);
	}

	private static List<Param>	parseParams(ClassPool pool, Element methodElt)
	throws NotFoundException
	{
		List<Param> out = new ArrayList<>();
		if (methodElt == null) return out;
		NodeList ps = methodElt.getElementsByTagName("*");
		for (int i = 0; i < ps.getLength(); i++) {
			Node n = ps.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE) continue;
			Element p = (Element) n;
			if (!"parameter".equals(tag(p))) continue;
			String t = p.getAttribute("type");
			String nme = p.getAttribute("name");
			CtClass ct = primitiveOrClass(pool, t);
			out.add(new Param(ct, (nme == null || nme.isEmpty()) ? ("p" + out.size()) : nme));
		}
		return out;
	}

	private static List<String>	parseThrows(Element methodElt)
	{
		List<String> out = new ArrayList<>();
		if (methodElt == null) return out;
		NodeList ts = methodElt.getElementsByTagName("*");
		for (int i = 0; i < ts.getLength(); i++) {
			Node n = ts.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE) continue;
			Element e = (Element) n;
			if (!"thrown".equals(tag(e))) continue;
			String qn = e.getTextContent();
			if (qn != null && !qn.trim().isEmpty()) out.add(qn.trim());
		}
		return out;
	}

	private static List<String>	paramNamesFromXml(Element methodElt, int arity)
	{
		List<String> names = new ArrayList<>();
		NodeList ps = methodElt.getElementsByTagName("*");
		for (int i = 0; i < ps.getLength(); i++) {
			Node n = ps.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE) continue;
			Element p = (Element) n;
			if (!"parameter".equals(tag(p))) continue;
			String nn = p.getAttribute("name");
			names.add((nn == null || nn.isEmpty()) ? ("p" + names.size()) : nn);
		}
		while (names.size() < arity) names.add("p" + names.size());
		return names;
	}

	private static void			lintBodiesAgainstOffered(
		Element root,
		CtClass offeredCt,
		Set<String> equipmentRefs
		) throws Exception
	{
		if (equipmentRefs.isEmpty()) return;

		Set<String> offeredMethods = new HashSet<String>();
		for (CtMethod m : offeredCt.getMethods()) offeredMethods.add(m.getName());

		Pattern call = Pattern.compile("\\b(" + String.join("|", mapQuote(equipmentRefs)) + ")\\s*\\.\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*\\(");

		List<BodyIssue> issues = new ArrayList<BodyIssue>();
		Deque<Element> stack = new ArrayDeque<Element>();
		stack.push(root);
		while (!stack.isEmpty()) {
			Element e = stack.pop();
			NodeList ch = e.getChildNodes();
			for (int i = 0; i < ch.getLength(); i++) {
				Node n = ch.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) stack.push((Element) n);
			}
			if ("body".equals(tag(e))) {
				String src = text(e);
				Matcher m = call.matcher(src);
				while (m.find()) {
					String method = m.group(2);
					if (!offeredMethods.contains(method)) {
						issues.add(new BodyIssue(method, snippet(src)));
					}
				}
			}
		}

		if (!issues.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("\n[GENERATOR] Unknown calls in body vs offered interface:\n");
			for (BodyIssue bi : issues) {
				sb.append("  - method missing: ").append(bi.method)
				  .append("\n    Snippet: ").append(bi.body).append("\n");
			}
			throw new IllegalStateException(sb.toString());
		}
	}

	private static String		rewriteBodyToOffering(
		String src,
		String offeredFqcn,
		Set<String> equipmentRefs
		)
	{
		if (src == null || src.isEmpty() || equipmentRefs.isEmpty()) return src;
		String rewritten = src;
		for (String ref : equipmentRefs) {
			Pattern p = Pattern.compile("(\\b" + Pattern.quote(ref) + "\\s*\\.\\s*)([A-Za-z_][A-Za-z0-9_]*)\\s*\\(");
			Matcher m = p.matcher(rewritten);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				String prefix = "(( " + offeredFqcn + " )this.offering).";
				m.appendReplacement(sb, Matcher.quoteReplacement(prefix + m.group(2) + "("));
			}
			m.appendTail(sb);
			rewritten = sb.toString();
		}
		return rewritten;
	}

	private static List<String>	mapQuote(Set<String> refs)
	{
		List<String> out = new ArrayList<String>();
		for (String r : refs) out.add(Pattern.quote(r));
		return out;
	}

	private static String		snippet(String s)
	{
		if (s == null) return "";
		s = s.replaceAll("\\s+", " ");
		if (s.length() > 140) return s.substring(0, 140) + " ...";
		return s;
	}

	private static void			addMethodFromInterfaceSignature(
		CtClass connectorCt,
		CtMethod ifaceMethod,
		Map<String, Element> xmlByName,
		String offeredFqcn,
		Set<String> equipmentRefs
		) throws Exception
	{
		String mName = ifaceMethod.getName();
		Element def = xmlByName.get(mName);

		CtClass ret = ifaceMethod.getReturnType();
		CtClass[] ptypes = ifaceMethod.getParameterTypes();
		CtClass[] etypes = ifaceMethod.getExceptionTypes();

		List<String> paramNames;
		if (def != null) {
			paramNames = paramNamesFromXml(def, ptypes.length);
		} else {
			paramNames = new ArrayList<>();
			for (int i = 0; i < ptypes.length; i++) paramNames.add("p" + i);
		}

		String src;
		if (def != null && firstChild(def, "body") != null) {
			src = text(firstChild(def, "body"));
			src = rewriteBodyToOffering(src, offeredFqcn, equipmentRefs);
		} else {
			src = "throw new UnsupportedOperationException(\"No XML body for " + mName + "\");";
		}

		StringBuilder decl = new StringBuilder();
		int mm = ifaceMethod.getModifiers();
		mm &= ~(Modifier.ABSTRACT | Modifier.INTERFACE);
		String mmStr = Modifier.toString(mm);
		if (mmStr == null || mmStr.isEmpty()) mmStr = "public";
		decl.append(mmStr).append(" ").append(ret.getName()).append(" ").append(mName).append("(");
		for (int i = 0; i < ptypes.length; i++) {
			if (i > 0) decl.append(", ");
			decl.append(ptypes[i].getName()).append(" ").append(paramNames.get(i));
		}
		decl.append(")");
		if (etypes.length > 0) {
			decl.append(" throws ");
			for (int i = 0; i < etypes.length; i++) {
				if (i > 0) decl.append(", ");
				decl.append(etypes[i].getName());
			}
		}
		decl.append("{\n").append(src).append("\n}");

		CtMethod mmeth = CtNewMethod.make(decl.toString(), connectorCt);
		connectorCt.addMethod(mmeth);
	}

	private static boolean		hasSameSignature(CtClass clazz, CtMethod target)
	throws NotFoundException
	{
		for (CtMethod m : clazz.getDeclaredMethods()) {
			if (!m.getName().equals(target.getName())) continue;
			if (!Arrays.equals(m.getParameterTypes(), target.getParameterTypes())) continue;
			return true;
		}
		return false;
	}
}
// -----------------------------------------------------------------------------