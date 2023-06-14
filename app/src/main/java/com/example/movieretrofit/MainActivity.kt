package com.example.movieretrofit

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.movieretrofit.databinding.ActivityMainBinding
import com.example.movieretrofit.fragments.ui.accountSettingsFragment.AccountSettingsFragment
import com.example.movieretrofit.fragments.ui.HomeFragment
import com.example.movieretrofit.fragments.ui.SearchFragment
import com.example.movieretrofit.fragments.ui.StatisticsFragment
import com.example.movieretrofit.fragments.ui.chat.VoiceFragment
import com.justai.aimybox.components.AimyboxAssistantFragment


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bNav.background = null

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.green)

        openFragment(HomeFragment.newInstance())
        onBottomNavClick()

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            1
        )
    }

    private fun onBottomNavClick() {
        binding.bNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.chat -> {
                    openFragment(VoiceFragment.newInstance())
                }
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
        if (supportFragmentManager.fragments.isEmpty())
            supportFragmentManager.beginTransaction()
                .replace(R.id.placeHolder, f)
                .add(R.id.placeHolder, AimyboxAssistantFragment())
                .commit()
        else
            supportFragmentManager.beginTransaction()
                .replace(R.id.placeHolder, f)
                .add(R.id.placeHolder, AimyboxAssistantFragment())
                .addToBackStack(null)
                .commit()
    }

    override fun onBackPressed() {
        val assistantFragment = (supportFragmentManager.findFragmentById(R.id.placeHolder)
                as? AimyboxAssistantFragment)
        if (assistantFragment?.onBackPressed() != true) super.onBackPressed()
    }
}