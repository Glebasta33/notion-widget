package com.trusov.notionwidget.data.dto.filter.filter_dto

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class FilterSerializer : JsonSerializer<FilterDto> {
    override fun serialize(
        src: FilterDto?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val result = JsonObject()
        src?.let {
            result.addProperty(
                src.propertyFieldName,
                src.propertyValue
            )
            result.add(
                src.conditionFieldName,
                context!!.serialize(src.condition)
            )
        }
        return result
    }

}

class ConditionSerializer : JsonSerializer<ConditionDto> {
    override fun serialize(
        src: ConditionDto?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val result = JsonObject()
        src?.let {
            result.addProperty(
                src.conditionFieldName,
                src.value
            )
        }
        return result
    }
}



