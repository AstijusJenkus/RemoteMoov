package lt.viko.eif.ajenkus.remotemoov.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import lt.viko.eif.ajenkus.remotemoov.R
import lt.viko.eif.ajenkus.remotemoov.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_main_top_sheet.*

/**
 * [MainTopSheetFragment]
 * [MainActivity] TopSheet Fragment
 * Layout in [R.layout.fragment_main_top_sheet]
 */
class MainTopSheetFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_top_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as MainActivity
        layout_information_content.alpha = .0f
        activity.companion.layout_info = layout_information_content
    }
}