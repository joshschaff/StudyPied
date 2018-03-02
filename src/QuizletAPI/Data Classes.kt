package QuizletAPI
import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac
import org.apache.http.client.fluent.Request
import java.io.Serializable
import java.lang.instrument.ClassDefinition
import java.net.URI
import java.util.*
import java.util.stream.IntStream


/**
 * The data object passed to the app in the 2nd half of the authentication process
 * Contains the "accessToken" which is used to authenticate API calls
 * (See QuizletAPI.QuizletAuthenticator for implementation)
 * Contains the "user_id" which is used to reference the signed on user
 * (See QuizletAPI.QuizletAPIManager for implementation)
 */
data class TokenHeader(val access_token : String,
                       val token_type : String,
                       val expires_in : Int,
                       var scope : String,
                       val user_id : String) : Serializable


/**
 * An enum is a limited collection of similarly typed or related values
 * They are useful as parameters for their  human-readability
 * over passing integer codes, and security over passing strings
 *
 * This is an enum of the various HTTP methods that web-severs and clients
 * use to communicate. Each method contains a reference to the appropriate
 * HttpClient api function to generate a request of that method.
 *
 * Allows for removing a giant case/switch statement in QuizletRequestBuilder
 */

enum class Methods {

    // An enum is essentially like an abstract class that each of these 4 extend
    GET { override val method : (URI) -> Request = Request::Get },
    POST { override val method : (URI) -> Request = Request::Post },
    PUT { override val method : (URI) -> Request = Request::Put },
    DELETE { override val method : (URI) -> Request = Request::Delete };


    // The abstract class has a property named "method" that all of the 4 options override
    // The property literally has a function as its type
    // If a function maps an input to an output, the following denotes a function
    //      that maps a URI (in parenthesis like a parameter) to a Request
    // This is the same signature as the functions we wish to store,
    //      like Request.Get(uri: URI)
    abstract val method : (URI) -> Request
}

sealed class QuizletObject(id : String) {
    data class QuizletSet(val id: String,
                          val title : String,
                          val created_by : String,
                          val term_count : Int,
                          val terms : Array<QuizletTerm>) : QuizletObject(id) {

    }

    data class QuizletTerm(val id: String,
                           val term : String,
                           val definition: String,
                           val rank : Int) : QuizletObject(id)

    data class QuizletUser(val id: String,
                           val username : String,
                           val sets : Array<QuizletSet>) : QuizletObject(id) {
        override fun equals(other: Any?): Boolean{
            //   Might want to comment out this line to avoid infinite loop
            if (this == other) return true
            if (other?.javaClass != javaClass) return false

            other as QuizletUser

            if (!Arrays.equals(sets, other.sets)) return false

            return true
        }

        override fun hashCode(): Int{
            return Arrays.hashCode(sets)
        }
    }
}

data class TermWrapper(var msgCarrier : String, val term : QuizletObject.QuizletTerm, val setTitle : String)