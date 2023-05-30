package com.umutduran.mynotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Note
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.umutduran.mynotes.databinding.ActivityMainBinding
import com.umutduran.mynotes.databinding.EditNoteBottomSheetBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val db = Firebase.firestore
    private val noteList = MutableLiveData<ArrayList<NoteModel>>()
    private lateinit var noteAdapter : NoteAdapter
    private lateinit var dialogChooseEditType : BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialogChooseEditType = BottomSheetDialog(this)

        getNotes()
        chooseNoteCategory(binding.cvAll)

        noteAdapter = NoteAdapter(arrayListOf(),object : EditNoteClickListener{
            override fun noteClicked(noteModel: NoteModel) {
                bottomSheetDialog(noteModel)
            }
        })
        binding.rvNotes.adapter = noteAdapter
        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.setHasFixedSize(true)
        clickListener()
        observeData()

    }

    private fun clickListener(){
        binding.btFloat.setOnClickListener {
            startActivity(Intent(this,AddNoteActivity::class.java))
        }

        binding.svInput.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchAdapter(binding.svInput.query.toString())
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
        })

        binding.svInput.setOnCloseListener {
            getNotes()
            true
        }

        binding.cvAll.setOnClickListener {
            chooseNoteCategory(it as MaterialCardView)
            getNotes()
        }
        binding.cvDaily.setOnClickListener {
            chooseNoteCategory(it as MaterialCardView)
            getNotesWithFilter("Daily")
        }
        binding.cvFamily.setOnClickListener {
            chooseNoteCategory(it as MaterialCardView)
            getNotesWithFilter("Family")
        }
        binding.cvPersonal.setOnClickListener {
            chooseNoteCategory(it as MaterialCardView)
            getNotesWithFilter("Personal")
        }
        binding.cvWork.setOnClickListener {
            chooseNoteCategory(it as MaterialCardView)
            getNotesWithFilter("Work")
        }
    }

    private fun getNotes() {
        val docIdRef = db.collection("Notes")
        docIdRef.get()
            .addOnSuccessListener { documents ->
                val noteModelList = ArrayList<NoteModel>()
                for (document in documents) {
                    val noteTitle = document.data["noteTitle"] as? String
                    val noteContent = document.data["noteContent"] as? String
                    val noteCategory = document.data["noteCategory"] as? String
                    val noteId = document.data["noteId"] as? String
                    noteModelList.add(NoteModel(noteTitle = noteTitle, noteContent = noteContent, noteCategory = noteCategory, noteId = noteId))
                }
                noteList.value = noteModelList
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: ${exception.localizedMessage}")
            }
    }

    private fun getNotesWithFilter(categoryNote: String) {
        val docIdRef = db.collection("Notes").whereEqualTo("noteCategory",categoryNote)
        docIdRef.get()
            .addOnSuccessListener { documents ->
                val noteModelList = ArrayList<NoteModel>()
                for (document in documents) {
                    val noteTitle = document.data["noteTitle"] as? String
                    val noteContent = document.data["noteContent"] as? String
                    val noteCategory = document.data["noteCategory"] as? String
                    val noteId = document.data["noteId"] as? String
                    noteModelList.add(NoteModel(noteTitle = noteTitle, noteContent = noteContent, noteCategory = noteCategory, noteId = noteId))
                }
                noteList.value = noteModelList
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: ${exception.localizedMessage}")
            }
    }

    private fun deleteNotes(noteModel: NoteModel){
        db.collection("Notes").document(noteModel.noteId!!)
            .delete()
            .addOnSuccessListener {
                dialogChooseEditType.dismiss()
                getNotes()
                Toast.makeText(this,"Note is deleted.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this,"Please try again later.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun searchAdapter(name: String){
        val docIdRef = db.collection("Notes")
            .whereEqualTo("noteTitle",name)
        docIdRef.get()
            .addOnSuccessListener { documents ->
                val noteModelList = ArrayList<NoteModel>()
                for (document in documents) {
                    val noteTitle = document.data["noteTitle"] as? String
                    val noteContent = document.data["noteContent"] as? String
                    val noteCategory = document.data["noteCategory"] as? String
                    val noteId = document.data["noteId"] as? String
                    noteModelList.add(NoteModel(noteTitle = noteTitle, noteContent = noteContent, noteCategory = noteCategory, noteId = noteId))
                }
                noteList.value = noteModelList
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: ${exception.localizedMessage}")
            }
    }

    private fun observeData(){
        noteList.observe(this){ noteList ->
            noteAdapter.updateNoteList(noteList)

        }
    }

    private fun bottomSheetDialog(noteModel: NoteModel){
        val bottomBinding = EditNoteBottomSheetBinding.inflate(layoutInflater)
        dialogChooseEditType.setContentView(bottomBinding.root)

        bottomBinding.ivEdit.setOnClickListener {
            dialogChooseEditType.dismiss()
            val intent = Intent(this@MainActivity,EditNoteActivity::class.java)
            intent.putExtra("noteModel",noteModel)
            startActivity(intent)
            finish()
        }

        bottomBinding.ivDelete.setOnClickListener {
            deleteNotes(noteModel)
            dialogChooseEditType.dismiss()
        }

        bottomBinding.ivClose.setOnClickListener {
            dialogChooseEditType.dismiss()
        }

        dialogChooseEditType.setCancelable(false)
        dialogChooseEditType.show()
    }

    private fun chooseNoteCategory(materialCardView: MaterialCardView){
        unSelectNoteCategory()
        materialCardView.setCardBackgroundColor(resources.getColor(android.R.color.darker_gray))
    }

    private fun unSelectNoteCategory(){
        binding.cvAll.setCardBackgroundColor(resources.getColor(R.color.white))
        binding.cvWork.setCardBackgroundColor(resources.getColor(R.color.white))
        binding.cvFamily.setCardBackgroundColor(resources.getColor(R.color.white))
        binding.cvDaily.setCardBackgroundColor(resources.getColor(R.color.white))
        binding.cvPersonal.setCardBackgroundColor(resources.getColor(R.color.white))
    }
}