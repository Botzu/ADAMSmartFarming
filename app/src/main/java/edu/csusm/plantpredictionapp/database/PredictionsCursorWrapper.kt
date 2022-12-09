package edu.csusm.plantpredictionapp.database

import android.database.Cursor
import android.database.CursorWrapper
import edu.csusm.plantpredictionapp.Prediction
import java.util.*

// cursor wrapper to bundle data into prediction class after query
class PredictionsCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    fun getPredictions(): Prediction {
        val predictionId: String = getString(getColumnIndex(PPDBSchema.PredictionTable.PR_ID))
        val soilPH: String = getString(getColumnIndex(PPDBSchema.PredictionTable.SOIL_PH))
        val nitroVal : String = getString(getColumnIndex(PPDBSchema.PredictionTable.NITROGEN))
        val phosphorousVal : String = getString(getColumnIndex(PPDBSchema.PredictionTable.PHOSPHOROUS))
        val potassiumVal : String = getString(getColumnIndex(PPDBSchema.PredictionTable.POTASSIUM))
        val predictionDate = Date(getLong(getColumnIndex(PPDBSchema.PredictionTable.DATE)))
        return Prediction(predictionId, soilPH, nitroVal, phosphorousVal, potassiumVal, predictionDate)
    }
}