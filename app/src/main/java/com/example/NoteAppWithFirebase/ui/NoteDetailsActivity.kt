package com.example.NoteAppWithFirebase.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.NoteAppWithFirebase.configs.Util
import com.example.NoteAppWithFirebase.databinding.ActivityCityDetailsBinding
import com.example.NoteAppWithFirebase.models.Note
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

class NoteDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCityDetailsBinding

    private var title: String? = null
    private var content: String? = null
    private var docId: String? = null
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        editMode()

        binding.saveCityButton.setOnClickListener { saveNote() }

    }

    private fun editMode() {
        title = intent.getStringExtra("title")
        content = intent.getStringExtra("content")
        docId = intent.getStringExtra("docId")

        if (docId != null) {
            isEditMode = true
        }

        if (isEditMode) {
            binding.cityTitleText.setText(title)
            binding.cityContentText.setText(content)
            binding.pageTitle.text = "Edit your note"
        }
    }

    private fun saveNote() {
        binding.apply {
            val cityTitle = cityTitleText.text.toString()
            val cityContent = cityContentText.text.toString()

            if (cityTitle.isEmpty()) {
                cityTitleText.error = "Title is required"
                return
            }
            val note = Note(cityTitle, cityContent, Timestamp.now())

            saveNoteToFirebase(note)

        }
    }

    private fun saveNoteToFirebase(note: Note) {
        var documentReference : DocumentReference = if (isEditMode) {
            Util.getCollectionReferenceForCities().document(docId!!)
        }else {
            Util.getCollectionReferenceForCities().document()
        }

        documentReference.set(note).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (isEditMode) {
                    Util.showToast(this, "Note updated successfully")
                } else {
                    Util.showToast(this, "Note added successfully")
                }
                finish()
            } else {
                if (isEditMode) {
                    Util.showToast(this, "Failed while updating note")
                }else {
                    Util.showToast(this, "Failed while adding note")
                }

            }
        }
    }

}