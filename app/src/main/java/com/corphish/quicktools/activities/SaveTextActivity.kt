package com.corphish.quicktools.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Result
import com.corphish.quicktools.ui.common.ListDialog
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.viewmodels.SaveTextViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SaveTextActivity : NoUIActivity() {

    private val viewmodel: SaveTextViewModel by viewModels()

    override fun handleIntent(intent: Intent): Boolean {
        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()

        setContent {
            QuickToolsTheme {
                SaveTextSelectionDialog(
                    onProcessNoteSelected = { processNote(text) },
                    onProcessFileSelected = { processTextFile(text) },
                    onDismiss = { finish() }
                )
            }
        }

        lifecycleScope.launch {
            viewmodel.saveTextStatus.collect {
                when (it) {
                    is Result.Initial -> {
                        // Do nothing
                    }
                    is Result.Error -> {
                        Toast.makeText(this@SaveTextActivity, R.string.save_text_error, Toast.LENGTH_LONG).show()
                        finish()
                    }
                    is Result.Success -> {
                        Toast.makeText(this@SaveTextActivity, R.string.save_text_success, Toast.LENGTH_LONG).show()
                        finish()
                    }
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
        if (result.resultCode == RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if (data != null) {
                viewmodel.saveText(data.data, mSelectedText)
                mSelectedText = ""
            }
        }
    }

    private fun processTextFile(text: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.type = "text/plain"
        mSelectedText = text
        mResultLauncher.launch(intent)
    }
}

@Composable
fun SaveTextSelectionDialog(
    onProcessNoteSelected: () -> Unit,
    onProcessFileSelected: () -> Unit,
    onDismiss: () -> Unit,
) {
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
                    onProcessFileSelected()
                }

                1 -> {
                    // Note
                    onProcessNoteSelected()
                }
            }
        },
        stringSelector = { stringResource(id = it) },
        iconSelector = { R.drawable.ic_save }
    ) {
        onDismiss()
    }
}