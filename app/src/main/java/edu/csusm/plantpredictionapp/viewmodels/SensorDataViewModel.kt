package edu.csusm.plantpredictionapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import edu.csusm.plantpredictionapp.SensorData
import edu.csusm.plantpredictionapp.utils.BundledSensorData
// viewModel for updating the sensor fragments
class SensorDataViewModel : ViewModel() {
    var sensorDataList = arrayListOf<SensorData>() // all sensors
    private var sensorDataMap: HashMap<String, Int> = HashMap() // maps sensor names mapped to their position in the arraylist
    val mWaterQualitySensorList = MutableLiveData<List<SensorData>>() // an updatable list for the water quality sensors
    val mSoilQualitySensorList = MutableLiveData<List<SensorData>>() // an updatable list for the soil quality sensors
    val mAtmosphereQualitySensorList = MutableLiveData<List<SensorData>>() // an updatable list for the atmospheric sensors
    val mSoilMoistureQualitySensor = MutableLiveData<List<SensorData>>() // we had to split the soil moisture from the other sensors
    var mSoilQualityBool : Boolean = false // flags to check if non null so I can combine the sensor data
    var mSoilWaterQualityBool : Boolean = false

    //soil sensor graphing MLD and arraylists
    var soilMoistureChartDataList = ArrayList<Entry>() // so that I can store it
    var soilMoistureChartData = MutableLiveData<Entry>() // so that I can observe it
    var soilPHChartDataList = ArrayList<Entry>()
    var soilPHChartData = MutableLiveData<Entry>()
    var soilNChartDataList = ArrayList<Entry>()
    var soilNChartData = MutableLiveData<Entry>()
    var soilPChartDataList = ArrayList<Entry>()
    var soilPChartData = MutableLiveData<Entry>()
    var soilKChartDataList = ArrayList<Entry>()
    var soilKChartData = MutableLiveData<Entry>()

    // atmosphere sensor graphing MLD and arraylists
    var atmosphericTempDataList = ArrayList<Entry>()
    var atmosphericTempData =  MutableLiveData<Entry>()
    var atmosphericHumidityDataList = ArrayList<Entry>()
    var atmosphericHumidityData = MutableLiveData<Entry>()

    // water sensor graphing MLD and arraylists
    var waterPHDataList = ArrayList<Entry>()
    var waterPHData = MutableLiveData<Entry>()
    var waterTurbidityDataList = ArrayList<Entry>()
    var waterTurbidityData = MutableLiveData<Entry>()

    // mapping all the sensors to an arrayList so I know whats been added and for easy access
    private fun handleNewSensorData(sensor: SensorData)
    {
        if(sensorDataMap.containsKey(sensor.sensorName)) // if its not a new sensor, we just update current values
        {
            sensorDataList[sensorDataMap[sensor.sensorName]!!].sensorValue = sensor.sensorValue
        }
        else
        {
            sensorDataMap[sensor.sensorName] = sensorDataList.size // if it is a new sensor add it to the map and list
            sensorDataList.add(sensor)
        }
    }
    // updating the sensors with the arraylist and type passed in
    fun handleNewSensorDataListWithType(sensorList: ArrayList<SensorData>, type: String)
    {
        for (sensor in sensorList) {
            handleNewSensorData(sensor)
        }
        when(type) {
            "atmospheric" -> {
                mAtmosphereQualitySensorList.value = sensorList
            }
            "water" -> {
                mWaterQualitySensorList.value = sensorList
            }
            "soil" -> {
                mSoilQualityBool = true
                mSoilQualitySensorList.value = sensorList
            }
            "soil_moisture" -> {
                mSoilWaterQualityBool = true
                mSoilMoistureQualitySensor.value = sensorList
            }
        }
    }
    // check if all data has been added before trying predictions
    fun checkMapContainsBundle() : Boolean {
        //data class BundledSensorData(val temp: Float, val humidity: Float, val n: Float, val p: Float, val k: Float, val ph: Float)
        if(sensorDataMap.containsKey("atmosphericTemp") &&
            sensorDataMap.containsKey("atmosphericHumidity") &&
            sensorDataMap.containsKey("soilPH") &&
            sensorDataMap.containsKey("soilN") &&
            sensorDataMap.containsKey("soilP") &&
            sensorDataMap.containsKey("soilK"))
        {
            return true
        }
        return false
    }
    // bundle the sensor data for predictions
    fun bundleMySensorData(): BundledSensorData {

        return BundledSensorData(
            sensorDataList[sensorDataMap["atmosphericTemp"]!!].sensorValue,
            sensorDataList[sensorDataMap["atmosphericHumidity"]!!].sensorValue,
            sensorDataList[sensorDataMap["soilPH"]!!].sensorValue,
            sensorDataList[sensorDataMap["soilN"]!!].sensorValue,
            sensorDataList[sensorDataMap["soilP"]!!].sensorValue,
            sensorDataList[sensorDataMap["soilK"]!!].sensorValue
        )
    }

    // bundling the soil sensors from different microcontrollers
    fun bundleSoilSensorData(): ArrayList<SensorData> {
        val arrayToReturn = ArrayList<SensorData>()
        if(sensorDataMap.containsKey("soilPH") &&
            sensorDataMap.containsKey("soilN") &&
            sensorDataMap.containsKey("soilP") &&
            sensorDataMap.containsKey("soilK")) {
            arrayToReturn.add(SensorData("soilPH",sensorDataList[sensorDataMap["soilPH"]!!].sensorValue))
            arrayToReturn.add(SensorData("soilN",sensorDataList[sensorDataMap["soilN"]!!].sensorValue))
            arrayToReturn.add(SensorData("soilP",sensorDataList[sensorDataMap["soilP"]!!].sensorValue))
            arrayToReturn.add(SensorData("soilK",sensorDataList[sensorDataMap["soilK"]!!].sensorValue))
        }
        if(sensorDataMap.containsKey("soilMoisture")) {
            arrayToReturn.add(SensorData("soilMoisture",sensorDataList[sensorDataMap["soilMoisture"]!!].sensorValue))
        }
        return arrayToReturn
    }

    fun insertChartData(entry: Entry, type: String) {
        when(type) {
            "waterPH" -> {
                waterPHDataList.add(entry)
                waterPHData.value = entry
            }
            "waterTurbidity" -> {
                waterTurbidityDataList.add(entry)
                waterTurbidityData.value = entry
            }
            "atmosphericTemp" -> {
                atmosphericTempDataList.add(entry)
                atmosphericTempData.value = entry
            }
            "atmosphericHumidity" -> {
                atmosphericHumidityDataList.add(entry)
                atmosphericHumidityData.value = entry
            }
            "soilN" -> {
                soilNChartDataList.add(entry)
                soilNChartData.value = entry
            }
            "soilP" -> {
                soilPChartDataList.add(entry)
                soilPChartData.value = entry
            }
            "soilK" -> {
                soilKChartDataList.add(entry)
                soilKChartData.value = entry
            }
            "soilPH" -> {
                soilPHChartDataList.add(entry)
                soilPHChartData.value = entry
            }
            "soilMoisture" -> {
                soilMoistureChartDataList.add(entry)
                soilMoistureChartData.value = entry
            }
        }

    }
}