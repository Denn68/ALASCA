package fr.sorbonne_u.components.hem2025.bases.connector_generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// -------------------------------------------------------------------------
/**
 * Describes all information extracted from a control-adapter XML descriptor.
 * Used by {@link DynamicConnectorFactory} to generate a BCM connector at
 * runtime via Javassist.
 *
 * <p>
 * Instances are <b>immutable</b> — use {@link Builder} to construct.
 * </p>
 *
 * <p>
 * Created on : 2026-01-30
 * </p>
 */
public class AdapterDescriptor {

    // -------------------------------------------------------------------------
    // Instance fields
    // -------------------------------------------------------------------------

    private final String equipmentId;
    private final String offeredInterface;
    private final List<String> dependencies;
    private final List<Field> fields;
    private final List<Operation> operations;

    // -------------------------------------------------------------------------
    // Constructor (private — use Builder)
    // -------------------------------------------------------------------------

    private AdapterDescriptor(String equipmentId,
            String offeredInterface,
            List<String> dependencies,
            List<Field> fields,
            List<Operation> operations) {
        this.equipmentId = equipmentId;
        this.offeredInterface = offeredInterface;
        this.dependencies = Collections.unmodifiableList(dependencies);
        this.fields = Collections.unmodifiableList(fields);
        this.operations = Collections.unmodifiableList(operations);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getOfferedInterface() {
        return offeredInterface;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public List<Field> getFields() {
        return fields;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    // =====================================================================
    // Field — describes an instance variable of the generated connector
    // =====================================================================

    public static class Field {

        private final String visibility;
        private final String dataType;
        private final String identifier;
        private final String initialValue;

        public Field(String visibility, String dataType,
                String identifier, String initialValue) {
            this.visibility = visibility;
            this.dataType = dataType;
            this.identifier = identifier;
            this.initialValue = initialValue;
        }

        public String getVisibility() {
            return visibility;
        }

        public String getDataType() {
            return dataType;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getInitialValue() {
            return initialValue;
        }

        public boolean isStatic() {
            return visibility != null && visibility.contains("static");
        }

        public boolean hasInitialValue() {
            return initialValue != null && !initialValue.isEmpty();
        }

        @Override
        public String toString() {
            return visibility + " " + dataType + " " + identifier
                    + (hasInitialValue() ? " = " + initialValue : "");
        }
    }

    // =====================================================================
    // Argument — describes a method parameter
    // =====================================================================

    public static class Argument {

        private final String dataType;
        private final String identifier;

        public Argument(String dataType, String identifier) {
            this.dataType = dataType;
            this.identifier = identifier;
        }

        public String getDataType() {
            return dataType;
        }

        public String getIdentifier() {
            return identifier;
        }

        @Override
        public String toString() {
            return dataType + " " + identifier;
        }
    }

    // =====================================================================
    // Operation — describes one method of the generated connector
    // =====================================================================

    public static class Operation {

        private final String label;
        private final String access;
        private final String resultType;
        private final List<Argument> arguments;
        private final List<String> exceptions;
        private final String implementation;
        private final String equipmentAlias;

        public Operation(String label, String access, String resultType,
                List<Argument> arguments, List<String> exceptions,
                String implementation, String equipmentAlias) {
            this.label = label;
            this.access = access;
            this.resultType = resultType;
            this.arguments = arguments != null ? arguments : new ArrayList<>();
            this.exceptions = exceptions != null ? exceptions : new ArrayList<>();
            this.implementation = implementation;
            this.equipmentAlias = equipmentAlias;
        }

        public String getLabel() {
            return label;
        }

        public String getAccess() {
            return access;
        }

        public String getResultType() {
            return resultType;
        }

        public List<Argument> getArguments() {
            return arguments;
        }

        public List<String> getExceptions() {
            return exceptions;
        }

        public String getImplementation() {
            return implementation;
        }

        public String getEquipmentAlias() {
            return equipmentAlias;
        }

        public boolean hasEquipmentRef() {
            return equipmentAlias != null && !equipmentAlias.isEmpty();
        }

        @Override
        public String toString() {
            return access + " " + resultType + " " + label
                    + "(" + arguments + ") { " + implementation + " }";
        }
    }

    // =====================================================================
    // Builder — step-by-step construction of an AdapterDescriptor
    // =====================================================================

    public static class Builder {

        private String equipmentId;
        private String offeredInterface;
        private final List<String> dependencies = new ArrayList<>();
        private final List<Field> fields = new ArrayList<>();
        private final List<Operation> operations = new ArrayList<>();

        public Builder equipmentId(String id) {
            this.equipmentId = id;
            return this;
        }

        public Builder offeredInterface(String iface) {
            this.offeredInterface = iface;
            return this;
        }

        public Builder addDependency(String dep) {
            this.dependencies.add(dep);
            return this;
        }

        public Builder addField(Field f) {
            this.fields.add(f);
            return this;
        }

        public Builder addOperation(Operation op) {
            this.operations.add(op);
            return this;
        }

        public AdapterDescriptor build() {
            return new AdapterDescriptor(
                    equipmentId, offeredInterface,
                    dependencies, fields, operations);
        }
    }
}
