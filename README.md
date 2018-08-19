# XYZ Reader - "Make Your App Material"

My accepted submission for the sixth project in the "Grow With Google" scholarship Udacity/Google 
Android Developer Nanodegree program. #GoogleUdacityScholars #GrowWithGoogle #MadeWithUdacity

### Phone and Tablet Video Walkthroughs

![XYZ Reader phone video walkthrough](https://github.com/tachyonlabs/Grow-With-Google-Udacity-Android-Developer-Nanodegree-XYZ-Reader/blob/master/xyz_reader.gif "XYZ Reader phone video walkthrough") &nbsp; &nbsp; ![XYZ Reader tablet video walkthrough](https://github.com/tachyonlabs/Grow-With-Google-Udacity-Android-Developer-Nanodegree-XYZ-Reader/blob/master/xyz_reader_tablet.gif "XYZ Reader tablet video walkthrough")

## Why this Project?
   
This project gives you an opportunity to improve an app’s design, a vital skill for building apps users will love. 
It also replicates a common developer task of updating and changing an app's design as new standards are released.

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

* [x] App uses the Design Support library and its provided widget types (FloatingActionButton, AppBarLayout, SnackBar, etc).
* [x] App uses CoordinatorLayout for the main Activity.
* [x] App theme extends from AppCompat.
* [x] App uses an AppBar and associated Toolbars.
* [x] App provides a Floating Action Button for the most common action(s).
* [x] App properly specifies elevations for app bars, FABs, and other elements specified in the [Material Design specification](https://material.io/design/introduction/).
* [x] App has a consistent color theme defined in styles.xml. Color theme does not impact usability of the app.
* [x] App provides sufficient space between text and surrounding elements.
* [x] App uses images that are high quality, specific, and full bleed.
* [x] App uses fonts that are either the Android defaults, are complementary, and aren't otherwise distracting.
* [x] App utilizes stable release versions of all libraries, Gradle, and Android Studio.
* [x] App conforms to common standards found in the Android Nanodegree General Project Guidelines.

## Android Nanodegree General Project Guidelines

### Visual Design and User Interaction

#### Standard Design
* [x] App does not redefine the expected function of a system icon (such as the Back button).
* [x] App does not redefine or misuse Android UI patterns, such that icons or behaviors could be misleading or confusing to users.

#### Navigation
* [x] App supports standard system Back button navigation and does not make use of any custom, on-screen "Back button" prompts.
* [x] All dialogs are dismissible using the Back button.
* [x] Pressing the Home button at any point navigates to the Home screen of the device.

### Functionality

#### Permissions
* [x] App does not redefine or misuse Android UI patterns, such that icons or behaviors could be misleading or confusing to users.
* [x] App does not request permissions to access sensitive data or services that can cost the user money, unless related to a core capability of the app.

#### User/App State
* [x] App correctly preserves and restores user or app state, that is, student uses a bundle to save app state and restores it via onSaveInstanceState/onRestoreInstanceState. For example,
    * [n/a] When a list item is selected, it remains selected on rotation.
    * [x] When an activity is displayed, the same activity appears on rotation.
    * [n/a] User text input is preserved on rotation.
    * [n/a with StaggeredGridLayoutManager, and number of columns dependent on orientation] Maintains list items positions on device rotation.
* [x] When the app is resumed after the device wakes from sleep (locked) state, the app returns the user to the exact state in which it was last used.
* [x] When the app is relaunched from Home or All Apps, the app restores the app state as closely as possible to the previous state.

### Performance and Stability

#### Stability
* [x] App does not crash, force close, freeze, or otherwise function abnormally on any targeted device.

### Google Play

#### Content Policies
* [x] All content is safe for work content.
* [x] App adheres to the [Google Play Store App policies](https://play.google.com/about/developer-content-policy.html).
* [x] App’s code follows standard Java/Android Style Guidelines.

## Open-source libraries used

- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android
- [PicassoPalette](https://github.com/florent37/PicassoPalette) = Make Android Palettes easy to use with Picasso
- [OkHttp](http://square.github.io/okhttp/) = HTTP & HTTP/2 client for Android and Java applications
- [Volley](https://github.com/google/volley) = HTTP library that makes networking for Android apps easier and, most importantly, faster

## Notes and acknowledgments

I made the accent color the same color as the icon and the primary color, because the only place you see the accent 
color is in the Article Detail activity, which takes its palette from the photo rather than the app theme, so I 
thought it was good to have the Floating Action Button reinforce the app primary color rather than be some 
other color that had no relation to anything other than contrasting with colors that aren't even onscreen.
