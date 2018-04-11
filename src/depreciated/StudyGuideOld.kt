package depreciated


//1FWatZZUFOhlltMeWc3sWR99McHQuCqiOAf82XoHzHYA
/*

// constructed from a drive file
class StudyGuideOld(
        id : String,
        drive : Drive) : Serializable {


    private val termDelim : String = "terms"
    private val peopDelim : String = "people"

    // not gonna do questions and charts, will instead have end delimiter for end of people
    //val quesDelim : String = "Questions"

    private val endDelim : String = "what problem was it trying to solve?"

    val terms : List<GeneralTerm>
    val people : List <GeneralTerm>
    val questions : List <GeneralTerm>


    // The name of the set, i.e. the unit
    val title : String

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


        title = list2[0]


        println("we're runnign the sg constructor...")

        println("terms index: ${list2.indexOf("terms")}")

        terms = list2.subList(list2.indexOf(termDelim)+1, list2.indexOf(peopDelim))
                .map { str -> GeneralTerm(str, "", false) }
        people = list2.subList(list2.indexOf(peopDelim)+1, list2.indexOf(endDelim))
                .map { str -> GeneralTerm(str, "", false) }
        questions = list2.subList(list2.indexOf(endDelim)+1, list2.size)
                .map { str -> GeneralTerm(str, "", false) }
    }

    fun assemble() : Unit{}

}

*/


/*data class GeneralTerm(val term : String, var definition : String, var complete : Boolean) : Serializable {
    // initial value
    var query : String = term


    override fun toString(): String {
        return if (term.equals(query)) term else "$term {$query}"
    }
}*/
