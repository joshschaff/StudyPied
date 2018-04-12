import content.DocxGuideBuilder
import content.driveAPI.*
import gui.DefinitionBox
import gui.FxUtilTest
import gui.ListManager
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import content.publicAPI.StudyGuide
import content.saveToDocx
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.*
import javafx.scene.layout.*
import content.quizletAPI.QuizletObject.QuizletSet as Set
import content.quizletAPI.QuizletObject.QuizletTerm as QuizletTerm
import gui.QuizletResultsListView as QListView
import content.publicAPI.*
import content.search.SearchManager

import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Priority
import javafx.scene.layout.BorderPane






// Only variable I use twice?



/*fun deprecated.main(args : Array<String>) {


    for (term in QuizletSearchResults("remember the maine").terms) {
        println(term)
    }

}*/

val sgFileID = "1FWatZZUFOhlltMeWc3sWR99McHQuCqiOAf82XoHzHYA"





class StudyPied : Application() {

    companion object {
        public final val COLUMN_WIDTH = 400.0

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

    private val FILE_NAME = "unit9"

    private val fileManager: FileManager<content.publicAPI.StudyGuide> = FileManager(FILE_EXTENSION, DATA_STORE_DIR)

    val sg : StudyGuide by lazy {
        println("objectListSize:${fileManager.objectList.size}")
        if (fileManager.objectList.size > 0) {
            fileManager.objectList[0]
        } else {


            val diag = TextInputDialog()
            diag.headerText = "Please enter a Google Doc ID which you have access to"


            var id = ""

            diag.showAndWait()
                    .ifPresent({ response ->
                        id = response
                    })

            println("MAKING A NEW STUDY GUIDE TF")


            val outputStream2 = ByteArrayOutputStream()
            GoogleAuthenticator().driveService.files()
                    .export(id, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    .executeMediaAndDownloadTo(outputStream2) // https://developers.google.com/drive/v2/web/manage-downloads }


            val input: ByteArrayInputStream = ByteArrayInputStream(outputStream2.toByteArray())

            //.executeMediaAndDownloadTo(str)
            val doc: XWPFDocument = XWPFDocument(input)


            //val delimiters : List<String> = listOf("Complete these during the Roundtable.")
            val delimiters : List<String> = listOf("Terms","People","Concepts")

            var _sg : StudyGuide = DocxGuideBuilder(doc, delimiters).guide
            //println("serializng:${fileManager.safelySerialize(_sg, FILE_NAME)}")
            _sg
        }
    }

    //val sg : StudyGuide = content.publicAPI.StudyGuide("1FWatZZUFOhlltMeWc3sWR99McHQuCqiOAf82XoHzHYA", GoogleAuthenticator().driveService)




    // both default values
    var selectedCategory: MutableList<in GeneralTerm> = sg.terms[0]
        set(value) {
            // replace the whole fucking listmanager when we get a new list. Yep.
            // DO I NEED TO UPDATE VIEW THEN??
            termManager.updateOptions(value)
            //selectedTerm = value[0]x
            selectedTerm = null
            // progressLabel.text = getProgressString()
            field = value
        }


    val termManager: ListManager = ListManager(selectedCategory, "Edit Query")




    var selectedTerm: GeneralTerm? = null //selectedCategory[0]
        set(value) {
            // quick and dirty solution to it being updated multiple times
            if (field != value) {
                termManager.button.isDisable = value == null
                termManager.checkBox.isDisable = value == null
                println("we're changing term")


                termManager.checkBox.isSelected = value?.complete ?: false

                // Save the old definition
                saveTerm()
                // Load in the new definition



                progressLabel.text = getProgressString()

                // UPDATES THE VIEW OF QUIZLET OBJECTS
                // TODO :: create loading bar
                // https://stackoverflow.com/a/9167420
                // SPECIFIES A THREAD (javafx) WITHIN A THREAD (results updating)!!!!!
                if (value != null) {
                    definitionBox.updateTerm(value)
                    Thread(Runnable {
                        progress.progress = 0.0
                        println("new term is $value" )
                        println("queriesArray is: ${value.queries}")


                        /*val terms: List<TermWrapper> = value.queriesArray
                                .map { q -> QuizletSearchResults(q, progress, lis) }
                        for (query in value.queriesArray) {

                        }
                        QuizletSearchResults(value.queriesArray, progress, listView)*/

                        if (value.queries != null) {
                            searchManager.postResults(value.queries.keys.toTypedArray())
                        }

                    }).start()
                } else listView.items = FXCollections.observableList(FXCollections.observableArrayList())



                field = value
            }

        }


    var progress = ProgressBar(1.0)

    var listView: gui.QuizletResultsListView = gui.QuizletResultsListView(FXCollections
            .observableList(FXCollections.observableArrayList()))
                                // They're already term wrappers
                                //.map { term -> TermWrapper("", term!!) })))


    val searchManager : SearchManager = SearchManager(progress, listView)

    // The box for entering your definition
    //var textArea: TextArea = TextArea()


    var definitionBox : DefinitionBox = DefinitionBox()


    /* necessary for binding progress bar each time
    fun getListViewItems(queriesArray : String) : ObservableList<TermWrapper> {
        val sr : QuizletSearchResults = QuizletSearchResults(queriesArray)
        progress.progressProperty().bind(sr.progress)
        return FXCollections.observableList(FXCollections.observableList(sr.terms))
    }*/


    val progressLabel = Label(getProgressString())

    fun getProgressString() : String {
        return "${selectedCategory.filter{t -> (t as GeneralTerm).complete }.count()}/${selectedCategory.size}"
    }


    val center = GridPane()

    override fun start(primaryStage: Stage) {


        searchManager.endpoints.add(QuizletEndpoint(searchManager, "apush"))

        termManager.button.isDisable = true
        termManager.checkBox.isDisable = true

        
        val categoryManager : ComboBox<String> = ComboBox(FXCollections.observableList(sg.categories))
        categoryManager.setOnAction { 
            selectedCategory= sg.terms[categoryManager.selectionModel.selectedIndex]
        }


        val root: BorderPane = BorderPane()
        //root.setAlignment(Pos.CENTER);
        //root.setFillWidth(true);

        val listSelector = HBox()
        listSelector.padding = Insets(20.0)
        
        val docxButton = Button("save to docx")

        
        docxButton.setOnAction { saveToDocx(sg, FILE_NAME) }


        listSelector.children.addAll(categoryManager, docxButton, progressLabel)

        termManager.setSelectAction {
            if (FxUtilTest.getComboBoxValue(termManager.comboBox) != null ) {
                selectedTerm = FxUtilTest.getComboBoxValue(termManager.comboBox) as GeneralTerm
            }}
        // edit eury button
        termManager.setButtonAction {
            selectedTerm?.queries = QueriesMap {
                val result = TextInputDialog(selectedTerm?.queries.toString()).showAndWait()
                if (result.isPresent) {
                    result.get()
                } else {
                    // reset to original value if dialog is cancelled
                    selectedTerm?.queries.toString()
                }
            }

            // update search after updating queries
            val tempTerm : GeneralTerm? = selectedTerm
            selectedTerm = null
            selectedTerm = tempTerm
            // TODO :: update listmanager secleted toString?
            // Actually this is not really necessary....
            // Don't need to clog up the UI with the queriesArray...
        }
        termManager.setCheckAction { selectedTerm?.complete = termManager.checkBox.isSelected }

        val top : TilePane = TilePane(Orientation.HORIZONTAL)
        top.children.addAll(listSelector,termManager)
        root.top = top

        //val terms: List<QuizletObject.QuizletTerm> = QuizletSearchResults(selectedTerm.queriesArray).terms
        //listView.terms = FXCollections
        //        .observableList(FXCollections.observableList(terms.map { term -> TermWrapper("", term!!) }))


        println("terms:${listView.items.size}")



        progress.prefWidthProperty().bind(root.widthProperty().subtract(20));  //  -20 is for
        // padding from right and left, since we aligned it to TOP_CENTER.
        root.bottom = progress







        /*
        tiles.setPadding(Insets(20.0, 10.0, 20.0, 0.0))
        tiles.hgap = 10.0
        tiles.vgap = 8.0
        tiles.children.addAll(listView,definitionBox)
        tiles.maxWidth = Double.MAX_VALUE

        tiles.prefWidth = primaryStage.width*/

        center.add(listView, 0, 0)
        center.add(definitionBox, 1,0)


        listView.prefWidth = 400.00
        definitionBox.prefWidth = 400.00


        //root.add(listView, 0, 1, 1, 2)
        //root.add(definitionBox, 1, 1, 1, 2)
        root.center = center

        BorderPane.setMargin(center,  Insets(15.0))
        
        //root.children.addAll(listSelector, termManager, progress, deprecated.main)

        
        // used for old copy selection button
        /*// TODO :: FIND SOME WAY TO CARRY THE TEXT PROPERTY OF LSITCELL OVER TO THIS CLASS
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

        })*/




        // Pass stage into
        primaryStage.setScene(Scene(root));
        primaryStage.show();

        primaryStage.minHeight = 600.00
        primaryStage.minWidth = 800.00

        //root.setPrefSize(800.0,600.0)
        //root.setMinSize(BorderPane.USE_PREF_SIZE, BorderPane.USE_PREF_SIZE);

        primaryStage.setOnCloseRequest {saveTerm()}

        primaryStage.sizeToScene();
    }
    
    fun saveTerm() {
        selectedTerm?.definition = definitionBox.definition
        fileManager.serialize(sg, FILE_NAME)
    }


    fun adjustCenter() {
        val column1 = ColumnConstraints(40.0, 40.0, java.lang.Double.MAX_VALUE)
        column1.hgrow = Priority.ALWAYS
        val column2 = ColumnConstraints(20.0)
        center.getColumnConstraints().addAll(column1, column2)
    }






    //override fun stop() {
        //This does nothing to force you to use the quit button
        //Yeah ik its bad practice I hope it doesnt crash either
        //super.stop()
    //}
}
