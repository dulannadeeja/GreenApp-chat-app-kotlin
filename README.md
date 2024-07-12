# GreenApp - chat app using kotlin and firebase
“GreenApp” is an android chat app that is implemented by using Kotlin and Firebase.

# GREEN APP

## Introduction

We introduce "GREEN APP," a real-time chat application designed for the university system. GREEN APP allows students to connect with new peers through texting. It is an Android application implemented using Kotlin, and Firebase. Students can chat with others and share their experiences through this app.

## Features of the System
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
- **Chat App Message Interface**: An interactive messaging interface to enhance user interaction.
- **Messaging and Sharing**: Users can exchange text messages and share images.
- **Login**: Users log in using their personal mobile number and an OTP.
- **Notifications**: Users are notified when a message arrives.
- **Encryption**: Ensures personal conversations are secure from unauthorized access.
- **User Profile**: Users can create and update their profile with their name and image.
- **Show Availability**: Users can view the availability status of other users.
- **Cloud Synchronization (Optional)**: Users can log in and view their previous conversations.

## Technical Diagrams

### Entity Relationship Diagram

![Entity Relationship Diagram](https://i.ibb.co/r0WvpGs/er.png)

### Use Case Diagram



https://ibb.co/wzTVXpJ
https://ibb.co/XVrC5MP
https://ibb.co/5WdgtL6
https://ibb.co/Z8wFzVC
https://ibb.co/gDkM8Vc
https://ibb.co/ZVkLmwQ
https://ibb.co/LZbKqjd
https://ibb.co/4SxTTFP
https://ibb.co/5BjJRF5
https://ibb.co/0CP3qyG
https://ibb.co/zNb5CDc
https://ibb.co/4J6TNgY

![Use Case Diagram](https://i.ibb.co/GQHHTVm/use-case.png)

## Requirements of the System

### Functional Requirements

- User registration
- Adding new contacts
- Sending messages
- Sending attachments
- Message status
- One-to-One Chat
- Group chat
- Text / Images / Videos
- Read Receipt
- Last seen
- User profile

### Non-Functional Requirements

- Scalability
- Privacy
- Robustness
- Performance
- Low Latency
- High Availability
- No Lag
- Usability
- Maintainability
- Reliability
- Portability
- Supportability

## System Design and Functions

### Welcome Screen

Users must agree to the terms and conditions to use GREEN APP. By clicking "Agree & Start Chat," they agree to the app's terms and conditions.

### Send OTP

Users must create a profile by entering their mobile number to receive an OTP for verification. After entering the OTP, the system verifies the number.

### Automatic Verification System

The app can verify users automatically in certain conditions:
1. Instant verification without needing a code.
2. Auto-retrieval where Google Play services detect the incoming verification SMS.

### Profile Setup

After mobile number validation, users can set up their profile with a name, about, and profile picture, which can be updated later.

### Home Screen

After registration, users access the home screen with three tabs: Chats, Contacts, and Status.

### Chats Tab

Displays all previous chats and allows users to create new chats.

### Contacts Tab

Shows users whose mobile numbers are saved in the user's contact list.

### Status Tab

Displays statuses created by the user and their contacts. Users can create new statuses using the camera or gallery.

### Settings

Allows users to update their profile information like name, about, and profile picture.

### Chat Screen

Displays the chat log with a particular contact. Users can send text and image messages and delete messages.

### Status View

Displays the status of a particular user when tapped.

### User Profile View

Displays information about a particular user and allows blocking them.

## Scope of the Project

GREEN APP addresses the lack of modern communication technologies in universities, especially during the Covid-19 pandemic. It is user-friendly and requires minimal technical knowledge. 

Our project aims to enhance communication and productivity among university students. GREEN APP is secure, reliable, easy to use, and free.

### Processes of Green App

- Satisfies user requirements for university communication.
- Easy to operate.
- Unique user interface.
- Increases student productivity.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Thanks to the university for the support and resources.
- Kudos to the development team for their hard work and dedication.
