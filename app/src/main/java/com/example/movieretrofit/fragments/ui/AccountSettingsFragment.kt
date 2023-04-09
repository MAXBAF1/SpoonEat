package com.example.movieretrofit.fragments.ui

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
import com.example.movieretrofit.R
import com.example.movieretrofit.data.Diet
import com.example.movieretrofit.databinding.FragmentAccountSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class AccountSettingsFragment : Fragment() {
    lateinit var binding: FragmentAccountSettingsBinding
    lateinit var firebase: com.example.movieretrofit.Firebase
    private var diet: Diet = Diet()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebase = com.example.movieretrofit.Firebase()
        setUpUserPicture(binding.avatar, binding.tvUsername)
        createRangeSlider()
        createSaveDiet()
    }

    private fun createRangeSlider() {
        val rangeSlider = binding.slider
        rangeSlider.valueFrom = 0f
        rangeSlider.valueTo = 100f
        rangeSlider.stepSize = 5f

        firebase.getUserDietFromFirebase {
            rangeSlider.values = listOf(it.proteinCoeff, it.proteinCoeff + it.fatCoeff)
        }

        rangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values // Get the current values
            Log.e("item", values.toString())
            val proteinCf = values.first()
            val fatCf = values.last() - values.first()
            val carbCf = (100.0 - values.last()).toFloat()

            updateTvCf(proteinCf, fatCf, carbCf)

            diet = Diet(proteinCf, fatCf, carbCf)

            Log.e("item", "diet is $diet")
            Log.e("item", "$proteinCf, $carbCf, $fatCf")
        }
    }

    private fun createSaveDiet() {
        binding.sendDietToFirebase.setOnClickListener {
            Log.e(
                "item",
                "createSaveDiet in AccountSettingsFragment, fatCoeff is  ${diet.fatCoeff}"
            )
            if (diet.proteinCoeff != 0f && diet.fatCoeff != 0f && diet.carbsCoeff != 0f) {
                firebase.sendUserDietToFirebase(diet)
                Toast.makeText(requireContext(), "Сохранено", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(requireContext(), "Не может быть 0%!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTvCf(proteinCf: Float, fatCf: Float, carbCf: Float) {
        val proteinText = "${getString(R.string.protein)} ${proteinCf.toInt()}%"
        val fatText = "${getString(R.string.fat)} ${fatCf.toInt()}%"
        val carbText = "${getString(R.string.carb)} ${carbCf.toInt()}%"
        binding.tvProtein.text = proteinText
        binding.tvFat.text = fatText
        binding.tvCarb.text = carbText
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