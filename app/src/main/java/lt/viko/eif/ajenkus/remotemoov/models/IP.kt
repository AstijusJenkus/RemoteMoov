package lt.viko.eif.ajenkus.remotemoov.models
import lt.viko.eif.ajenkus.remotemoov.R

/**
 * [IP] represents a [R.string.preference_saved_conns] SharedPreference instance
 */
data class IP(
    var name: String,
    var ip: String
) {
}