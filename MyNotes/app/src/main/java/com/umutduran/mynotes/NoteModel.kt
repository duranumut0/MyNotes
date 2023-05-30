package com.umutduran.mynotes

data class NoteModel(
    var noteTitle: String? =null,
    var noteContent: String? = null,
    var noteCategory: String? = null,
    var noteId: String? = null
): java.io.Serializable
