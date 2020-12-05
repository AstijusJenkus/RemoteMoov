package lt.viko.eif.ajenkus.remotemoov.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import lt.viko.eif.ajenkus.remotemoov.R
import kotlinx.android.synthetic.main.fragment_control_arms.*

/**
 * [ControlArmsFragment]
 * Fragment for Arm controls
 * Layout in [R.layout.fragment_control_arms]
 */
class ControlArmsFragment : Fragment() {

    companion object {
        lateinit var fragment: ControlSliderFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control_arms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment = parentFragment?.childFragmentManager?.fragments?.get(0) as ControlSliderFragment

        fragment.setPart(R.string.right_rotate)
        button_rotate_right.requestFocus()
        setUpButtonRequests()
    }

    private fun setUpButtonRequests() {
        button_rotate_right.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.right_rotate)
        }

        button_bicep_right.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.right_bicep)
        }

        button_shoulder_right.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.right_shoulder)
        }

        button_omoplate_right.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.right_omoplate)
        }

        button_rotate_left.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.left_rotate)
        }

        button_bicep_left.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.left_bicep)
        }

        button_shoulder_left.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.left_shoulder)
        }

        button_omoplate_left.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.left_omoplate)
        }
    }
}
