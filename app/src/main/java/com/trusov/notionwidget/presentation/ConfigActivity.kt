package com.trusov.notionwidget.presentation

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }

        setResult(RESULT_CANCELED, resultValue)
        setContentView(binding.root)

        val sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE)
        val editor = sp.edit()
        if (sp.contains("$CHECKED_COLOR_BTN-$widgetId")) {
            binding.rgColor.check(sp.getInt("$CHECKED_COLOR_BTN-$widgetId", 0))
        }
        if (sp.contains("$CHECKED_TEXT_SIZE_BTN-$widgetId")) {
            binding.rgTextSizes.check(sp.getInt("$CHECKED_TEXT_SIZE_BTN-$widgetId", 0))
        }

        binding.rgColor.setOnCheckedChangeListener { radioGroup, id -> setSelectedColor(id) }

        binding.rgTextSizes.setOnCheckedChangeListener { radioGroup, id -> setTextSize(id)}

        binding.buttonSaveConfig.setOnClickListener {
            val checkedColorId = binding.rgColor.checkedRadioButtonId
            val checkedTextSizeId = binding.rgTextSizes.checkedRadioButtonId
            setSelectedColor(checkedColorId)
            setTextSize(checkedTextSizeId)
            editor.putInt("$WIDGET_COLOR-$widgetId", color)
            editor.putInt("$WIDGET_TEXT_SIZE-$widgetId", textSize)
            editor.putInt("$CHECKED_COLOR_BTN-$widgetId", checkedColorId)
            editor.putInt("$CHECKED_TEXT_SIZE_BTN-$widgetId", checkedTextSizeId)
            editor.apply()

            setResult(RESULT_OK, resultValue)
            sendBroadcast(resultValue)
            finish()
        }
    }

    private fun setSelectedColor(id: Int) {
        color = when (id) {
            R.id.radioRed -> Color.DKGRAY
            R.id.radioGreen -> Color.GREEN
            R.id.radioBlue -> Color.BLUE
            else -> Color.DKGRAY
        }
    }

    private fun setTextSize(id: Int) {
        textSize = when (id) {
            R.id.radioSizeSmall -> SMALL_TEXT_SIZE
            R.id.radioSizeMedium -> MEDIUM_TEXT_SIZE
            R.id.radioSizeBig -> BIG_TEXT_SIZE
            else -> SMALL_TEXT_SIZE
        }
    }

    companion object {
        const val LOG_TAG = "myLogs"
        const val WIDGET_PREF = "widget_pref"
        const val WIDGET_COLOR = "widget_color_"
        const val WIDGET_TEXT_SIZE = "widget_text_size_"
        const val CHECKED_COLOR_BTN = "checked_color_btn"
        const val CHECKED_TEXT_SIZE_BTN = "checked_text_size_btn"

        const val SMALL_TEXT_SIZE = 14
        const val MEDIUM_TEXT_SIZE = 16
        const val BIG_TEXT_SIZE = 20

        private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID
        private var resultValue: Intent? = null
        private var color: Int = Color.RED
        private var textSize: Int = 14
    }
}