package com.ivanbm.frontend_btz.network

import com.google.gson.JsonDeserializer

class BooleanAdapter : JsonDeserializer<Boolean> {

    override fun deserialize(
        json: com.google.gson.JsonElement?,
        typeOfT: java.lang.reflect.Type?,
        context: com.google.gson.JsonDeserializationContext?
    ): Boolean {

        if (json == null || json.isJsonNull) {
            return false
        }

        return when {
            json.isJsonPrimitive && json.asJsonPrimitive.isBoolean ->
                json.asBoolean

            json.isJsonPrimitive && json.asJsonPrimitive.isNumber ->
                json.asInt != 0

            else ->
                false
        }
    }
}