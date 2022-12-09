package edu.csusm.plantpredictionapp.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.amazonaws.regions.Regions
import com.github.mikephil.charting.data.Entry
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import edu.csusm.plantpredictionapp.R
import edu.csusm.plantpredictionapp.SensorData
import edu.csusm.plantpredictionapp.database.PredictionManager
import edu.csusm.plantpredictionapp.utils.*
import edu.csusm.plantpredictionapp.viewmodels.PredictionUpdaterViewModel
import edu.csusm.plantpredictionapp.viewmodels.SensorDataViewModel
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import java.io.UnsupportedEncodingException
import java.util.*
import kotlinx.serialization.json.Json
import okhttp3.*
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

// fragment to display the menu on the home page
class DisplayMenu(context: Context) : Fragment() {
    private lateinit var displayMenuView: View
    private lateinit var btnConnect: Button
    private lateinit var btnPredict: Button
    private lateinit var btnDisconnect: Button
    private lateinit var btnSubscribe: Button
    private lateinit var waterSensorArrayList: ArrayList<SensorData>
    private lateinit var soilSensorArrayList: ArrayList<SensorData>
    private lateinit var atmosphericSensorArrayList: ArrayList<SensorData>
    private lateinit var soilMoistureSensorArrayList: ArrayList<SensorData>
    private lateinit var progressBar: ProgressBar
    private lateinit var sensorDataViewPager: ViewPager2
    private lateinit var sensorDataTabLayout: TabLayout
    private lateinit var sensorDataViewPagerAdapter: SensorDataViewPagerAdapter
    private val sensorViewModel: SensorDataViewModel by activityViewModels()
    private val predictionUpdaterViewModel: PredictionUpdaterViewModel by activityViewModels()
    private lateinit var predictionManager: PredictionManager // class to handle our SQLLite functions
    private lateinit var mqttManager: AWSIotMqttManager  // mqtt manager class for sending and receiving
    private lateinit var credentialProvider: CognitoCachingCredentialsProvider // cognito credential provider
    private val clientID = UUID.randomUUID().toString() // the client id for the cognito pool
    private val format = Json { ignoreUnknownKeys  = true }  // format for Json serialization
    private val cSpecificEndpoint = "xxxxxxxxxxxx-xxx.iot.us-west-1.amazonaws.com" // AWS iOT core customer specific endpoint for your thing device
    private val cPoolId = "us-west-1:00000000-0000-0000-0000-000000000000" // This is where your AWS cognito pool ID for the AWS credential provider
    private val awsRegion = Regions.US_WEST_1 // Region our AWS service is located

    //attaching the sensor fragments to the viewpager
    override fun onAttach(context: Context) {
        super.onAttach(context)
        sensorDataViewPagerAdapter = SensorDataViewPagerAdapter(this)
        val waterSensorFragment = SensorContainerFragment.newInstance("Water")
        val soilSensorFragment = SensorContainerFragment.newInstance("Soil")
        val atmosphericSensorFragment = SensorContainerFragment.newInstance("Atmospheric")
        predictionManager = PredictionManager(context)
        sensorDataViewPagerAdapter.addFrag(soilSensorFragment, "Soil")
        sensorDataViewPagerAdapter.addFrag(waterSensorFragment,"Water")
        sensorDataViewPagerAdapter.addFrag(atmosphericSensorFragment,"Atmospheric")


    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        displayMenuView = inflater.inflate(R.layout.fragment_get_prediction_data_screen, container, false)
        sensorDataViewPager = displayMenuView.findViewById(R.id.sensor_pager)
        sensorDataTabLayout = displayMenuView.findViewById(R.id.sensor_tabs)
        sensorDataViewPager.adapter = sensorDataViewPagerAdapter
        waterSensorArrayList = ArrayList()
        soilSensorArrayList = ArrayList()
        atmosphericSensorArrayList = ArrayList()
        soilMoistureSensorArrayList = ArrayList()
        progressBar = displayMenuView.findViewById(R.id.buttonProgressBar)
        progressBar.visibility = View.INVISIBLE
        TabLayoutMediator(sensorDataTabLayout,sensorDataViewPager) { tab,position ->
            tab.text = sensorDataViewPagerAdapter.getPageTitle(position)
        }.attach()
        return displayMenuView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnConnect = displayMenuView.findViewById(R.id.btn_connect)
        btnPredict = displayMenuView.findViewById(R.id.btn_predict)
        btnDisconnect = displayMenuView.findViewById(R.id.btn_disconnect)
        btnSubscribe = displayMenuView.findViewById(R.id.btn_subscribe)
        credentialProvider = CognitoCachingCredentialsProvider(context, cPoolId,awsRegion)
        mqttManager = AWSIotMqttManager(clientID,cSpecificEndpoint)
        btnConnect.setOnClickListener {
            onConnectClick()
        }

        btnPredict.setOnClickListener {
            if (!sensorViewModel.checkMapContainsBundle()) // if we don't have all the sensor we can't make a prediction
            {
                Toast.makeText(context, getString(R.string.prediction_requirement), Toast.LENGTH_SHORT) // requires soil and atmospheric sensors to work
                    .show()
                return@setOnClickListener
            }
            onPredictClick()
        }

        btnSubscribe.setOnClickListener {
            onSubscribeClick()
        }

        btnDisconnect.setOnClickListener {
            mqttManager.disconnect()
        }

    }

