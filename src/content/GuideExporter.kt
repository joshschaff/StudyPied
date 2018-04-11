package content

import javafx.scene.control.TextInputDialog
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileOutputStream
import content.publicAPI.*
import content.publicAPI.GeneralTerm
import content.publicAPI.StudyGuide

fun saveToDocx(sg : StudyGuide, fileName : String) {



    // directory + "/" + name + "." + extension
    var filePath = java.io.File(
            System.getProperty("user.home"), "StudyPied").path + "/${fileName}.DOCX"
    TextInputDialog(filePath).showAndWait()
            .ifPresent({ response ->
                filePath = response
            })
    val document = XWPFDocument()

    // for the amount of categories, the same as indices of sg.terms
    for (i : Int in sg.categories.indices) {
        val tmpParagraph = document.createParagraph()
        val tmpRun = tmpParagraph.createRun()
        tmpRun.setText("${sg.categories[i]}")
        tmpRun.fontSize = 18
        tmpRun.isBold = true

        for (t in sg.terms[i]) {
            printTerm(t as GeneralTerm, document)
        }
    }


    /*
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

    }*/


    document.write(FileOutputStream(File(filePath)))
    document.close()


}

fun printTerm(term : GeneralTerm, doc : XWPFDocument) {
    val tmpParagraph = doc.createParagraph()
    val tmpRun = tmpParagraph.createRun()
    tmpRun.setText("${term.term}")
    tmpRun.fontSize = 12
    tmpRun.isBold = true


    for (def : String in term.definition) {
        println(def)
        val lines : List<String> = def.split(Regex("\\n"))
        println("term ${term} has ${lines.size} lines")
        for (line : String in lines) {
            val tmpParagraph2 = doc.createParagraph()
            val tmpRun2 = tmpParagraph2.createRun()
            tmpRun2.setText("${line}")
            tmpRun2.fontSize = 12
        }
    }

}

