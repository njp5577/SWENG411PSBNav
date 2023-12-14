# PSB Navigator

This app is implemented with Kotlin and Java for the Android operating system.
<br>
The required libraries are handled by the gradle build file.

To run the app, an android emulator is required.
<br>
Download Android Studio here: https://developer.android.com/studio

<br>
Unzip the file and open the project in Android Studio.

When the project is loaded, sync the gradle build to import the required dependencies.

Then on the right vertical bar select "Device Manager", then "Create Device" in the upper left of the window that pops up.

The emulator used for testing the app is the Pixel 3a with the S system image.

NOTE: Android emulators take up a lot of storage, it is not uncommon to used 10+ GB of storage for an emulator.

<br>
Before running anything with Android Studio, it is a good idea to optimize performance in the settings.

First go to the "File" tab in the upper left, then select "Settings".

Then navigate to "Plugins".

Here we can disable some of these plugins to make the IDE/Gradle Builds run faster.

Some possible plugins that can be disabled include:

Unnecessary language support lke C/C++

Some of the Google Cloud plugins

Some of the Clang plugins

After adjusting plugins we can then adjust how much RAM Android Studio uses.

Go to "Settings" under File in the upper left.

Then go to Appearance & Behavior.

Then go to System Settings, and then Memory Settings.

Here you can adjust the heap sizes that are best for your device.

<br>
Before running the app the emulator needs to have a current location set (or else the current location will be Google Headquarters in California).

To do this, start by clicking the Play button on the emulator you made in the "Device Manager" (you may need to open "Running Devices" in the right bar to see the emulator running).

Then, on the top of the "Running Devices" window, select the triple dots. This will bring up the Extended Controls window.

From here, go to "Location" and then navigate to your desired location on the map.

Then click where you want the current location to be. This will bring up a red marker and a blue box at the bottom of the window. Click "Save Point".

Then in the right window you will see a list of your saved locations. Click on one and then click "Set Location" at the bottom.

It is the suggested to click the triple dots next to your emulator in the "Device Manager" and click "Wipe Data".

Then when run, the emulator should have the current location.
<br>
Next, we need to run the app while the emulator is open.

Select the "app" configuration on the top bar and selected the desired emulator on the top bar as well. Then, click the play button to run the app.

From there, either create a new account or login using one of the following accounts.

Admin Login:
* Username: admin
* Password: password

Event Organizer Login:
* Username: testevent
* Password: password

User Login:
* Username: user
* Password: password

After logging in, the entire app is available for use.

If you have trouble rerunning the app or reopening the emulator, in "Device Manager" click triple dots then "Wipe Data". Then run the emulator and app again (may need to reset current location).

NOTE: Android emulators can crash or be finicky when run on devices with low RAM (16 GB RAM suggested but 8 GB can work with sometimes wiping the emulator data). 
