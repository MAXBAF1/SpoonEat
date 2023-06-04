package com.example.movieretrofit.fragments.ui

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.R
import com.example.movieretrofit.SignInActivity
import com.example.movieretrofit.charts.PieCharts
import com.example.movieretrofit.data.Diet
import com.example.movieretrofit.databinding.FragmentAccountSettingsBinding
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.slider.RangeSlider
import com.google.firebase.auth.FirebaseAuth

class AccountSettingsFragment : Fragment() {
    lateinit var binding: FragmentAccountSettingsBinding
    lateinit var firebase: Firebase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebase = Firebase()
        setUpUserPicture(binding.avatar, binding.tvUsername)

        buttonSignOut()

        binding.buttonDiet.setOnClickListener {
            val fragment = DietFragment.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.diet_fragment, fragment)
                .addToBackStack(null)
                .commit()
        }
    }


    private fun setUpUserPicture(imageView: ImageView, userName: TextView) {
        Glide.with(this)
            .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
            .transform(RoundedCorners(300))
            .into(imageView)
        userName.text = FirebaseAuth.getInstance().currentUser!!.displayName
    }

    private fun buttonSignOut() {
        binding.buttonSignOut.setOnClickListener {
            val firebase = Firebase()
            firebase.signOut()
            val intent = Intent(activity, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            requireContext().getSharedPreferences("MY_APP", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("IS_LOGGED_IN", false)
                .apply()
            startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AccountSettingsFragment()
    }
}