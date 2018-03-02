package DriveAPI

import com.google.gson.InstanceCreator
import java.lang.reflect.Type


data class TokenHeader (val access_token : String,
                        val refresh_token : String,
                        val expires_in : Int,
                        var token_type : String)



