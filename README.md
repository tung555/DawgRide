# 🐾 DawgRide

DawgRide is a full-featured ride-sharing Android app that enables users to post and accept ride offers or requests, manage accepted rides, and keep track of their ride history and points. This app is built using **Java**, **Firebase Realtime Database**, and **Firebase Authentication**.

---

## 🚀 Features

### 🔐 Authentication
- User **registration** with email format and duplicate email validation.
- **Login** to access app functionality.
- **Logout** to terminate the session and restrict access.

### 🚘 Ride Functionality
- View all **ride offers** or **ride requests** depending on the user’s role.
- **Post** a ride offer (as a driver) or ride request (as a rider).
- Edit or delete **unaccepted** rides.
- **Accept** a ride (either as a driver or rider):
  - Ride is moved from available list to accepted rides.
  - Driver gains points; rider spends points.
  - Ride info is stored under both users’ accepted lists.
- View and **complete** accepted rides, moving them to **ride history**.
- Ride points are automatically updated when a ride is confirmed.

### 📄 History & Profile
- Profile displays username and current ride points.
- History view shows completed rides with driver and rider info.
- Button to **view ride history**.
- **Logout** button to end session.

### 🧭 Navigation
- Bottom Navigation Bar for:
  - Requests
  - Offers
  - Post
  - Accepted Rides
  - Profile
- Tab layout under Accepted Rides for:
  - Accepted Rides
  - Unaccepted Rides (with Edit/Delete)

---

## 🧱 Tech Stack

- **Java** (Android)
- **Firebase Authentication** – for user login/register
- **Firebase Realtime Database** – for storing rides, users, and history
- **Material Design Components** – for UI elements like FAB, BottomNavigationView, etc.

---

## 📁 Project Structure Highlights

