package com.example.movieretrofit

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.movieretrofit.databinding.ActivityMainBinding
import com.example.movieretrofit.fragments.ui.AccountSettingsFragment
import com.example.movieretrofit.fragments.ui.HomeFragment
import com.example.movieretrofit.fragments.ui.SearchFragment
import com.example.movieretrofit.fragments.ui.StatisticsFragment
import com.justai.aimybox.components.AimyboxAssistantFragment
import com.justai.aimybox.components.AimyboxAssistantViewModel


class MainActivity : AppCompatActivity(){
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bNav.background = null
        binding.bNav.menu.getItem(2).isEnabled = false

        openFragment(HomeFragment.newInstance())
        onBottomNavClick()

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO), 1)
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
        supportFragmentManager.fragments.forEach {
            if (it.javaClass == f.javaClass) return
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.placeHolder, f)
            .add(R.id.placeHolder, AimyboxAssistantFragment())
            .setReorderingAllowed(true)
            .commit()
    }

    override fun onBackPressed() {
        val assistantFragment = (supportFragmentManager.findFragmentById(R.id.placeHolder)
                as? AimyboxAssistantFragment)
        if (assistantFragment?.onBackPressed() != true) super.onBackPressed()
    }
}