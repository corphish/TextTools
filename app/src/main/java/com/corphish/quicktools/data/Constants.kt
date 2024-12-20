package com.corphish.quicktools.data

/**
 * Constants.
 */
object Constants {
    // Phone number regular expression
    const val PHONE_NUMBER_REGEX = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*\$"

    // Remove these special characters from the phone number
    val PHONE_NUMBER_SPECIAL_CHARACTERS = listOf(" ", "-", "(", ")")

    // URLs
    const val SOURCE_LINK = "https://github.com/corphish/TextTools/"
    const val CONTRIBUTORS_LINK = "${SOURCE_LINK}blob/main/CONTRIBUTORS.md"
    const val RELEASES_PAGE_LINK = "${SOURCE_LINK}releases"
    const val ISSUES_PAGE_LINK = "${SOURCE_LINK}issues"
    const val DONATE_LINK = "https://www.paypal.com/paypalme/corphish"
    const val WHATSAPP_API_LINK = "https://wa.me/"
}