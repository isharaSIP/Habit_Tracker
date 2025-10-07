
# HabitZen (Kotlin, SharedPreferences, Widget, WorkManager)

## How to run
1. Open **Android Studio** → *Open an Existing Project* → select this folder.
2. Let Gradle sync. If prompted to create a Gradle wrapper, accept.
3. Run on an emulator or device (Android 7.0+). Grant **Notifications** on Android 13+.

## Features
- Add/delete habits, check off daily completion (stored in SharedPreferences).
- Mood journal with emoji selector and optional note.
- Hydration reminder via WorkManager (set minutes in Settings).
- Home screen widget shows **today's completion %** (long-press home → Widgets → add *HabitZen*).
- Share weekly mood summary via Android share sheet (implicit intent).

## Notes
- No database used; all data stored in SharedPreferences as JSON.
- If the widget doesn't update immediately, interact with the app once (it updates on habit changes).
