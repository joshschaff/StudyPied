import Depreciated.OAuthAuthenticator
import QuizletAPI.TokenHeader
import com.google.gson.reflect.TypeToken
import org.apache.http.client.fluent.Request
import org.apache.http.client.utils.URIBuilder
import java.net.URI
import javax.xml.ws.Response


//https://developers.google.com/api-client-library/java/apis/drive/v2


class GoogleAuthenticatorOld(): OAuthAuthenticator<DriveAPI.TokenHeader>(
        type = object : TypeToken<DriveAPI.TokenHeader>() {},
        clientID = "1025815803277-g9a870ng63af5b9ehkvlrahkacv3iqob.apps.googleusercontent.com",
        scope = "https://www.googleapis.com/auth/drive", // TODO:: switch to https://www.googleapis.com/auth/drive.file, see https://developers.google.com/drive/v3/web/about-auth
        codeHost = "accounts.google.com",
        codePath = "/o/oauth2/v2/auth",
        tokenHost = "www.googleapis.com",
        tokenPath = "/oauth2/v4/token") {

    //private val clientSecret : String = "OsKHsfnWdnYeBali9XU1tsEG"

    override val codeURI : URI
        get() = baseCodeURI
                .setParameter("access_type", "offline") // allows for refreshing tokens
                .setParameter("redirect_uri", "http://127.0.0.1:9090")
                .build()


    override fun generateTokenRequest(builder: URIBuilder): Request {
        // Google spec literally says to send client and secret in url... https://developers.google.com/identity/protocols/OAuth2WebServer
        return Request.Post(builder
                .setParameter("client_id", clientID)
                .setParameter("client_secret", "OsKHsfnWdnYeBali9XU1tsEG")
                .setParameter("redirect_uri", "http://127.0.0.1:9090")
                .build())
    }
}