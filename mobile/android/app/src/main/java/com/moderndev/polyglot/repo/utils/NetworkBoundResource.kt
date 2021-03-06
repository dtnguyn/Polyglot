package com.moderndev.polyglot.repo.utils

import android.util.Log
import com.moderndev.polyglot.util.CustomAppException
import com.moderndev.polyglot.util.UIState
import io.ktor.client.features.*
import kotlinx.coroutines.flow.*
import java.net.ConnectException

inline fun <DomainType> mainGetNetworkBoundResource(
    crossinline query: () -> Flow<DomainType?>,
    crossinline fetch: suspend () -> DomainType?,
    crossinline saveFetchResult: suspend (DomainType?) -> Unit,
    crossinline shouldFetch: (DomainType?) -> Boolean = { true },
    tag: String = "NetworkBoundResource",
): Flow<UIState<DomainType>> {
    var cacheData: DomainType? = null
    return flow {
        try {
            emit(UIState.Loading())

            cacheData = query().first()

            if(cacheData != null){
                Log.d(tag, "emit 1: $cacheData")
                emit(UIState.Loaded<DomainType>(cacheData))
            }

            if(shouldFetch(cacheData)){
                saveFetchResult(fetch())

                val newCache = query().first()
                Log.d(tag, "emit 2: $newCache")
                emit(UIState.Loaded(newCache))
            }
        } catch(error: CustomAppException){
            Log.d(tag, "CustomAppException: ${error.message}")
            Log.d(tag, "emit 3: ${error.message}")
            emit(UIState.Error<DomainType>(error.message, cacheData))
        } catch (error: ClientRequestException) {
            Log.d(tag, "ClientRequestException: ${error.message}")
            Log.d(tag, "emit 3: ${error.message}")
            emit(UIState.Error<DomainType>("Something went wrong!", cacheData))
        } catch (error: ConnectException) {
            Log.d(tag, "ConnectException: ${error.message}")
            Log.d(tag, "emit 3: ${error.message}")
            emit(UIState.Error<DomainType>("Something went wrong with connection!", cacheData))
        }

    }
}


inline fun <ResponseType> mainPostNetworkBoundResource(
    crossinline submit: suspend () -> ResponseType?,
    crossinline shouldSave: suspend (ResponseType?) -> Boolean,
    crossinline saveSubmitResult:  (ResponseType?) -> Unit,
    defaultResponse: ResponseType? = null
): Flow<UIState<ResponseType>> {
    return flow {
        try {
            emit(UIState.Loading())

            val response = submit()

            if(shouldSave(response)){
                saveSubmitResult(response)
            }
            emit(UIState.Loaded(response))


        } catch(error: CustomAppException){
            emit(UIState.Error<ResponseType>(error.message))
        } catch (error: ClientRequestException) {
            emit(UIState.Error<ResponseType>("Something went wrong!"))
        } catch (error: ConnectException) {
            if(shouldSave(null)){
                saveSubmitResult(defaultResponse)
                emit(UIState.Loaded(defaultResponse))
            } else {
                emit(UIState.Error<ResponseType>("Something went wrong with connection!"))
            }
        }

    }
}
