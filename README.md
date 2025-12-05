# RemindMe v2

## Description

Location-based reminder application

- Allows users to set reminders that trigger when entering or leaving specific locations
- Users can manage their reminders, tag them for organization, and customize their settings
- The app serves as a simple personal assistant to help users remember tasks associated with physical places

## Design

Here is a link to my [Figma](https://github.com/KevinBeltran23/436_project/blob/main/436_design.fg)

- **Reminder List:** Displays all active and inactive reminders
- **Add/Edit Reminder:** Allows users to create or modify reminders
- **Location Picker:** A map interface using Google Maps SDK to pinpoint locations
- **Settings:** Options for dark mode and push notifications

## Features

### Android & Jetpack Compose Features

- **Jetpack Compose:**
- **Navigation Compose:** Single-activity architecture with composable destinations
- **Room Database:**
- **Google Maps SDK:** Interactive map for location selection
- **Geofencing API (Play Services Location):** Triggers background notifications when entering/exiting geofences
- **Fused Location Provider:**
- **ViewModel & StateFlow:**
- **Kotlin Coroutines:**
- **Notification Manager:**

## Dependencies

- Android SDK 34 (upside down cake)
- Google Play Services (Location, Maps)
- Internet connection (for Maps)
- Location Permissions (Fine, Coarse, Background)

## Setup

1. Clone the repository.
2. Open in Android Studio.
3. Get local properties file from me and add it to root
4. Build and run on a device or emulator

## Testing

go to GeoFenceHelper.kt and set flags to true to force notifications based on whether you're inside or outside geofence
