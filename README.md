# RemindMe v2

## Description

RemindMe v2 is a location-based reminder application that allows users to set reminders that trigger when entering or leaving specific locations. Users can manage their reminders, tag them for organization, and customize their settings. The app serves as a simple personal assistant to help users remember tasks associated with physical places.

## Design

The app follows a clean, Material 3 design with the following screens:

- **Reminder List:** Displays all active and inactive reminders, filterable by tags.
- **Add/Edit Reminder:** Allows users to create or modify reminders, set location, radius, and notes.
- **Location Picker:** A map interface using Google Maps SDK to pinpoint locations.
- **Settings:** Options for dark mode and notifications.

## Features

### Android & Jetpack Compose Features

- **Jetpack Compose:** Fully declarative UI toolkit for building native UI.
- **Navigation Compose:** Single-activity architecture with composable destinations.
- **Room Database:** Local data persistence for reminders and settings.
- **Google Maps SDK:** Interactive map for location selection.
- **Geofencing API (Play Services Location):** Triggers background notifications when entering/exiting geofences.
- **Fused Location Provider:** Efficient location tracking.
- **ViewModel & StateFlow:** Reactive state management following MVVM architecture.
- **Kotlin Coroutines:** Asynchronous programming for database and location operations.
- **Notification Manager:** System notifications for alerts.

## Dependencies

- Android SDK 34 (upside down cake)
- Google Play Services (Location, Maps)
- Internet connection (for Maps)
- Location Permissions (Fine, Coarse, Background)

## Setup

1. Clone the repository.
2. Open in Android Studio.
3. Add your Google Maps API Key in `AndroidManifest.xml` (replace `YOUR_API_KEY`).
4. Build and run on a device or emulator with Google Play Services.
