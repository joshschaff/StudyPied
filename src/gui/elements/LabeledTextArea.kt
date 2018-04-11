package  gui.elements

import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.BorderPane

class LabeledTextArea(labelText: String) : BorderPane() {
    var label : Label = Label(labelText)
    var textArea : TextArea = TextArea()


    constructor(labelText: String, textAreaText: String) : this(labelText) {
        textArea.text = textAreaText
    }

    init {
        super.setMaxHeight(Double.MAX_VALUE)
        textArea.isWrapText = true
        textArea.maxHeight = Double.MAX_VALUE
        // used to extend vbox
        // super.getChildren().addAll(label, textArea)
        super.setTop(label)
        super.setCenter(textArea)
    }
}