package com.example.movieretrofit.fragments.ui

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
import com.example.movieretrofit.R
import com.example.movieretrofit.SignInActivity
import com.example.movieretrofit.data.Diet
import com.example.movieretrofit.databinding.FragmentAccountSettingsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.SignInAccount
import com.google.firebase.auth.FirebaseAuth

class AccountSettingsFragment : Fragment() {
    lateinit var binding: FragmentAccountSettingsBinding
    lateinit var firebase: com.example.movieretrofit.Firebase
    lateinit var diet: Diet
    lateinit var googleSignInClient: GoogleSignInClient


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
        createSaveDiet()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient= GoogleSignIn.getClient(requireActivity(),gso)

        binding.buttonSignOut.setOnClickListener {
            buttonSignOut()
        }
    }

    private fun createSaveDiet() {
        binding.sendDietToFirebase.setOnClickListener {
            firebase = com.example.movieretrofit.Firebase()
            firebase.sendUserDietToFirebase(diet)
            Log.e("item", "createSaveDiet in AccountSettingsFragment, fatCoeff is  ${diet.fatCoeff}")
            Toast.makeText(requireContext(), "Сохранено", Toast.LENGTH_SHORT).show()
        }
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

            diet = Diet()
            if (slider.values.isNotEmpty()) {
                diet.proteinCoeff = proteinCoefficient
                diet.fatCoeff = fatCoefficient.toFloat()
                diet.carbsCoeff = carbsCoefficient
            } else {
                diet.proteinCoeff = 25.0F
                diet.fatCoeff = 25.0F
                diet.carbsCoeff = 25.0F
            }

            Log.e("item", "diet is $diet")
            Log.e("item", "$proteinCoefficient, $carbsCoefficient, $fatCoefficient")
        }
    }

    private fun setUpUserPicture(imageView: ImageView, userName: TextView) {
            Glide.with(this)
            .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
            .into(imageView)
            userName.text = FirebaseAuth.getInstance().currentUser!!.displayName
    }

    private fun buttonSignOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            val intent= Intent(requireActivity(), SignInActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AccountSettingsFragment()
    }
}