package com.example.notes.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.notes.model.NoteModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(noteModel: NoteModel)

    @Update
    suspend fun ubdeta(noteModel: NoteModel)

    @Delete
    suspend fun delet(noteModel: NoteModel)

    @Query("SELECT * from note_title")
    fun  getAllNotes():LiveData<List<NoteModel>>


}
