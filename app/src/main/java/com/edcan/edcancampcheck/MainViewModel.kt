package com.edcan.edcancampcheck

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MainViewModel : ViewModel() {
    val db = FirebaseFirestore.getInstance()
    val name = MutableLiveData("")
    val userNameList = mutableListOf<String>()

    val result = MutableLiveData("")

    init {
        getUserNameList()
    }

    fun getUserNameList() : Int{
        db.collection("user").get()
            .addOnSuccessListener {
                for(docs in it){
                    userNameList.add(docs.id)
                }
            }

        return 1
    }

    suspend fun getUserById(id : String) : User?{
        var user : User? = null

        db.collection("user").document(id).get()
            .addOnSuccessListener {
                user = it.toObject(User::class.java)
            }.await()

        return user
    }

    suspend fun sendCheck() : Int {
        var result = 0

        db.collection("user").document(name.value!!)
            .set(User(
                name = name.value!!,
                chaek = true,
            ))
            .addOnSuccessListener {
                result = 1
            }.await()

        return result
    }
}