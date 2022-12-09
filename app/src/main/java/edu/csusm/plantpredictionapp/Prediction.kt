package edu.csusm.plantpredictionapp

import java.util.Date

data class Prediction (var predictionId:String, var soilPH:String, var nitroVal:String, var phosphorousVal:String, var potassiumVal:String, var date: Date)