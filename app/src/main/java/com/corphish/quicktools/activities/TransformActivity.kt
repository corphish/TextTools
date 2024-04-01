package com.corphish.quicktools.activities

import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.corphish.quicktools.R
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.TypographyV2
import java.lang.StringBuilder
import java.util.Locale

class TransformActivity : NoUIActivity() {

    private val transformOptions = listOf(
        R.string.wrap_text,
        R.string.change_case,
    )

    private val wrapOptions = listOf(
        R.string.wrap_single_inverted,
        R.string.wrap_double_inverted,
        R.string.wrap_first_bracket,
        R.string.wrap_curly_braces,
        R.string.wrap_square_bracket,
        R.string.custom_wrapper_text
    )

    private val caseOptions = listOf(
        R.string.upper_case,
        R.string.lower_case,
        R.string.title_case_first_word_only,
        R.string.title_case_all_words,
        R.string.random_case
    )

    override fun handleIntent(intent: Intent): Boolean {
        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
            if (readonly) {
                // We are only interested in editable text
                return true
            }

            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
            val resultIntent = Intent()

            setContent {
                QuickToolsTheme {
                    val mainOptionDialog = remember {
                        mutableStateOf(true)
                    }

                    val wrapOptionDialog = remember {
                        mutableStateOf(false)
                    }

                    val caseOptionDialog = remember {
                        mutableStateOf(false)
                    }

                    val customWrapDialog = remember {
                        mutableStateOf(false)
                    }

                    val optionsArray = arrayOf(wrapOptionDialog, caseOptionDialog)

                    when {
                        mainOptionDialog.value -> {
                            ListDialog(
                                title = stringResource(id = R.string.transform),
                                message = stringResource(id = R.string.transform_message),
                                list = transformOptions, onItemSelected = {
                                    optionsArray[it].value = true
                                    mainOptionDialog.value = false
                                }) {
                                mainOptionDialog.value = false
                            }
                        }

                        wrapOptionDialog.value -> {
                            ListDialog(
                                title = stringResource(id = R.string.wrap_text),
                                message = stringResource(id = R.string.wrap_message),
                                list = wrapOptions,
                                supportBack = true,
                                onBackPressed = {
                                    wrapOptionDialog.value = false
                                    mainOptionDialog.value = true
                                },
                                onItemSelected = {
                                    var res = ""
                                    when (it) {
                                        0 -> res = "'$text'"
                                        1 -> res = "\"$text\""
                                        2 -> res = "($text)"
                                        3 -> res = "{$text}"
                                        4 -> res = "[$text]"
                                        5 -> {
                                            customWrapDialog.value = true
                                            wrapOptionDialog.value = false
                                        }
                                    }

                                    if (it != 5) {
                                        resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, res)
                                        setResult(RESULT_OK, resultIntent)
                                        finish()
                                    }
                                }) {
                                wrapOptionDialog.value = false
                            }
                        }

                        caseOptionDialog.value -> {
                            ListDialog(
                                title = stringResource(id = R.string.change_case),
                                message = stringResource(id = R.string.change_case_message),
                                list = caseOptions,
                                supportBack = true,
                                onBackPressed = {
                                    caseOptionDialog.value = false
                                    mainOptionDialog.value = true
                                },
                                onItemSelected = {
                                    var res = ""
                                    when (it) {
                                        0 -> res = text.uppercase(Locale.getDefault())
                                        1 -> res = text.lowercase(Locale.getDefault())
                                        2 -> res = titleCase(text, true)
                                        3 -> res = titleCase(text, false)
                                        4 -> res = randomCase(text)
                                    }

                                    resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, res)
                                    setResult(RESULT_OK, resultIntent)
                                    finish()
                                }
                            ) {
                                caseOptionDialog.value = false
                            }
                        }

                        customWrapDialog.value -> {
                            CustomWrapperTextDialog(
                                onTextConfirmed = {
                                    resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, customWrap(text, it))
                                    setResult(RESULT_OK, resultIntent)
                                    finish()
                                },
                                onBackPressed = {
                                    customWrapDialog.value = false
                                    wrapOptionDialog.value = true
                                }
                            ) {
                                customWrapDialog.value = false
                            }
                        }
                    }
                }
            }
        } else {
            finish()
        }

        // Do not finish or else the dialog will go away
        return false
    }

    private fun titleCase(str: String, firstWordOnly: Boolean): String {
        val parts = str.split(" ")
        val builder = StringBuilder()
        var firstWordConverted = false

        for (word in parts) {
            var w = word
            if (!firstWordOnly || !firstWordConverted) {
                val array = w.toCharArray()
                array[0] = Character.toUpperCase(array[0])
                w = String(array)
                firstWordConverted = true
            }

            builder.append(w).append(" ")
        }

        return builder.toString().trim()
    }

    private fun customWrap(str: String, wrap: String): String {
        val first = wrap.substring(0, wrap.length/2)
        val second = wrap.substring(wrap.length/2)

        return "${first}${str}${second}"
    }

    private fun randomCase(str: String): String {
        val array = str.toCharArray()
        for (i in array.indices) {
            array[i] =
                if ((Math.random() * 2).toInt() % 2 == 0) Character.toUpperCase(array[i]) else Character.toLowerCase(
                    array[i]
                )
        }

        return String(array)
    }
}

/**
 * A list of selectable options shown inside a dialog.
 */
@Composable
fun ListDialog(
    title: String,
    message: String,
    list: List<Int>,
    supportBack: Boolean = false,
    onItemSelected: (Int) -> Unit,
    onBackPressed: () -> Unit = {},
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    if (supportBack) {
                        IconButton(
                            onClick = { onBackPressed() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                                contentDescription = "",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Text(
                        text = title,
                        style = TypographyV2.headlineSmall,
                        fontFamily = BrandFontFamily,
                        modifier = Modifier.padding(start = if (supportBack) 16.dp else 0.dp)
                    )
                }

                Text(
                    text = message,
                    style = TypographyV2.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                for ((index, id) in list.withIndex()) {
                    Button(
                        onClick = {
                            onItemSelected(index)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = id))
                    }
                }
            }
        }
    }
}

/**
 * Dialog that lets the user select the custom wrapper text.
 */
@Composable
fun CustomWrapperTextDialog(
    onTextConfirmed: (String) -> Unit,
    onBackPressed: () -> Unit = {},
    onDismissRequest: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    var isError by remember {
        mutableStateOf(false)
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    IconButton(
                        onClick = { onBackPressed() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                            contentDescription = "",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Text(
                        text = stringResource(id = R.string.wrap_text),
                        style = TypographyV2.headlineSmall,
                        fontFamily = BrandFontFamily,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Text(
                    text = stringResource(id = R.string.custom_wrapper_desc),
                    style = TypographyV2.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    value = text,
                    isError = isError,
                    onValueChange = {
                        text = it
                        isError = text.length % 2 == 1
                    },
                    supportingText = {
                        if (isError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(id = R.string.custom_wrapper_error),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    singleLine = true,
                    label = { Text("Enter text here") },
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { if (!isError) onTextConfirmed(text) },
                        modifier = Modifier.align(Alignment.CenterEnd),
                        enabled = !isError
                    ) {
                        Text(text = stringResource(id = android.R.string.ok))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListPreview() {
    QuickToolsTheme {
        CustomWrapperTextDialog(onTextConfirmed = {}) {

        }
    }
}