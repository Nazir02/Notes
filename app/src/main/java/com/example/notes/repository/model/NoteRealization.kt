package com.example.notes.repository.model

import androidx.lifecycle.LiveData
import com.example.notes.dao.NoteDao
import com.example.notes.model.NoteModel

class NoteRealization(private val noteDao: NoteDao):NoteRepository {
    override val allNote: LiveData<List<NoteModel>>
        get() = noteDao.getAllNotes()

    override suspend fun inserNote(noteModel: NoteModel, onSuccess: () -> Unit) {
             noteDao.insert(noteModel)
        onSuccess
    }

    override suspend fun deletNote(noteModel: NoteModel, onSuccess: () -> Unit) {
       noteDao.delet(noteModel)
        onSuccess
    }

    override suspend fun ubtadeNote(noteModel: NoteModel, onSuccess: () -> Unit) {
        noteDao.ubdeta(noteModel)
        onSuccess    }


}