package lt.viko.eif.ajenkus.remotemoov.fragments

import android.os.Bundle
import android.view.View
import android.app.Activity
import android.content.Intent
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.speech.RecognizerIntent
import android.view.animation.Animation
import lt.viko.eif.ajenkus.remotemoov.R
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.fragment_main_voice.*
import lt.viko.eif.ajenkus.remotemoov.activities.MainActivity
import androidx.activity.result.contract.ActivityResultContracts

/**
 * [MainVoiceFragment]
 * Fragment view for voice controls
 * Layout in [R.layout.fragment_main_voice]
 */
class MainVoiceFragment : Fragment() {

    lateinit var activityMain: MainActivity
    lateinit var pulse: Animation

    // Registering a callback for an Activity Result
    val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val matches = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val command = matches?.get(0)?.replace("$", "")
            activityMain.getResponse(command!!)
        }
        // Start the pulse animation again
        view_pulse.startAnimation(pulse)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_voice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Gets the host Activity of this Fragment
        activityMain = activity as MainActivity
        activityMain.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pulse = AnimationUtils.loadAnimation(context, R.anim.pulse)
        view_pulse.startAnimation(pulse)

        button_voice_start.setOnClickListener {
            // Stops pulse animation after clicking
            view_pulse.clearAnimation()
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition ready")
            getContent.launch(intent)
        }
    }
}