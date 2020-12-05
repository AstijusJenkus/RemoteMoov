package lt.viko.eif.ajenkus.remotemoov.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import lt.viko.eif.ajenkus.remotemoov.R
import kotlinx.android.synthetic.main.fragment_control_hands.*

/**
 * [ControlHandsFragment]
 * Fragment for Hand controls
 * Layout in [R.layout.fragment_control_hands]
 */
class ControlHandsFragment : Fragment() {

    companion object {
        lateinit var fragment: ControlSliderFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control_hands, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment = parentFragment?.childFragmentManager?.fragments?.get(0) as ControlSliderFragment

        fragment.setPart(R.string.right_wrist)
        button_wrist_right.requestFocus()
        setUpButtonRequests()
    }

    private fun setUpButtonRequests() {

        button_wrist_right.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.right_wrist)
        }

        button_pinky_right.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.right_pinky)
        }

        button_ring_right.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.right_ring)
        }

        button_middle_right.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.right_middle)
        }

        button_index_right.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.right_index)
        }

        button_thumb_right.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.right_thumb)
        }

        button_wrist_left.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.left_wrist)
        }

        button_pinky_left.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.left_pinky)
        }

        button_ring_left.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.left_ring)
        }

        button_middle_left.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.left_middle)
        }

        button_index_left.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.left_index)
        }

        button_thumb_left.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.left_thumb)
        }
    }
}