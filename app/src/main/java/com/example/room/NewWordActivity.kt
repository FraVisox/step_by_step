package com.example.room

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/*
Questa attività permette agli utenti di inserire una
 parola e inviarla all'attività principale. L'attività gestisce due scenari:
  quando l'input è vuoto e quando contiene del testo. Questo è essenziale per
  applicazioni che richiedono input dall'utente prima di procedere con altre operazioni.
 */

// Estende AppCompatActivity, una classe di base per le attività che supportano funzionalità moderne.
class NewWordActivity : AppCompatActivity() {

    // Metodo onCreate viene chiamato quando l'attività viene inizialmente creata.
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_word)

        // Trova l'EditText nel layout, dove gli utenti possono inserire una nuova parola.
        val editWordView = findViewById<EditText>(R.id.edit_word)

        // Trova il Button nel layout, configurato per salvare la nuova parola inserita.
        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            // Crea un nuovo Intent per passare il risultato all'attività chiamante.
            val replyIntent = Intent()
            // Controlla se l'EditText è vuoto.
            if (TextUtils.isEmpty(editWordView.text)) {
                // Se è vuoto, imposta il risultato come cancellato e non passa alcun dato.
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                // Se non è vuoto, recupera la stringa inserita.
                val word = editWordView.text.toString()
                // Aggiunge la parola all'intent come extra con la chiave EXTRA_REPLY.
                replyIntent.putExtra(EXTRA_REPLY, word)
                // Imposta il risultato come OK e passa l'intent con la parola inserita.
                setResult(Activity.RESULT_OK, replyIntent)
            }
            // Termina l'attività e torna a quella chiamante.
            finish()
        }
    }

    // Oggetto companion che contiene costanti che possono essere utilizzate in altre parti dell'app.
    companion object {
        // Chiave usata per passare la parola inserita all'attività chiamante.
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}
