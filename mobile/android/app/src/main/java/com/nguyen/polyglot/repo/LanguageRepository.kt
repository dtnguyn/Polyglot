package com.nguyen.polyglot.repo

import android.util.Log
import com.nguyen.polyglot.api.model.ApiResponse
import com.nguyen.polyglot.api.model.PickLearningLanguagesRequestBody
import com.nguyen.polyglot.db.PolyglotDatabase
import com.nguyen.polyglot.db.mapper.LanguageMapper
//import com.nguyen.pawn.db.PawnDatabase
//import com.nguyen.pawn.db.entity.LanguageCacheEntity
import com.nguyen.polyglot.model.Language
import com.nguyen.polyglot.repo.utils.mainGetNetworkBoundResource
import com.nguyen.polyglot.repo.utils.mainPostNetworkBoundResource
import com.nguyen.polyglot.util.Constants
import com.nguyen.polyglot.util.UIState
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LanguageRepository
@Inject constructor(
    private val apiClient: HttpClient,
    private val database: PolyglotDatabase
){
    companion object{
        private const val TAG = "LanguageRepo"
    }


    suspend fun pickLearningLanguages(languages: List<Language>, accessToken: String?): Flow<UIState<List<Language>>> {

        return mainPostNetworkBoundResource(
            submit = {
                Log.d(TAG, "Here1")
                val languagesString = languages.map { language ->
                    language.id
                }
                apiClient.post<ApiResponse<Void>>("${Constants.apiURL}/language/save") {
                    contentType(ContentType.Application.Json)
                    body = PickLearningLanguagesRequestBody(languagesString )
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                }
                Log.d(TAG, "Here2")
                languages
            },
            shouldSave = {
                true
            },
            saveSubmitResult = {
                Log.d(TAG, "saving to cache ${it?.size}")
//                database.languageDao().clearAll()
//                database.languageDao().insertMany(LanguageMapper.mapToCacheEntityList(languages))
            },
            defaultResponse = languages
        )
    }

     fun getLearningLanguages(accessToken: String?): Flow<UIState<List<Language>>>{

        return mainGetNetworkBoundResource(
            query = {
                database.languageDao().getMany().map {
                    LanguageMapper.mapFromCacheEntityList(it)
                }
            },
            fetch = {
                val response: ApiResponse<ArrayList<Language>> = apiClient.get("${Constants.apiURL}/language/") {
                    contentType(ContentType.Application.Json)
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                }
                response.data
            },
            saveFetchResult = {languages ->
                languages?.let {
                    database.languageDao().clearAll()
                    database.languageDao().insertMany(LanguageMapper.mapToCacheEntityList(it))
                }
            },
            tag= "getLearningLanguages"
        )

    }
}