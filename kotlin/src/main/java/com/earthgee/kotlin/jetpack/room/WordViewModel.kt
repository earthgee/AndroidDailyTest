package com.earthgee.kotlin.jetpack.room

import androidx.lifecycle.*
import kotlinx.coroutines.launch

/**
 *  Created by zhaoruixuan1 on 2023/12/6
 *  CopyRight (c) haodf.com
 *  功能：
 */
class WordViewModel(private val wordRepository: WordRepository) : ViewModel(){

    val allWords: LiveData<List<Word>> = wordRepository.allWords.asLiveData()

    fun insert(word: Word) = viewModelScope.launch {
        wordRepository.insert(word)
    }

}

class WordViewModelFactory(private val wordRepository: WordRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(WordViewModel::class.java)) {
            return WordViewModel(wordRepository) as T
        }
        return super.create(modelClass)
    }

}