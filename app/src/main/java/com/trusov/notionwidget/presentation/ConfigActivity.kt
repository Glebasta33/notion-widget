package com.trusov.notionwidget.presentation

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.trusov.notionwidget.R
import com.trusov.notionwidget.data.NoteAppWidgetProvider
import com.trusov.notionwidget.databinding.ActivityConfigBinding

class ConfigActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityConfigBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.extras?.let { extras ->
            widgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }

        resultValue = Intent(this, NoteAppWidgetProvider::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            action =  AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }

        setResult(RESULT_CANCELED, resultValue)
        setContentView(binding.root)

        val sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE)
        val editor = sp.edit()
        if(sp.contains("$CHECKED_COLOR_BTN-$widgetId")) {
            binding.rgColor.check(sp.getInt("$CHECKED_COLOR_BTN-$widgetId", 0))
        }

        binding.rgColor.setOnCheckedChangeListener { radioGroup, id ->
            getSelectedColor(id)
        }

        binding.buttonSaveConfig.setOnClickListener {
            val checkedRadioButtonId = binding.rgColor.checkedRadioButtonId
            getSelectedColor(checkedRadioButtonId)
            editor.putInt("$WIDGET_COLOR-$widgetId", color)
            editor.putInt("$CHECKED_COLOR_BTN-$widgetId", checkedRadioButtonId)
            editor.apply()

            setResult(RESULT_OK, resultValue)
            sendBroadcast(resultValue)
            finish()
        }
    }

    private fun getSelectedColor(id: Int) {
        color = when (id) {
            R.id.radioRed -> {
                Color.RED
            }
            R.id.radioGreen -> {
                Color.GREEN
            }
            R.id.radioBlue -> {
                Color.BLUE
            }
            else -> Color.DKGRAY
        }
    }

    companion object {
        const val LOG_TAG = "myLogs"
        const val WIDGET_PREF = "widget_pref"
        const val WIDGET_COLOR = "widget_color_"
        const val CHECKED_COLOR_BTN = "checked_color_btn"

        private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID
        private var resultValue: Intent? = null
        private var color: Int = Color.RED
    }
}