package  content.publicAPI
import java.io.Serializable

sealed class GeneralTerm(open var term : String) : Serializable {
    abstract var definition : Array<String>
    var complete : Boolean = false
    // initial value
    abstract var queries: Array<String>


    companion object {
        private const val serialVersionUID: Long = 456
    }


    override fun toString(): String {
        return ("${if (complete) "✓" else "✘"}") + (if (term.equals(queries)) term else "$term {$queries}")
    }

    fun updateQuery(delimited : String) : Boolean {
        val _queries : Array<String> = delimited.split("/").toTypedArray()
        when (_queries.size) {
            definition.size -> {
                queries = _queries
                return true
            }
            else -> return false
        }

    }

    fun queryString() : String {
        var ret = ""
        for (i in queries.indices) {
            ret += queries[i]
            if (i != queries.size - 1) {
                ret += " / "
            }
        }
        return ret
    }


    data class SimpleTerm(override var term: String) : GeneralTerm(term) {
        override var definition: Array<String> = arrayOf("")
        override var queries: Array<String> = arrayOf(term)

        override fun toString(): String {
            return super.toString()
        }

        companion object {
            private const val serialVersionUID: Long = 567
        }
    }

    data class PropertiedTerm(override var term: String, val properties : Array<String>) : GeneralTerm(term) {
        override var definition: Array<String> = Array(properties.size, {""})
        override var queries: Array<String> = arrayOf("")

        override fun toString(): String {
            return super.toString()
        }

        companion object {
            private const val serialVersionUID: Long = 678
        }
    }
}




