package com.corphish.quicktools.text

import android.util.Log
import androidx.compose.ui.text.TextRange

/**
 * Class that handles text replacement to some initial text with proper tracking of actions.
 */
class TextReplacementManager(private val inputString: String) {
    // List of actions performed
    private val mActionList = mutableListOf<TextReplacementAction>()

    // Current state of action
    private var mActionPointer = 0

    private var mCurrentText = inputString

    private val _debug = true

    init {
        dumpState("Init")
    }

    /**
     * Resets the text to initial state.
     */
    fun reset(): String {
        mCurrentText = inputString
        mActionList.clear()
        mActionPointer = 0

        return mCurrentText
    }

    /**
     * Replaces one occurrence of the text defined by the range with the newText.
     */
    fun replaceOne(range: TextRange, newText: String): String {
        val res = mCurrentText.substring(0, range.start) + newText + mCurrentText.substring(range.end)

        addAction(TextReplacementAction(mCurrentText, res))

        mCurrentText = res
        return res
    }

    /**
     * Replaces all occurrence of the text defined by the range with the newText.
     */
    fun replaceAll(oldText: String, newText: String, ignoreCase: Boolean): String {
        val res = mCurrentText.replace(oldText, newText, ignoreCase)

        addAction(TextReplacementAction(mCurrentText, res))

        mCurrentText = res
        return res
    }

    /**
     * Checks whether it is possible to perform undo on current state.
     */
    fun canUndo() =
        mActionList.isNotEmpty() && mActionPointer > 0

    /**
     * Checks whether it is possible to perform redo on current state.
     */
    fun canRedo() =
        mActionList.isNotEmpty() && mActionPointer < mActionList.size

    private fun addAction(action: TextReplacementAction) {
        if (mActionList.isEmpty() || mActionPointer == mActionList.size) {
            mActionList += action
            mActionPointer += 1
        } else {
            // Currently we are in a state where an undo operation is done.
            // In such case, we add the action at the current pointer index
            // and remove the next action elements.
            mActionList.add(mActionPointer, action)
            mActionList.subList(mActionPointer + 1, mActionList.size).clear()
            mActionPointer += 1
        }

        dumpState("addAction")
    }

    /**
     * Performs the undo operation.
     */
    fun undo(): String {
        if (mActionPointer == 0) {
            return mCurrentText
        }

        // We undo the last operation
        val action = mActionList[mActionPointer - 1]

        mCurrentText = action.oldText
        mActionPointer -= 1

        dumpState("Undo")

        return mCurrentText
    }

    /**
     * Performs the redo operation.
     */
    fun redo(): String {
        if (mActionPointer == mActionList.size) {
            return mCurrentText
        }

        // We redo the current operation
        val action = mActionList[mActionPointer]

        mCurrentText = action.newText
        mActionPointer += 1

        dumpState("Redo")

        return mCurrentText
    }

    fun updateText(newText: String) {
        addAction(TextReplacementAction(mCurrentText, newText))
        mCurrentText = newText
    }

    private fun dumpState(caller: String) {
        if (_debug) {
            Log.d("TextReplacementManager", "[$caller] actions=$mActionList, ptr=$mActionPointer")
        }
    }
}