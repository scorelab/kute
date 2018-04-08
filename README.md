Kute App
===================


**Kute** is an app that makes navigation and transportation easier and more efficient for people travelling to the same destination.People can easily navigate and identify public transport services around them without having to look through the schedules and timings.Travellers can share their location and tackle the problems of ever-changing schedules together.

![enter image description here](http://res.cloudinary.com/raghavp/image/upload/v1513531690/kute1_bhhvhj.png)

![enter image description here](http://res.cloudinary.com/raghavp/image/upload/v1513531691/kute2_vtvneu.png)

----------


Installation
-------------

You can install and run **Kute App** easily on your android device or emulator by using the following instructions

1) You can download the zip file from the repository at 
https://github.com/scorelab/kute/tree/c_in_refine or,use the following git command to clone the file 
> `git clone https://github.com/scorelab/kute.git`

2) Set up Android Studio on your device, using the following link. This is an IDE for Android development
https://developer.android.com/studio/install.html

3) Start Android studio and download the SDKs. Marshmallow is recommended, although you can try API 27.

4) Set Up your emulator in Android Studio,you can do so by going to Tools > Android > AVD Manager,next you will be prompted with instructions to set up your emulator. Also, do note that this app can be run on Android phones (rather, it is only to be run there when it is ready for actual use). To enable this app to be run on your Android device, you must:
- Go to 'About Phone'
- Tap on Build Number 7 times to become a developer/enable developer options
- Now, go to the main settings page --> developer options --> Enable USB debugging
- Apps can now be run on your phone from a PC through USB

5) Go to the zip file, extract it and navigate to the directory using the "Cd" command and launch the project. Use forward slashes for Git Bash, and backslashes for Powershell/cmd.

6) The project will build automatically,there might be some errors but android studio will provide you with the problem and even give you an option to install the required components causing the error.

7) After this Run the app and select the emulator or your android device. If you have a react-native environment, try `react-native run-android`.

8) Wait for the emulator/device to start up, the APK file will install automatically 

9) Kute App will start up once you open the APK file; you can use the following facebook credentials to log in 

**Login 1**

Email: kute_rukdjlk_one@tfbnw.net
Password: kute123

**Login 2**

Email: kute_jmzumqg_two@tfbnw.net
Password: kute123

**That's It :)**

Troubleshooting
-------------
1) Android Manifest incorrect root - In this case, restart the project and on the bottom left you will see an option to configure android framework ,do so.

2) Google Maven files not installed - In this case, just follow the prompt provided by android studio.

3) SDK Files for emulator not installed - In this case, you can try restarting the project and set up the emulator.




