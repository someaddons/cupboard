package com.cupboard.config;

import com.google.gson.JsonObject;

public class CommonConfiguration implements ICommonConfig
{
    public boolean showCommandExecutionErrors = true;
    public boolean debugChunkloadAttempts = false;
    public boolean logOffthreadEntityAdd = true;

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

        final JsonObject entry2 = new JsonObject();
        entry2.addProperty("desc:", "Enables debug logging of chunks being forceloaded on serverthread by directly accessing an unloaded chunk, which stalls the server until the chunk finishes loading: default:false");
        entry2.addProperty("debugChunkloadAttempts", debugChunkloadAttempts);
        root.add("debugChunkloadAttempts", entry2);

        final JsonObject entry3 = new JsonObject();
        entry3.addProperty("desc:", "Entities should only be added on the server thread itself, cupboard fixes the crashes caused by mods violating that, this option enables the logging of those: default:true");
        entry3.addProperty("logOffthreadEntityAdd", logOffthreadEntityAdd);
        root.add("logOffthreadEntityAdd", entry3);

        return root;
    }

    public void deserialize(JsonObject data)
    {
        showCommandExecutionErrors = data.get("showCommandExecutionErrors").getAsJsonObject().get("showCommandExecutionErrors").getAsBoolean();
        debugChunkloadAttempts = data.get("debugChunkloadAttempts").getAsJsonObject().get("debugChunkloadAttempts").getAsBoolean();
        logOffthreadEntityAdd = data.get("logOffthreadEntityAdd").getAsJsonObject().get("logOffthreadEntityAdd").getAsBoolean();
    }
}