    // handles the subscribe button click
    private fun onSubscribeClick() {
        val topic = "zone1" // hardcoded the topic as zone1 but could be later set by user selection
        btnSubscribe.isEnabled = false
        try {
            mqttManager.subscribeToTopic(
                topic, AWSIotMqttQos.QOS0
            ) { _, data ->
                requireActivity().runOnUiThread {
                    try {
                        val message =  String(data, Charsets.UTF_8)
                        if(message.contains("\"waterPH\"",ignoreCase = false) //water sensors
                            && message.contains("\"waterTurbidity\"",ignoreCase = false))
                        {
                            val wPHData = format.decodeFromString<WaterQualitySensorSerializer>(message).waterPH
                            val wTDData = format.decodeFromString<WaterQualitySensorSerializer>(message).waterTurbidity
                            waterSensorArrayList.clear()
                            waterSensorArrayList.add(SensorData("waterPH",wPHData))
                            waterSensorArrayList.add(SensorData("waterTurbidity",wTDData))
                            sensorViewModel.handleNewSensorDataListWithType(waterSensorArrayList,"water")
                            sensorViewModel.insertChartData(Entry(compactTimeMilliseconds().toFloat(),wPHData),"waterPH")
                            sensorViewModel.insertChartData(Entry(compactTimeMilliseconds().toFloat(),wTDData),"waterTurbidity")

                        }
                        if(message.contains("\"atmosphericTemp\"",ignoreCase = false)
                            && message.contains("\"atmosphericHumidity\"",ignoreCase = false)) //atmospheric sensors
                        {
                            val aTData = format.decodeFromString<AtmosphericSensorSerializer>(message).atmosphericTemp
                            val aHData = format.decodeFromString<AtmosphericSensorSerializer>(message).atmosphericHumidity
                            atmosphericSensorArrayList.clear()
                            atmosphericSensorArrayList.add(SensorData("atmosphericTemp",aTData))
                            atmosphericSensorArrayList.add(SensorData("atmosphericHumidity",aHData))
                            sensorViewModel.handleNewSensorDataListWithType(atmosphericSensorArrayList,"atmospheric")
                            sensorViewModel.insertChartData(Entry(compactTimeMilliseconds().toFloat(),aTData),"atmosphericTemp")
                            sensorViewModel.insertChartData(Entry(compactTimeMilliseconds().toFloat(),aHData),"atmosphericHumidity")
                        }
                        if(message.contains("\"soilN\"",ignoreCase = false)
                            && message.contains("\"soilP\"",ignoreCase = false)
                            && message.contains("\"soilK\"",ignoreCase = false)
                            && message.contains("\"soilPH\"",ignoreCase = false))
                        {
                            val sNData:Float = format.decodeFromString<SoilQualitySensorSerializer>(message).soilN
                            val sPData:Float = format.decodeFromString<SoilQualitySensorSerializer>(message).soilP
                            val sKData:Float = format.decodeFromString<SoilQualitySensorSerializer>(message).soilK
                            val sPHData:Float = format.decodeFromString<SoilQualitySensorSerializer>(message).soilPH //soil sensors
                            soilSensorArrayList.clear()
                            soilSensorArrayList.add(SensorData("soilN",sNData))
                            soilSensorArrayList.add(SensorData("soilP",sPData))
                            soilSensorArrayList.add(SensorData("soilK",sKData))
                            soilSensorArrayList.add(SensorData("soilPH",sPHData))
                            sensorViewModel.handleNewSensorDataListWithType(soilSensorArrayList,"soil")
                            sensorViewModel.insertChartData(Entry(compactTimeMilliseconds().toFloat(),sNData),"soilN")
                            sensorViewModel.insertChartData(Entry(compactTimeMilliseconds().toFloat(),sPData),"soilP")
                            sensorViewModel.insertChartData(Entry(compactTimeMilliseconds().toFloat(),sKData),"soilK")
                            sensorViewModel.insertChartData(Entry(compactTimeMilliseconds().toFloat(),sPHData),"soilPH")

                        }
                        if (message.contains("\"soilMoisture\"",ignoreCase = false))
                        {
                            val sData: Float = format.decodeFromString<SoilMoistureQualitySensorSerializer>(message).soilMoisture //soil moisture sensor is separated from others

                            soilMoistureSensorArrayList.clear()
                            soilMoistureSensorArrayList.add(SensorData("soilMoisture",sData))
                            sensorViewModel.handleNewSensorDataListWithType(soilMoistureSensorArrayList,"soil_moisture")
                            sensorViewModel.insertChartData(Entry(compactTimeMilliseconds().toFloat(),sData),"soilMoisture")
                        }

                    } catch (e: Exception) {
                        when(e) {
                            is UnsupportedEncodingException -> {
                                Log.e(TAG, getString(R.string.encoding_error), e)
                            }
                            is SerializationException -> {
                                Log.e(TAG, getString(R.string.serialization_error), e)
                            }
                        }
                    }
                }
            }
        } catch (e:Exception) {
            Log.e(TAG, getString(R.string.subscription_error),e)
            btnSubscribe.isEnabled = true
        }
    }

