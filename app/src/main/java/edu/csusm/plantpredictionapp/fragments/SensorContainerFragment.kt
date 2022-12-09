package edu.csusm.plantpredictionapp.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import edu.csusm.plantpredictionapp.R
import edu.csusm.plantpredictionapp.SensorData
import edu.csusm.plantpredictionapp.utils.SensorUtils
import edu.csusm.plantpredictionapp.viewmodels.SensorDataViewModel

// a generic fragment to display the recyclerview for the sensor groups
class SensorContainerFragment: Fragment()  {
    private lateinit var displaySensorsView : View
    private lateinit var sensorQualitySensorRecyclerView: RecyclerView
    private lateinit var sensorGroupName: String
    private var sensorDataList = arrayListOf<SensorData>()
    private val sensorViewModel: SensorDataViewModel by activityViewModels()

    companion object {
        @JvmStatic
        fun newInstance(sensorGroupName: String) = SensorContainerFragment().apply {
            arguments = Bundle().apply {
                putString("sensor_group_name",(sensorGroupName))
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getString("sensor_group_name")?.let {
            sensorGroupName = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        displaySensorsView = inflater.inflate(R.layout.recycler_view_fragment, container, false)
        return displaySensorsView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorQualitySensorRecyclerView = displaySensorsView.findViewById(R.id.content_recycler_view)
        sensorQualitySensorRecyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        when(sensorGroupName) {
            "Atmospheric" -> {
                sensorViewModel.mAtmosphereQualitySensorList.observe(viewLifecycleOwner) {
                    updateSensorDataList()
                }
            }
            "Soil" -> {
                sensorViewModel.mSoilQualitySensorList.observe(viewLifecycleOwner) {
                    updateSensorDataList()
                }
                sensorViewModel.mSoilMoistureQualitySensor.observe(viewLifecycleOwner) {
                    updateSensorDataList()
                }
            }
            "Water" -> {
                sensorViewModel.mWaterQualitySensorList.observe(viewLifecycleOwner) {
                    updateSensorDataList()
                }
            }
        }
    }

    private fun setupAdapter() {
        if (isAdded)
        {
            sensorQualitySensorRecyclerView.adapter = SensorDataItemListAdapter(sensorDataList)
        }
    }

    // updating the sensor Data
    private fun updateSensorDataList() {
        when (sensorGroupName) {
            "Atmospheric" -> {
                sensorDataList = sensorViewModel.mAtmosphereQualitySensorList.value as ArrayList<SensorData>
            }
            "Soil" -> {
                sensorDataList = sensorViewModel.bundleSoilSensorData()
            }
            "Water" -> {
                sensorDataList = sensorViewModel.mWaterQualitySensorList.value as ArrayList<SensorData>
            }
        }
        setupAdapter()
    }

    private inner class SensorDataItemHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.recycler_view_sensor_items, parent,false)),
        View.OnClickListener {
        var currentSensorData : SensorData? = null
        private val sensorName : TextView
        private val sensorValue : TextView
        private val sensorImage : ImageView
        private val sensorInfoImage : ImageView
        private val sensorUtils = context?.let { SensorUtils(it) }

        fun bindClassItem(sensorData: SensorData) {
            currentSensorData = sensorData
            if(sensorUtils!!.checkIfNeedsSensor(sensorData.sensorName)) {
                sensorInfoImage.setImageResource(sensorUtils.setInfoIcon(currentSensorData!!.sensorName,
                    currentSensorData!!.sensorValue))
                sensorInfoImage.visibility = View.VISIBLE
            }
            val sensorText = String.format("%.2f",currentSensorData!!.sensorValue) +" "+ sensorUtils.getSensorUnits(sensorData.sensorName)
            sensorName.text = sensorUtils.getFullSensorName(sensorData.sensorName)
            sensorValue.text = sensorText
            sensorUtils.getSensorImageResource(currentSensorData!!.sensorName)
                ?.let { sensorImage.setImageResource(it) }
        }

        override fun onClick(v: View?) {
            val sensorGraphFragment = SensorGraphFragment.newInstance(currentSensorData!!.sensorName)
            activity!!.supportFragmentManager.beginTransaction()
                .add(R.id.coordinator_container,sensorGraphFragment,"Displaying a graph")
                .addToBackStack(null)
                .commit()
        }

        init {
            itemView.setOnClickListener(this)
            sensorName = itemView.findViewById(R.id.sensor_name)
            sensorValue = itemView.findViewById(R.id.sensor_value)
            sensorImage = itemView.findViewById(R.id.sensor_image_view)
            sensorInfoImage = itemView.findViewById(R.id.sensor_icon_info)
            sensorInfoImage.setOnClickListener {
                val text = sensorUtils!!.setInfoIconText(currentSensorData!!.sensorName, currentSensorData!!.sensorValue)

                val snack = Snackbar.make(displaySensorsView.findViewById(R.id.icon_coordinator), text, Snackbar.LENGTH_INDEFINITE)
                    .setTextColor(Color.BLACK)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.snackBarColor))
                    .setActionTextColor(resources.getColor(R.color.snackBarActionColor))
                    .setAction("Dismiss") {
                        // dismiss by default
                    }
                val snackBarView: View = snack.view
                snackBarView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                snack.show()
            }
        }
    }

    private inner class SensorDataItemListAdapter(private val sensorDataList: List<SensorData>) :
        RecyclerView.Adapter<SensorDataItemHolder?>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorDataItemHolder {
            val layoutInflater = LayoutInflater.from(activity)
            return SensorDataItemHolder(layoutInflater, parent)
        }
        override fun onBindViewHolder(holder: SensorDataItemHolder, position: Int) {
            val sensorDataItemHolderItem = sensorDataList[position]
            holder.bindClassItem(sensorDataItemHolderItem)
            holder.setIsRecyclable(false)
        }
        override fun getItemCount(): Int {
            return sensorDataList.size
        }
    }
}