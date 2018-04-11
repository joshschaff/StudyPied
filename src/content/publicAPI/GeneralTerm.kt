package  content.publicAPI
import java.io.Serializable

sealed class GeneralTerm(open val term : String) : Serializable {
    abstract var definition : Array<String>
    var complete : Boolean = false
    // initial value
    abstract var query : String


    companion object {
        private const val serialVersionUID: Long = 456
    }


    override fun toString(): String {
        return ("${if (complete) "✓" else "✘"}") + (if (term.equals(query)) term else "$term {$query}")
    }


    data class SimpleTerm(override val term: String) : GeneralTerm(term) {
        override var definition: Array<String> = arrayOf("")
        override var query: String = term

        override fun toString(): String {
            return super.toString()
        }
    }

    data class PropertiedTerm(override val term: String, val properties : Array<String>) : GeneralTerm(term) {
        override var definition: Array<String> = Array(properties.size, {""})
        override var query: String = term

        override fun toString(): String {
            return super.toString()
        }
    }
}




