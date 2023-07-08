package com.example.NoteAppWithFirebase.configs

import android.content.Context
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class Util {
    companion object {
        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun getCollectionReferenceForCities(): CollectionReference {
            val currentUser = FirebaseAuth.getInstance().currentUser
            return FirebaseFirestore.getInstance().collection("cities")
                .document(currentUser!!.uid).collection("my_cities")
        }

        fun timestampToString(timestamp: Timestamp?): String {
            return SimpleDateFormat("dd/MM/yyyy").format(timestamp!!.toDate())
        }

    }
}