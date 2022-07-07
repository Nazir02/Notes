package com.example.notes.repository.model

import androidx.lifecycle.LiveData
import com.example.notes.model.NoteModel

interface NoteRepository {
    val allNote:LiveData<List<NoteModel>>
    suspend fun inserNote(noteModel: NoteModel, onSuccess:()-> Unit)
    suspend fun deletNote(noteModel: NoteModel, onSuccess:()-> Unit)
    suspend fun ubtadeNote(noteModel: NoteModel, onSuccess:()-> Unit)
}