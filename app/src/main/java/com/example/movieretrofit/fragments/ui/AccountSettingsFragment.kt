package com.example.movieretrofit.fragments.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.movieretrofit.Firebase
import com.example.movieretrofit.SignInActivity
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