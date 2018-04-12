package  content.publicAPI

import java.io.IOException
import java.io.ObjectStreamException
import java.io.Serializable

sealed class GeneralTerm(open var term : String) : Serializable {

    abstract var definition : Array<String>
    var complete : Boolean = false

    init {
        println("GeneralTerm's term is $term")
    }

    // Has default value of the term itself
    abstract var queries : QueriesMap

    companion object {
        private const val serialVersionUID: Long = 456
    }


    override fun toString(): String {
        return ("${if (complete) "✓" else "✘"}") + (if (term.equals(queries.toString().trim())) term else "$term ($queries)")
    }


    class SimpleTerm(term: String) : GeneralTerm(term) {
        init {
            println("SimpleTerms's term is $term")
        }
        override var definition: Array<String> = arrayOf("")

        override var queries: QueriesMap = QueriesMap { term }

        companion object {
            private const val serialVersionUID: Long = 567
        }
    }

    class PropertiedTerm(term: String, val properties : Array<String>) : GeneralTerm(term) {
        override var definition: Array<String> = Array(properties.size, {""})

        override var queries: QueriesMap = QueriesMap { QueriesMap.delimitedStringFromArray(properties) }

        companion object {
            private const val serialVersionUID: Long = 678
        }
    }
}




