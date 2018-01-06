.# Kute
A Commute App for Sri Lanka

People travel to the same destination on a regular basis for various reasons using public and private transport. In Kute, the users (also known as travellers) themselves are allowed to share their current location, so that the other travellers can see the current location of the vehicle. A common type of vehicle used for public transport in Sri Lanka is the passenger train. Kute is currently implemented for trains. A registered user can update the current location of a train when travelling by sharing the current location. Another user can view location of a selected train and the expected time a particular train arrives at the nearest station using the location data updated by another user. 

![alt tag](https://github.com/Dilu9218/kute/blob/master/shareShow.png)
![alt tag](https://github.com/Dilu9218/kute/blob/master/search.png)



## Installation

1. Go to your terminal and execute this command

   ```
   git clone https://github.com/scorelab/kute.git
   ```
   or download the zip file

2. Launch android studio (version 1.4 above).

3. Click on File, then New and then Import Project Or from the welcome screen of android studio click on Import project (If you wish to contribute, you can directly link to GitHub)

4. Browse to the directory where you cloned (extracted the zip). 

5. Update kute-android-app/app/src/main/res/values/strings.xml with a facebook app id and google maps api key
   ```
   ...
    <string name="facebook_app_id">YOUR_FB_APP_ID</string>
    <string name="googlemapApi">YOUR_GOOGLE_MAPS_API_KEY</string>
   ...
   ```
   
6. Run and build the Gradle

7. If Gradle finishes to build without error run the project by clicking on the play button on the tool bar.

8. Wait for a few seconds and the app should start on your device (android phone or emulator).

9. Make sure you are connected to internet.

## Google Code-in 2017

Google Code-in users should checkout the **c_in_refine** branch for all tasks.

   ```git checkout c_in_refine```
   
You'll have all the necessary information to procede in there.


## License

This project is licensed under the terms of the Apache License 2.0.
 
