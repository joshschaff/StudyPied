package Depreciated

import QuizletAPI.RandomString
import QuizletAPI.TokenHeader
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.apache.http.client.fluent.Request
import org.apache.http.client.utils.URIBuilder
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.reflect.Type
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URI
import java.util.*
import javax.xml.ws.Response

/**
 * The Oauth 2 authentication process has 2 main stages
 * First, the user is redirected to the api in order to delegate access, and they receive a code
 * Second, the app makes a POST request to an api endpoint with that code, and receives a accessToken
 * These stages are hence referred to as the "code" and "token" processes, and their related values labeled as such
 * The resulting accessToken allows the app to make authenticated API calls
 */
abstract class OAuthAuthenticator<T> (
        val type : TypeToken<T>, // https://stackoverflow.com/questions/14503881/strange-behavior-when-deserializing-nested-generic-classes-with-gson/14506181#14506181
        val clientID : String,
        val scope : String,
        val codeHost : String,
        val codePath : String,
        val tokenHost : String,
        val tokenPath : String) {


    // TODO:: investigate requiring get()'s (switch order of properties maybe?)
    val token : T by lazy {
        postForToken(redirectForCode())
    }


    // intended to be used to resolve codeURI
    protected val baseCodeURI : URIBuilder
        get() = URIBuilder()
                .setScheme("https")
                .setHost(codeHost)
                .setPath(codePath)
                .setParameter("client_id", clientID)
                .setParameter("scope", scope)
                .setParameter("state", RandomString().nextString())
                .setParameter("response_type", "code") // NOT SURE ABOUT THIS ONE


    private val unauthenticatedBaseTokenURI : URIBuilder
        get() = URIBuilder()
                .setScheme("https")
                .setHost(tokenHost)
                .setPath(tokenPath)
                .setParameter("grant_type", "authorization_code")



    abstract val codeURI : URI

    // Defined as a request to allow for specifying your own type of authentication
    // Some APIs call for just passing clientID and secret as URI parameters
    // Other require HTTPS Basic Authentication in a URI header
    abstract fun generateTokenRequest( builder : URIBuilder) : Request


    private fun redirectForCode() : String {

        // printed to console so the user can click it
        // TODO:: Find a way to launch this uri inside program, without console-launched web browser
        println("take this " + codeURI)

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


        //println(gets.substring(gets.indexOf("code=") + 5, gets.indexOf(" HTTP")))

        return gets.substring(gets.indexOf("code=") + 5, gets.indexOf(" HTTP"))
    }

    private fun postForToken(code : String) : T{


        println(generateTokenRequest(unauthenticatedBaseTokenURI.setParameter("code", code)))

        println("code is $code")

        var json: String? = null
        try {
            json = generateTokenRequest(unauthenticatedBaseTokenURI.setParameter("code", code))
                    .execute().returnContent().asString()
            println(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }




        val gson : Gson = GsonBuilder().registerTypeAdapter(type.javaClass, InterfaceAdapter<TypeToken<T>>()).create()
        val response : T = gson.fromJson(json, type.type)


        return response

        //val featuresFromJson : HashMap<String, T> = Gson().fromJson(json, object : TypeToken<HashMap<String, T>>() {}.type)
        //val at : T = Gson().fromJson(json, object : TypeToken<T>() {}.type)


        /*println(featuresFromJson)

        var tokenResponse : T




        //for (entry : Map.Entry<String, T> in featuresFromJson.entries) {
        object : TypeToken<T>() {}.javaClass.fields.iterator()
                .forEach { it -> it.set(token, featuresFromJson.entries.filter{m -> m.key == it.name }.map { m -> m.value }) }


        // have this just set the instance variable
        //featuresFromJson.entries.stream().forEach { entry : Map.Entry<String, T> -> token = entry.value }




        //eturn tokenResponse!!*/
    }



}