package org.aliexpressApp;

public class ApiParameter {
    private String name;
    private String description;
    private String defaultValue;

    public ApiParameter(String name, String description, String defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
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
}
