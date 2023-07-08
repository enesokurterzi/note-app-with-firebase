package com.example.NoteAppWithFirebase.models

import com.google.firebase.Timestamp


data class Note (
    var title: String = "",
    var content: String = "",
    var timestamp: Timestamp = Timestamp.now()
        )


