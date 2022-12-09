package edu.csusm.plantpredictionapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import edu.csusm.plantpredictionapp.R
import edu.csusm.plantpredictionapp.utils.SensorUtils
import edu.csusm.plantpredictionapp.viewmodels.SensorDataViewModel
import java.util.*
import kotlin.math.floor


class SensorGraphFragment : Fragment() {

    private lateinit var displayChartView: View
    private lateinit var mLineChart: LineChart
    private lateinit var lineChartYValues : ArrayList<Entry>
    private lateinit var backBtn : Button
    private lateinit var textTitle : TextView
    private lateinit var fullName: String
    private lateinit var sensorType : String
    private lateinit var sensorUtils: SensorUtils
    private val sensorViewModel: SensorDataViewModel by activityViewModels()

    companion object {
        @JvmStatic
        fun newInstance(sensorType: String) = SensorGraphFragment().apply {
            arguments = Bundle().apply {
                putString("sensor_type",sensorType)
            }
        }
    }
    // passing in the values prediction_id and prediction_time
    override fun onAttach(context: Context) {
        super.onAttach(context)
        sensorUtils = SensorUtils(context)
        arguments?.getString("sensor_type")?.let {
            sensorType = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        displayChartView = inflater.inflate(R.layout.sensor_chart, container, false)
        return displayChartView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLineChart = displayChartView.findViewById(R.id.sensor_line_chart)
        backBtn = displayChartView.findViewById(R.id.return_graph_button)
        textTitle = displayChartView.findViewById(R.id.chart_page_title)
        fullName = sensorUtils.getFullSensorName(sensorType).toString()
        val fullTitleText = "Graphing $fullName Data"
        textTitle.text = fullTitleText
        mLineChart.xAxis.labelRotationAngle = -20F
        mLineChart.xAxis.valueFormatter = XAxisValueFormatter()
        mLineChart.axisLeft.setDrawLabels(false)
        mLineChart.xAxis.spaceMin = 40F
        mLineChart.extraBottomOffset = 10F
        mLineChart.description.isEnabled = false
        mLineChart.isDragEnabled = true
        mLineChart.setScaleEnabled(true)
        lineChartYValues = ArrayList()
        setupGraph()

        backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        when (sensorType) {
            "atmosphericTemp" -> {
                sensorViewModel.atmosphericTempData.observe(viewLifecycleOwner) {
                    lineChartYValues = sensorViewModel.atmosphericTempDataList
                    setupGraph()
                    mLineChart.notifyDataSetChanged()
                    mLineChart.invalidate()
                }
            }
            "atmosphericHumidity" -> {
                sensorViewModel.atmosphericHumidityData.observe(viewLifecycleOwner) {
                    lineChartYValues = sensorViewModel.atmosphericHumidityDataList
                    setupGraph()
                    mLineChart.notifyDataSetChanged()
                    mLineChart.invalidate()
                }
            }
            "soilPH" -> {
                sensorViewModel.soilPHChartData.observe(viewLifecycleOwner) {
                    lineChartYValues = sensorViewModel.soilPHChartDataList
                    setupGraph()
                    mLineChart.notifyDataSetChanged()
                    mLineChart.invalidate()
                }
            }
            "soilN" -> {
                sensorViewModel.soilNChartData.observe(viewLifecycleOwner) {
                    lineChartYValues = sensorViewModel.soilNChartDataList
                    setupGraph()
                    mLineChart.notifyDataSetChanged()
                    mLineChart.invalidate()
                }
            }
            "soilP" -> {
                sensorViewModel.soilPChartData.observe(viewLifecycleOwner) {
                    lineChartYValues = sensorViewModel.soilPChartDataList
                    setupGraph()
                    mLineChart.notifyDataSetChanged()
                    mLineChart.invalidate()
                }
            }
            "soilK" -> {
                sensorViewModel.soilKChartData.observe(viewLifecycleOwner) {
                    lineChartYValues = sensorViewModel.soilKChartDataList
                    setupGraph()
                    mLineChart.notifyDataSetChanged()
                    mLineChart.invalidate()
                }
            }
            "waterTurbidity" -> {
                sensorViewModel.waterTurbidityData.observe(viewLifecycleOwner) {
                    lineChartYValues = sensorViewModel.waterTurbidityDataList
                    setupGraph()
                    mLineChart.notifyDataSetChanged()
                    mLineChart.invalidate()
                }
            }
            "waterPH" -> {
                sensorViewModel.waterPHData.observe(viewLifecycleOwner) {
                    lineChartYValues = sensorViewModel.waterPHDataList
                    setupGraph()
                    mLineChart.notifyDataSetChanged()
                    mLineChart.invalidate()
                }
            }
            "soilMoisture" -> {
                sensorViewModel.soilMoistureChartData.observe(viewLifecycleOwner) {
                    lineChartYValues = sensorViewModel.soilMoistureChartDataList
                    setupGraph()
                    mLineChart.notifyDataSetChanged()
                    mLineChart.invalidate()
                }
            }
        }
    }

    private fun setupGraph() {
        val set1 = LineDataSet(lineChartYValues, "$fullName data Set")
        set1.fillAlpha = 110
        val dataSets : ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(set1)

        val data = LineData(dataSets)
        mLineChart.data = data
    }

    class XAxisValueFormatter : IndexAxisValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val hours = floor(value/3600) // converting the values to string
            println("the hours was ${value/3600}")
            val minutes = floor((value - hours * 3600)/60)
            val seconds = ((value - hours * 3600) % 60)
            val date = Date() // your date
            val cal = Calendar.getInstance()
            cal.time = date
            return if(cal.get(Calendar.AM_PM) == 1) {
                String.format("%.0f",hours)+":"+String.format("%02.0f",minutes)+":"+String.format("%02.0f",seconds) + "PM"
            } else String.format("%.0f",hours)+":"+String.format("%02.0f",minutes)+":"+String.format("%02.0f",seconds) + "AM"
        }
    }


}
