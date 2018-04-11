package content.search

import content.quizletAPI.QuizletObject

sealed class ResultWrapper<T> (open val result : T, open val srcInfo : String) {
    data class QuizletResultWrapper(val term : QuizletObject.QuizletTerm, val setTitle : String) :
            ResultWrapper<QuizletObject.QuizletTerm>(result = term, srcInfo = setTitle)
}