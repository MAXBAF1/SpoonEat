package com.example.movieretrofit.fragments.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.movieretrofit.databinding.FragmentAccountSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class AccountSettingsFragment : Fragment() {
    lateinit var binding: FragmentAccountSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpUserPicture(binding.avatar, binding.tvUsername)
        createSeekBar()
    }

    private fun createSeekBar() {
        val rangeSlider = binding.slider
        rangeSlider.valueFrom = 0f
        rangeSlider.valueTo = 100f

        rangeSlider.values = listOf(25f, 75f)

        rangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values // Get the current values
            Log.e("item", values.toString())
            val proteinCoefficient = values.first()
            val carbsCoefficient = values.last() - values.first()
            val fatCoefficient = 100.0 - values.last()

            Log.e("item", "$proteinCoefficient, $carbsCoefficient, $fatCoefficient")


        }
    }

    private fun setUpUserPicture(imageView: ImageView, userName: TextView) {
            Glide.with(this)
            .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
            .into(imageView)
            userName.text = FirebaseAuth.getInstance().currentUser!!.displayName
    }

//    private fun buttonSignOut() {
//        var firebase = Firebase()
//        firebase.signOut()
//        val intent = Intent(activity, SignInActivity::class.java)
//        startActivity(intent)
//    }

    companion object {
        @JvmStatic
        fun newInstance() = AccountSettingsFragment()

    }
}