package com.example.tugasroom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.example.tugasroom.database.Note
import com.example.tugasroom.database.NoteDao
import com.example.tugasroom.database.NoteRoomDatabase
import com.example.tugasroom.databinding.ActivityAddDataBinding
import com.example.tugasroom.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddData : AppCompatActivity() {
    private lateinit var binding: ActivityAddDataBinding
    private lateinit var mNotesDao: NoteDao
    private lateinit var executorService: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddDataBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        var tanggal = ""
        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!
        with(binding){
            val dataNote = intent.extras?.getSerializable("note") as Note?
            inpDate.init(inpDate.year, inpDate.month, inpDate.dayOfMonth)
            { _, year, monthOfYear, dayOfMonth ->
                tanggal = "$dayOfMonth/${monthOfYear + 1}/$year"
            }
            val intentToMain = Intent(this@AddData, MainActivity::class.java)
            if (dataNote == null){
                addBtn.setOnClickListener {
                    insert(
                        Note(
                            title = inpTitle.text.toString(),
                            description = inpDesc.text.toString(),
                            date = tanggal
                        )
                    )
                    setEmptyField()
                    startActivity(intentToMain)
                }
            } else {
                addBtn.text = "Update"
                inpTitle.setText(dataNote.title)
                inpDesc.setText(dataNote.description)
                val tgl = dataNote.date.split("/")
                if (tgl.size != 1){
                    inpDate.updateDate(tgl[2].toInt() ,tgl[1].toInt()-1 ,tgl[0].toInt())
                }
                addBtn.setOnClickListener {
                    update(
                        Note(
                            id = dataNote.id,
                            title = inpTitle.text.toString(),
                            description = inpDesc.text.toString(),
                            date = tanggal
                        )
                    )
                    setEmptyField()
                    startActivity(intentToMain)
                }
            }
            back.setOnClickListener {
                startActivity(intentToMain)
            }
        }
    }
    private fun update(note: Note) {
        executorService.execute { mNotesDao.update(note) }
    }

    private fun insert(note: Note) {
        executorService.execute { mNotesDao.insert(note) }
    }
    private fun setEmptyField() {
        with(binding){
            inpTitle.setText("")
            inpDesc.setText("")
        }
    }
}