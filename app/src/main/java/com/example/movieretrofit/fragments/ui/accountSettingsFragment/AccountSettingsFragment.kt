package com.example.movieretrofit.fragments.ui.accountSettingsFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.movieretrofit.firebase.Firebase
import com.example.movieretrofit.SignInActivity
import com.example.movieretrofit.charts.PieCharts
import com.example.movieretrofit.databinding.FragmentAccountSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class AccountSettingsFragment : Fragment() {
    lateinit var binding: FragmentAccountSettingsBinding
    val firebase: Firebase = Firebase()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpUserPicture(binding.avatar, binding.tvUsername)

        setSelectedChart()
        val alertDialog = ChooseDietAlertDialog(requireContext(), firebase).create()
        binding.changeDietBtn.setOnClickListener {
            alertDialog.show()
            setSelectedChart()
        }

        buttonSignOut()
    }

    private fun setUpUserPicture(imageView: ImageView, userName: TextView) {
        Glide.with(this).load(FirebaseAuth.getInstance().currentUser?.photoUrl)
            .transform(RoundedCorners(300)).into(imageView)
        userName.text = FirebaseAuth.getInstance().currentUser!!.displayName
    }

    private fun setSelectedChart() {
        firebase.getUserDietFromFirebase {
            binding.selectedDietName.text = it.name
            PieCharts(requireContext(), binding.dietSelectedChart).setPieChart(it)

            binding.dietSelectedChart.setOnClickListener {
                Toast.makeText(context, "Ваша текущая диета", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buttonSignOut() {
        binding.buttonSignOut.setOnClickListener {
            val firebase = Firebase()
            firebase.signOut()
            val intent = Intent(activity, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            requireContext().getSharedPreferences("MY_APP", Context.MODE_PRIVATE).edit()
                .putBoolean("IS_LOGGED_IN", false).apply()
            startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AccountSettingsFragment()
    }
}