package lt.viko.eif.ajenkus.remotemoov.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import lt.viko.eif.ajenkus.remotemoov.R
import androidx.navigation.fragment.findNavController
import lt.viko.eif.ajenkus.remotemoov.utils.ActivityUtil
import kotlinx.android.synthetic.main.fragment_main_default.*
import lt.viko.eif.ajenkus.remotemoov.activities.MainActivity

/**
 * [MainDefaultFragment]
 * Fragment for main controls
 * Layout in [R.layout.fragment_main_default]
 */
class MainDefaultFragment : Fragment() {

    companion object {
        lateinit var activityMain: MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_default, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityMain = activity as MainActivity
        activityMain.supportActionBar?.setDisplayShowTitleEnabled(false)
        activityMain.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Send
        button_send.setOnClickListener {
            val input = input_command.text.toString()
            input_command.setText("")
            activityMain.getResponse("\"$input\"")
        }

        // Voice
        button_voice.setOnClickListener {
            findNavController().navigate(R.id.action_mainDefaultFragment_to_mainVoiceFragment)
        }

        // Kinect
        button_kinect.setOnClickListener {
            findNavController().navigate(R.id.action_mainDefaultFragment_to_mainKinectFragment)
        }

        // Rest
        button_rest.setOnClickListener {
            activityMain.getResponse(getString(R.string.command_rest))
        }

        ActivityUtil.setKeyboardFocus(layout_default, listOf(input_command), requireContext())
    }

}