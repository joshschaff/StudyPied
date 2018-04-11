import content.quizletAPI.QuizletAPIManager
import content.quizletAPI.QuizletObject
import content.quizletAPI.TermWrapper
import content.search.ContentEndpoint
import content.search.ResultWrapper
import content.search.SearchManager
import javafx.application.Platform
import org.apache.http.client.fluent.Request
import org.apache.http.client.utils.URIBuilder
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.util.concurrent.FutureTask
import java.util.regex.Matcher
import java.util.regex.Pattern

class QuizletEndpoint(val sm : SearchManager, val topic : String) : ContentEndpoint(sm) {
    override fun postResults() {
        for (q in queries) {
            postQuizletTerms(q)
        }
    }

    // Increments progress .8 = (.04 x 2 action items x 10 results)
    private fun postQuizletTerms(query: String) {
        // TODO :: in formatting search terms and criteria, remove all non alphanumeric characters and delete all values between parenthesis??? - does newbug put dates

        val ids : Array<String> = getQuizletIDs(query) // Progress = 0.2
        //println("there are ${ids.size} ids")


        val manager : QuizletAPIManager = QuizletAPIManager("CPkQMhGzqs")
        for (id in ids) {
            //println(manager.getSet(id))
        }


        val sets : ArrayList<QuizletObject.QuizletSet> = ArrayList<QuizletObject.QuizletSet>()

        for (id : String in ids) {
            if (id.toIntOrNull() != null) {
                sets.add(manager.getSet(id))
            }
            incrementProgress(.04)
        }



        //val terms : ArrayList<QuizletObject.QuizletTerm> = ArrayList<QuizletObject.QuizletTerm>()

        for (set : QuizletObject.QuizletSet in sets) {
            //terms.add(set.terms.maxBy { term -> getContains(term.term + term.definition, query)} ?: QuizletObject.QuizletTerm("", "Term not found", "definition not found", 0))

            set.terms.withIndex().forEach { e -> println("index: ${e.index}") }

            val matchIndex = set.terms.withIndex().maxBy { indexedTerm -> getContains(indexedTerm.value.term + indexedTerm.value.definition, query)}?.index
            if (matchIndex != null && matchIndex != -1) println(set.terms[matchIndex])

            if (matchIndex != null && matchIndex != -1) {
                sm.addResultToView(ResultWrapper.QuizletResultWrapper(set.terms[matchIndex], set.title))
            }
            incrementProgress(.04)



            /* TODO:: Do you need to do this in another thread..??
            val updateUITask: FutureTask<Unit> =
                    FutureTask(
                            {
                                //listView.items = FXCollections
                                //       .observableList(FXCollections.observableList(terms.indices.map{i -> TermWrapper("", terms[i], sets[i].title)}))
                            },
                            Unit)


            // submit for execution on FX Application Thread:
            Platform.runLater(updateUITask)*/



        }
        /*val terms : List<QuizletObject.QuizletTerm> = sets.map { set-> set.terms }
                .map { terms -> terms
                        .maxBy { term -> getContains(term.term + term.definition, queries) } }.onEach { progress += .06 }.filterIsInstance<QuizletObject.QuizletTerm>()
*/
        println("Query is : ${query}")

        //println("wtf is ging on")
        //println("there are ${terms.size} terms")
        //terms.forEach { term -> println(term) }
        //println("we should have printed all terms by now..")
    }

    // Increments progress .2 (.1 x 2)
    private fun getQuizletIDs(query : String) : Array<String> {
        val uri4: URI = URIBuilder()
                .setScheme("https")
                .setHost("www.googleapis.com")
                .setPath("/customsearch/v1")
                .addParameter("cx", "002652694160528428543:sh8jtnkkaew")
                .addParameter("key", "AIzaSyC7KUL7MKqmqpFucO3CgbuEEMoYO8rPPTg")
                .addParameter("q", "${topic} ${query}")
                .build()

        //println(uri4)
        val br : BufferedReader = BufferedReader(InputStreamReader(Request.Get(uri4).execute().returnResponse().entity.content))

        incrementProgress(0.1)

        val result = StringBuffer()

        val arryay = br.lines().toArray()

        for (i in arryay.indices) {
            //if (arryay[i].contains("items"))
            result.append(arryay[i])
        }

        var content = result.toString()

        //println(content)


        // https://stackoverflow.com/a/28269120
        var containedUrls : List<String> = ArrayList<String>();
        var urlRegex : String  = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        var pattern : Pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        var urlMatcher : Matcher = pattern.matcher(content);

        while (urlMatcher.find())
        {
            containedUrls+= content.substring(urlMatcher.start(0),
                    urlMatcher.end(0))
        }



        val ids : Array<String> = containedUrls.filter{!it.contains("u003")}.filter { !it.contains("google") }
                .filter{!it.contains("...")}.distinct().map { it.substring(it.indexOf(".com") +5)}
                .map{it.substring(0, it.indexOf("/"))}.map{it.toString()}.toTypedArray()
        //, it.substring(it.indexOf(".com")+5).indexOf("/"))/*+4, it.substring(it.indexOf(".com")+5).indexOf("/")) )}

        for (id : String in ids) {
            println(id)
        }

        incrementProgress(0.1)

        return ids
    }


    // algorithm for findng best term in set
    private fun getContains(searchStr : String, findStr : String) : Int {


        // Kotlin doesn't have Java's str.replaceAll({Regex as string}, replacement)
        // Instead you have to call replace on an actual regex object
        // (or else you can instantiate one from string with str.toRegex())
        // https://stackoverflow.com/a/45929744
        val re = Regex("[^A-Za-z0-9]")


        //println("pattern:\t${re.replace(findStr, "").toLowerCase()}")
        //println("matcher:\t${re.replace(searchStr, "").toLowerCase()}")


        val pattern = Pattern.compile(re.replace(findStr, "").toLowerCase())
        val matcher = pattern.matcher(re.replace(searchStr, "").toLowerCase())
        var count = 0
        while (matcher.find()) count++
        return count
    }
}