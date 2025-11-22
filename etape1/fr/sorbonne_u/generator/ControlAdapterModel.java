package fr.sorbonne_u.generator;

import java.util.*;

public class ControlAdapterModel {

    // Identification
    private String uid;
    private String offeredInterface;

    // Consommation
    public static class Consumption {
        public final Double min;      // peut être null
        public final Double nominal;  // requis
        public final Double max;      // peut être null
        public Consumption(Double min, Double nominal, Double max) {
            this.min = min;
            this.nominal = nominal;
            this.max = max;
        }
        @Override public String toString() {
            return "Consumption{min=" + min + ", nominal=" + nominal + ", max=" + max + "}";
        }
    }
    private Consumption consumption;

    // Required classes
    private final List<String> requiredClasses = new ArrayList<>();

    // Instance vars
    public static class InstanceVar {
        public final String modifiers;   // ex: "protected static"
        public final String type;        // ex: "int"
        public final String name;        // ex: "MAX_MODE"
        public final String staticInit;  // ex: "6" (peut être null)
        public InstanceVar(String modifiers, String type, String name, String staticInit) {
            this.modifiers = modifiers;
            this.type = type;
            this.name = name;
            this.staticInit = staticInit;
        }
        @Override public String toString() {
            return modifiers + " " + type + " " + name + (staticInit != null ? " = " + staticInit : "");
        }
    }
    private final List<InstanceVar> instanceVars = new ArrayList<>();

    // Méthodes internes
    public static class Parameter {
        public final String type;  // peut être null (dans ton XML, souvent présent)
        public final String name;
        public Parameter(String type, String name) {
            this.type = type;
            this.name = name;
        }
        @Override public String toString() {
            return (type != null ? type + " " : "") + name;
        }
    }
    public static class Body {
        public final String equipmentRef; // peut être null
        public final String code;         // code Java brut
        public Body(String equipmentRef, String code) {
            this.equipmentRef = equipmentRef;
            this.code = code;
        }
        @Override public String toString() {
            return "{equipmentRef=" + equipmentRef + ", code=\n" + code + "\n}";
        }
    }
    public static class InternalMethod {
        public final String modifiers;     // ex: "protected"
        public final String returnType;    // ex: "double"
        public final String name;          // ex: "computePowerLevel"
        public final List<Parameter> parameters;
        public final List<String> thrownExceptions;
        public final Body body;
        public InternalMethod(String modifiers, String returnType, String name,
                              List<Parameter> parameters, List<String> thrownExceptions, Body body) {
            this.modifiers = modifiers;
            this.returnType = returnType;
            this.name = name;
            this.parameters = parameters;
            this.thrownExceptions = thrownExceptions;
            this.body = body;
        }
        @Override public String toString() {
            return modifiers + " " + returnType + " " + name + "(" + parameters + ")"
                    + (thrownExceptions.isEmpty() ? "" : " throws " + thrownExceptions) + " " + body;
        }
    }
    private final List<InternalMethod> internalMethods = new ArrayList<>();

    // Opérations (maxMode, upMode, downMode, setMode, currentMode, getModeConsumption, suspended, suspend, resume, emergency)
    public static class Operation {
        public final String name;
        public final Parameter parameter; // null si pas de paramètre (ex: upMode)
        public final Body body;
        public Operation(String name, Parameter parameter, Body body) {
            this.name = name;
            this.parameter = parameter;
            this.body = body;
        }
        @Override public String toString() {
            return "Operation{" + name + ", param=" + parameter + ", body=" + body + "}";
        }
    }
    private final Map<String, Operation> operations = new LinkedHashMap<>();

    // Getters / Setters simples
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getOfferedInterface() { return offeredInterface; }
    public void setOfferedInterface(String offeredInterface) { this.offeredInterface = offeredInterface; }

    public Consumption getConsumption() { return consumption; }
    public void setConsumption(Consumption consumption) { this.consumption = consumption; }

    public List<String> getRequiredClasses() { return requiredClasses; }
    public List<InstanceVar> getInstanceVars() { return instanceVars; }
    public List<InternalMethod> getInternalMethods() { return internalMethods; }
    public Map<String, Operation> getOperations() { return operations; }

    @Override public String toString() {
        return "ControlAdapterModel{" +
                "uid='" + uid + '\'' +
                ", offeredInterface='" + offeredInterface + '\'' +
                ", consumption=" + consumption +
                ", requiredClasses=" + requiredClasses +
                ", instanceVars=" + instanceVars +
                ", internalMethods=" + internalMethods +
                ", operations=" + operations.keySet() +
                '}';
    }
}
