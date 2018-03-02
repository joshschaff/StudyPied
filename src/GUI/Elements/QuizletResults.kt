package GUI.Elements

import QuizletAPI.QuizletObject
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableArray
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.*
import QuizletAPI.QuizletObject.QuizletTerm as Term
import javafx.scene.layout.HBox
import javafx.util.Callback
import javafx.scene.control.ListCell
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import sun.misc.Signal.handle
import QuizletAPI.TermWrapper
import javafx.geometry.Insets


/**
 * Because by far the most common use case for cells is to show textArea to a user, this use case is specially optimized for
 * within Cell. This is done by Cell extending from Labeled. This means that subclasses of Cell need only set the textArea
 * property, rather than create a separate Label and set that within the Cell. However, for situations where something
 * more than just plain textArea is called for, it is possible to place any Node in the Cell graphic property. Despite the
 * term, a graphic can be any Node, and will be fully interactive. For example, a ListCell might be configured with a
 * Button as its graphic. The Button textArea could then be bound to the cells item property. In this way, whenever the item
 * in the Cell changes, the Button textArea is automatically updated.
 *
 * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Cell.html#itemProperty
 */


// Constructor parameter is in essence a default value
// Further updates are observed by the items field https://docs.oracle.com/javafx/2/api/javafx/scene/control/ListView.html
class QuizletResultsListView(terms : ObservableList<TermWrapper>) : ListView<TermWrapper>() {

    // This allows utilizing the observable property in auto updating
    //var terms : ObservableList<TermWrapper> = terms


    init {
        cellFactory = object : Callback<ListView<TermWrapper>, ListCell<TermWrapper>> {
           override fun call(p0: ListView<TermWrapper>): ListCell<TermWrapper> {
               return TermCell()
            }
        }
        items = terms
        maxHeight = Double.MAX_VALUE




        //children.filter { it -> it.isFocused }.map { it -> it.accessibleText }.forEach { it -> println(it) }
    }
}


internal class TermCell : ListCell<TermWrapper>() {
    public override fun updateItem(term: TermWrapper?, empty: Boolean) {
        super.updateItem(term, empty)

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            // see explanation at top of class


            val hBox = HBox()
            //hBox.width = listView.width
            val termBox = TextArea()
            termBox.isEditable = false
            termBox.isWrapText = true
            HBox.setHgrow(termBox, Priority.ALWAYS)

            termBox.text = "[ ${term?.term?.term} / ${term?.setTitle} ]"
            termBox.text += "\n${term?.term?.definition}"

            termBox.autosize()
            //hBox.autosize()


            val button = Button("Add Selection")
            VBox.setVgrow(button, Priority.ALWAYS)
            button.onAction = EventHandler {
                // Kotlin makes you run your code right in your eventHandler uhhhh....
                //https://stackoverflow.com/questions/36516330/from-java-to-kotlin
                //override fun handle(e: ActionEvent) {
                    println("at least we here")
                    listView.selectionModel.select(term)
                    /**
                     * Takes advantaged of the ListCell's superclass of Labelled by storing the passed textArea in the
                     * "textArea" property
                     */

                    println("selected textArea is ${termBox.selectedText}")

                    // TODO :: if nothing selected just add the whole box
                    term?.msgCarrier = termBox.selectedText
                    termBox.end()

                    println("msgCarrier property is ${term?.msgCarrier}")
                //}
            }

            hBox.children.addAll(termBox, button)


            graphic = hBox
            //graphic = (Label(item.toString()))
            //setTextArea(item.toString());
        }

        //val cell : ListCell<Term> = ListCell()
        //textArea = term?.term ?: "null"
        //children.add(Label(term?.definition))

    }
}



