package com.umutduran.mynotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.umutduran.mynotes.databinding.ActivityAddNoteBinding
import java.util.UUID

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding

    private val firestore = Firebase.firestore
    private val noteCategory = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clickListener()
    }

    private fun clickListener(){
        binding.ivBack.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

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
            addNewNote()
        }
    }

    private fun addNewNote(){
        if (isValid()){
            val uuid = UUID.randomUUID().toString()
            val newNote = hashMapOf(
                "noteTitle" to binding.etNoteTitle.text.toString(),
                "noteContent" to binding.etNote.text.toString(),
                "noteCategory" to noteCategory.value.toString(),
                "noteId" to uuid
            )

            firestore.collection("Notes").document(uuid)
                .set(newNote)
                .addOnSuccessListener {
                    Toast.makeText(this,"New note is saved.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Error when saving new note.", Toast.LENGTH_SHORT).show()
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