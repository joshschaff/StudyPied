package QuizletAPI

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.http.client.fluent.Request
import org.apache.http.client.utils.URIBuilder
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URI
import QuizletAPI.RandomString
import FileManager


/**
 * RETRIEVES AN ACCESCTOKEN
 */


class QuizletAuthenticator(val client_id : String) {

    val token : TokenHeader by lazy {
        println("objectListSize:${fileManager.objectList.size}")
        if (fileManager.objectList.size > 0) {
            fileManager.objectList[0]
        } else {
            val token = requestToken(redirectForCode())
            println("serializng:${fileManager.safelySerialize(token, "token")}")
            token
        }
    }

    /** Directory to store user credentials for this application.  */
    private val DATA_STORE_DIR = java.io.File(
            System.getProperty("user.home"), ".credentials/studypied")

    private val FILE_EXTENSION = "QZLT"

    private val fileManager: FileManager<TokenHeader> = FileManager(FILE_EXTENSION, DATA_STORE_DIR)

    // TODO: allow the storing of a token so you don't have to authenticate every time lmao (Josh has a filemanager class for serialization) (use encryption?)

    private fun redirectForCode(): String {

        val Randomer = RandomString()

        val randomString = Randomer.nextString()

        var uri1: URI = URIBuilder()
                .setScheme("https")
                .setHost("quizlet.com")
                .setPath("/authorize")
                .setParameter("response_type", "code")
                .setParameter("client_id", client_id)
                .setParameter("scope", "read")
                .setParameter("state", randomString)
                //.setParameter("redirect_uri", "http://localhost/quizlet/oauth-callback.html")
                .build()

        println(uri1)


        println(InetAddress.getByName(null))


        // https://stackoverflow.com/questions/3033755/reading-post-data-from-html-form-sent-to-serversocket#21729100
        // user reports how to read GETs
        var gets: String = ""

        try {

            // https://stackoverflow.com/questions/2205073/how-to-create-java-socket-that-is-localhost-only#2994761
            // InetAddress.getByName(null) points to the loopback address (127.0.0.1)
            val ss: ServerSocket = ServerSocket(9090, 0, InetAddress.getByName(null))

            println("Waiting for connection")

            var final: Boolean = false

            while (!final) {
                try {
                    // wait for a connection
                    val remote: Socket = ss.accept()
                    // remote is now the connected socket
                    System.out.println("Connection, sending data.")
                    var input: BufferedReader = BufferedReader(InputStreamReader(remote.getInputStream()))
                    var output: PrintWriter = PrintWriter(remote.getOutputStream());

                    //var totalLines : Int = input.lines().toArray().size

                    var str: String = "."

                    while (!str.equals("")) {
                        str = input.readLine()
                        if (str.contains("GET")) {
                            gets = str;
                            break;
                        }
                    }
                    final = true

                    output.println("HTTP/1.0 200 OK")
                    output.println("Content-Type: textArea/html")
                    output.println("")
                    // Send the HTML page
                    val method: String = "get"
                    output.print("<html><form method=" + method + ">");
                    output.print("<p>" + gets + "</form></html>");
                    println(gets);
                    output.flush();

                    remote.close();
                } catch (e: Exception) {
                    System.out.println("Error: " + e);
                }
            }
        } catch (v: InterruptedException) {
            println(v)
        }

        return gets.substring(gets.indexOf("code=") + 5, gets.indexOf(" HTTP"))
    }


    private fun requestToken(code: String): TokenHeader {

        //Using HTTP Basic Authorization you need to specify your client ID and secret (see the API dashboard).
        // This is simply your client ID and password separated by a colon (:) and base64-encoded.
        val basicAuthentication: String = "Q1BrUU1oR3pxczo1RDNlem5iNGtLSDhROXA5Q0E3SDRX"

        var uri2: URI = URIBuilder()
                .setScheme("https")
                .setHost("api.quizlet.com")
                .setPath("/oauth/token")
                .setParameter("grant_type", "authorization_code")
                .setParameter("code", code)
                .build()

        //"access_token","Q1BrUU1oR3pxczo1RDNlem5iNGtLSDhROXA5Q0E3SDRX")


        var json: String? = null
        try {
            //https://stackoverflow.com/a/13530628
            json = Request.Post(uri2).addHeader("Authorization", "Basic $basicAuthentication").execute().returnContent().asString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val at: TokenHeader = Gson().fromJson(json, object : TypeToken<TokenHeader>() {}.type)
        println("Access Token " + at.access_token + " expires in " + at.expires_in + "seconds")
        return at
    }


}