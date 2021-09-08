package com.nguyen.pawn.db.mapper

import android.annotation.SuppressLint
import android.util.Log
import com.nguyen.pawn.db.entity.SavedWordCacheEntity
import com.nguyen.pawn.db.entity.WordDetailCacheEntity
import com.nguyen.pawn.model.Word
import com.nguyen.pawn.model.WordDetail
import java.text.SimpleDateFormat
import java.util.*

object WordDetailMapper {

    fun mapToCacheEntity(word: WordDetail): WordDetailCacheEntity {
        Log.d("SavedWordMapper", "debug: $word")
        return WordDetailCacheEntity(
            id = UUID.randomUUID().toString(),
            value = word.value,
            language = word.language,
            definitions = word.definitions,
            pronunciations = word.pronunciations
        )
    }

    fun mapToNetworkEntity(word: WordDetailCacheEntity): WordDetail {
        return WordDetail(
            value = word.value,
            language = word.language,
            definitions = word.definitions,
            pronunciations = word.pronunciations,
        )
    }

    fun mapToListCacheEntity(words: List<WordDetail>): List<WordDetailCacheEntity> {
        return words.map{
            mapToCacheEntity(it)
        }
    }

    fun mapToListNetworkEntity(words: List<WordDetailCacheEntity>): List<WordDetail> {
        return words.map{
            mapToNetworkEntity(it)
        }
    }
}