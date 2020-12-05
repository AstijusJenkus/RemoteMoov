package lt.viko.eif.ajenkus.remotemoov.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.view.MenuItem
import android.content.Intent
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.RequestQueue
import lt.viko.eif.ajenkus.remotemoov.R
import androidx.navigation.NavController
import com.android.volley.toolbox.Volley
import androidx.navigation.ui.NavigationUI
import androidx.navigation.findNavController
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.androidbolts.topsheet.TopSheetBehavior
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main_top_sheet.*
import kotlinx.android.synthetic.main.fragment_main_bottom_nav.*
import com.google.android.material.bottomnavigation.BottomNavigationView.*

/**
 * [MainActivity]
 * This is Applications main activity
 * Layout in [R.layout.activity_main]
 *
 * This Activity hosts the following:
 *  TopSheet: displays MyRobotLab connection information. View of this can be found [R.layout.fragment_main_top_sheet].
 *  Toolbar: this is used only to display a back arrow and nothing else.
 *  NavHostFragment: the main view of the screen. Check [R.navigation.navigation_main] for all possible destinations.
 *  BottomNavigationView: this is used to navigate the NavHostFragment.
 *  Layout in [R.layout.fragment_main_bottom_nav].
 *  Menu in [R.menu.bottom_nav_main]
 *
 * This Activity must be bundled extra with:
 *  String: url - this is the full api URL address of a working MyRobotLab connection.
 */
class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {

    var companion = Companion
    companion object {
        // vars
        var URL = ""
        private var hasExited = false
        private var toolbarElevation = .0f

        // late inits
        lateinit var queue: RequestQueue
        lateinit var layout_info: ViewGroup
        private lateinit var navController: NavController
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initializes a new Volley.RequestQueue instance
        queue = Volley.newRequestQueue(this)
        // Gets URL address from the Bundle
        URL = intent.getStringExtra("url")!!
        // Retrieves the current toolbar elevation
        // NOTE! this is used to hide the toolbar when topsheet is expanded and show it when it is collapsed
        toolbarElevation = view_toolbar_main.elevation

        // Sets the toolbar as SupportActionBar
        setSupportActionBar(view_toolbar_main)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)

        // Override TopSheet behavior
        val topSheet = TopSheetBehavior.from(view_card_main_topsheet)
        topSheet.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    // When expanded hide layout information
                    TopSheetBehavior.STATE_EXPANDED -> {
                        view_card_main_topsheet.bringToFront()
                        layout_info.alpha = 1f
                    }
                    TopSheetBehavior.STATE_DRAGGING -> {
                        // Hides layout information
                        layout_info.alpha = 0f;
                        // Hides Toolbar
                        view_toolbar_main.elevation = 0f
                    }
                    // STATE_SETTLING is called when dragging is initialized but released shortly after
                    TopSheetBehavior.STATE_SETTLING -> layout_info.alpha = 0f
                    TopSheetBehavior.STATE_COLLAPSED -> {
                        // Sets Toolbars elevation back to normal
                        view_toolbar_main.elevation = toolbarElevation
                        // Updates TopSheet user information
                        updateUser()
                    }
                    else -> {
                    } // Default behavior does nothing
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float, isOpening: Boolean?) {
            }
        })

        // Gets IP address from URL
        view_text_ip_content.text = URL.replace("http://", "").replace(":8888/api/service/", "")
        // Gets all the layout information here
        getVersionData()
        updateUser()

        // Sets up BottomNavigation with NavController
        navController = findNavController(R.id.view_fragment_main_navhost)
        NavigationUI.setupWithNavController(bottom_nav_main, navController)
        bottom_nav_main.setOnNavigationItemSelectedListener(this)

    }

    /**
     * Overrides SupportActionBar NavigateUp event
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Interface [OnNavigationItemSelectedListener] implementation
     *
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            // When BottomNavigationView's item Connect is pressed
            // Opens ConnectActivity and returns false
            R.id.bottom_nav_connect -> {
                val intent = Intent(this, ConnectActivity::class.java)
                intent.putExtra("playAnimation", false)
                intent.putExtra("displayBackArrow", true)
                startActivity(intent)
                return false
            }
            // Otherwise uses the default values specified in NavController and returns true
            else -> NavigationUI.onNavDestinationSelected(item, navController)
        }
        return true
    }

    /**
     * Makes a request to get MyRobotLab version information
     */
    private fun getVersionData() {
        // Set up a request
        val request = JsonObjectRequest(
                Request.Method.GET, URL + getString(R.string.api_runtime), null,
                // Response
                {
                    val platform = it.getJSONObject("platform")
                    val mrlVersion = platform.getString("mrlVersion") + " v"
                    val javaVersion = platform.getString("vmVersion") + " v"
                    view_text_mrl_content.text = mrlVersion
                    view_text_java_content.text = javaVersion
                },
                // Error
                {
                    onConnectionError()
                }
        )

        queue.add(request)
    }

    /**
     * Makes a request to get MyRobotLab current user
     * And updates TopSheet users layout information
     */
    private fun updateUser() {
        // Set up a request
        val request = JsonObjectRequest(
                Request.Method.GET, URL + getString(R.string.api_chatbot), null,
                // Response
                {
                    view_text_user_content.text = it.getString("currentUserName")
                },
                // Error
                {
                    onConnectionError()
                }
        )

        queue.add(request)
    }

    /**
     * Makes a getResponse request to MyRobotLab
     * [command] is the command string you want to getResponse from
     */
    fun getResponse(command: String) {
        // Constructs request url
        val url = URL + getString(R.string.api_get_response) + command
        // Set up a request
        val request = StringRequest(
            Request.Method.GET, url,
            // Response
            {
                // This request returns no response so a StringRequest instance is used
                // Using a JSONObjectRequest returns a ParseError instead
            },
            // Error
            {
                onConnectionError()
            }
        )

        queue.add(request)
    }

    /**
     * Closes [MainActivity] and navigates to [ConnectActivity]
     * This method should be called when [RequestQueue] returns a VolleyError
     */
    fun onConnectionError() {
        // Since Requests a handled asynchronously
        // This method can be called multiple times after the first
        // This checks prevents opening activity more than 1 time
        if (hasExited)
            return

        Toast.makeText(this,
                "Connection Error", // Fixme: put string to xml?
                Toast.LENGTH_LONG).show()

        val intent = Intent(this, ConnectActivity::class.java)
        intent.putExtra("playAnimation", false)
        intent.putExtra("displayBackArrow", false)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
        hasExited = true
    }

}