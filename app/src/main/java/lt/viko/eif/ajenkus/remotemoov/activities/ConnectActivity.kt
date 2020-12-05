package lt.viko.eif.ajenkus.remotemoov.activities

import android.view.*
import android.os.Bundle
import android.widget.Toast
import android.graphics.Color
import android.content.Intent
import android.content.Context
import android.app.AlertDialog
import com.android.volley.Request
import com.android.volley.RequestQueue
import lt.viko.eif.ajenkus.remotemoov.R
import android.content.SharedPreferences
import com.android.volley.toolbox.Volley
import android.content.res.ColorStateList
import androidx.navigation.findNavController
import com.android.volley.DefaultRetryPolicy
import lt.viko.eif.ajenkus.remotemoov.models.IP
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_connect.*
import lt.viko.eif.ajenkus.remotemoov.utils.ActivityUtil
import kotlinx.android.synthetic.main.fragment_connect_new.*
import kotlinx.android.synthetic.main.dialog_conn_edit.view.*
import lt.viko.eif.ajenkus.remotemoov.fragments.ConnectNewFragment
import lt.viko.eif.ajenkus.remotemoov.fragments.ConnectBottomNavFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * [ConnectActivity]
 * The purpose of this Activity is to make a connection between the application and MyRobotLab
 * MyRobotLab is an Open Source Java Framework for Robotics and Creative Machine Control
 * Github: https://github.com/MyRobotLab/myrobotlab Website: http://myrobotlab.org/
 * The connection is to be made on MyRobotLab version 1.0.2693 Manticore
 * Layout in [R.layout.activity_connect]
 *
 * This Activity hosts the following:
 *  Toolbar: this is used only to display a back arrow and nothing else
 *  NavHostFragment: the main view of the screen. Check [R.navigation.navigation_connect] for all possible destinations.
 *  BottomNavigationView: this is used to navigate the NavHostFragment.
 *  Layout in [R.layout.fragment_connect_bottom_nav]
 *  Menu in [R.menu.bottom_nav_connect]
 *
 * This Activity can be bundled extra with:
 *  Boolean: playAnimation - specifies whether to play the starting animation or not
 *  Boolean: displayBackArrow - specifies whether to display a back arrow on Toolbar
 */
class ConnectActivity : AppCompatActivity() {

    var companion = Companion
    companion object {
        var displayBackArrow = false
        // Constants
        private var URL = "http://%s:8888/api/service/"
        private var GET_META_DATA = "chatBot/getMetaData/"
        private var TIMEOUT_DURATION = 2500

        // Volley RequestQueue
        private lateinit var queue: RequestQueue
        private lateinit var sharedPref: SharedPreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)

        // Initializes a [SharedPreference]
        // Interface for accessing and modifying preference data
        // For more information visit: https://developer.android.com/training/data-storage/shared-preferences
        sharedPref = getSharedPreferences(
                getString(R.string.preference_saved_conns),
                Context.MODE_PRIVATE
        )

        // Sets up a Volley RequestQueue
        // Volley is an HTTP library that makes networking for Android apps easier
        // For more information visit: https://developer.android.com/training/volley
        queue = Volley.newRequestQueue(this)

        // This part retrieves any Extras sent to Intent
        // Intent:
        // An intent is an abstract description of an operation to be performed.
        // It can be used with Context#startActivity(Intent) to launch an Activity
        // Extras:
        // This is a Bundle of any additional information.
        // This can be used to provide extended information to the component.
        // BooleanExtra - playAnimation: specifies whether to play starting Activity's animation
        // If no value is specified the default value is set to true
        val playAnimation = intent.getBooleanExtra("playAnimation", true)
        // BooleanExtra - displayBackArrow: specifies whether to display a Toolbar back arrow
        // If no value is specified the daulf value is set to false
        displayBackArrow = intent.getBooleanExtra("displayBackArrow", false)

