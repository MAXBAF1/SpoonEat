package com.example.movieretrofit.fragments.ui

import android.content.Context
import android.content.Intent
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
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.R
import com.example.movieretrofit.SignInActivity
import com.example.movieretrofit.data.Diet
import com.example.movieretrofit.databinding.FragmentAccountSettingsBinding
import com.google.android.material.slider.RangeSlider
import com.google.firebase.auth.FirebaseAuth

class AccountSettingsFragment : Fragment() {
    lateinit var binding: FragmentAccountSettingsBinding
    lateinit var firebase: Firebase
    private var diet: Diet = Diet()
    lateinit var rangeSlider: RangeSlider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebase = Firebase()
        setUpUserPicture(binding.avatar, binding.tvUsername)
        createRangeSlider()
        createSaveDiet()
        buttonSignOut()

        chooseDiet()
    }

    private fun chooseDiet() {
        binding.dietClassic.setOnClickListener { updateDiet(Diet(30, 20, 50)) }
        binding.dietKeto.setOnClickListener { updateDiet(Diet(20, 75, 5)) }
        binding.dietProtein.setOnClickListener { updateDiet(Diet(55, 15, 20)) }
    }

    private fun updateDiet(diet: Diet) {
        rangeSlider.values = listOf(diet.proteinCf.toFloat(), diet.proteinCf.toFloat() + diet.fatCf)
        updateTvCf(diet)
    }

    private fun createRangeSlider() {
        rangeSlider = binding.slider

        rangeSlider.valueFrom = 0f
        rangeSlider.valueTo = 100f
        rangeSlider.stepSize = 5f

        firebase.getUserDietFromFirebase {
            rangeSlider.values = listOf(it.proteinCf.toFloat(), it.proteinCf.toFloat() + it.fatCf)
        }

        rangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values // Get the current values
            Log.e("item", values.toString())
            val proteinCf = values.first()
            val fatCf = values.last() - values.first()
            val carbCf = (100 - values.last())

            diet = Diet(proteinCf.toInt(), fatCf.toInt(), carbCf.toInt())
            if (activity != null) updateTvCf(diet)

            Log.e("item", "diet is $diet")
            Log.e("item", "$proteinCf, $carbCf, $fatCf")
        }
    }

    private fun createSaveDiet() {
        binding.sendDietToFirebase.setOnClickListener {
            Log.e(
                "item", "createSaveDiet in AccountSettingsFragment, fatCf is  ${diet.fatCf}"
            )
            if (diet.proteinCf != 0 && diet.fatCf != 0 && diet.carbsCf != 0) {
                firebase.sendUserDietToFirebase(diet)
                Toast.makeText(requireContext(), "Сохранено", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(requireContext(), "Не может быть 0%!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTvCf(diet: Diet) {
        val proteinText = "${getString(R.string.protein)} ${diet.proteinCf}%"
        val fatText = "${getString(R.string.fat)} ${diet.fatCf}%"
        val carbText = "${getString(R.string.carb)} ${diet.carbsCf}%"
        binding.tvProtein.text = proteinText
        binding.tvFat.text = fatText
        binding.tvCarb.text = carbText
    }

    private fun setUpUserPicture(imageView: ImageView, userName: TextView) {
        Glide.with(this).load(FirebaseAuth.getInstance().currentUser?.photoUrl).into(imageView)
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