    // handles the connection button clicks
    private fun onConnectClick() {
        btnConnect.isEnabled = false
        try {
            mqttManager.connect(
                credentialProvider
            ) { status, throwable ->
                when (status) {
                    AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connecting -> {
                        Log.d(TAG,getString(R.string.connecting))
                        requireActivity().runOnUiThread {
                            btnConnect.text = getString(R.string.connecting)
                        }
                    }
                    AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected -> {
                        Log.d(TAG,getString(R.string.connected))
                        requireActivity().runOnUiThread {
                            btnDisconnect.isEnabled = true
                            btnSubscribe.isEnabled = true
                            btnConnect.text = getString(R.string.connected)
                            btnDisconnect.text = getString(R.string.disconnect)
                        }
                    }
                    AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Reconnecting -> {
                        if (throwable != null) {
                            Log.e(TAG, getString(R.string.connection_error), throwable)
                            throwable.printStackTrace()
                        }
                    }
                    AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost -> {
                        if (throwable != null) {
                            Log.e(TAG, getString(R.string.connection_error), throwable)
                            throwable.printStackTrace()
                        }
                        Log.d(TAG, getString(R.string.disconnected))
                        requireActivity().runOnUiThread {
                            btnConnect.isEnabled = true
                            btnConnect.text = getString(R.string.connect)
                            btnDisconnect.isEnabled = false
                            btnSubscribe.isEnabled = false
                            btnDisconnect.text = getString(R.string.disconnected)
                        }

                    }
                    else -> {
                        Log.d(TAG, getString(R.string.disconnected))
                        requireActivity().runOnUiThread {
                            btnConnect.isEnabled = true
                            btnDisconnect.isEnabled = false
                            btnSubscribe.isEnabled = false
                            btnDisconnect.text = getString(R.string.disconnected)
                        }
                    }
                }
            }
        } catch (e:Exception) {
            Log.e(TAG, getString(R.string.connection_error), e)
            requireActivity().runOnUiThread {
                btnConnect.isEnabled = true
            }
        }
    }

