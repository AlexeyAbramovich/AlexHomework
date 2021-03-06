package com.abra.homework_10.presentation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.abra.homework_10.R
import com.abra.homework_10.presentation.fragments.AddCityFragment
import com.abra.homework_10.presentation.fragments.ChooseCityFragment
import com.abra.homework_10.presentation.fragments.FragmentLoader

class MainActivity : AppCompatActivity(), FragmentLoader {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.commit {
            add(R.id.fragmentContainer, ChooseCityFragment::class.java,
                    bundleOf("isBackFromForecast" to false))
        }
    }

    override fun loadFragment(fragmentClass: Class<out Fragment>, bundle: Bundle) {
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, fragmentClass, bundle)
            addToBackStack(null)
        }
    }

    override fun loadDialogFragment() {
        val dialog = AddCityFragment()
        dialog.show(supportFragmentManager, "customDialog")
    }
}