package lt.viko.eif.ajenkus.remotemoov.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import lt.viko.eif.ajenkus.remotemoov.R
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import lt.viko.eif.ajenkus.remotemoov.IPRecyclerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_connect_saved.*
import lt.viko.eif.ajenkus.remotemoov.activities.ConnectActivity

/**
 * [ConnectSavedFragment]
 * Fragment which hosts [RecyclerView] for holding [R.string.preference_saved_conns]
 * Layout in [R.layout.fragment_connect_saved]
 */
class ConnectSavedFragment : Fragment() {

    private companion object {
        lateinit var ipAdapter: IPRecyclerAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect_saved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Gets the host Activity of this Fragment instance
        val activity = activity as ConnectActivity
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Initializes RecyclerView
        initRecyclerView(view)
        // Retrieves Saved Connections SharedPreferences
        // and sets them as RecyclerView.Adapters list
        ipAdapter.setList(activity.retrieveSavedConns())
        // Checks if the list is empty
        isAdapterEmpty()

        // Event for checking when data is changed in RecyclerView.Adapter
        ipAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                // Gets all items from adapter and send them to be updated to activity
                 activity.updateSavedConns(ipAdapter.getItems())
                // Then retrieves the changed saved connections and set them as the new adapter list
                ipAdapter.setList(activity.retrieveSavedConns())
                // Performs a check if the list is empty
                isAdapterEmpty()
            }
        })

        button_connect.setOnClickListener {
            // Get string from selected item
            val ip = ipAdapter.getSelectedItem()?.findViewById<TextView>(R.id.saved_conn_ip)
            activity.connect(ip?.text.toString())
        }
        // Setting up OnClickListener also sets buttons to a clickable state
        // Even if the state is defined in xml as non clickable
        button_connect.isClickable = false
    }

    /**
     * Initializes RecyclerView
     */
    private fun initRecyclerView(view: View) {
        val recyclerIP = view.findViewById<RecyclerView>(R.id.recycler_ip)
        recyclerIP.apply {
            layoutManager = LinearLayoutManager(view.context)
            ipAdapter = IPRecyclerAdapter()
            recyclerIP.adapter = ipAdapter
        }
    }

    /**
     * Checks if [IPRecyclerAdapter] is empty
     * If it is then displays message to the user and disables [R.id.button_connect]
     */
    private fun isAdapterEmpty() {
        if (ipAdapter.itemCount == 0) {
            message_saved_conn.visibility = View.VISIBLE
            button_connect.backgroundTintList = ContextCompat.getColorStateList(view?.context!!, R.color.gray)
            button_connect.isClickable = false
        }
    }
}