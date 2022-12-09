package edu.csusm.plantpredictionapp.utils

import android.content.Context
import edu.csusm.plantpredictionapp.R

// class to handle utilities for the crop information
class CropUtils(context: Context) {
    private val cropNameArr = context.resources.getStringArray(R.array.crops)
    private val cropTypeArr = context.resources.getStringArray(R.array.crop_types)
    private var cropTypeHashMap : HashMap<String, String>
            = HashMap()
    private var cropImageHashMap: HashMap<String, Int>
            = HashMap()

    init {
        // Adding the types to map
        // 0 = creeper
        // 1 = climber
        // 2 = herb
        // 3 = shrub
        // 4 = tree
        cropTypeHashMap["1"] = cropTypeArr[4]
        cropTypeHashMap["2"] = cropTypeArr[2]
        cropTypeHashMap["3"] = cropTypeArr[1]
        cropTypeHashMap["4"] = cropTypeArr[3]
        cropTypeHashMap["5"] = cropTypeArr[4]
        cropTypeHashMap["6"] = cropTypeArr[3]
        cropTypeHashMap["7"] = cropTypeArr[2]
        cropTypeHashMap["8"] = cropTypeArr[1]
        cropTypeHashMap["9"] = cropTypeArr[1]
        cropTypeHashMap["10"] = cropTypeArr[1]
        cropTypeHashMap["11"] = cropTypeArr[1]
        cropTypeHashMap["12"] = cropTypeArr[2]
        cropTypeHashMap["13"] = cropTypeArr[4]
        cropTypeHashMap["14"] = cropTypeArr[0]
        cropTypeHashMap["15"] = cropTypeArr[0]
        cropTypeHashMap["16"] = cropTypeArr[0]
        cropTypeHashMap["17"] = cropTypeArr[4]
        cropTypeHashMap["18"] = cropTypeArr[4]
        cropTypeHashMap["19"] = cropTypeArr[1]
        cropTypeHashMap["20"] = cropTypeArr[3]
        cropTypeHashMap["21"] = cropTypeArr[2]
        cropTypeHashMap["22"] = cropTypeArr[0]

        // Adding the images to map
        cropImageHashMap["1"] = R.drawable.apple128x128
        cropImageHashMap["2"] = R.drawable.banana128x128
        cropImageHashMap["3"] = R.drawable.blackgram128x128
        cropImageHashMap["4"] = R.drawable.chickpea128x128
        cropImageHashMap["5"] = R.drawable.coconut128x128
        cropImageHashMap["6"] = R.drawable.coffee128x128
        cropImageHashMap["7"] = R.drawable.cotton128x128
        cropImageHashMap["8"] = R.drawable.grape128x128
        cropImageHashMap["9"] = R.drawable.jute128x128
        cropImageHashMap["10"] = R.drawable.kidneybean128x128
        cropImageHashMap["11"] = R.drawable.lentil128x128
        cropImageHashMap["12"] = R.drawable.maize128x128
        cropImageHashMap["13"] = R.drawable.mango128x128
        cropImageHashMap["14"] = R.drawable.mothbeans128x128
        cropImageHashMap["15"] = R.drawable.mungbean128x128
        cropImageHashMap["16"] = R.drawable.muskmelon128x128
        cropImageHashMap["17"] = R.drawable.orange128x128
        cropImageHashMap["18"] = R.drawable.papaya128x128
        cropImageHashMap["19"] = R.drawable.pigeonpeas128x128
        cropImageHashMap["20"] = R.drawable.pomegranate128x128
        cropImageHashMap["21"] = R.drawable.rice128x128
        cropImageHashMap["22"] = R.drawable.watermelon128x128
    }

    // returns the name of the crop
    fun getCropName(itemNumber: String): String {
        return cropNameArr[itemNumber.toInt() -1]
    }

    // returns the type of the crop
    fun getCropType(itemNumber: String): String?
    {
        return cropTypeHashMap[itemNumber]
    }

    // returns the image resource ID
    fun getImageResourceID(itemNumber: String): Int?
    {
        return cropImageHashMap[itemNumber]
    }

}