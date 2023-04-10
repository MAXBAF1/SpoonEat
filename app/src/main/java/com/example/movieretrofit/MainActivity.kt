package com.example.movieretrofit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.movieretrofit.databinding.ActivityMainBinding
import com.example.movieretrofit.fragments.ui.AccountSettingsFragment
import com.example.movieretrofit.fragments.ui.HomeFragment
import com.example.movieretrofit.fragments.ui.SearchFragment
import com.example.movieretrofit.fragments.ui.StatisticsFragment
import com.justai.aimybox.components.AimyboxAssistantFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openFragment(HomeFragment.newInstance())
        onBottomNavClick()
    }

    private fun onBottomNavClick() {
        binding.bNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    openFragment(HomeFragment.newInstance())
                }
                R.id.statistics -> {
                    openFragment(StatisticsFragment.newInstance())
                }
                R.id.search -> {
                    openFragment(SearchFragment.newInstance())
                }
                R.id.settings -> {
                    openFragment(AccountSettingsFragment.newInstance())
                }
            }
            true
        }
    }

    private fun AppCompatActivity.openFragment(f: Fragment) {
        if (supportFragmentManager.fragments.isNotEmpty())
            if (supportFragmentManager.fragments[0].javaClass == f.javaClass) return

        supportFragmentManager.beginTransaction()
            .replace(R.id.placeHolder, f)
            .add(R.id.placeHolder, AimyboxAssistantFragment())
            .commit()
    }
}