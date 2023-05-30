package com.umutduran.mynotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.umutduran.mynotes.databinding.ActivityEditNoteBinding
import com.umutduran.mynotes.databinding.ActivityMainBinding
import java.util.*

class EditNoteActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditNoteBinding
    private lateinit var noteModel : NoteModel
    private val db = Firebase.firestore
    private val noteCategory = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteModel = intent.getSerializableExtra("noteModel") as NoteModel

        binding.etNoteTitle.setText(noteModel.noteTitle)
        binding.etNote.setText(noteModel.noteContent)

        when(noteModel.noteCategory){
            "Daily" -> binding.cvDaily.setCardBackgroundColor(resources.getColor(android.R.color.darker_gray))
            "Family" -> binding.cvFamily.setCardBackgroundColor(resources.getColor(android.R.color.darker_gray))
            "Work" -> binding.cvWork.setCardBackgroundColor(resources.getColor(android.R.color.darker_gray))
            "Personal" -> binding.cvPersonal.setCardBackgroundColor(resources.getColor(android.R.color.darker_gray))
        }

        clickListener()
    }

    private fun clickListener(){
        binding.cvDaily.setOnClickListener {
            chooseNoteCategory(it as MaterialCardView)
            noteCategory.value = "Daily"
        }
        binding.cvFamily.setOnClickListener {
            chooseNoteCategory(it as MaterialCardView)
            noteCategory.value = "Family"
        }
        binding.cvPersonal.setOnClickListener {
            chooseNoteCategory(it as MaterialCardView)
            noteCategory.value = "Personal"
        }
        binding.cvWork.setOnClickListener {
            chooseNoteCategory(it as MaterialCardView)
            noteCategory.value = "Work"
        }
        binding.btSave.setOnClickListener {
            updateNote(noteModel)
        }
        binding.ivBack.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    private fun updateNote(noteModel: NoteModel){
        if (isValid()){
            val updateNote = hashMapOf(
                "noteTitle" to binding.etNoteTitle.text.toString(),
                "noteContent" to binding.etNote.text.toString(),
                "noteCategory" to noteCategory.value.toString(),
                "noteId" to noteModel.noteId
            )

            db.collection("Notes").document(noteModel.noteId!!)
                .update(updateNote as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this,"Note is updated.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Error when update note.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
        }else{
            Toast.makeText(this,"Please control the note title length and note content length.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun chooseNoteCategory(materialCardView: MaterialCardView){
        unSelectNoteCategory()
        materialCardView.setCardBackgroundColor(resources.getColor(android.R.color.darker_gray))
    }

    private fun unSelectNoteCategory(){
        binding.cvWork.setCardBackgroundColor(resources.getColor(R.color.white))
        binding.cvFamily.setCardBackgroundColor(resources.getColor(R.color.white))
        binding.cvDaily.setCardBackgroundColor(resources.getColor(R.color.white))
        binding.cvPersonal.setCardBackgroundColor(resources.getColor(R.color.white))
    }

    private fun isValid(): Boolean{

        if (binding.etNote.text.toString().length < 3) return false
        if (binding.etNoteTitle.text.toString().length < 3) return false
        if (noteCategory.value.toString().isEmpty() || noteCategory.value == null) return false
        return true
    }
}