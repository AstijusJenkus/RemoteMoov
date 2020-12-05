package lt.viko.eif.ajenkus.remotemoov.fragments

import android.os.Bundle
import android.view.View
import android.graphics.Color
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import lt.viko.eif.ajenkus.remotemoov.R
import android.content.res.ColorStateList
import kotlinx.android.synthetic.main.fragment_control_head.*

/**
 * [ControlHeadFragment]
 * Fragment for Head controls
 * Layout in [R.layout.fragment_control_head]
 */
class ControlHeadFragment : Fragment() {

    companion object {
        lateinit var fragment: ControlSliderFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control_head, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment = parentFragment?.childFragmentManager?.fragments?.get(0) as ControlSliderFragment

        fragment.checkEyelids(getString(R.string.eyelids), this)

        fragment.setPart(R.string.eye_y)
        button_eye_y.requestFocus()
        setUpButtonRequests()
    }

    private fun setUpButtonRequests() {
        button_eye_lids.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.eyelids)
        }

        button_eye_y.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.eye_y)
        }

        button_jaw.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.jaw)
        }

        button_rotate.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.head_rotate)
        }

        button_eye_x.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.eye_x)
        }

        button_neck_roll.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.neck_roll)
        }

        button_neck.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                fragment.setPart(R.string.neck)
        }
    }

    fun setEyelids(enabled: Boolean) {
        if (!enabled) {
            button_eye_lids.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            button_eye_lids.isEnabled = enabled
        }
    }
}