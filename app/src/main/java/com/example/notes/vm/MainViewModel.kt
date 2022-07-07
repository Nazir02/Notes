package com.example.notes.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.notes.db.NoteDatabase
import com.example.notes.model.NoteModel
import com.example.notes.repository.model.NoteRealization
import com.example.notes.view.REPOSITORY

class MainViewModel(application: Application): AndroidViewModel(application) {
    val context=application
    fun initDatabase(){
        val daoNote=NoteDatabase.getInstance(context).getNoteDao()
    REPOSITORY=NoteRealization(daoNote)
    }
    fun getallNote():LiveData<List<NoteModel>>{
        return REPOSITORY.allNote
    }


}
