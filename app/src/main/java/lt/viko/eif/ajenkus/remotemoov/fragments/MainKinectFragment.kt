package lt.viko.eif.ajenkus.remotemoov.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.animation.Animation
import lt.viko.eif.ajenkus.remotemoov.R
import android.view.animation.AnimationUtils
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.fragment_main_kinect.*
import lt.viko.eif.ajenkus.remotemoov.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_main_voice.view_pulse

/**
 * [MainKinectFragment]
 * Fragment view for kinect controls
 * Layout in [R.layout.fragment_main_kinect]
 */
class MainKinectFragment : Fragment() {

    lateinit var activityMain: MainActivity
    lateinit var pulse: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_kinect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Gets the host Activity of this Fragment
        activityMain = activity as MainActivity
        activityMain.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pulse = AnimationUtils.loadAnimation(context, R.anim.pulse)
        view_pulse.startAnimation(pulse)

        button_kinect_start.setOnClickListener {
            if (button_kinect_start.text == getString(R.string.button_start)) {
                activityMain.getResponse(getString(R.string.command_tracking))
                button_kinect_start.text = getString(R.string.button_stop)
                view_pulse.clearAnimation()
            }
            else {
                activityMain.getResponse(getString(R.string.command_kinect_off))
                button_kinect_start.text = getString(R.string.button_start)
                view_pulse.startAnimation(pulse)
            }
        }

        checkOpenNi()
    }

    private fun checkOpenNi() {
        // Set up a request
        val request = JsonObjectRequest(
                Request.Method.GET, activityMain.companion.URL + "i01.openni/getLastError", null,
                // Response
                {
                    view_pulse.clearAnimation()
                    button_kinect_start.visibility = View.GONE
                    view_pulse.visibility = View.GONE
                    view_kinect_message.visibility = View.VISIBLE
                },
                // Error
                {
                    // TODO: explanation goes here :)
                }
        )

        activityMain.companion.queue.add(request)
    }
}