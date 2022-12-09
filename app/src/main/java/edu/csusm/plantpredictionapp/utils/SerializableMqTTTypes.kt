package edu.csusm.plantpredictionapp.utils

import kotlinx.serialization.Serializable

// serializable data classes to bundle our data for predictions, prediction results and our MQTT sensor data from AWS
@Serializable
data class BundledSensorData(val temp: Float, val humidity: Float, val n: Float, val p: Float, val k: Float, val ph: Float)

@Serializable
data class BundledPredictionValues(
    val predicted_crops: List<Float>
)

@Serializable
data class WaterQualitySensorSerializer(val waterPH: Float, val waterTurbidity:Float)

@Serializable
data class AtmosphericSensorSerializer(
    val atmosphericTemp: Float, val atmosphericHumidity: Float
)

@Serializable
data class SoilQualitySensorSerializer(
    val soilPH: Float, val soilN: Float, val soilP: Float, val soilK: Float
)

@Serializable
data class SoilMoistureQualitySensorSerializer(
    val soilMoisture: Float
)