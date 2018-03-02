import DriveAPI.GeneralTerm
import DriveAPI.StudyGuide
import GUI.Elements.FxUtilTest
import GUI.Elements.ListManager
import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage

import QuizletAPI.SearchResults
import QuizletAPI.TermWrapper
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.HBox
import java.util.concurrent.FutureTask
import QuizletAPI.QuizletObject.QuizletSet as Set
import QuizletAPI.QuizletObject.QuizletTerm as QuizletTerm

import GUI.Elements.QuizletResultsListView as QListView
import javafx.scene.layout.Priority
import java.io.FileOutputStream
import javax.swing.text.StyleConstants.setFontSize


import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File


// Only variable I use twice?



/*fun main(args : Array<String>) {


    for (term in SearchResults("remember the maine").terms) {
        println(term)
    }

}*/

val sgFileID = "1FWatZZUFOhlltMeWc3sWR99McHQuCqiOAf82XoHzHYA"





class StudyPied : Application() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            //(might have to do that in start)
            // TODO:: start a filemanager and load any found sgs as the sg field
            // then the getter in the sg field will see if there is one or not
            // if there is not then grab this from the internet?

            //StudyGuide("1FWatZZUFOhlltMeWc3sWR99McHQuCqiOAf82XoHzHYA", GoogleAuthenticator().driveService)

            GoogleAuthenticator().driveService


            launch(StudyPied::class.java)


        }
    }



    /** Directory to store user credentials for this application.  */
    private val DATA_STORE_DIR = java.io.File(
            System.getProperty("user.home"), "StudyPied")

    private val FILE_EXTENSION = "GUIDE"

    private val FILE_NAME = "unit7"

    private val fileManager: FileManager<StudyGuide> = FileManager(FILE_EXTENSION, DATA_STORE_DIR)

    val sg : StudyGuide by lazy {
        println("objectListSize:${fileManager.objectList.size}")
        if (fileManager.objectList.size > 0) {
            fileManager.objectList[0]
        } else {
            println("MAKING A NEW STUDY GUIDE TF")
            val _sg = DriveAPI.StudyGuideBuilder("1FWatZZUFOhlltMeWc3sWR99McHQuCqiOAf82XoHzHYA", GoogleAuthenticator().driveService).guide
            println("serializng:${fileManager.safelySerialize(_sg, FILE_NAME)}")
            _sg
        }
    }

    //val sg : StudyGuide = DriveAPI.StudyGuide("1FWatZZUFOhlltMeWc3sWR99McHQuCqiOAf82XoHzHYA", GoogleAuthenticator().driveService)


    // both default values
    var selectedList: List<GeneralTerm> = sg.terms
        set(value) {
            // replace the whole fucking listmanager when we get a new list. Yep.
            // DO I NEED TO UPDATE VIEW THEN??
            listManager.updateOptions(value)
            //selectedTerm = value[0]x
            selectedTerm = null
            // progressLabel.text = getProgressString()
            field = value
        }


    val listManager: ListManager = ListManager(selectedList, "Edit Query")


    var selectedTerm: GeneralTerm? = null //selectedList[0]
        set(value) {
            listManager.button.isDisable = value == null
            listManager.checkBox.isDisable = value == null
            println("we're changing term")

            fileManager.serialize(sg, FILE_NAME)

            listManager.checkBox.isSelected = value?.complete ?: false

            // Save the old definition
            field?.definition = textArea.text
            // Load in the new definition
            textArea.text = value?.definition


            progressLabel.text = getProgressString()

            // UPDATES THE VIEW OF QUIZLET OBJECTS
            // TODO :: create loading bar
            // https://stackoverflow.com/a/9167420
            // SPECIFIES A THREAD (javafx) WITHIN A THREAD (results updating)!!!!!
            if (value != null) {
                Thread(Runnable {
                    progress.progress = 0.0
                    val sr : SearchResults = SearchResults(value.query, progress, listView)



                    val terms: List<TermWrapper> = sr.terms



                }).start()
            } else listView.items = FXCollections.observableList(FXCollections.observableArrayList())



            field = value
        }


    var progress = ProgressBar(1.0)

    var listView: GUI.Elements.QuizletResultsListView = GUI.Elements.QuizletResultsListView(FXCollections
            .observableList(FXCollections.observableArrayList()))
                                // They're already term wrappers
                                //.map { term -> TermWrapper("", term!!) })))

    // The box for entering your definition
    var textArea: TextArea = TextArea()





    /* necessary for binding progress bar each time
    fun getListViewItems(query : String) : ObservableList<TermWrapper> {
        val sr : SearchResults = SearchResults(query)
        progress.progressProperty().bind(sr.progress)
        return FXCollections.observableList(FXCollections.observableList(sr.terms))
    }*/


    val progressLabel = Label(getProgressString())

    fun getProgressString() : String {
        return "${selectedList.filter { term -> term.complete }.count()}/${selectedList.size}"
    }

    override fun start(primaryStage: Stage) {

        listManager.button.isDisable = true
        listManager.checkBox.isDisable = true



        val root: VBox = VBox(25.0)
        root.setAlignment(Pos.CENTER);
        root.setFillWidth(true);

        val listSelector = HBox()
        listSelector.padding = Insets(20.0)

        val tButton = Button("terms")
        val pButton = Button("people")
        val qButton = Button("questions")
        val docxButton = Button("save to docx")

        tButton.onAction = EventHandler { selectedList = sg.terms }
        pButton.onAction = EventHandler { selectedList = sg.people }
        qButton.onAction = EventHandler { selectedList = sg.questions }
        docxButton.onAction = EventHandler {saveDocx()}


        listSelector.children.addAll(tButton, pButton, qButton, docxButton, progressLabel)

        listManager.setSelectAction {
            if (FxUtilTest.getComboBoxValue(listManager.comboBox) != null ) {
                selectedTerm = FxUtilTest.getComboBoxValue(listManager.comboBox) as GeneralTerm
            }}
        listManager.setButtonAction {
            TextInputDialog(selectedTerm?.query).showAndWait()
                    .ifPresent({ response ->
                        selectedTerm?.query = response // set query to dialog result
                    })
            val tempTerm : GeneralTerm? = selectedTerm
            selectedTerm = null
            selectedTerm = tempTerm
            // TODO :: update listmanager secleted toString?
            // Actually this is not really necessary....
            // Don't need to clog up the UI with the querys...
        }
        listManager.setCheckAction { selectedTerm?.complete = listManager.checkBox.isSelected }

        //val terms: List<QuizletObject.QuizletTerm> = SearchResults(selectedTerm.query).terms
        //listView.terms = FXCollections
        //        .observableList(FXCollections.observableList(terms.map { term -> TermWrapper("", term!!) }))


        println("terms:${listView.items.size}")



        progress.prefWidthProperty().bind(root.widthProperty().subtract(20));  //  -20 is for
        // padding from right and left, since we aligned it to TOP_CENTER.


        textArea.isWrapText = true

        root.children.addAll(listSelector, listManager, progress, listView, textArea)

        // TODO :: FIND SOME WAY TO CARRY THE TEXT PROPERTY OF LSITCELL OVER TO THIS CLASS
        listView.selectionModel.selectedItemProperty().addListener(ChangeListener { observableValue, t_old, t_new ->


            // TODO  :: // MAKE THIS A FUNCTION (i.e. update)
            val updateUITask: FutureTask<Unit> =
                    FutureTask(
                            {
                                textArea.text += t_new.msgCarrier + "\n"
                                t_new.msgCarrier = ""
                                listView.selectionModel.clearSelection()
                                //println("msgCarrier is ${t_new.msgCarrier}")
                            },
                            Unit)

            // submit for execution on FX Application Thread:
            Platform.runLater(updateUITask)

        })




        // Pass stage into
        primaryStage.setScene(Scene(root));
        primaryStage.show();

        primaryStage.setOnCloseRequest {
            selectedTerm?.definition = textArea.text
            fileManager.serialize(sg, FILE_NAME) }

        primaryStage.sizeToScene();
    }


    fun saveDocx() {
        // directory + "/" + name + "." + extension
        var filePath = java.io.File(
                System.getProperty("user.home"), "StudyPied").path + "/${FILE_NAME}.DOCX"
        TextInputDialog(filePath).showAndWait()
                .ifPresent({ response ->
                    filePath = response
                })
        val document = XWPFDocument()
        for (term :GeneralTerm in sg.terms) {
            val tmpParagraph = document.createParagraph()
            val tmpRun = tmpParagraph.createRun()
            tmpRun.setText("${term.term}")
            tmpRun.fontSize = 12
            tmpRun.isBold = true


            println(term.definition)
            val lines : List<String> = term.definition.split(Regex("\\n"))
            println("term ${term} has ${lines.size} lines")
            for (line : String in lines) {
                val tmpParagraph2 = document.createParagraph()
                val tmpRun2 = tmpParagraph2.createRun()
                tmpRun2.setText("${line}")
                tmpRun2.fontSize = 12
            }

        }
        for (term :GeneralTerm in sg.people) {
            val tmpParagraph = document.createParagraph()
            val tmpRun = tmpParagraph.createRun()
            tmpRun.setText("${term.term}")
            tmpRun.fontSize = 12
            tmpRun.isBold = true


            println(term.definition)
            val lines : List<String> = term.definition.split(Regex("\\n"))
            println("term ${term} has ${lines.size} lines")
            for (line : String in lines) {
                val tmpParagraph2 = document.createParagraph()
                val tmpRun2 = tmpParagraph2.createRun()
                tmpRun2.setText("${line}")
                tmpRun2.fontSize = 12
            }

        }


        document.write(FileOutputStream(File(filePath)))
        document.close()
    }

    //override fun stop() {
        //This does nothing to force you to use the quit button
        //Yeah ik its bad practice I hope it doesnt crash either
        //super.stop()
    //}
}
