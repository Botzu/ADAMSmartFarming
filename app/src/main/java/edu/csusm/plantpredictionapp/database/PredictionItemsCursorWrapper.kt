package edu.csusm.plantpredictionapp.database

import android.database.Cursor
import android.database.CursorWrapper
import edu.csusm.plantpredictionapp.PredictionItems

// cursor wrapper to bundle data into prediction item class after query
class PredictionItemsCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    fun getPredictionItems(): PredictionItems {
        val itemID: String = getString(getColumnIndex(PPDBSchema.PredictionItemTable.I_ID))
        val prStr: String = String.format("%.2f", getFloat(getColumnIndex(PPDBSchema.PredictionItemTable.PR_PERC))) // formatting to 2 point precision
        return PredictionItems(itemID, prStr)
    }
}