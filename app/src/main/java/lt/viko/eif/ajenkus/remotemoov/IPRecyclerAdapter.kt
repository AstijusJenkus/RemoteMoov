package lt.viko.eif.ajenkus.remotemoov

import android.view.*
import android.widget.Toast
import android.app.AlertDialog
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import lt.viko.eif.ajenkus.remotemoov.models.IP
import androidx.recyclerview.widget.RecyclerView
import lt.viko.eif.ajenkus.remotemoov.utils.ActivityUtil
import kotlinx.android.synthetic.main.item_saved_conn.view.*
import kotlinx.android.synthetic.main.dialog_conn_edit.view.*
import kotlinx.android.synthetic.main.fragment_connect_saved.view.*

/**
 * [IPRecyclerAdapter]
 * A [RecyclerView.Adapter] implementation to handle [R.string.preference_saved_conns] SharedPreferences in a [RecyclerView]
 * Items layout in [R.layout.item_saved_conn]
 * Items menu in [R.menu.menu_saved_conn]
 */
class IPRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var item: View? = null
    private var items: MutableList<IP> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return IPViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_saved_conn, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is IPViewHolder -> holder.bind(items[position])
        }
    }

    /**
     * Gets [items] as a [MutableList] of [IP] objects
     */
    fun getItems(): MutableList<IP> {
        return items
    }

    /**
     * Sets [IPRecyclerAdapter] item list
     * [ipList] is a [MutableList] of [IP] objects to set to
     */
    fun setList(ipList: MutableList<IP>) {
        items = ipList
    }

    /**
     * Retrieves the currently selected [item] in the list and returns it as [IP] object
     */
    fun getSelectedItem(): View? {
        return item
    }

    inner class IPViewHolder (itemView: View) :
        RecyclerView.ViewHolder(itemView),
        PopupMenu.OnMenuItemClickListener {

        init {
            // Overrides OnFocusChangeListener
            // When items is focused makes Connect button clickable and changes its color
            itemView.saved_conn_layout.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    item = view
                    val connectButton = view.rootView.button_connect
                    connectButton.backgroundTintList = ContextCompat.getColorStateList(view.context, R.color.button)
                    connectButton.isClickable = true
                }
            }

            // Overrides OnClickListener
            // When menu icon is clicked opens a PopupMenu
            itemView.saved_conn_menu.setOnClickListener {
                val popup = PopupMenu(itemView.context, itemView.saved_conn_menu)
                popup.inflate(R.menu.menu_saved_conn)
                popup.setOnMenuItemClickListener(this)
                popup.show()
            }
        }

        fun bind(ip: IP) {
            itemView.saved_conn_name.text = ip.name
            itemView.saved_conn_ip.text = ip.ip
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when(item?.itemId) {
                R.id.saved_conn_edit -> onItemEdit()
                R.id.saved_conn_delete -> onItemDelete()
            }

            return true
        }

        /**
         * Creates an edit [AlertDialog] with selected [IP] data
         * layout of this dialog can be found in: [R.layout.dialog_conn_edit]
         */
        private fun onItemEdit() {
            // Creates edit View from saved_conn_edit Layout
            val editView = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.dialog_conn_edit, null)
            // Binds the edit View and sets the title of the AlertDialog
            val builder = AlertDialog.Builder(itemView.context)
                    .setView(editView)
                    .setTitle("Edit")
                    .setCancelable(false)
            // Populates edit Views text fields with selected items data
            editView.edit_saved_conn_name.setText(items[adapterPosition].name)
            editView.edit_saved_conn_ip.setText(items[adapterPosition].ip)
            // Sets actions as buttons
            builder.setNegativeButton("Cancel", null)
            builder.setPositiveButton("Ok", null)
            // Create and show the dialog
            val dialog = builder.create()
            dialog.setOnShowListener {
                // Button Ok
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    // Get the name
                    val newName = editView.edit_saved_conn_name.text.toString()
                    val newIP = editView.edit_saved_conn_ip.text.toString()
                    // Check if the new name is the same as the current one
                    if (items[adapterPosition].name != newName) {
                        // If there's no same name already then update data
                        if (!items.map { it.name }.contains(newName)) {
                            items[adapterPosition].name = newName
                            items[adapterPosition].ip = newIP
                            notifyDataSetChanged()
                            dialog.dismiss()
                        } else { // Notify user that the name is occupied
                            Toast.makeText(itemView.context,
                            "This name already exists", // Fixme: put string to xml?
                            Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Checks if IP value has changed
                        if (items[adapterPosition].ip != newIP) {
                            items[adapterPosition].ip = newIP
                            notifyDataSetChanged()
                        }
                        dialog.dismiss()
                    }
                }
            }
            // Opens keyboard when dialog is shown
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.show()

            // Request focus of the name text field
            editView.edit_saved_conn_name.requestFocus()

            // Sets up keyboard focusing
            ActivityUtil.setKeyboardFocus(editView.rootView as ViewGroup,
                    listOf(
                            editView.edit_saved_conn_ip,
                            editView.edit_saved_conn_name
                    ),
                    editView.context)
        }

        /**
         * Creates a [AlertDialog] window asking user to confirm and
         * Deletes the currently selected [IP] from [items] list
         */
        private fun onItemDelete() {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("Delete").setMessage("Delete connection " + items[adapterPosition].name + "?")
            builder.setNegativeButton("Cancel") { dialog, which ->  }
            builder.setPositiveButton("Ok") { dialog, which ->
                items.removeAt(adapterPosition)
                notifyDataSetChanged()
            }
            builder.show()
        }

    }

}