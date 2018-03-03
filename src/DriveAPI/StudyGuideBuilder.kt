package DriveAPI

import com.google.api.services.drive.Drive
import java.io.ByteArrayOutputStream
import java.io.Serializable


//1FWatZZUFOhlltMeWc3sWR99McHQuCqiOAf82XoHzHYA


// constructed from a drive file
class StudyGuideBuilder(
        id : String,
        drive : Drive) : Serializable {


    private val termDelim : String = "terms"
    private val peopDelim : String = "people"

    // not gonna do questions and charts, will instead have end delimiter for end of people
    //val quesDelim : String = "Questions"

    private val quesDelim : String = "questions"

    val guide : StudyGuide

    init{
        //println(drive.files().get(id))

        //val str : String
        val outputStream = ByteArrayOutputStream()
        drive.files().export(id, "text/plain").executeMedia().download(outputStream) // https://developers.google.com/drive/v2/web/manage-downloads

                //.executeMediaAndDownloadTo(str)
        val str = outputStream.toString()
                //.toString().replace("\n+", "\n")



        val list : List<String> = str.split("\n")//.filter { str -> str != "" }

        val list2 = list.filter { s-> !s.isBlank() && !s.isBlank() }.map { str -> str.toLowerCase().trim() }


        list2.indices.forEach { i -> println("$i:${list2[i]}" ) }



        val title = list2[0]


        println("we're runnign the sg constructor...")

        println("terms index: ${list2.indexOf("terms")}")

        println("term delim index: ${list2.indexOf(termDelim)}")
        println("people delim index: ${list2.indexOf(peopDelim)}")


        val terms = list2.subList(list2.indexOf(termDelim)+1, list2.indexOf(peopDelim))
                .map { str -> GeneralTerm(str, "", false) }
        val people = list2.subList(list2.indexOf(peopDelim)+1, list2.indexOf(quesDelim))
                .map { str -> GeneralTerm(str, "", false) }
        val questions = list2.subList(list2.indexOf(quesDelim)+1, list2.size)
                .map { str -> GeneralTerm(str, "", false) }

        guide= StudyGuide(terms,people,questions,title)
    }

    fun assemble() : Unit{}

}


data class StudyGuide(var terms : List<GeneralTerm>,
                      var people : List <GeneralTerm>,
                      var questions : List <GeneralTerm>,
                      var title : String) : Serializable




data class GeneralTerm(val term : String, var definition : String, var complete : Boolean) : Serializable {
    // initial value
    var query : String = term


    override fun toString(): String {
        return ("${if (complete) "✓" else "✘"}") + (if (term.equals(query)) term else "$$term {$query}")
    }
}
