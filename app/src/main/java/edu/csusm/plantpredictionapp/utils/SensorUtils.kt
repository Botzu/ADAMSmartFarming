package edu.csusm.plantpredictionapp.utils

import android.content.Context
import android.util.Log
import edu.csusm.plantpredictionapp.R
import java.util.HashMap

class SensorUtils(context: Context) {
    private var sensorNameHashMap: HashMap<String, String>
            = HashMap()
    private var sensorImageHashMap: HashMap<String, Int>
            = HashMap()
    private var sensorUnitsMap: HashMap<String, String>
            = HashMap()
    private val sensorInfoArr = arrayOf("waterPH","waterTurbidity","soilMoisture") // sensors that need info icons

    init {
        sensorNameHashMap["atmosphericTemp"] = context.resources.getString(R.string.temp)
        sensorNameHashMap["atmosphericHumidity"] = context.resources.getString(R.string.humid)
        sensorNameHashMap["soilPH"] = context.resources.getString(R.string.ph)
        sensorNameHashMap["soilN"] = context.resources.getString(R.string.n)
        sensorNameHashMap["soilP"] = context.resources.getString(R.string.p)
        sensorNameHashMap["soilK"] = context.resources.getString(R.string.k)
        sensorNameHashMap["waterTurbidity"] = context.resources.getString(R.string.turbidity)
        sensorNameHashMap["waterPH"] = context.resources.getString(R.string.water_ph)
        sensorNameHashMap["soilMoisture"] = context.resources.getString(R.string.soil_moisture)
        sensorImageHashMap["atmosphericTemp"] = R.drawable.temperature_sensor_icon
        sensorImageHashMap["waterTurbidity"] = R.drawable.water_turbidity_icon
        sensorImageHashMap["atmosphericHumidity"] = R.drawable.humidity_sensor_image
        sensorImageHashMap["waterPH"] = R.drawable.water_ph_icon
        sensorImageHashMap["soilPH"] = R.drawable.ph_balance_sensor
        sensorImageHashMap["soilN"] = R.drawable.npk_soil_sensor_icon
        sensorImageHashMap["soilP"] = R.drawable.npk_soil_sensor_icon
        sensorImageHashMap["soilK"] = R.drawable.npk_soil_sensor_icon
        sensorImageHashMap["soilMoisture"] = R.drawable.soil_moisture
        sensorUnitsMap["waterPH"] = ""
        sensorUnitsMap["waterTurbidity"] = "ntu"
        sensorUnitsMap["soilN"] = "mg/kg"
        sensorUnitsMap["soilP"] = "mg/kg"
        sensorUnitsMap["soilK"] = "mg/kg"
        sensorUnitsMap["soilMoisture"] = "%"
        sensorUnitsMap["atmosphericTemp"] = "°C"
        sensorUnitsMap["atmosphericHumidity"] = "%"
    }

    fun getFullSensorName(shortSensorName: String): String? {
        return if(sensorNameHashMap.containsKey(shortSensorName))
            sensorNameHashMap[shortSensorName]
        else
            shortSensorName
    }

    fun getSensorImageResource(shortSensorName: String): Int? {
        return if(sensorImageHashMap.containsKey(shortSensorName))
            sensorImageHashMap[shortSensorName]
        else
            R.drawable.generic_sensor_item
    }

    fun getSensorUnits(shortSensorName: String): String? {
        return if(sensorUnitsMap.containsKey(shortSensorName))
            sensorUnitsMap[shortSensorName]
        else
            return ""
    }

    fun checkIfNeedsSensor(shortSensorName: String): Boolean {
        return sensorInfoArr.contains(shortSensorName)
    }

    // custom sensor info icons for different conditions
    fun setInfoIcon(shortSensorName: String, value:Float): Int {
        Log.i("hello", "The value was $shortSensorName")
        if (shortSensorName == "waterTurbidity")
        {
            return if (value > 1200F ) {
                //"Turbidity is too high, please check your water"
                R.drawable.info_icon_turbidity_high
            } else {
                R.drawable.info_icon
            }
        }
        if (shortSensorName == "waterPH")  {
            when (value) {
                in 6F..8F -> {
                    //"Water PH levels look normal"
                    return R.drawable.info_icon
                }
                in 4F..6F -> {
                    //"Water PH levels look slightly acidic"
                    return R.drawable.info_icon_slightly_high_ph
                }
                in 0F..4F -> {
                    //"Water PH levels look highly acidic"
                    return R.drawable.info_icon_high_ph
                }
                in 8F..10F -> {
                    //"Water PH levels look slightly alkaline"
                    return R.drawable.info_icon_slightly_high_alkaline
                }
                else -> {
                    //"Water PH levels look highly alkaline"
                    return R.drawable.info_icon_high_alkaline
                }
            }
        }
        if (shortSensorName == "soilMoisture")  {
            return if (value > 50F) {
                // Soil is moist
                R.drawable.info_icon
            }  else {
                // Soil not moist enough
                R.drawable.low_soil_moisture
            }
        }
        return R.drawable.info_icon // if it doesn't fit anything return the default
    }

    // this is the info text description when the info icon is pressed
    fun setInfoIconText(shortSensorName: String, value:Float):String {
        var text = ""
        if (shortSensorName == "waterTurbidity")
        {
            text = if (value > 1200F ) {
                "Turbidity is too high, please check your water"
            } else
                "Turbidity values are normal"
        }
        if (shortSensorName == "waterPH")  {

            text = when (value) {
                in 6F..8F -> {
                    "Water PH levels look normal"
                }
                in 4F..6F -> {
                    "Water PH levels look slightly acidic"
                }
                in 0F..4F -> {
                    "Water PH levels look highly acidic"
                }
                in 8F..10F -> {
                    "Water PH levels look slightly alkaline"
                }
                else -> {
                    "Water PH levels look highly alkaline"
                }
            }
        }

        if (shortSensorName == "soilMoisture")  {

            text = if (value > 50F) {
                "The soil is moist"
            }  else {
                "Soil moisture is too low, add more water"
            }
        }
        return text
    }

}