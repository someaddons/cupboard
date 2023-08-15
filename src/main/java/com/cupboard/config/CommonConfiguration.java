package com.cupboard.config;

import com.google.gson.JsonObject;

public class CommonConfiguration implements ICommonConfig
{
    public boolean showCommandExecutionErrors = true;

    public CommonConfiguration()
    {
    }

    public JsonObject serialize()
    {
        final JsonObject root = new JsonObject();

        final JsonObject entry = new JsonObject();
        entry.addProperty("desc:", "Whether to display errors during command execution: default:true");
        entry.addProperty("showCommandExecutionErrors", showCommandExecutionErrors);
        root.add("showCommandExecutionErrors", entry);

        return root;
    }

    public void deserialize(JsonObject data)
    {
        showCommandExecutionErrors = data.get("showCommandExecutionErrors").getAsJsonObject().get("showCommandExecutionErrors").getAsBoolean();
    }
}
