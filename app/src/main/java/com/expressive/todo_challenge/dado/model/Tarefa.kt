package com.expressive.todo_challenge.dado.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(tableName = "tb_tarefa")
@Parcelize
data class Tarefa (
    val nome:String,
    val completado:Boolean=false,
    val criado:Long=System.currentTimeMillis(),
@PrimaryKey(autoGenerate = true) val id:Int=0):Parcelable {

    val dataCriada:String
        get()= DateFormat.getDateTimeInstance().format(criado)
}