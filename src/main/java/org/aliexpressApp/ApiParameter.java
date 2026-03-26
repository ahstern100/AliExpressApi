package org.aliexpressApp;

public class ApiParameter {
    private final String name;
    private final String description;
    private final String defaultValue;
    private final Class<?> type;
    private final boolean isComplex;

    public ApiParameter(String name, String description, String defaultValue) {
        this(name, description, defaultValue, String.class, false);
    }

    public ApiParameter(String name, String description, Class<?> type) {
        this(name, description, "", type, true);
    }

    public ApiParameter(String name, String description, String defaultValue, Class<?> type, boolean isComplex) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.type = type;
        this.isComplex = isComplex;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isComplex() {
        return isComplex;
    }
}
