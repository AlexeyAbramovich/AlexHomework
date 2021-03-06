package com.abra.homework_9.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.abra.homework_9.R
import com.abra.homework_9.functions.getIconId
import com.abra.homework_9.network.WeatherRootObject
import com.abra.homework_9.repositories.DatabaseRepository
import com.abra.homework_9.repositories.RequestRepository
import com.abra.homework_9.repositories.SharedPrefRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.roundToInt

class WeatherWidget : AppWidgetProvider() {
    private val disposableContainer: CompositeDisposable = CompositeDisposable()
    private val requestRepository = RequestRepository()
    private lateinit var preferencesRepository: SharedPrefRepository
    private val widgetScope = CoroutineScope(Dispatchers.Main + Job())
    private val repository = DatabaseRepository(widgetScope)
    private var checkedCityId = 0

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        preferencesRepository = SharedPrefRepository(context)
        loadLastCityId(context)
        widgetScope.launch {
            val cityData = repository.getCityById(checkedCityId)
            appWidgetIds?.forEach {
                loadForecast(cityData.name, context, appWidgetManager, it)
            }
        }

    }

    private fun loadForecast(cityName: String, context: Context?, appWidgetManager: AppWidgetManager?, widgetId: Int) {
        requestRepository.createRequest(cityName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data -> setLoadedData(data, context, appWidgetManager, widgetId) }
                .also { disposableContainer.add(it) }
    }

    private fun setLoadedData(response: WeatherRootObject, context: Context?, appWidgetManager: AppWidgetManager?, widgetId: Int) {
        with(response) {
            val widgetView = RemoteViews(context?.packageName, R.layout.widget)
            with(widgetView) {
                setTextViewText(R.id.tvCityNameWidget, "${city.name}, ${city.country}")
                setTextViewText(R.id.tvDateWidget, SimpleDateFormat.getDateInstance().format(Date()))
                setTextViewText(R.id.tvTemperatureWidget, list[0].main.temp.roundToInt().toString())
                setImageViewResource(R.id.ivCurrentWeatherWidget, getIconId(list[0].weather[0].icon))
                appWidgetManager?.updateAppWidget(widgetId, this)
            }
        }
    }

    private fun loadLastCityId(context: Context?) {
        context?.run {
            checkedCityId = preferencesRepository.getId()
        }
    }

    override fun onDisabled(context: Context?) {
        disposableContainer.clear()
        super.onDisabled(context)
    }
}