    // handles prediction button clicks
    private fun onPredictClick() {
        try {
            // link to Flask server
            var urlString = "link-to-your-endpoint-goes-here.com/predict?"
            //bundling the data
            val bundledSensors = sensorViewModel.bundleMySensorData()
            for (sensorItem in sensorViewModel.sensorDataList) { // adding params to the url string
                urlString = urlString+sensorItem.sensorName+"="+sensorItem.sensorValue + "&"
            }
            urlString = urlString.dropLast(1) // drop the last &
            if (urlString.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                Log.i(TAG,urlString)
                val flaskPredictionFetch = OkHttpClient()
                val request = Request.Builder()
                    .url(urlString)
                    .build()
                flaskPredictionFetch.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }
                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful)
                            {
                                requireActivity().runOnUiThread {
                                    Toast.makeText(context, getString(R.string.prediction_failed), Toast.LENGTH_SHORT)
                                        .show()
                                    progressBar.visibility = View.INVISIBLE
                                }
                            }
                            else
                            {
                                val body = response.body!!.string()
                                val obj = format.decodeFromString<BundledPredictionValues>(body)
                                val now = Date()
                                // finally adding the prediction to the DB
                                val prId = predictionManager.addPrediction(
                                    bundledSensors.ph.toString(),
                                    bundledSensors.n.toString(),
                                    bundledSensors.p.toString(),
                                    bundledSensors.k.toString(),
                                    now).toString()
                                var count = 1
                                requireActivity().runOnUiThread {
                                    predictionUpdaterViewModel.sendMessage("updating to add prediction $prId")
                                    progressBar.visibility = View.INVISIBLE
                                }
                                for(crops in obj.predicted_crops)
                                {
                                    val cropValue = crops * 100 // multiple by 100 to get our prediction %
                                    predictionManager.addPredictionItem(count.toString(),cropValue,prId)
                                    count += 1
                                }
                                val df: DateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa", Locale.getDefault()) // current time in correct format
                                val nowFormatted = df.format(now)

                                val predictionItemFragment = DisplayPredictionItems.newInstance(prId, nowFormatted)
                                activity!!.supportFragmentManager.beginTransaction()
                                    .add(R.id.coordinator_container,predictionItemFragment,"Display Prediction Item Fragment")
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }
                    }
                })
            }
        } catch (e: java.lang.Exception) {
            Log.e(TAG, getString(R.string.failed_prediction), e)
            requireActivity().runOnUiThread {
                progressBar.visibility = View.INVISIBLE
            }
        }
    }

    // returns the current hour+minutes+seconds as seconds for the graphing
    private fun compactTimeMilliseconds() : Int {
        val date = Date() // your date
        val cal = Calendar.getInstance()
        cal.time = date
        val hour = cal.get(Calendar.HOUR)
        val minute = cal.get(Calendar.MINUTE)
        val seconds = cal.get(Calendar.SECOND)
        return (hour * 3600 + minute * 60 + seconds)
    }

    // view pager inner class for the sensor display
    private inner class SensorDataViewPagerAdapter(fragment: Fragment) :
        FragmentStateAdapter(fragment) {
        private val sensorFragmentList = arrayListOf<Fragment>()
        private val sensorFragmentListTitles = arrayListOf<String>()

        fun addFrag(fragment: Fragment, fragmentTitle: String) {
            sensorFragmentList.add(fragment)
            sensorFragmentListTitles.add(fragmentTitle)
        }

        fun getPageTitle(position: Int) : CharSequence {
            return sensorFragmentListTitles[position]
        }

        override fun getItemCount(): Int {
            return sensorFragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return sensorFragmentList[position]
        }

    }

}