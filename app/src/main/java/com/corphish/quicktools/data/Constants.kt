package com.corphish.quicktools.data

/**
 * Constants.
 */
object Constants {
    // Phone number regular expression
    const val PHONE_NUMBER_REGEX = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*\$"

    // Remove these special characters from the phone number
    val PHONE_NUMBER_SPECIAL_CHARACTERS = listOf(" ", "-", "(", ")")
}