package content

import content.driveAPI.GoogleAuthenticator
import java.io.ByteArrayOutputStream
import org.apache.poi.xwpf.usermodel.*
import java.io.ByteArrayInputStream
import content.publicAPI.*
import content.publicAPI.GeneralTerm
import content.publicAPI.StudyGuide


//1FWatZZUFOhlltMeWc3sWR99McHQuCqiOAf82XoHzHYA


/* HOW TO CONVERT DRIVE DOWNLOAD TO POI FORMAT

        val outputStream2 = ByteArrayOutputStream()
        drive.files().export(id, "application/vnd.openxmlformats-officedocument.wordprocessingml.document").executeMedia().download(outputStream2) // https://developers.google.com/drive/v2/web/manage-downloads



        val input : ByteArrayInputStream = ByteArrayInputStream(outputStream2.toByteArray())

        //.executeMediaAndDownloadTo(str)
        val doc : XWPFDocument = XWPFDocument(input)

        doc.write(FileOutputStream(File(java.io.File(
                System.getProperty("user.home"), "StudyPied").path + "/guide.DOCX")))
 */



// constructed from a drive file


fun main(arxs: Array<String>) {

    val id: String = "1fw49fGH-k07nPAFG_0t2-PyFM38DPFJ_-oADyaYpeDA"

    val drive = GoogleAuthenticator().driveService

    val outputStream2 = ByteArrayOutputStream()
    drive.files().export(id, "application/vnd.openxmlformats-officedocument.wordprocessingml.document").executeMedia().download(outputStream2) // https://developers.google.com/drive/v2/web/manage-downloads

    val input: ByteArrayInputStream = ByteArrayInputStream(outputStream2.toByteArray())

    //.executeMediaAndDownloadTo(str)
    val doc: XWPFDocument = XWPFDocument(input)

/*
    for (table in doc.tables) {
        var properties : Array<String> = table.getRow(0).
                // Rids of first column (used to hold terms)
                tableCells.subList(1, table.getRow(0).tableCells.size)
                // each cell now becomes a list of paragraphs
                .map {c -> c.paragraphs}
                // each list of paragraphs becomes a single string
                .map { l-> l.map { p -> p.paragraphText }.joinToString("", prefix = "", postfix = "", limit = -1, truncated = "") }.toTypedArray()
        var terms : Array<String> = table.rows
                // rids of row i.e. empty upper left hand corner cell
                .subList(1, table.rows.size)
                // retrieves first cell of each row (the one holding a term)
                .map {r -> r.getCell(0)}
                // each cell now becomes a list of paragraphs
                .map {c -> c.paragraphs}
                // each list of paragraphs becomes a single string
                .map { l-> l.map { p -> p.paragraphText }.joinToString("", prefix = "", postfix = "", limit = -1, truncated = "") }.toTypedArray()

        for (property in properties) {
            println("P: $property")
        }
        for (term in terms) {
            println("T: $term")
        }


        val delimiters = arrayOf("terms", "people", "questions")

        val tableBodyIndex : Int = doc.bodyElements.indexOf(table)
        val delimiterBodyIndices : Array<Int> = delimiters
                .map { d ->
                    doc.bodyElements.indexOfFirst { e ->
                        if (e is XWPFParagraph) {
                            e.paragraphText.toLowerCase().contains(d.toLowerCase())
                        } else false
                    }
                }.toTypedArray()

        println("table index ${tableBodyIndex}")
        delimiterBodyIndices.indices.forEach { i -> println("delimiter \"${delimiters[i]}\" index is ${delimiterBodyIndices[i]}") }



    }



    //println("hopefully # of rows $tables")

    //doc.paragraphs.forEach { p -> println(p.paragraphText) }*/


    val delimiters = arrayOf("terms", "people", "questions")

    val delimiterBodyIndices: Array<Int> = delimiters
            .map { d ->
                doc.bodyElements.indexOfFirst { e ->
                    if (e is XWPFParagraph) {
                        e.paragraphText.toLowerCase().trim().equals(d.toLowerCase().trim())
                    } else false
                }
            }.toTypedArray()

    var generalTerms: MutableList<MutableList<in GeneralTerm>> = mutableListOf()
    for (i in delimiterBodyIndices) {
        generalTerms.add(mutableListOf())
    }


    // Fill up our general terms
    for (i in delimiterBodyIndices.indices) {
        // for each delimiter we're gonna create a list of all elements from...
        doc.bodyElements.subList(
                // The delimiter itself
                delimiterBodyIndices[i] + 1,
                // to either the next delimiter or the end of the document (if there is no next delimiter)
                if (i == delimiterBodyIndices.size - 1) {
                    println("this is last")
                    doc.bodyElements.size
                } else {
                    println("this is not last")
                    delimiterBodyIndices[i + 1]
                })
                .forEach { e: IBodyElement ->
                    when (e) {
                    // Everywhere you see "e" it is inferred to be a paragraph
                        is XWPFParagraph -> {
                            // Get rid of blank paragraphs
                            if (!e.paragraphText.isNullOrBlank()) {
                                generalTerms[i].add(GeneralTerm.SimpleTerm(e.paragraphText.trim()))
                            }
                        }
                    // Everywhere you see "e" it is inferred to be a table
                        is XWPFTable -> {
                            var properties: Array<String> = e.getRow(0).
                                    // Rids of first column (used to hold terms)
                                    tableCells.subList(1, e.getRow(0).tableCells.size)
                                    // each cell now becomes a list of paragraphs
                                    .map { c -> c.paragraphs }
                                    // each list of paragraphs becomes a single string
                                    .map { l ->
                                        l.map { p -> p.paragraphText }
                                                .joinToString("", prefix = "", postfix = "", limit = -1, truncated = "")
                                    }.toTypedArray()
                            var terms: Array<String> = e.rows
                                    // rids of row i.e. empty upper left hand corner cell
                                    .subList(1, e.rows.size)
                                    // retrieves first cell of each row (the one holding a term)
                                    .map { r -> r.getCell(0) }
                                    // each cell now becomes a list of paragraphs
                                    .map { c -> c.paragraphs }
                                    // each list of paragraphs becomes a single string
                                    .map { l ->
                                        l.map { p -> p.paragraphText }
                                                .joinToString("", prefix = "", postfix = "", limit = -1, truncated = "")
                                    }.toTypedArray()

                            terms.map { s -> GeneralTerm.PropertiedTerm(s, properties) }.forEach { t -> generalTerms[i].add(t) }
                        }
                    }
                }


    }

    for (terms in generalTerms) {
        println("_______________________")
        for (t in terms) {
            if (t is GeneralTerm.SimpleTerm) {
                println(t)
            } else if (t is GeneralTerm.PropertiedTerm) {
                println(t)
            }
        }
    }

}




