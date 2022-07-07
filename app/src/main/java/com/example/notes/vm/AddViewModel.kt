package com.example.notes.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.model.NoteModel
import com.example.notes.view.REPOSITORY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddViewModel:ViewModel() {
    fun insert(noteModel:NoteModel,onSuccess:()->Unit)=
        viewModelScope.launch(Dispatchers.IO) {
            REPOSITORY.inserNote(noteModel){
                onSuccess
            }
        }
}