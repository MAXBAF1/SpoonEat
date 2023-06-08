package com.example.movieretrofit.fragments.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.slider.RangeSlider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_diet.*

class AccountSettingsFragment : Fragment() {
    lateinit var binding: FragmentAccountSettingsBinding
    lateinit var firebase: Firebase
    lateinit var rangeSlider: RangeSlider
    private var diet: Diet = Diet()

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
            val alertDialog = AlertDialog.Builder(context).create()
            val layoutInflater = LayoutInflater.from(context)
            val view = layoutInflater.inflate(R.layout.fragment_diet, null)
            alertDialog.setView(view)

            rangeSlider = view!!.findViewById<RangeSlider>(R.id.slider)


            val dietStandart = view.findViewById<LinearLayout>(R.id.diet_standart)
            val dietKeto = view.findViewById<LinearLayout>(R.id.diet_keto)
            val dietProtein = view.findViewById<LinearLayout>(R.id.diet_protein)
            chooseDiet(dietStandart, dietKeto, dietProtein)

            val dietStandardChart = view.findViewById<PieChart>(R.id.diet_standard_Chart)
            val dietKetoChart = view.findViewById<PieChart>(R.id.diet_keto_Chart)
            val dietProteinChart = view.findViewById<PieChart>(R.id.diet_protein_Chart)
            setPieCharts(dietStandardChart, dietKetoChart, dietProteinChart)

            alertDialog.window?.setBackgroundDrawableResource(R.drawable.rounded_background)
            alertDialog.show()
        }
    }


    private fun setPieCharts(dietStandardChart: PieChart, dietKetoChart: PieChart, dietProteinChart: PieChart) {
        val standardEntries = arrayListOf(PieEntry(30f), PieEntry(30f), PieEntry(40f))
        context?.let { PieCharts(it, dietStandardChart).setPieChart(standardEntries) }

        val ketoEntries = arrayListOf(PieEntry(20f), PieEntry(75f), PieEntry(5f))
        context?.let { PieCharts(it, dietKetoChart).setPieChart(ketoEntries) }

        val proteinEntries = arrayListOf(PieEntry(50f), PieEntry(20f), PieEntry(30f))
        context?.let { PieCharts(it, dietProteinChart).setPieChart(proteinEntries) }
    }

    private fun chooseDiet(dietStandart: LinearLayout, dietKeto: LinearLayout, dietProtein: LinearLayout) {
        dietStandart.setOnClickListener { updateDiet(Diet(30, 30, 40)) }
        dietKeto.setOnClickListener { updateDiet(Diet(20, 75, 5)) }
        dietProtein.setOnClickListener { updateDiet(Diet(50, 20, 30)) }
    }

    private fun updateDiet(diet: Diet) {
        rangeSlider.values = listOf(diet.proteinCf.toFloat(), diet.proteinCf.toFloat() + diet.fatCf)
        //updateTvCf(diet)
    }

    private fun createSaveDiet() {
        val sendDietToFirebase = requireActivity().findViewById<Button>(R.id.send_diet_to_firebase)
        sendDietToFirebase.setOnClickListener {
            Log.e("item", "createSaveDiet in AccountSettingsFragment, fatCf is  ${diet.fatCf}")
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
        tvProtein.text = proteinText
        tvFat.text = fatText
        tvCarb.text = carbText
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