open class DocxGuideBuilder(doc : XWPFDocument, delimiters : List<String>){



    var guide : StudyGuide

    init {

        val delimiterBodyIndices: Array<Int> = delimiters
                .map { d ->
                    doc.bodyElements.indexOfFirst { e ->
                        if (e is XWPFParagraph) {
                            e.paragraphText.toLowerCase().trim().equals(d.toLowerCase().trim())
                        } else false
                    }
                }.toTypedArray()


        // 2D list used to instantiate study guide
        var generalTerms: MutableList<MutableList<in GeneralTerm>> = mutableListOf()

        // Populates outer list with empty inner lists
        for (i in delimiterBodyIndices) {
            generalTerms.add(mutableListOf())
        }


        // Fill up our general terms
        for (i in delimiterBodyIndices.indices) {
            // for each delimiter we're gonna create a list of all elements from...
            doc.bodyElements.subList(
                    // The delimiter itself
                    delimiterBodyIndices[i] + 1,
                    // to either the next delimiter or the end of the document (if there is no next delimiter)
                    if (i == delimiterBodyIndices.size - 1) { doc.bodyElements.size } else { delimiterBodyIndices[i + 1] })
                    .forEach { e: IBodyElement ->
                        when (e) {
                        // Everywhere you see "e" it is inferred to be a paragraph
                            is XWPFParagraph -> {
                                // Get rid of blank paragraphs
                                if (!e.paragraphText.isNullOrBlank()) {
                                    generalTerms[i].add(GeneralTerm.SimpleTerm(e.paragraphText.trim()))
                                }
                            }
                        // Everywhere you see "e" it is inferred to be a table
                            is XWPFTable -> {
                                var properties: Array<String> = e.getRow(0).
                                        // Rids of first column (used to hold terms)
                                        tableCells.subList(1, e.getRow(0).tableCells.size)
                                        // each cell now becomes a list of paragraphs
                                        .map { c -> c.paragraphs }
                                        // each list of paragraphs becomes a single string
                                        .map { l ->
                                            l.map { p -> p.paragraphText }
                                                    .joinToString("", prefix = "", postfix = "", limit = -1, truncated = "")
                                        }.toTypedArray()
                                var terms: Array<String> = e.rows
                                        // rids of row i.e. empty upper left hand corner cell
                                        .subList(1, e.rows.size)
                                        // retrieves first cell of each row (the one holding a term)
                                        .map { r -> r.getCell(0) }
                                        // each cell now becomes a list of paragraphs
                                        .map { c -> c.paragraphs }
                                        // each list of paragraphs becomes a single string
                                        .map { l ->
                                            l.map { p -> p.paragraphText }
                                                    .joinToString("", prefix = "", postfix = "", limit = -1, truncated = "")
                                        }.toTypedArray()

                                terms.map { s -> GeneralTerm.PropertiedTerm(s, properties) }.forEach { t -> generalTerms[i].add(t) }
                            }
                        }
                    }


        }

        for (terms in generalTerms) {
            println("_______________________")
            for (t in terms) {
                if (t is GeneralTerm.SimpleTerm) {
                    println(t)
                } else if (t is GeneralTerm.PropertiedTerm) {
                    println(t)
                }
            }
        }

        guide = StudyGuide(generalTerms, delimiters, doc.paragraphs.map { p -> p.paragraphText }.filter { s -> s.isNotBlank() }.first())
    }


}






/*
class DriveGuideBuilder {




    val outputStream2 = ByteArrayOutputStream().also { drive.files().export(id, "application/vnd.openxmlformats-officedocument.wordprocessingml.document").executeMedia().download(outputStream2) // https://developers.google.com/drive/v2/web/manage-downloads }




    constructor(id : String, drive: Drive, delimiters : List<String>) : DocxGuideBuilder())
    constructor {




        val input : ByteArrayInputStream = ByteArrayInputStream(outputStream2.toByteArray())

        //.executeMediaAndDownloadTo(str)
        val doc : XWPFDocument = XWPFDocument(input)
        return DocxGuideBuilder(doc, delimiters)
    }
}*/