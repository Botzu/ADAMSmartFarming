package edu.csusm.plantpredictionapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import edu.csusm.plantpredictionapp.fragments.PlantPredictionFragment

class PlantPredictionActivity : AppCompatActivity() {

    private lateinit var frameContainer: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        frameContainer = findViewById(R.id.coordinator_container)
        createPlantPredictionFragment()
    }

    // transitions to the fragment container
    private fun createPlantPredictionFragment() {
        if (isFinishing) {
            return
        }
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(frameContainer.id, PlantPredictionFragment())
        fragmentTransaction.commitAllowingStateLoss()
    }
}