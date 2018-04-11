import depreciated.OAuthAuthenticator
import content.quizletAPI.TokenHeader
import com.google.gson.reflect.TypeToken
import java.net.URI

import org.apache.http.client.fluent.Request
import org.apache.http.client.utils.URIBuilder

class QuizletAuthenticatorFailed : OAuthAuthenticator<TokenHeader>(
        type = object : TypeToken<content.quizletAPI.TokenHeader>() {},
        clientID = "CPkQMhGzqs",
        scope = "read", // Do you need more permissions for write than read?
        codeHost = "quizlet.com",
        codePath = "/authorize",
        tokenHost = "api.quizlet.com",
        tokenPath = "/oauth/token") {

    // Gets are required otherwise Kotlin doesnt initialize variables
    override val codeURI : URI
        get() = baseCodeURI.build()


    override fun generateTokenRequest(builder: URIBuilder): Request {
        return Request.Post(builder.build()).addHeader("Authorization", "Basic Q1BrUU1oR3pxczo1RDNlem5iNGtLSDhROXA5Q0E3SDRX")
    }
}