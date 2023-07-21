package com.cupboard.config;

import com.google.gson.JsonObject;

public interface ICommonConfig
{
    public JsonObject serialize();

    public void deserialize(JsonObject data);
}
