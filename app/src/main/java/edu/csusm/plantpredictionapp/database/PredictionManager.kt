package edu.csusm.plantpredictionapp.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import edu.csusm.plantpredictionapp.Prediction
import edu.csusm.plantpredictionapp.PredictionItems
import java.util.*

// Class to manage the database functions
class PredictionManager(context: Context) {
    private var predictionDatabase : SQLiteDatabase = PPOpenHelper(context).readableDatabase

    companion object {
        private fun getPredictionContentValues(soilPH: String, nitroVal:String, phosphorousVal:String, potassiumVal:String, date: Date) : ContentValues {
            val values = ContentValues()
            values.put(PPDBSchema.PredictionTable.SOIL_PH, soilPH)
            values.put(PPDBSchema.PredictionTable.NITROGEN, nitroVal)
            values.put(PPDBSchema.PredictionTable.PHOSPHOROUS, phosphorousVal)
            values.put(PPDBSchema.PredictionTable.POTASSIUM, potassiumVal)
            values.put(PPDBSchema.PredictionTable.DATE, date.time)
            return values
        }

        private fun getPredictionItemContentValues(itemId: String, predictionPercent: Float, prID: String) : ContentValues {
            val values = ContentValues()
            values.put(PPDBSchema.PredictionItemTable.I_ID, itemId)
            values.put(PPDBSchema.PredictionItemTable.PR_PERC, predictionPercent)
            values.put(PPDBSchema.PredictionItemTable.PR_ID, prID)
            return values
        }
    }

    fun getPredictionItemsByPredictionID(predictionID:String) : List<PredictionItems> {
        val predictionItemList = arrayListOf<PredictionItems>()
        val predictionItemsToReturn : PredictionItemsCursorWrapper = returnPredictionItems(PPDBSchema.PredictionItemTable.PR_ID + " = ?", arrayOf(predictionID), PPDBSchema.PredictionItemTable.PR_PERC + " DESC")
        predictionItemsToReturn.use { predictionItemsToReturn ->
            while(predictionItemsToReturn.moveToNext()) {
                predictionItemList.add(predictionItemsToReturn.getPredictionItems())
            }
        }
        return predictionItemList
    }

    fun getPredictions() : List<Prediction> {
        val predictionList = arrayListOf<Prediction>()
        val predictionsToReturn : PredictionsCursorWrapper = returnPredictions(null,null)
        predictionsToReturn.use { predictionsToReturn ->
            while(predictionsToReturn.moveToNext()) {
                predictionList.add(predictionsToReturn.getPredictions())
            }
        }
        return predictionList
    }

    fun addPredictionItem(itemId: String, predictionPercent: Float, prID:String) {
        val values : ContentValues = getPredictionItemContentValues(itemId, predictionPercent, prID)
        predictionDatabase.insert(PPDBSchema.PredictionItemTable.TABLE_NAME,null,values)
    }

    private fun removePredictionItem(prID: String)
    {
        predictionDatabase.delete(PPDBSchema.PredictionItemTable.TABLE_NAME,PPDBSchema.PredictionItemTable.PR_ID + " = ?", arrayOf(prID))
    }

    fun addPrediction(soilPH: String, nitroVal: String, phosphorousVal: String, potassiumVal: String, date: Date): Long {
        val values: ContentValues =
            getPredictionContentValues(soilPH, nitroVal, phosphorousVal, potassiumVal, date)
        return predictionDatabase.insert(PPDBSchema.PredictionTable.TABLE_NAME, null, values)
    }

    fun removePrediction(prID: String)
    {
        predictionDatabase.delete(PPDBSchema.PredictionTable.TABLE_NAME,PPDBSchema.PredictionTable.PR_ID + " = ?", arrayOf(prID))
        removePredictionItem(prID)
    }

    private fun returnPredictions(whereClause: String?, whereArgs: Array<String>?) : PredictionsCursorWrapper {
        val cursor : Cursor = predictionDatabase.query(
            PPDBSchema.PredictionTable.TABLE_NAME,
            null,
            whereClause,
            whereArgs,
            null,
            null,
            null
        )
        return PredictionsCursorWrapper(cursor)
    }

    private fun returnPredictionItems(whereClause: String?, whereArgs: Array<String>, orderArg: String) : PredictionItemsCursorWrapper {
        val cursor : Cursor = predictionDatabase.query(
            PPDBSchema.PredictionItemTable.TABLE_NAME,
            null,
            whereClause,
            whereArgs,
            null,
            null,
            orderArg
        )
        return PredictionItemsCursorWrapper(cursor)
    }
}