package lt.viko.eif.ajenkus.remotemoov.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import lt.viko.eif.ajenkus.remotemoov.R
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_main_control.*
import lt.viko.eif.ajenkus.remotemoov.activities.MainActivity

/**
 * [MainControlFragment]
 * Intermediate Fragment for navigating to specific InMoov parts
 * Layout in [R.layout.fragment_main_control]
 */
class MainControlFragment : Fragment() {

    lateinit var activityMain: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_control, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityMain = activity as MainActivity
        activityMain.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        button_arms.setOnClickListener {
            findNavController().navigate(
                    MainControlFragmentDirections.actionMainControlFragmentToControlSliderFragment(
                            R.layout.fragment_control_arms
                    )
            )
        }

        button_hands.setOnClickListener {
            findNavController().navigate(
                    MainControlFragmentDirections.actionMainControlFragmentToControlSliderFragment(
                            R.layout.fragment_control_hands
                    )
            )
        }

        button_head.setOnClickListener {
            findNavController().navigate(
                    MainControlFragmentDirections.actionMainControlFragmentToControlSliderFragment(
                            R.layout.fragment_control_head
                    )
            )
        }

        button_torso.setOnClickListener {
            findNavController().navigate(
                    MainControlFragmentDirections.actionMainControlFragmentToControlSliderFragment(
                            R.layout.fragment_control_torso
                    )
            )
        }
    }
}