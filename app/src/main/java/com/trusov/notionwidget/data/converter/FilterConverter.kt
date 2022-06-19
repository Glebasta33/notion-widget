package com.trusov.notionwidget.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.trusov.notionwidget.data.dto.filter.db_model.FilterRuleDbModel
import com.trusov.notionwidget.domain.entity.FilterRule
import org.json.JSONArray

class FilterConverter {
    @TypeConverter
    fun listOfRulesToString(rules: List<FilterRuleDbModel>): String {
        return Gson().toJson(rules)
    }

    @TypeConverter
    fun stringToListOfRules(rulesAsString: String): List<FilterRuleDbModel> {
        val gson = Gson()
        val objects = gson.fromJson(rulesAsString, ArrayList::class.java)
        val rules = mutableListOf<FilterRuleDbModel>()
        for (obj in objects) {
            rules.add(gson.fromJson(obj.toString(), FilterRuleDbModel::class.java))
        }
        return rules
    }
}