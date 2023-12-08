package com.example.android.unscramble.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *  Created by zhaoruixuan1 on 2023/12/7
 *  CopyRight (c) haodf.com
 *  功能：
 */
class GameViewModel: ViewModel() {

    private val _score = MutableLiveData<Int>(0)
    val score: LiveData<Int>
        get() = _score

    private val _currentWordCount = MutableLiveData<Int>(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<String>
        get() = _currentScrambledWord

    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    init {
        getNextWord()
    }

    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
//        tempWord.shuffle()
//
//        while (String(tempWord).equals(currentWord, false)) {
//            tempWord.shuffle()
//        }
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
//            ++_currentWordCount
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordsList.add(currentWord)
        }
    }

    fun nextWord(): Boolean {
        return if (currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

    private fun increaseScore() {
//        _score += SCORE_INCREASE
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }

}