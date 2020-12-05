package lt.viko.eif.ajenkus.remotemoov.utils

import android.view.ViewGroup
import android.content.Context
import android.widget.EditText
import android.view.MotionEvent
import android.annotation.SuppressLint
import android.view.inputmethod.InputMethodManager

/**
 * [ActivityUtil]
 * A utility class for handling Activities
 */
class ActivityUtil {

    companion object {
        /**
         * Drops keyboard focus when pressed anywhere else on the screen.
         * [viewGroup] is the view on which the touch focus should be handled
         * [inputFields] is the [ArrayList] of [EditText] fields you want to drop focus from
         * [context] is the host of [viewGroup]
         */
        @JvmStatic
        @SuppressLint("ClickableViewAccessibility")
        fun setKeyboardFocus(viewGroup: ViewGroup, inputFields: List<EditText> , context: Context) {
            viewGroup.setOnTouchListener { view, event ->
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        inputFields.forEach { field ->
                            if (field.isFocused) {
                                field.clearFocus()
                                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(view.windowToken, 0)
                            }
                        }
                    }
                }
                view?.onTouchEvent(event) ?: true
            }
        }
    }

}