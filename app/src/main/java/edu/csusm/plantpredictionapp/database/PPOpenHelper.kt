package edu.csusm.plantpredictionapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// The open helper class to instantiate and update the database
class PPOpenHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "PP_AD_DB"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("create table " + PPDBSchema.PredictionTable.TABLE_NAME + " (" +
                PPDBSchema.PredictionTable.PR_ID + " integer primary key autoincrement, " +
                PPDBSchema.PredictionTable.NITROGEN + ", " +
                PPDBSchema.PredictionTable.SOIL_PH + ", " +
                PPDBSchema.PredictionTable.PHOSPHOROUS + ", " +
                PPDBSchema.PredictionTable.POTASSIUM + ", " +
                PPDBSchema.PredictionTable.DATE + ")"
        )

        db.execSQL("create table " + PPDBSchema.PredictionItemTable.TABLE_NAME + " (" +
                PPDBSchema.PredictionItemTable.PRI_ID + " integer primary key autoincrement, " +
                PPDBSchema.PredictionItemTable.I_ID + ", " +
                PPDBSchema.PredictionItemTable.PR_ID + ", " +
                PPDBSchema.PredictionItemTable.PR_PERC + ", " +
                "foreign key("+PPDBSchema.PredictionItemTable.PR_ID+") references "+PPDBSchema.PredictionTable.TABLE_NAME+
                "("+PPDBSchema.PredictionTable.PR_ID+")" +
                ")"
        )

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + PPDBSchema.PredictionTable.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + PPDBSchema.PredictionItemTable.TABLE_NAME)
        onCreate(db)
    }
}