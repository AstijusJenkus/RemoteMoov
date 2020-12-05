package lt.viko.eif.ajenkus.remotemoov.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import lt.viko.eif.ajenkus.remotemoov.R
import kotlinx.android.synthetic.main.fragment_control_torso.*

/**
 * [ControlTorsoFragment]
 * Fragment for Torso controls
 * Layout in [R.layout.fragment_control_torso]
 */
class ControlTorsoFragment : Fragment() {

    companion object {
        lateinit var fragment: ControlSliderFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control_torso, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment = parentFragment?.childFragmentManager?.fragments?.get(0) as ControlSliderFragment

        fragment.setPart(R.string.torso_top)
        button_top.requestFocus()
        setUpButtonRequests()
    }

    private fun setUpButtonRequests() {
        button_top.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.torso_top)
        }

        button_mid.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.torso_mid)
        }
        button_low.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.torso_low)
        }
    }

}