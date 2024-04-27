package com.dicoding.asclepius.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "result_history")
@Parcelize
data class ResultHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "image_uri")
    var imageUri: String? = null,

    @ColumnInfo(name = "prediction")
    var prediction: String? = null,

    @ColumnInfo(name = "confidence_score")
    var confidenceScore: Float = 0f,

    @ColumnInfo(name = "date")
    var date: String? = null
) : Parcelable