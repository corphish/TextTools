package com.corphish.quicktools.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.corphish.quicktools.R
import com.corphish.quicktools.ui.common.ListDialog
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter


class SaveTextActivity : NoUIActivity() {

    override fun handleIntent(intent: Intent): Boolean {
        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()

        setContent {
            QuickToolsTheme {
                // A surface container using the 'background' color from the theme
                ListDialog(
                    title = stringResource(id = R.string.save_text_title),
                    message = stringResource(id = R.string.save_text_message),
                    list = listOf(
                        R.string.save_option_txt,
                        // R.string.save_option_note, Note taking supported in keep only in A14 but not working in keep
                    ),
                    onItemSelected = {
                        when (it) {
                            0 -> {
                                // Text
                                processTextFile(text)
                            }

                            1 -> {
                                // Note
                                processNote(text)
                            }
                        }
                    },
                    stringSelector = { stringResource(id = it) },
                    iconSelector = { R.drawable.ic_save }
                ) {
                    finish()
                }
            }
        }

        // Do not finish or else the dialog will go away
        return false
    }

    private fun processNote(text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            try {
                val intent = Intent(Intent.ACTION_CREATE_NOTE)
                intent.putExtra(Intent.EXTRA_TEXT, text)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, R.string.no_note_taking_apps, Toast.LENGTH_LONG).show()
            }
        } else {
            // Ideally the option should not be visible
            Toast.makeText(this, R.string.note_taking_not_supported, Toast.LENGTH_LONG).show()
        }

        finish()
    }

    // Looks like it is not possible to transmit the selected text via the intent
    // and receive it in the result. So we save it as instance variable.
    // P.S - Passing via intent.putExtra() and then retrieving in intent.getExtra() does not work.
    private var mSelectedText = ""
    private val mResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if (data != null) {
                writeInFile(data.data, mSelectedText)
                mSelectedText = ""
            }
        }
    }

    private fun processTextFile(text: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.setType("text/plain")
        mSelectedText = text
        mResultLauncher.launch(intent)
    }

    private fun writeInFile(uri: Uri?, text: String) {
        if (uri == null) {
            finish()
            return
        }

        val outputStream: OutputStream?
        try {
            outputStream = contentResolver.openOutputStream(uri)
            val bw = BufferedWriter(OutputStreamWriter(outputStream))
            bw.write(text)
            bw.flush()
            bw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        finish()
    }
}