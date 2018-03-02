package QuizletAPI

import org.apache.http.client.fluent.Request
import org.apache.http.client.utils.URIBuilder


/**
 * I made this class with a really stupid design before I found a good one
 * This prepares a Http Request (essentially just a URL and an authentication code)
 * which would then be executed (sent to Quizlet) in QuizletAPIManager
 *
 */
// TODO :: Make this a sealed class with 5 subclasses... or true builder?

class QuizletRequestBuilder(val token : TokenHeader, val methodEnum : Methods) {

    private val uriBuilder : URIBuilder = URIBuilder()
            .setScheme("https")
            .setHost("api.quizlet.com/2.0")
            .setPath("/sets")

    fun buildSetRequest(id : String) : Request {
        uriBuilder.path = "/sets/$id"
        return build()
    }

    fun buildSetRequest() : Request {
        uriBuilder.path = "/sets/"
        return build()
    }


    fun buildTermRequest(setID : String) : Request {
        buildSetRequest(setID)
        uriBuilder.path += "/terms"
        return build()
    }

    fun buildTermRequest(setID: String, termID : String) : Request {
        buildTermRequest(setID)
        uriBuilder.path += "/$termID"
        return build()
    }

    fun buildUserRequest() : Request {
        uriBuilder.path = "/users/${token.user_id}"
        return build()
    }

    // for accessing a single term // query??? // or access all and afilter?

    private fun build() : Request {
        val r : Request = methodEnum.method.invoke(uriBuilder.build()).addHeader("Authorization:", "Bearer ${token.access_token}")
        //println("This is the request!!" + r)
        return r
    }


}

