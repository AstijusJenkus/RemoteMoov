package lt.viko.eif.ajenkus.remotemoov.fragments

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.animation.Animation
import lt.viko.eif.ajenkus.remotemoov.R
import androidx.core.content.ContextCompat
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import androidx.core.widget.addTextChangedListener
import lt.viko.eif.ajenkus.remotemoov.utils.ActivityUtil
import kotlinx.android.synthetic.main.fragment_connect_new.*
import lt.viko.eif.ajenkus.remotemoov.activities.ConnectActivity

/**
 * [ConnectNewFragment]
 * Fragment for initiating new connections to MyRobotLab
 * Layout in [R.layout.fragment_connect_new]
 */
class ConnectNewFragment : Fragment() {

    private companion object {
        lateinit var animMoveUp: Animation
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gets the host Activity of this Fragment
        val activity = activity as ConnectActivity
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(activity.companion.displayBackArrow)

        // IP EditText field TextChangedListener
        edit_text_ip.addTextChangedListener {
            // Check if field is not empty and set the properties accordingly
            if (edit_text_ip.text.isNotEmpty()) {
                button_connect.isClickable = true
                button_connect.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.button)
            } else {
                button_connect.isClickable = false
                button_connect.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray)
            }
        }

        button_connect.setOnClickListener {
            activity.connect(edit_text_ip.text.toString())
        }

        // Setting up OnClickListener also sets buttons to a clickable state
        // Even if the state is defined in xml as non clickable
        button_connect.isClickable = false

        // Sets up keyboard focusing
        ActivityUtil.setKeyboardFocus(layout_connect, listOf(edit_text_ip), requireContext())
    }

    /**
     * Triggers the starting animation
     * Also includes the [ConnectBottomNavFragment]
     * [startDuration] specifies how long the initial logo should be displayed in milliseconds before starting the animation process
     * [fadeDuration] specifies how long the animations fade durations should take in milliseconds
     * [ConnectBottomNavFragment] instance to [ConnectBottomNavFragment]
     */
    fun triggerStartingAnimation(startDuration: Long, fadeDuration: Long, bottomNavFragment: ConnectBottomNavFragment) {
        // Sets up view visibility
        view_image_shrink.visibility  = View.VISIBLE
        view_image_inmoov.visibility  = View.GONE
        view_image_move_up.visibility = View.GONE
        // Sets alpha values to zero
        bottomNavFragment.setAlpha(.0f)
        bottomNavFragment.setVisibility(View.INVISIBLE)
        view_text_title.alpha       = .0f
        view_text_description.alpha = .0f
        edit_text_ip.alpha          = .0f
        button_connect.alpha        = .0f
        // Visibility
        view_text_title.visibility       = View.INVISIBLE
        view_text_description.visibility = View.INVISIBLE
        edit_text_ip.visibility          = View.INVISIBLE
        button_connect.visibility        = View.INVISIBLE
        // Calculates the scalar value based on devices screen dimensions
        val scale = 1f * view_image_move_up.width / view_image_shrink.width
        // Sets up shrink animation
        // Fixme: move to a xml animation declaration?
        val animShrink = ScaleAnimation(
            1f, scale,
            1f, scale,
            Animation.RELATIVE_TO_SELF, .5f,
            Animation.RELATIVE_TO_SELF, .5f
        )
        animShrink.duration = 1000

        // Shrink animations listener
        animShrink.setAnimationListener(object : Animation.AnimationListener {
            // Triggers on animation end
            override fun onAnimationEnd(animation: Animation?) {
                view_image_shrink.clearAnimation()
                view_image_shrink.visibility = View.GONE
                view_image_move_up.visibility = View.VISIBLE
                view_image_move_up.startAnimation(animMoveUp)
            }
            override fun onAnimationRepeat(animation: Animation?) {
            }
            override fun onAnimationStart(animation: Animation?) {
            }
        })

        animMoveUp = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_move_up)
        animMoveUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                // On ending of this animation starts an alpha fade animation
                // on every component of this and BottomNavigationView instance
                view_image_move_up.clearAnimation()
                // Visibility modifiers
                view_image_move_up.visibility    = View.GONE
                view_image_inmoov.visibility     = View.VISIBLE
                view_text_description.visibility = View.VISIBLE
                edit_text_ip.visibility          = View.VISIBLE
                button_connect.visibility        = View.VISIBLE
                // Fade animation
                view_text_title.animate().alpha(1f).duration       = fadeDuration
                view_text_description.animate().alpha(1f).duration = fadeDuration
                edit_text_ip.animate().alpha(1f).duration          = fadeDuration
                button_connect.animate().alpha(1f).duration        = fadeDuration
                bottomNavFragment.triggerFadeInAnimation(fadeDuration)
            }
            override fun onAnimationStart(animation: Animation?) {
            }
            override fun onAnimationRepeat(animation: Animation?) {
            }
        })

        // Start of the animation
        Handler().postDelayed({
            view_image_shrink.startAnimation(animShrink)
        }, startDuration)
    }
}