package gui

import javafx.collections.ObservableList
import javafx.scene.control.*
import content.quizletAPI.QuizletObject.QuizletTerm as Term
import javafx.util.Callback
import javafx.scene.control.ListCell
import content.quizletAPI.TermWrapper

import content.search.ResultWrapper
import content.search.ResultWrapper.*


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



val width = StudyPied.COLUMN_WIDTH - 100.0

// Constructor parameter is in essence a default value
// Further updates are observed by the items field https://docs.oracle.com/javafx/2/api/javafx/scene/control/ListView.html
class QuizletResultsListView(terms : ObservableList<ResultWrapper<out Any>>) : ListView<ResultWrapper<out Any>>() {

    // This allows utilizing the observable property in auto updating
    //var terms : ObservableList<TermWrapper> = terms



    init {

        cellFactory = object : Callback<ListView<ResultWrapper<out Any>>, ListCell<ResultWrapper<out Any>>> {
           override fun call(p0: ListView<ResultWrapper<out Any>>): ListCell<ResultWrapper<out Any>> {
               return ResultCell()
            }
        }
        items = terms
        maxHeight = Double.MAX_VALUE
        this.prefWidth = StudyPied.COLUMN_WIDTH - 100.0




        //children.filter { it -> it.isFocused }.map { it -> it.accessibleText }.forEach { it -> println(it) }
    }
}


internal class ResultCell : ListCell<ResultWrapper<out Any>>() {
    public override fun updateItem(result: ResultWrapper<out Any>?, empty: Boolean) {
        super.updateItem(result, empty)

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            // see explanation at top of class

            //hBox.width = listView.width
            when (result) {

                is ResultWrapper.QuizletResultWrapper -> {
                    val term = result.term

                    val termBox = LabeledTextArea("[ ${term.term} / ${result.setTitle} ]", term.definition )
                    termBox.textArea.isEditable = false
                    termBox.textArea.isWrapText = true
                    //termBox.maxWidth = super.widthProperty().get() - 25.0
                    termBox.label.isWrapText = true
                    // Something arbitraily small
                    termBox.label.prefWidth = StudyPied.COLUMN_WIDTH-100.0
                    termBox.textArea.prefWidth = StudyPied.COLUMN_WIDTH-100.0
                    termBox.textArea.autosize()
                    //HBox.setHgrow(termBox, Priority.ALWAYS)
                    graphic = termBox
                }
            }

        }


    }
}



