package org.aliexpressApp;

import java.util.List;

public class ApiCommand {
    private String name;
    private String description;
    private List<ApiParameter> parameters;

    public ApiCommand(String name, String description, List<ApiParameter> parameters) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ApiParameter> getParameters() {
        return parameters;
    }
}
