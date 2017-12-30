# What is Kute?
A Commute App for Sri Lanka

![cover](https://user-images.githubusercontent.com/17242746/34432772-7414905c-eca0-11e7-874e-c38a2c96671f.jpg)

>People travel to the same destination on a regular basis for various reasons using _public and private_ transport. In **Kute**, the users (also known as travellers) themselves are allowed to share their current location, so that the other travellers can see the current location of the vehicle. A common type of vehicle used for public transport in Sri Lanka is the passenger train. Kute is currently implemented for trains. A registered user can update the current location of a train when travelling by sharing the current location. Another user can view location of a selected train and the expected time a particular train arrives at the nearest station using the location data updated by another user.

# Features
- Makes navigation and transportation easier.
- People can navigate & identify public transport services.
- Travellers can share their location.

![ss](https://user-images.githubusercontent.com/17242746/34433097-c0e5f16c-eca2-11e7-922e-086a2a7c0e91.png)

# Setting up the Project
- Fork the repository by clicking on the Fork icon at the top right corner of the repository page.
- Clone the repository on to your local machine by running the following commands on git:
- git clone git clone `https://github.com/[YOUR-USERNAME]/kute.git` Refer Forking and Cloning in -
- git if you are stuck somewhere.
- Download and install Android Studio which is an IDE for android application development.
- You will also need to download the Android SDK from the IDE itself.

# Development Setup

- Launch android studio (version 1.4 above)
- you will see a Welcome to Android window. Under Quick Start, select Import Project (Eclipse ADT, Gradle, etc.)
- Navigate to the directory where you saved the Kute project, select the root folder of the project (the folder named "Kute-master"), and hit OK. 
- Update `kute-android-app/app/src/main/res/values/strings.xml` with a facebook app id and google maps api key
```
<string name="facebook_app_id">YOUR_FB_APP_ID</string>
<string name="googlemapApi">YOUR_GOOGLE_MAPS_API_KEY</string>
```
- Make sure you are connected to internet
- Run and build the Gradle.
- Wait for a few seconds and the app should start on your device (android phone or emulator).

# Running the Application

- Via your own android smartphone
Enable USB Debugging in your phone. Click Run on the Android Studio tool bar, or `Shift + F10` to run the app. By running a virtual device.

- Setup a Android Virtual Device in the IDE.
Then running the application by clicking on Run on the Android Studio tool bar, or `Shift + F10` and then choose the newly created virtual device to run the app.

**Login 1**
Email: kute_rukdjlk_one@tfbnw.net Password: kute123
**Login 2**
Email: kute_jmzumqg_two@tfbnw.net Password: kute123

# Google Code-in 2017

Google Code-in users should checkout the c_in_refine branch for all tasks.
`git checkout c_in_refine`
You'll have all the necessary information to proceed in there.

# License
This project is licensed under the terms of the Apache License 2.0.
