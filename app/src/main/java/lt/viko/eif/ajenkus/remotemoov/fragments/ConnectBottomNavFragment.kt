package lt.viko.eif.ajenkus.remotemoov.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import lt.viko.eif.ajenkus.remotemoov.R
import lt.viko.eif.ajenkus.remotemoov.activities.ConnectActivity
import kotlinx.android.synthetic.main.fragment_connect_bottom_nav.*

/**
 * [ConnectBottomNavFragment]
 * A BottomNavigationView of [ConnectActivity]
 * Layout in [R.layout.fragment_connect_bottom_nav]
 */
class ConnectBottomNavFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect_bottom_nav, container, false)
    }

    /**
     * Sets alpha value of [ConnectBottomNavFragment]
     * [alpha] should be a [Float] value from .0f to 1.0f
     */
    fun setAlpha(alpha: Float) {
        bottom_nav_connect.alpha = alpha
        view_shadow.alpha = alpha
    }

    /**
     * Starts [ConnectBottomNavFragment] fade in animation to alpha value 1.0f
     * [fadeDuration] is the duration this animation should take in milliseconds as a [Long] value
     */
    fun triggerFadeInAnimation(fadeDuration: Long) {
        view_shadow.visibility = View.VISIBLE
        bottom_nav_connect.visibility = View.VISIBLE
        view_shadow.animate().alpha(1f).duration = fadeDuration
        bottom_nav_connect.animate().alpha(1f).duration = fadeDuration
    }

    fun setVisibility(visibility: Int) {
        view_shadow.visibility = visibility
        bottom_nav_connect.visibility = visibility
    }

}