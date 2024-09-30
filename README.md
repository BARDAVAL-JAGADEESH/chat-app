# Chat Application Clone

## Overview
The Chat Application Clone is a modern mobile application built using Jetpack Compose and Firebase. 
This project aims to enhance skills in Jetpack Compose and Firebase integration, providing users with a seamless chatting experience. 
The application features user authentication, real-time messaging, and a responsive UI tailored for modern Android development practices.

## Features
- **User Authentication:** Users can sign up and log in using Firebase Authentication.
- **Real-Time Messaging:** Supports real-time messaging between users, enabling instant communication.
- **Responsive UI:** Designed using Jetpack Compose for a smooth and adaptive user interface across different devices.
- **Chat Rooms:** Users can create and join chat rooms to interact with others.
- **User Profiles:** Each user has a profile with their display name and profile picture.

## Technologies Used
- **Programming Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Backend:** Firebase (Authentication, Firestore Database)
- **Architecture:** MVVM (Model-View-ViewModel) 
- **Dependencies:**  implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // For instrumentation tests
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    // For local unit tests
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)

    val nav_version = "2.7.7"
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.coil.compose)


## Installation
To run the Chat Application Clone on your local machine, follow these steps:

1. **Clone the Repository**
   ```bash
   git clone https://github.com/BARDAVAL-JAGADEESH/chat-app.git
   cd chat-app
