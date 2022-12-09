package edu.csusm.plantpredictionapp.database

import android.provider.BaseColumns

// database object schema
object PPDBSchema {
    object PredictionTable : BaseColumns {
        const val TABLE_NAME = "predictions"
        const val PR_ID = "pr_id"
        const val SOIL_PH = "soil_ph"
        const val NITROGEN = "nitrogen"
        const val PHOSPHOROUS = "phosphorous"
        const val POTASSIUM = "potassium"
        const val DATE = "date"
    }

    object PredictionItemTable : BaseColumns {
        const val TABLE_NAME = "prediction_item"
        const val PRI_ID = "pri_id"
        const val I_ID = "i_id"
        const val PR_ID = "pr_id"
        const val PR_PERC = "pr_perc"
    }

}