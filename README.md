# XYZ Reader - "Make Your App Material"

When complete, this will be my submission for the sixth project in the "Grow With Google" 
scholarship Udacity/Google Android Developer Nanodegree program. #GoogleUdacityScholars 
#GrowWithGoogle #MadeWithUdacity

## How Will I Complete this Project?

You will improve an app for this project:

* XYZ Reader: A mock RSS feed reader featuring banner photos and headlines. [Download the code here](https://github.com/udacity/xyz-reader-starter-code).

* The app is currently functional, and work in most cases for most users.

* Your job will be to take the user feedback in the UI Review node, and implement changes that will improve the UI and make it conform to Material Design.

  **User Feedback for XYZ Reader starter code:**
  
  Lyla says, "This app is starting to shape up but it feels a bit off in quite a few places. I can't put finger on it but it feels odd."

  Jay says, "Is the text supposed to be so wonky and unreadable? It is not accessible to those of us without perfect vision."
  
  Kagure says, "The color scheme is really sad and I shouldn't feel sad."

## Project Rubric

Your project will be evaluated by a Udacity Code Reviewer according to this rubric. Be sure to review it 
thoroughly before you submit. All criteria must "meet specifications" in order to pass. 

* [ ] App uses the Design Support library and its provided widget types (FloatingActionButton, AppBarLayout, SnackBar, etc).
* [x] App uses CoordinatorLayout for the main Activity.
* [x] App theme extends from AppCompat.
* [x] App uses an AppBar and associated Toolbars.
* [ ] App provides a Floating Action Button for the most common action(s).
* [ ] App properly specifies elevations for app bars, FABs, and other elements specified in the Material Design specification.
* [x] App has a consistent color theme defined in styles.xml. Color theme does not impact usability of the app.
* [ ] App provides sufficient space between text and surrounding elements.
* [ ] App uses images that are high quality, specific, and full bleed.
* [ ] App uses fonts that are either the Android defaults, are complementary, and aren't otherwise distracting.
* [x] App utilizes stable release versions of all libraries, Gradle, and Android Studio.
* [ ] App conforms to common standards found in the Android Nanodegree General Project Guidelines.

## Android Nanodegree General Project Guidelines

### Visual Design and User Interaction

#### Standard Design
* [x] App does not redefine the expected function of a system icon (such as the Back button).
* [ ] App does not redefine or misuse Android UI patterns, such that icons or behaviors could be misleading or confusing to users.

#### Navigation
* [ ] App supports standard system Back button navigation and does not make use of any custom, on-screen "Back button" prompts.
* [ ] All dialogs are dismissible using the Back button.
* [ ] Pressing the Home button at any point navigates to the Home screen of the device.

### Functionality

#### Permissions
* [ ] App does not redefine or misuse Android UI patterns, such that icons or behaviors could be misleading or confusing to users.
* [ ] App does not request permissions to access sensitive data or services that can cost the user money, unless related to a core capability of the app.

#### User/App State
* [ ] App correctly preserves and restores user or app state, that is , student uses a bundle to save app state and restores it via onSaveInstanceState/onRestoreInstanceState. For example,
    * [ ] When a list item is selected, it remains selected on rotation.
    * [ ] When an activity is displayed, the same activity appears on rotation.
    * [ ] User text input is preserved on rotation.
    * [ ] Maintains list items positions on device rotation.
* [ ] When the app is resumed after the device wakes from sleep (locked) state, the app returns the user to the exact state in which it was last used.
* [ ] When the app is relaunched from Home or All Apps, the app restores the app state as closely as possible to the previous state.

### Performance and Stability

#### Stability
* [ ] App does not crash, force close, freeze, or otherwise function abnormally on any targeted device.

### Google Play

#### Content Policies
* [ ] All content is safe for work content.
* [ ] App adheres to the [Google Play Store App policies](https://play.google.com/about/developer-content-policy.html).
* [ ] App’s code follows standard Java/Android Style Guidelines.

## Notes and acknowledgments

Many thanks to [Extracting colors from images: Integrating Picasso and Palette](https://medium.com/david-developer/extracting-colors-from-images-integrating-picasso-and-palette-b9ba45c9c418) 
for a technique for getting a palette from within Picasso code rather than having to reload the image.
