# Kute <img src="https://github.com/padamchopra/kute/blob/c_in_refine/logo.png" width="80" height="80"><br>
A Commute App for Sri Lanka

People travel to the same destination on a regular basis for various reasons using public and private transport. In Kute, the users (also known as travellers) themselves are allowed to share their current location, so that the other travellers can see the current location of the vehicle. A common type of vehicle used for public transport in Sri Lanka is the passenger train. Kute is currently implemented for trains. A registered user can update the current location of a train when travelling by sharing the current location. Another user can view location of a selected train and the expected time a particular train arrives at the nearest station using the location data updated by another user. 


![alt tag](https://github.com/Dilu9218/kute/blob/master/shareShow.png)
![alt tag](https://github.com/Dilu9218/kute/blob/master/search.png)



## Installation

1. Download and install android studio for IDE
   ```
   https://developer.android.com/studio/index.html?hl=id
   ```     
2. Download the Android SDK from the IDE on SDK Manager.

3. Go to your terminal and execute this command

     `git clone https://github.com/scorelab/kute.git`
     
     or download the zip file and then unzip the contents.

4. Checkout the branch 'c_in_refine' (Only for Google Code In participants).

    `cd kute`
    
    `git checkout c_in_refine`

5. Launch android studio(version 1.4 above).

6. To import the project: Click on File, then New and then Import Project or from the welcome screen of android studio click on Import project. (If you wish to contribute, you can directly link to GitHub).

7. Browse to the location where you had extracted the zip and then select the folder kute-android-app in it.

8. Android studio will automatically run and build the Gradle for you. In case of anything missing, studio will prompt you to download the needed file and sync project, click on it.

9. If Gradle finishes to build without error, connect your phone or emulator and run the project by clicking on the play button on the tool bar.

10. Wait for a few seconds and the app should start on your device(android phone or emulator), make sure you are connected to Internet.

11. Possible errors while building the app:
   - Colors not found
   Solution- Within studio, navigate to res-> values -> colors.xml and add the missing colors here.
   - String not found
   Solution- Within studio, navigate to res-> values -> strings.xml and add the missing strings here.
   - Dusplicate strings found
   Solution- Within studio, navigate to res-> values -> strings.xml and remove the strings that are mentioned more than once.
  
12. The app should now build successfully and run without any errors

Note: Please use the Facebook credentails given below to access the application.

## Test users

 user 01
 - email: kute_rukdjlk_one@tfbnw.net
 - pwd: kute123

 user 02
 - email: kute_jmzumqg_two@tfbnw.net
 - pwd: kute123

## License

This project is licensed under the terms of the Apache License 2.0.
 
