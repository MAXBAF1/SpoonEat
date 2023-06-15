package com.example.movieretrofit.fragments.ui.chat

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieretrofit.firebase.Firebase
import com.example.movieretrofit.databinding.FragmentVoiceBinding
import com.example.movieretrofit.fragments.ui.chat.common.BotResponse
import com.example.movieretrofit.fragments.ui.chat.common.Constants
import com.example.movieretrofit.fragments.ui.chat.common.Time
import com.example.movieretrofit.fragments.ui.chat.model.Message
import com.example.movieretrofit.fragments.ui.ui.adapter.ChatAdapter
import com.example.movieretrofit.model.restFoodApi
import com.example.movieretrofit.translator.Translator
import kotlinx.android.synthetic.main.fragment_voice.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.math.roundToInt

class VoiceFragment : Fragment(), TextToSpeech.OnInitListener  {
    lateinit var binding: FragmentVoiceBinding
    private var animationDrawable: AnimationDrawable? = null
    private val REQUEST_CODE_SPEECH_INPUT = 1
    private var tts: TextToSpeech? = null

    var messagesList = mutableListOf<Message>()
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //startFrameAnimation()
        tts = TextToSpeech(context, this)

        recyclerView()
        clickEvents()
        createrecordbutton()
        customBotMessage("Здравствуйте! Как я могу вам помочь?")
    }

    private fun createrecordbutton() {
        binding.recordbutton.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                Toast
                    .makeText(
                        context, " " + e.message,
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
        }
    }

    private fun startFrameAnimation() {
        val layout = binding.layoutRoot
        animationDrawable = layout.background as AnimationDrawable
        animationDrawable!!.setEnterFadeDuration(3)
        animationDrawable!!.setExitFadeDuration(5000)
    }

    override fun onResume() {
        super.onResume()
        //animationDrawable!!.start()
    }

    companion object {
        @JvmStatic
        fun newInstance() = VoiceFragment()
    }


    private fun clickEvents() {
        btn_send.setOnClickListener {
            val messagetosend = et_message.text.toString()
            sendMessage(messagetosend)
        }

        et_message.setOnClickListener {
            GlobalScope.launch {
                delay(100)

                withContext(Dispatchers.Main) {
                    rv_messages.scrollToPosition(adapter.itemCount - 1)

                }
            }
        }
    }

    private fun recyclerView() {
        adapter = ChatAdapter()
        rv_messages.adapter = adapter
        rv_messages.layoutManager = LinearLayoutManager(context)

    }

    override fun onStart() {
        super.onStart()
        GlobalScope.launch {
            delay(100)
            withContext(Dispatchers.Main) {
                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private fun sendMessage(message: String) {
        val timeStamp = Time.timeStamp()

        if (message.isNotEmpty()) {
            messagesList.add(Message(message, Constants.SEND_ID, timeStamp))
            et_message.setText("")

            adapter.insertMessage(Message(message, Constants.SEND_ID, timeStamp))
            rv_messages.scrollToPosition(adapter.itemCount - 1)

            botResponse(message)
        }
    }

    private fun botResponse(message: String) {
        val timeStamp = Time.timeStamp()

        GlobalScope.launch {
            //Fake response delay
            delay(1000)

            withContext(Dispatchers.Main) {
                val response = BotResponse.basicResponses(message)

                if (response.split(" ")[0] == "Продукт"){
                    suspend fun processMessage(message: String) {
                        var food = response.split(" ")[1]

                        var translator = Translator()
                        withContext(Dispatchers.IO) {
                            val translatedFood = translator.translateRuEn2(food).await()
                            food = translatedFood
                        }

                        val response = restFoodApi.getFoodRecipe(food)
                        Log.e("translator", "food is ENG1 $food")

                        response.body()?.hints?.filter { it.food.image != "" }?.firstOrNull()?.let {
                            val firebase = Firebase()
                            Log.e("translator", "food is ENG ${food}")
                            Log.e("translator", "food it is ENG ${it}")
                            Log.e("translator", "foodlabel is ENG ${it.food.label}")
                            firebase.sendCurrentMealDataToFirebase(it.food)
                        }

                    }
                    lifecycleScope.launch {
                        processMessage("my message")
                    }
                    messagesList.add(Message(response, Constants.RECEIVE_ID, timeStamp))
                    adapter.insertMessage(Message(response, Constants.RECEIVE_ID, timeStamp))
                    rv_messages.scrollToPosition(adapter.itemCount - 1)
                    speakOut(response)
                }

                if (response.split(" ")[0] == "В" && response.split(" ")[1] == "продукте") {
                    suspend fun processMessage(message: String) {
                        var food = response.split(" ")[2]
                        var action = response.split(" ")[3]

                        var translator = Translator()
                        withContext(Dispatchers.IO) {
                            val translatedFood = translator.translateRuEn2(food).await()
                            food = translatedFood
                        }

                        val apiResponse = restFoodApi.getFoodRecipe(food)
                        Log.e("translator", "food is ENG1 $food")


                        when(action) {
                            "калорий:" -> {
                                val ans = apiResponse.body()?.hints?.filter { it.food.image != "" }?.firstOrNull()?.let {
                                    it.food.nutrients.calories.roundToInt().toString()
                                }
                                messagesList.add(Message("$response", Constants.RECEIVE_ID, timeStamp))
                                adapter.insertMessage(Message("$response $ans", Constants.RECEIVE_ID, timeStamp))
                                rv_messages.scrollToPosition(adapter.itemCount - 1)
                                speakOut("$response $ans")
                            }
                            "белков:" -> {
                                val ans = apiResponse.body()?.hints?.filter { it.food.image != "" }?.firstOrNull()?.let {
                                    it.food.nutrients.protein.roundToInt().toString()
                                }
                                messagesList.add(Message("$response", Constants.RECEIVE_ID, timeStamp))
                                adapter.insertMessage(Message("$response $ans", Constants.RECEIVE_ID, timeStamp))
                                rv_messages.scrollToPosition(adapter.itemCount - 1)
                                speakOut("$response $ans")
                            }
                            "жиров:" -> {
                                val ans = apiResponse.body()?.hints?.filter { it.food.image != "" }?.firstOrNull()?.let {
                                    it.food.nutrients.fat.roundToInt().toString()
                                }
                                messagesList.add(Message("$response", Constants.RECEIVE_ID, timeStamp))
                                adapter.insertMessage(Message("$response $ans", Constants.RECEIVE_ID, timeStamp))
                                rv_messages.scrollToPosition(adapter.itemCount - 1)
                                speakOut("$response $ans")
                            }
                            "углеводов:" -> {
                                val ans = apiResponse.body()?.hints?.filter { it.food.image != "" }?.firstOrNull()?.let {
                                    it.food.nutrients.carb.roundToInt().toString()
                                }
                                messagesList.add(Message("$response", Constants.RECEIVE_ID, timeStamp))
                                adapter.insertMessage(Message("$response $ans", Constants.RECEIVE_ID, timeStamp))
                                rv_messages.scrollToPosition(adapter.itemCount - 1)
                                speakOut("$response $ans")
                            }
                            "БЖУ:" -> {
                                val ans = apiResponse.body()?.hints?.filter { it.food.image != "" }?.firstOrNull()?.let {
                                    "\nБелков: ${it.food.nutrients.protein.roundToInt()}\nЖиров: ${it.food.nutrients.fat.roundToInt()}\nУглеводов: ${it.food.nutrients.carb.roundToInt()}"
                                }
                                messagesList.add(Message("$response", Constants.RECEIVE_ID, timeStamp))
                                adapter.insertMessage(Message("$response $ans", Constants.RECEIVE_ID, timeStamp))
                                rv_messages.scrollToPosition(adapter.itemCount - 1)
                                speakOut("$response $ans")
                            }
                        }
                    }
                    lifecycleScope.launch {
                        processMessage("my message")
                    }

                }

                //Starts Google
                when (response) {
                    Constants.OPEN_GOOGLE -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        site.data = Uri.parse("https://www.google.com/")
                        startActivity(site)
                    }
                    Constants.OPEN_SEARCH -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        val searchTerm: String? = message.substringAfterLast("search")
                        site.data = Uri.parse("https://www.google.com/search?&q=$searchTerm")
                        startActivity(site)
                    }

                }
            }
        }
    }

    private fun customBotMessage(message: String) {

        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                val timeStamp = Time.timeStamp()
                messagesList.add(Message(message, Constants.RECEIVE_ID, timeStamp))
                adapter.insertMessage(Message(message, Constants.RECEIVE_ID, timeStamp))

                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                val res: ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>

                Log.e("tag", "${ Objects.requireNonNull(res)[0]}")
                sendMessage(Objects.requireNonNull(res)[0])

            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale("ru"))

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            }
        }
    }

    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    public override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}