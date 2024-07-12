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

![Use Case Diagram](https://i.ibb.co/4VczG4s/1.png)

### Send OTP

Users must create a profile by entering their mobile number to receive an OTP for verification. After entering the OTP, the system verifies the number.

![Use Case Diagram](https://i.ibb.co/hCJmdwN/2.png)

### Automatic Verification System

The app can verify users automatically in certain conditions:
1. Instant verification without needing a code.
2. Auto-retrieval where Google Play services detect the incoming verification SMS.

![Use Case Diagram](https://i.ibb.co/C5q3NQz/3.png)

### Profile Setup

After mobile number validation, users can set up their profile with a name, about, and profile picture, which can be updated later.

![Use Case Diagram](https://i.ibb.co/vPrT4H8/4.png)

### Home Screen

After registration, users access the home screen with three tabs: Chats, Contacts, and Status.

![Use Case Diagram](https://i.ibb.co/JzDRTQS/5.png)

### Chats Tab

Displays all previous chats and allows users to create new chats.

![Use Case Diagram](https://i.ibb.co/4VczG4s/1.png)

### Contacts Tab

Shows users whose mobile numbers are saved in the user's contact list.

![Use Case Diagram](https://i.ibb.co/Js0Zb6j/7.png)

### Status Tab

Displays statuses created by the user and their contacts. Users can create new statuses using the camera or gallery.

![Use Case Diagram](https://i.ibb.co/5jV881B/8.png)

### Settings

Allows users to update their profile information like name, about, and profile picture.

![Use Case Diagram](https://i.ibb.co/YpdVLZt/9.png)

### Chat Screen

Displays the chat log with a particular contact. Users can send text and image messages and delete messages.

![Use Case Diagram](https://i.ibb.co/jy7Fhkr/10.png)

### Status View

Displays the status of a particular user when tapped.

![Use Case Diagram](https://i.ibb.co/0QZX1vN/11.png)

### User Profile View

Displays information about a particular user and allows blocking them.

![Use Case Diagram](https://i.ibb.co/BwXZsKr/12.png)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Thanks to the university for the support and resources.
- Kudos to the development team for their hard work and dedication.
