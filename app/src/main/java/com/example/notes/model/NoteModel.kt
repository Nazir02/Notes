package com.example.notes.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "note_title")
class NoteModel(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        @ColumnInfo
        var title: String = "",
        @ColumnInfo
        var subTitle: String = "",
        @ColumnInfo
        var dataAndTime:String="",
        @ColumnInfo
        var addres: String ="",
        @ColumnInfo
        var filePhoto: String? = "",
        @ColumnInfo
        var filePhoto1: String? = ""



) : Serializable, Parcelable
