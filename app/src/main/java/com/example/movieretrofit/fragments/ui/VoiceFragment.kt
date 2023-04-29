package com.example.movieretrofit.fragments.ui

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.movieretrofit.databinding.FragmentVoiceBinding


class VoiceFragment : Fragment() {
    lateinit var binding: FragmentVoiceBinding
    private var animationDrawable: AnimationDrawable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        startFrameAnimation()
    }

    private fun startFrameAnimation() {
        val layout = binding.layoutRoot
        animationDrawable = layout.background as AnimationDrawable
        animationDrawable!!.setEnterFadeDuration(3)
        animationDrawable!!.setExitFadeDuration(5000)
    }

    override fun onResume() {
        super.onResume()
        animationDrawable!!.start()
    }

    companion object {
        @JvmStatic
        fun newInstance() = VoiceFragment()
    }
}