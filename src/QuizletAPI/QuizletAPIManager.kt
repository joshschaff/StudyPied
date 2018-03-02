package QuizletAPI

import QuizletAPI.Methods.*
import QuizletAPI.QuizletObject.*
import com.google.gson.Gson



/**
 * This should in essence be a list of mappings to common endpoints
 *  I may want to implement in the future....
 * GoogleAPIManager wouldn't be worth it for the one use case of Google Custom Search API
 */


class QuizletAPIManager (val client_id : String) {


    /**
     * var json: String? = null
    try {
    json = Request.Get("https://api.soundcloud.com/users/$josh_id/favorites?limit=1000&client_id=$client_id")
    .execute().returnContent().asString()
    //println(json)
    } catch (e: IOException) {
    e.printStackTrace()
    }

    val listType = object : TypeToken<List<SoundCloudObject.Track>>() {

    }.type


    val tracks : List<SoundCloudObject> = Gson().fromJson(json, object : TypeToken<List<SoundCloudObject.Track>>() {}.type)
    //val tracks: List<api.Track> = Gson().fromJson((json), Array<api.Track>::class.java)



    for (obj : SoundCloudObject in tracks) { // for (SoundCloudObj obj : tracks)
    when (obj) {
    is SoundCloudObject.Track -> {
    println(obj)
    }
    //is SoundCloudObject.User -> print(obj.username)
    }

    }

     */


    /**
     * This authenticator is protected inside the APIManager
     * Its token should not be passed explicitly outside of the Manager
     * As per Quizlet security spec https://quizlet.com/api/2.0/docs/security
     */
    private val th : TokenHeader = QuizletAuthenticator(client_id).token

    // GET /users/USERNAME - View basic user information, including their sets, favorites, last 25 sessions, etc.
    fun getUser() : QuizletUser{
        return Gson().fromJson(QuizletRequestBuilder(th, GET)
                .buildUserRequest()
                .execute().returnContent().asString(), QuizletUser::class.java)
    }

    //GET /sets/SET_ID - View complete details (including all terms) of a single set.
    fun getSet(setID : String) : QuizletSet {
        println("requesting setID: $setID")
        return Gson().fromJson(QuizletRequestBuilder(th, GET)
                .buildSetRequest(setID)
                .execute().returnContent().asString(), QuizletSet::class.java)
    }

    //GET /sets/SET_ID/terms - View just the terms in a single set.
    fun getTerms(setID : String) : Array<QuizletTerm> {
        return Gson().fromJson(QuizletRequestBuilder(th, GET)
                .buildTermRequest(setID)
                .execute()
                .returnContent()
                .asString(), Array<QuizletTerm>::class.java)
    }




    //DELETE /sets/SET_ID - Delete an existing set
    fun deleteSet(setID : String) {
        QuizletRequestBuilder(th, DELETE).buildSetRequest(setID)
    }

    //DELETE /sets/SET_ID/terms/TERM_ID - Delete a single term within a set
    fun deleteTerm(setID : String, termID: String) {
        QuizletRequestBuilder(th, DELETE).buildTermRequest(setID, termID)
    }

    //POST /sets - Add a new set
    fun postSet(setID : String) {
        QuizletRequestBuilder(th, POST).buildSetRequest()
    }

    //POST /sets/SET_ID/terms - Add a single term to a set
    fun postTerm(setID : String) {
        QuizletRequestBuilder(th, POST).buildTermRequest(setID)
    }


    /**
     * Put requests usually allow you to edit something
     * This endpoint only lets you edit (read: REPLACE) individual parameters of a set
     * This means for example you have to replace the entire array of terms, or entire array of definitions
     * It would be more appropriate for StudyPied to edit (PUT /sets/SET_ID/terms/TERM_ID) or add (POST /sets/SET_ID/terms) individual terms
     * This endpoint COULD be useful for editing titles and descriptions of sets...
     */
    // PUT /sets/SET_ID - Edit an existing set
    fun putSet(setID : String, set : QuizletSet) {
        QuizletRequestBuilder(th, PUT)
                .buildSetRequest(setID)
                .execute()
    }




    //PUT /sets/SET_ID/terms/TERM_ID - Edit a single term within a set
    fun putTerm(setID : String, termID: String) {
        QuizletRequestBuilder(th, PUT).buildTermRequest(setID, termID)
    }

}


/*
data class putRequest<T>() {
    var persistentClass = (javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[0] as Class<T>


    fun test() {
        persistentClass.fields
    }
}*/