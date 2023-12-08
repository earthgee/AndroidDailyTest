package com.earthgee.kotlin.jetpack.room

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.earthgee.kotlin.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 *  Created by zhaoruixuan1 on 2023/12/6
 *  CopyRight (c) haodf.com
 *  功能：room test
 */
class WordListActivity : AppCompatActivity(){

    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory(WordDataBaseUtil.getWordRepository(this))
    }

    private val newWordActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activtiy_room_word_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_kotlin)
        val adapter = WordListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        wordViewModel.allWords.observe(this) {
            adapter.submitList(it)
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@WordListActivity, NewWordActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewWordActivity.EXTRA_REPLY)?.let {
                val word = Word(it)
                wordViewModel.insert(word)
            }
        }
    }

    class WordListAdapter : ListAdapter<Word, WordListAdapter.WordViewHolder>(WordsComparator()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
            return WordViewHolder.create(parent)
        }

        override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
            val current = getItem(position)
            holder.bind(current.word)
        }

        class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val wordItemView: TextView = itemView.findViewById(R.id.tv_content)

            fun bind(text: String?) {
                wordItemView.text = text
            }

            companion object {
                fun create(parent: ViewGroup): WordViewHolder {
                    val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_kotlin, parent, false)
                    return WordViewHolder(view)
                }
            }
        }

        class WordsComparator : DiffUtil.ItemCallback<Word>() {
            override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
                return oldItem.word == newItem.word
            }
        }
    }

}