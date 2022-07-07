package com.example.notes.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.model.NoteModel
import com.example.notes.view.REPOSITORY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel:ViewModel() {
    fun ubdate(noteModel: NoteModel, onSuccess:()->Unit)=
            viewModelScope.launch(Dispatchers.IO){
                REPOSITORY.ubtadeNote(noteModel){
                    onSuccess
                }
            }
    fun delet(noteModel: NoteModel, onSuccess:()->Unit)=
            viewModelScope.launch(Dispatchers.IO) {
                REPOSITORY.deletNote(noteModel){
                    onSuccess
                }
            }
}