        // Sets up Toolbar as an ActionBar
        setSupportActionBar(view_toolbar_connect)
        // Disables ActionBar Title
        supportActionBar?.setDisplayShowTitleEnabled(false)
        // Changes ActionBars back button
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)

        // Sets up NavHostFragments viewTreeObserver GlobalLayoutListener event
        // Its an interface definition for a callback to be invoked when the global layout state or the visibility of views within the view tree changes
        // This is used to find out when the NavHostFragments Fragment is displayed before performing any actions on it
        view_fragcontainer_connect_navhost.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Check whether to play the starting animation or not
                if (playAnimation) {
                    // Retrieves a instance of SupportFragmentManager
                    val navFragmentManager = supportFragmentManager.findFragmentById(R.id.view_fragcontainer_connect_navhost)
                    // Gets top Fragment instance from NavHostFragment using SupportFragmentManager and casts it to a ConnectNewFragment object
                    val fragment = navFragmentManager?.childFragmentManager?.fragments?.get(0) as ConnectNewFragment
                    // Invokes a method to trigger the starting animation
                    fragment.triggerStartingAnimation(
                            500,
                            500,
                            supportFragmentManager.findFragmentById(R.id.view_fragcontainer_connect_bottomnav) as ConnectBottomNavFragment
                    )
                }

                // Sets up BottomNavigation with NavController
                val bottomNavigationMenuView = findViewById<BottomNavigationView>(R.id.bottom_nav_connect)
                val navController = findNavController(R.id.view_fragcontainer_connect_navhost)
                bottomNavigationMenuView.setupWithNavController(navController)

                // Cleans up the GlobalLayoutListener otherwise this loops forever
                view_fragcontainer_connect_navhost.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

    }

    /**
     * Overrides SupportActionBar NavigateUp event
     */
    override fun onSupportNavigateUp(): Boolean {
        // Triggers onBackPressed event
        onBackPressed()
        return true
    }
    
    /**
     * Synchronously clears and updates [R.string.preference_saved_conns]
     * [items] is the [MutableList] of [IP] data class objects to replace the SharedPreferences with
     */
    fun updateSavedConns(items: MutableList<IP>) {
        val editor = sharedPref.edit()
        editor.clear()
        editor.commit()
        items.forEach {
            editor.putString(it.name, it.ip)
        }
        editor.commit()
    }

    /**
     * Retrieves all Saved Connections as a [MutableList] of [IP] data class objects from [R.string.preference_saved_conns]
     */
    fun retrieveSavedConns(): MutableList<IP> {
        val savedConns: MutableList<IP> = ArrayList()
        sharedPref.all.forEach {
            savedConns.add(IP(it.key, it.value as String))
        }
        return savedConns
    }

    /**
     * Performs a VolleyRequest to check for available MyRobotLab connection
     *
     * When called this method shows a progressBar dialog window
     *      this dialog window gets dismissed when a Response or Error is retrieved from asynchronous VolleyRequest call
     * If the connection is successful checks to see if the ip address is already saved in [R.string.preference_saved_conns]
     *      no: proceeds to open MainActivity and clear all other screens
     *      yes: prompts the user if they want to save this connection
     *          canceled: proceeds with navigating to MainActivity
     *          yes: prompts the user to name the new connection
     *              canceled: proceeds with navigating to MainActivity
     *              done: saves a new connection in [R.string.preference_saved_conns] and proceeds with navigating to MainActivity
     *
     * [ip] is the devices internal IP address on which MyRobotLab is running
     */
    fun connect(ip: String) {
        // Constructs the full URL String
        val url = String.format(URL, ip)

        // ---------------------
        // ProgressBar
        // ---------------------
        // View
        val vProgress = LayoutInflater.from(this)
                .inflate(R.layout.loading_dialog, null)
        // Dialog
        val dProgress = AlertDialog.Builder(this)
                .setView(vProgress)
                .setCancelable(false)
                .show()

        // ---------------------
        // New Connection Name
        // ---------------------
        // View
        val vEdit = LayoutInflater.from(this)
                .inflate(R.layout.dialog_conn_edit, null)
        // Builder
        var builder = AlertDialog.Builder(this)
                .setView(vEdit)
                .setCancelable(false)
                .setTitle("Name this connection")   // Fixme: put string to xml?
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Done", null)
        // Dialog
        val dConnName = builder.create()
        dConnName.setOnShowListener {
            // Button Cancel
            dConnName.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                dConnName.dismiss()
                navigateToMain(url)
            }
            // Button Done
            dConnName.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val connName = vEdit.edit_saved_conn_name.text.toString()
                // Checks if the name does NOT exist in SharedPreferences
                if (!sharedPref.contains(connName)) {
                    // Opens SharedPreferences in Edit Mode
                    val editor = sharedPref.edit()
                    editor.putString(connName, ip)
                    editor.apply()
                    dConnName.dismiss()
                    navigateToMain(url)
                } else
                    Toast.makeText(this,
                            "This name already exists", // Fixme: put string to xml?
                            Toast.LENGTH_SHORT).show()
            }
        }

        // ---------------------
        // New Connection Found
        // ---------------------
        // Builder
        builder = AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("New Connection Found")                       // Fixme: put string to xml?
                .setMessage("Would you like to save this connection?")  // Fixme: put string to xml?
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", null)
        // Dialog
        val dConnNew = builder.create()
        dConnNew.setOnShowListener {
            // Button Cancel
            dConnNew.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                dConnNew.dismiss()
                navigateToMain(url)
            }
            // Button Ok
            dConnNew.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                dConnNew.dismiss()
                // Name check if New Connection already exists
                // Fixme: better way to do this ?
                var newName = "New Connection"
                if (sharedPref.contains(newName)) {
                    var connCount = 1
                    newName = "New Connection($connCount)"
                    while (sharedPref.contains(newName)) {
                        connCount = connCount.inc()
                        newName = "New Connection($connCount)"
                    }
                }

                // Sets the new name to name text field
                vEdit.edit_saved_conn_name.setText(newName)
                // Removes ip text field
                vEdit.edit_saved_conn_ip.visibility = View.GONE

                // Shows keyboard
                dConnName.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                dConnName.show()

                // Requests focus for name text field
                vEdit.edit_saved_conn_name.requestFocus()
                // Sets up keyboard focusing
                ActivityUtil.setKeyboardFocus(vEdit.rootView as ViewGroup,
                        listOf(vEdit.edit_saved_conn_name),
                        vEdit.context)
            }
        }

        // Sets up JsonObjecRequest
        val request = JsonObjectRequest(Request.Method.GET, url + GET_META_DATA, null,
                // Response
                {
                    val response = it.getString("available")
                    if (response == "true") {
                        // Dismisses progressBar dialog
                        dProgress.dismiss()
                        // Changes Connect Buttons color to green
                        button_connect.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                        // Checks if this IP address already exists in SharedPreferences
                        if (!sharedPref.all.map { IP -> IP.value }.contains(ip))
                            dConnNew.show()
                        else
                            navigateToMain(url)
                    }
                },
                // Error
                {
                    dProgress.dismiss()
                    Toast.makeText(this,
                            "Connection Error", // Fixme: put string to xml?
                            Toast.LENGTH_LONG).show()
                }
        )

        // Sets up a custom retryPolicy for the request
        request.retryPolicy = DefaultRetryPolicy(
                TIMEOUT_DURATION,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        // Adds request to a RequestQueue which executes its requests asynchronously
        queue.add(request)
    }

    /**
     * Navigates to [MainActivity] and clears all open Activities
     * [url] expects the full api URL address
     */
    private fun navigateToMain(url: String) {
            val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("url", url)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
    
}