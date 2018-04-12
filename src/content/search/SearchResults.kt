package content.search

import gui.QuizletResultsListView
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.scene.control.ProgressBar
import java.util.concurrent.FutureTask

/**
 * ======SEARCH MANAGER FOR DUMMIES=====
 *  (1) Instantiate one SearchManager for whole StudyPied session
 *  (2) Add the various endpoints you want to retrieve content from
 *          This automatically increments progress. It is each endpoints job to call incrementProgress(x) such that the
 *          sum of all x's are 1.0
 *  (3) Call postResults()
 *
 */
class SearchManager(private val progressBar : ProgressBar, private var listView : QuizletResultsListView) {


    // Some number from 0 to 1, reset every search
    private var progress : Double = 0.0
        private set(value) {
            println("progress: $value")
            progressBar.progress = value
            field = value
        }

    // It is SearchManagers job to offset progress by a factor of the number of endpoints
    // It is the endpoints job to offset progress by a factor of the number of queriesArray
    fun incrementProgress(value : Double) {
        progress += (value / (endpoints.size))
    }

    // We want to hold in memory each endpoint that we are using at a given time
    val endpoints = ArrayList<ContentEndpoint>()

    // Accessed by ContentEnpoints for retrieving search
    var queriesArray: Array<String> = arrayOf("") // default value updated every search (every call of postResults)

    fun postResults(queriesParam : Array<String>) {
        listView.items = FXCollections.observableList(FXCollections.observableArrayList())
        progress = 0.0
        queriesArray = queriesParam
        for (e in endpoints) {
            e.postResults()
        }
        // each endpoint will be responsible
    }


    fun addResultToView(rw : ResultWrapper<out Any> ) {
        val updateUITask: FutureTask<Unit> =
                FutureTask(
                        {
                            listView.items.add(rw)
                        },
                        Unit)


        // submit for execution on FX Application Thread:


        Platform.runLater(updateUITask)

    }

}

open abstract class ContentEndpoint(searchManager : SearchManager) {
    private var sm: SearchManager = searchManager
        set(value) {
            value.endpoints.add(this)
            field = value
        }

    protected var queries : Array<String> = sm.queriesArray
        get() = sm.queriesArray

    // It is SearchManagers job to offset progress by a factor of the number of endpoints
    // It is the endpoints job to offset progress by a factor of the number of queriesArray
    protected fun incrementProgress(value : Double) {
        sm.incrementProgress(value / (queries.size))
    }

    abstract fun postResults()
}