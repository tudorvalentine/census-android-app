# Census Android App  

This application was developed by me during the production practice. The application aims to carry out the census process of the population of the Republic of Moldova digitally. The Jetpack Compose framework was used in the application development process and Material Design 3 was used for the design. 
The development of this app was a challenge for me with the purpose of studying new technologies and libraries for myself (Jetpack Compose, Material Design 3, MPAndroidCharts, Retrofit, PSPDF Kit and others)  
In the same way I used Google's recommended architecture to make the app structured and high quality.  ([Guide to app architecture](https://developer.android.com/topic/architecture))
### Backend

An API has been designed for the purposes of the application. The application exchanges data with the server to store it in the database and then to extract it when drawing diagrams.The server is written on **Node.js** along with the **express.js** framework. 

The source code can be found on this [link](https://github.com/tudorvalentine/census-nodejs-server)

The application consists of 2 main screens: Questionnaire and Statistics.

## Features 

- Via the Questionnaire screen the operator who is responsible for the census can enter the data of the persons being censused 
- Real-time country statistics can be viewed on the Statistics screen.
- Interactive charts on the Statistics screen
- Easy navigation between screens
- The app is designed for ease of use and intuitive navigation.

## Screens
<div>
  <img src="https://user-images.githubusercontent.com/66331277/220405693-5708b8b2-5664-4d5c-b06b-afc1e7e5e9b4.png" alt="Alt Text" style="width:20%; height:20%;">
  <img src="https://user-images.githubusercontent.com/66331277/220405650-6aa15aac-58e5-44bd-bfa8-7a8ae4a610ed.png" alt="Alt Text" style="width:20%; height:20%;">
  <img src="https://user-images.githubusercontent.com/66331277/220407257-9dc6469f-6f98-4ddb-8375-189792a1612c.png" alt="Alt Text" style="width:20%; height:20%;">
  <img src="https://user-images.githubusercontent.com/66331277/220409256-73503ee7-65b8-49ee-b4b5-0eea0fc3d6f0.png" alt="Alt Text" style="width:20%; height:20%;">
</div>

### Interactive diagrams

  https://user-images.githubusercontent.com/66331277/220413886-48d53412-a85d-41a1-9d2a-ac033efd196e.mp4


## Installation



1. Clone this repository to your local machine.
2. Open the project in Android Studio.
3. Build and run the app on an emulator or physical device.
