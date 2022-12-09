package edu.csusm.plantpredictionapp.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.csusm.plantpredictionapp.Prediction
import edu.csusm.plantpredictionapp.R
import edu.csusm.plantpredictionapp.database.PredictionManager
import edu.csusm.plantpredictionapp.viewmodels.PredictionUpdaterViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

// Fragment to display the predictions
class DisplayPrediction(context: Context) : Fragment() {

    private lateinit var displayPredictionView: View
    private lateinit var displayRecyclerView: RecyclerView
    private var predictionList = arrayListOf<Prediction>()
    private var predictionManager : PredictionManager = PredictionManager(context)
    private val predictionUpdaterViewModel: PredictionUpdaterViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        displayPredictionView = inflater.inflate(R.layout.recycler_view_fragment, container, false)
        return displayPredictionView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayRecyclerView = displayPredictionView.findViewById(R.id.content_recycler_view)
        displayRecyclerView.layoutManager = LinearLayoutManager(activity)
        predictionList = predictionManager.getPredictions() as ArrayList<Prediction>
        predictionUpdaterViewModel.message.observe(viewLifecycleOwner) {
            Log.i(TAG, it) // Logging we received an update request
            updatePredictionList()
        }
        setupAdapter()
    }

    private fun setupAdapter() {
        if(isAdded)
        {
            displayRecyclerView.adapter = PredictionListAdapter(predictionList)
        }
    }

    private fun updatePredictionList() {
        predictionList = predictionManager.getPredictions() as ArrayList<Prediction>
        setupAdapter()
    }

    private inner class PredictionListItemHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.prediction_card_view,parent,false)),
        View.OnClickListener {
        private val soilPHText : TextView
        private val nitrogenText : TextView
        private val potassiumText : TextView
        private val phosphorousText : TextView
        private val predictionTime : TextView
        private var predictionTimeText : String
        private val deleteItemImage : ImageView
        var currentPrediction : Prediction? = null

        fun bindClassItem(prediction: Prediction)
        {
            currentPrediction = prediction
            soilPHText.text = currentPrediction?.soilPH
            nitrogenText.text = currentPrediction?.nitroVal
            potassiumText.text = currentPrediction?.potassiumVal
            phosphorousText.text = currentPrediction?.phosphorousVal
            val df: DateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa", Locale.getDefault())
            predictionTimeText = currentPrediction?.date?.let { df.format(it) }.toString()
            predictionTime.text = predictionTimeText
        }

        override fun onClick(v: View?) {
            val predictionItemFragment = DisplayPredictionItems.newInstance(currentPrediction!!.predictionId, predictionTimeText)

            activity!!.supportFragmentManager.beginTransaction()
                .add(R.id.coordinator_container,predictionItemFragment,"Display Prediction Item Fragment")
                .addToBackStack(null)
                .commit()

        }

        init {
            itemView.setOnClickListener(this)
            soilPHText = itemView.findViewById(R.id.soil_ph_val)
            deleteItemImage = itemView.findViewById(R.id.delete_item_view)
            nitrogenText = itemView.findViewById(R.id.ntrogen_value)
            potassiumText = itemView.findViewById(R.id.potassium_val)
            phosphorousText = itemView.findViewById(R.id.phosphorous_value)
            predictionTime = itemView.findViewById(R.id.item_prediction_time)
            predictionTimeText = getString(R.string.date_place_holder)
            deleteItemImage.setOnClickListener {
                predictionManager.removePrediction(currentPrediction!!.predictionId)
                updatePredictionList()
            }
        }
    }

    private inner class PredictionListAdapter(private val predictionList: List<Prediction>) :
        RecyclerView.Adapter<PredictionListItemHolder?>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionListItemHolder {
            val layoutInflater = LayoutInflater.from(activity)
            return PredictionListItemHolder(layoutInflater, parent)
        }

        override fun onBindViewHolder(holder: PredictionListItemHolder, position: Int) {
            val predictionHolderItem = predictionList[position]
            holder.bindClassItem(predictionHolderItem)
            holder.setIsRecyclable(false)
        }

        override fun getItemCount(): Int {
            return predictionList.size
        }
    }

}