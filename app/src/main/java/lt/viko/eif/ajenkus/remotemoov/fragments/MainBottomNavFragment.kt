package lt.viko.eif.ajenkus.remotemoov.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import lt.viko.eif.ajenkus.remotemoov.R

/**
 * [MainBottomNavFragment]
 * BottomNavigationView Fragment for MainActivity
 * Layout in [R.layout.fragment_main_bottom_nav]
 */
class MainBottomNavFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_bottom_nav, container, false)
    }

}