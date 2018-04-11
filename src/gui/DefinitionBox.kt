package gui


import content.publicAPI.GeneralTerm
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox

class DefinitionBox() : VBox() {

    private var definitionTextAreas : MutableList<TextArea> = mutableListOf()

    constructor(term : GeneralTerm) : this() {
        updateTerm(term)
    }

    fun updateTerm(term: GeneralTerm) {
        super.getChildren().clear()
        definitionTextAreas.clear()
        when (term) {
            is GeneralTerm.SimpleTerm -> {
                val ta = TextArea(term.definition[0])
                ta.maxHeight = Double.MAX_VALUE
                definitionTextAreas.add(ta)
                super.getChildren().addAll(definitionTextAreas)
            }
            is GeneralTerm.PropertiedTerm -> {
                term.properties.mapIndexed { i, s -> LabeledTextArea(s, term.definition[i]) }
                        .forEach { lta ->
                            super.getChildren().add(lta)
                            definitionTextAreas.add(lta.textArea)
                        }
            }

        }

    }


    var definition : Array<String> = arrayOf("")
        get() = definitionTextAreas.map { dta -> dta.text }.toTypedArray()
}