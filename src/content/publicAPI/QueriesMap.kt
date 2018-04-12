package content.publicAPI

import java.io.IOException
import java.io.Serializable

/**
 * Maps each query to whether or not it is active in search
 * Constructor takes a function which returns a String of queries delimited w/ "/"
 */
class QueriesMap(fn : () -> String) : AbstractMap<String, Boolean>(), Serializable {
    override var entries: Set<Map.Entry<String, Boolean>> = entriesFromDelimitedString(fn())
        private set

    companion object {
        private const val serialVersionUID: Long = 789

        fun entriesFromDelimitedString(delimited: String) : Set<Map.Entry<String, Boolean>> =
                delimited.split("/").map{ s -> s.trim()}.map { s -> Pair(s, true)}.toMap().entries
        fun delimitedStringFromArray(queries : Array<String>) : String =
                queries.indices.map{ i -> queries[i]}.joinToString(" / ")
    }

    override fun toString(): String = delimitedStringFromArray(keys.toTypedArray())


    @Throws(IOException::class)
    private fun writeObject(out : java.io.ObjectOutputStream ) {
        out.writeObject(this.toString())
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: java.io.ObjectInputStream ) {
        this.entries = entriesFromDelimitedString(`in`.readObject() as String)
    }

}