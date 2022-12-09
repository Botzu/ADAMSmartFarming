package edu.csusm.plantpredictionapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import edu.csusm.plantpredictionapp.R

// fragment view handler with viewpager to hold the TabLayout for our history and menu fragment
class PlantPredictionFragment: Fragment() {
    private lateinit var viewFragment: View
    private lateinit var plantPredictionViewPager: ViewPager2
    private lateinit var plantPredictionTabLayout: TabLayout
    private lateinit var plantPredictionViewPagerAdapter : PredictionViewPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        viewFragment = inflater.inflate(R.layout.pager_fragment,container,false)
        return viewFragment
    }

    // attaching the tabs
    override fun onAttach(context: Context) {
        super.onAttach(context)
        plantPredictionViewPagerAdapter = PredictionViewPagerAdapter(this)
        DisplayMenu(context)
            .let { plantPredictionViewPagerAdapter.addFrag(it, "Menu") }
        DisplayPrediction(context)
            .let { plantPredictionViewPagerAdapter.addFrag(it, "History") }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        plantPredictionTabLayout = viewFragment.findViewById(R.id.tab_layout)
        plantPredictionViewPager = viewFragment.findViewById(R.id.view_pager)
        plantPredictionViewPager.adapter = plantPredictionViewPagerAdapter
        TabLayoutMediator(plantPredictionTabLayout, plantPredictionViewPager) { tab, position ->
            tab.text = plantPredictionViewPagerAdapter.getPageTitle(position)
        }.attach()
        setUpTabIcon()
    }

    private fun setUpTabIcon() {
        plantPredictionTabLayout.getTabAt(0)?.setIcon(R.drawable.home_icon)
        plantPredictionTabLayout.getTabAt(1)?.setIcon(R.drawable.history_icon)
    }

    private inner class PredictionViewPagerAdapter(fragment: Fragment) :
        FragmentStateAdapter(fragment) {
        private val fragmentList = arrayListOf<Fragment>()
        private val fragmentListTitle = arrayListOf<String>()

        fun addFrag(fragment: Fragment, fragmentTitle: String)
        {
            fragmentList.add(fragment)
            fragmentListTitle.add(fragmentTitle)
        }

        fun getPageTitle(position: Int) : CharSequence {
            return fragmentListTitle[position]
        }

        override fun getItemCount(): Int {
            return fragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }


    }
}