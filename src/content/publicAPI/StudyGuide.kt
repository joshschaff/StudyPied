package content.publicAPI

import java.io.Serializable

data class StudyGuide(var terms : MutableList<MutableList<in GeneralTerm>>,
                      var categories : List<String>,
                      var title : String) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }
}