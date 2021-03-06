package com.moderndev.polyglot.ui.screens.search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moderndev.polyglot.model.Word
import com.moderndev.polyglot.repo.WordRepository
import com.moderndev.polyglot.util.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel
@Inject constructor (
    private val wordRepo: WordRepository,
): ViewModel() {

    private val _autoCompleteWordsUIState: MutableState<UIState<List<Word>>> = mutableStateOf(UIState.Initial(listOf()))
    val autoCompleteWordsUIState: State<UIState<List<Word>>> = _autoCompleteWordsUIState

    val searchValue: MutableState<String> = mutableStateOf("")
    var lastSearchLanguage = ""

    fun searchWords(search: String, targetLanguage: String?){
        if(targetLanguage == null) {
            _autoCompleteWordsUIState.value = UIState.Error("No current language provided!")
            return
        }
        if(search.isBlank()) {
            _autoCompleteWordsUIState.value = UIState.Loaded(listOf())
            return
        }
        viewModelScope.launch {
            wordRepo.getAutoCompleteWords(search, targetLanguage).collectLatest {
                _autoCompleteWordsUIState.value = it
            }
        }
    }
}