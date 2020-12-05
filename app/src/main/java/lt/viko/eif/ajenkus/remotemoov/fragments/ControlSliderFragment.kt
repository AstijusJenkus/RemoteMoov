package lt.viko.eif.ajenkus.remotemoov.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.android.volley.Request
import android.view.LayoutInflater
import com.jaygoo.widget.RangeSeekBar
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import lt.viko.eif.ajenkus.remotemoov.R
import com.android.volley.toolbox.Volley
import androidx.navigation.fragment.navArgs
import com.android.volley.toolbox.StringRequest
import com.jaygoo.widget.OnRangeChangedListener
import lt.viko.eif.ajenkus.remotemoov.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_control_slider.*

/**
 * [ControlSliderFragment]
 * Control part of the SliderFragment
 * This Fragment has a FragmentContainerView which swaps to a specific part Fragment passed reference
 * Layout in [R.layout.fragment_control_slider]
 */
class ControlSliderFragment : Fragment() {
    val args: ControlSliderFragmentArgs by navArgs()

    companion object {
        var currPart = 0
        var moveTo = "N/A"
        var getMin = "N/A"
        var getMax = "N/A"
        var setMinMax = "N/A"
        var getCurrentPos = "N/A"
        var prevProgress = 0
        lateinit var activityMain: MainActivity
        lateinit var queue: RequestQueue
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control_slider, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Gets the host Activity of this Fragment
        activityMain = activity as MainActivity
        // Volley.RequestQueue initialization
        queue = Volley.newRequestQueue(activityMain)

        moveTo        = getString(R.string.move_to)
        getMin        = getString(R.string.get_min)
        getMax        = getString(R.string.get_max)
        setMinMax     = getString(R.string.set_min_max)
        getCurrentPos = getString(R.string.get_current_pos)

        val manager = parentFragmentManager.beginTransaction()
        // This part swaps the FragmentContainerView to a passed Fragment reference
        when (args.fragmentView) {
            R.layout.fragment_control_arms  -> manager.replace(R.id.fragment_controls_view, ControlArmsFragment())
            R.layout.fragment_control_hands -> manager.replace(R.id.fragment_controls_view, ControlHandsFragment())
            R.layout.fragment_control_head  -> manager.replace(R.id.fragment_controls_view, ControlHeadFragment())
            R.layout.fragment_control_torso -> manager.replace(R.id.fragment_controls_view, ControlTorsoFragment())
        }
        manager.addToBackStack(null)
        manager.commit()

        seekbar_output.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Sends a progress request only when the progress actually changed values
                if (progress != prevProgress) {
                    // Set current value to label
                    label_output_num.text = progress.toString()
                    // Move part to new progress
                    movePartTo(progress)
                    prevProgress = progress
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        seekbar_limits.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                val minimum = leftValue.toInt()
                val maximum = rightValue.toInt()
                // Sets current minimum and maximum values to labels
                label_limit_min.text = minimum.toString()
                label_limit_max.text = maximum.toString()
                // when changing maximum and minimum values change output seekbars
                // minimum and maximum values as well
                setPartMinMax(minimum, maximum)
                seekbar_output.min = minimum
                seekbar_output.max = maximum
            }
            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }
            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }
        })
    }

    /**
     * Sets the currently selected part
     * [part] is the part API string ID
     */
    fun setPart(part: Int) {
        currPart = part
        setLabel()
        getServoPos()
    }

    /** Fixme: what the 'hack' is this?
     * This is only used to check if the eyelids part API has the getCurrentPos method
     * The original MyRobotLab version cannot control both of the eyelids at the same time
     * Using the original MyRobotLab this method disables the eyelids controls
     */
    fun checkEyelids(part: String, headFragment: ControlHeadFragment) {
        val request = StringRequest(
                Request.Method.GET, "${activityMain.companion.URL}$part$getCurrentPos", // Interpolates API string
                // Response
                {
                    if (it.contains("error"))
                        headFragment.setEyelids(false)
                    else
                        headFragment.setEyelids(true)
                },
                // Error
                {
                    activityMain.onConnectionError()
                }
        )
        queue.add(request)
    }

    /**
     * Sets the currently selected parts minimum and maximum servo values on MyRobotLab
     * [minimum] is a value between 0 and 180
     * [maximum] is a value between 0 and 180
     */
    private fun setPartMinMax(minimum: Int, maximum: Int) {
        val part = getString(currPart)
        val request = StringRequest(
                Request.Method.GET, "${activityMain.companion.URL}$part$setMinMax$minimum/$maximum", // Interpolates API string
                // Response
                {
                    // This request returns no response so a StringRequest instance is used
                    // Using a JSONObjectRequest returns a ParseError instead
                },
                // Error
                {
                    activityMain.onConnectionError()
                }
        )

        queue.add(request)
    }

    /**
     * Moves the currently selected part to a position on MyRobotLab
     * [position] is a value between the minimum and maximum values set
     */
    private fun movePartTo(position: Int) {
        val part = getString(currPart)
        // Set up a request
        val request = StringRequest(
                Request.Method.GET, "${activityMain.companion.URL}$part$moveTo$position", // Interpolates API string
                // Response
                {
                    // This request returns no response so a StringRequest instance is used
                    // Using a JSONObjectRequest returns a ParseError instead
                },
                // Error
                {
                    activityMain.onConnectionError()
                }
        )

        queue.add(request)
    }

    /**
     * Maps title label to the currently selected part
     */
    private fun setLabel() {
        when(currPart) {
            // Hand Right
            R.string.right_wrist    -> label_control_part.text = getString(R.string.title_right_wrist)
            R.string.right_pinky    -> label_control_part.text = getString(R.string.title_right_pinky)
            R.string.right_ring     -> label_control_part.text = getString(R.string.title_right_ring)
            R.string.right_middle   -> label_control_part.text = getString(R.string.title_right_middle)
            R.string.right_index    -> label_control_part.text = getString(R.string.title_right_index)
            R.string.right_thumb    -> label_control_part.text = getString(R.string.title_right_thumb)
            // Hand Left
            R.string.left_wrist     -> label_control_part.text = getString(R.string.title_left_wrist)
            R.string.left_pinky     -> label_control_part.text = getString(R.string.title_left_pinky)
            R.string.left_ring      -> label_control_part.text = getString(R.string.title_left_ring)
            R.string.left_middle    -> label_control_part.text = getString(R.string.title_left_middle)
            R.string.left_index     -> label_control_part.text = getString(R.string.title_left_index)
            R.string.left_thumb     -> label_control_part.text = getString(R.string.title_left_thumb)
            // Arm Right
            R.string.right_rotate   -> label_control_part.text = getString(R.string.title_right_rotate)
            R.string.right_bicep    -> label_control_part.text = getString(R.string.title_right_bicep)
            R.string.right_shoulder -> label_control_part.text = getString(R.string.title_right_shoulder)
            R.string.right_omoplate -> label_control_part.text = getString(R.string.title_right_omoplate)
            // Arm Left
            R.string.left_rotate    -> label_control_part.text = getString(R.string.title_left_rotate)
            R.string.left_bicep     -> label_control_part.text = getString(R.string.title_left_bicep)
            R.string.left_shoulder  -> label_control_part.text = getString(R.string.title_left_shoulder)
            R.string.left_omoplate  -> label_control_part.text = getString(R.string.title_left_omoplate)
            // Head
            R.string.eye_y          -> label_control_part.text = getString(R.string.title_eye_y)
            R.string.eye_x          -> label_control_part.text = getString(R.string.title_eye_x)
            R.string.jaw            -> label_control_part.text = getString(R.string.title_jaw)
            R.string.neck           -> label_control_part.text = getString(R.string.title_neck)
            R.string.neck_roll      -> label_control_part.text = getString(R.string.title_neck_roll)
            R.string.head_rotate    -> label_control_part.text = getString(R.string.title_head_rotate)
            R.string.eyelids        -> label_control_part.text = getString(R.string.title_eyelids)
            // Torso
            R.string.torso_top      -> label_control_part.text = getString(R.string.title_torso_top)
            R.string.torso_mid      -> label_control_part.text = getString(R.string.title_torso_mid)
            R.string.torso_low      -> label_control_part.text = getString(R.string.title_torso_low)
        }
    }

    /**
     * Sets all [SeekBar] data
     * [output] output seekbar value
     * [limits_min] limits seekbar minimum value
     * [limits_max] limits seekbar maximum value
     */
    private fun setSeekbars(output: Int, limits_min: Float, limits_max: Float) {
        val min = limits_min.toInt()
        val max = limits_max.toInt()

        if (output < seekbar_output.min)
            seekbar_output.min = min
        else if (output > seekbar_output.max)
            seekbar_output.max = max

        seekbar_output.progress = output
        label_output_num.text = output.toString()
        seekbar_limits.setProgress(limits_min, limits_max)
    }

    /**
     * Gets currently set parts servo positions from MyRobotLab
     */
    private fun getServoPos() {
        val part = getString(currPart)
        // Set up a request
        val request = StringRequest(
            Request.Method.GET, "${activityMain.companion.URL}$part$getCurrentPos", // Interpolates API string
            // Response
            { currentPos ->
                val request = StringRequest(
                        Request.Method.GET, "${activityMain.companion.URL}$part$getMin", // Interpolates API string
                        // Response
                        { minPos ->
                            val request = StringRequest(
                                    Request.Method.GET, "${activityMain.companion.URL}$part$getMax", // Interpolates API string
                                    // Response
                                    { maxPos ->
                                        setSeekbars(currentPos.toFloat().toInt(), minPos.toFloat(), maxPos.toFloat())
                                    },
                                    // Error
                                    {
                                        activityMain.onConnectionError()
                                    }
                            )
                            queue.add(request)
                        },
                        // Error
                        {
                            activityMain.onConnectionError()
                        }
                )
                queue.add(request)
            },
            // Error
            {
                activityMain.onConnectionError()
            }
        )

        queue.add(request)
    }

}