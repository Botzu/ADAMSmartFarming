package edu.csusm.plantpredictionapp.fragments

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.csusm.plantpredictionapp.PredictionItems
import edu.csusm.plantpredictionapp.R
import edu.csusm.plantpredictionapp.database.PredictionManager
import edu.csusm.plantpredictionapp.utils.CropUtils
import java.util.*

// Fragment to display the prediction items
class DisplayPredictionItems : Fragment() {
    private lateinit var displayPredictionItemView: View
    private lateinit var displayItemRecyclerView: RecyclerView
    private lateinit var predictionItemTimeView: TextView
    private var predictionItemList = arrayListOf<PredictionItems>()
    private lateinit var predictionItemManager : PredictionManager
    private lateinit var predictionId: String
    private lateinit var predictionTime: String
    private lateinit var returnButton: Button

    companion object {
        @JvmStatic
        fun newInstance(predictionID: String, predictionItemTime: String) = DisplayPredictionItems().apply {
            arguments = Bundle().apply {
                putString("prediction_id",predictionID)
                putString("prediction_time",predictionItemTime)
            }
        }
    }
    // passing in the values prediction_id and prediction_time
    override fun onAttach(context: Context) {
        super.onAttach(context)
        predictionItemManager = PredictionManager(context)
        arguments?.getString("prediction_id")?.let {
            predictionId = it
        }
        arguments?.getString("prediction_time")?.let {
            predictionTime = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        displayPredictionItemView = inflater.inflate(R.layout.prediction_item_fragment, container, false)
        return displayPredictionItemView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayItemRecyclerView = displayPredictionItemView.findViewById(R.id.recycler_prediction_items)
        displayItemRecyclerView.layoutManager = LinearLayoutManager(activity)
        predictionItemList = predictionItemManager.getPredictionItemsByPredictionID(predictionId) as ArrayList<PredictionItems>
        println(predictionId)
        predictionItemTimeView = displayPredictionItemView.findViewById(R.id.prediction_time_value)
        predictionItemTimeView.text = predictionTime
        returnButton = displayPredictionItemView.findViewById(R.id.return_btn)
        returnButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        setupAdapter()
    }

    private fun setupAdapter() {
        if(isAdded)
        {
            displayItemRecyclerView.adapter = PredictionItemListAdapter(predictionItemList)
        }
    }

    private inner class PredictionItemListItemHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.prediction_item_card_view,parent,false)),
        View.OnClickListener {
        var currentPredictionItem : PredictionItems? = null
        private val cropName : TextView
        private val cropType : TextView
        private val cropImage : ImageView
        private val predictionConstraint : ConstraintLayout
        private val cropInfo : ImageView
        private val cropPredictionPercent : TextView
        private val cropUtils = context?.let { CropUtils(it) }

        fun bindClassItem(predictionItems: PredictionItems)
        {
            currentPredictionItem = predictionItems
            val cropPercentText = currentPredictionItem?.prPerc + getString(R.string.percentage_val)
            cropUtils?.getImageResourceID(currentPredictionItem!!.itemID)
                ?.let { cropImage.setImageResource(it) }
            cropPredictionPercent.text = cropPercentText
            cropName.text = cropUtils?.getCropName(currentPredictionItem!!.itemID)
            cropType.text = cropUtils?.getCropType(currentPredictionItem!!.itemID)
            if(layoutPosition <= 2) // Highlight the top 3
            {
                predictionConstraint.setBackgroundResource(R.drawable.prediction_background)
                cropName.setTypeface(null, Typeface.BOLD)
                cropType.setTypeface(null, Typeface.BOLD)
                cropPredictionPercent.setTypeface(null, Typeface.BOLD)
            }

        }

        override fun onClick(v: View?) {
            println("clicked an item card")
        }


        init {
            itemView.setOnClickListener(this)
            cropName = itemView.findViewById(R.id.crop_name)
            cropType = itemView.findViewById(R.id.crop_type)
            cropImage = itemView.findViewById(R.id.prediction_image)
            cropInfo = itemView.findViewById(R.id.info_icon_desc)
            predictionConstraint = itemView.findViewById(R.id.prediction_constraint)
            cropPredictionPercent = itemView.findViewById(R.id.prediction_item_prediction_precentage)
            cropInfo.setOnClickListener {
                //println("clicked an item desc at position"+displayItemRecyclerView.getChildLayoutPosition(itemView))
                //Toast.makeText(itemView.context, "Clicked the item description at pos f", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private inner class PredictionItemListAdapter(private val predictionList: List<PredictionItems>) :
        RecyclerView.Adapter<PredictionItemListItemHolder?>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionItemListItemHolder {
            val layoutInflater = LayoutInflater.from(activity)
            return PredictionItemListItemHolder(layoutInflater, parent)
        }

        override fun onBindViewHolder(holder: PredictionItemListItemHolder, position: Int) {
            val predictionItemHolderItem = predictionList[position]
            holder.bindClassItem(predictionItemHolderItem)
            holder.setIsRecyclable(false)
        }

        override fun getItemCount(): Int {
            return predictionList.size
        }
    }
}