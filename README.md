# Text Tools

![GitHub Release](https://img.shields.io/github/v/release/corphish/TextTools)
[![Github All Releases](https://img.shields.io/github/downloads/corphish/TextTools/total.svg)]()

Collection of useful text related tools that can be accessed from the context menu that appears on text selection. The feature list will keep on growing. Let me know if you have any ideas.

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
alt="Get it on F-Droid"
height="80">](https://f-droid.org/packages/com.corphish.quicktools)

### Current Features
- Text unsaved numbers in WhatsApp
- Evaluate mathematical expressions inline
- Transform text
- Text count
- Save text to a file
- Find & Replace in text

For detailed description of each feature and their demo, please check the [features page](FEATURES.md).

### App usage
The above features are provided as context menu options on selected text. Text Tools also lets the user choose how he/she wants the context menu to appear. In other words, users have total control on what appears in the context menu.

- Users can choose between "Multiple options" and "Single option".
- "Multiple options" will present all the options at once in the context menu.
- "Single option" will present a single option (as "Text Tools") which will then reveal all the options.
- If "Single option" is selected, only relevant options (depending on whether the source text is editable or non-editable) will be shown.
- In all cases, users can choose which options they would want to see in the context. If a certain feature is not useful to an user, he/she can deselect it in the app and that option will not appear in the context menu (for "Multiple options") or in the dialog that presents the supported options (in case of "Single option").

### Development methods
- Developed in Jetpack compose using the latest libraries.
- Use Material design for UI, with color scheme derived using Material You.
- Follows MVVM architecture using ViewModels with StateFlows wherever possible.
- Uses Dependency Injection provided by the Hilt library.

### Installation warning
While installing the app (you have to side load), Google Play may block the installation saying it has never seen the app before. While it is true (from Google Play POV), you can proceed with installing the app. If you have doubts about the app being malicious, you are free to verify the same from the source code (it would not be open-source if the app was malicious in the first place, right?).

### Issues
Issues may be observed as in the contextual menu options may not be available inside certain apps, or the functionalities may not work as expected. In such cases, kindly raise issue in this Github repo.

### Translations
If you would like to contribute to this project by translating the various text shown in the app, you may do so by heading over to the [Crowdin Project](https://crowdin.com/project/text-tools).

### Support
If you like my work and would like to support me, you can help to [buy me un gelato](https://www.paypal.com/paypalme/corphish).