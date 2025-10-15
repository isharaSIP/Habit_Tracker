
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

## Screenshots
<img width="200" height="425" alt="Screenshot_20251014_132239" src="https://github.com/user-attachments/assets/f7b94e3d-d645-4d53-8491-7473216686ba" />
<img width="200" height="425" alt="Screenshot_20251014_132254" src="https://github.com/user-attachments/assets/a0b506bb-34c1-4edd-b7bd-12d6b74d5921" />
<img width="200" height="425" alt="Screenshot_20251014_132304" src="https://github.com/user-attachments/assets/0ea8c347-1782-41a6-a35f-ca950ed15401" />
<img width="200" height="425" alt="Screenshot_20251014_132519" src="https://github.com/user-attachments/assets/9c8ccc7f-387a-4f64-bf5b-164f7c84b869" />
<img width="200" height="425" alt="Screenshot_20251014_132333" src="https://github.com/user-attachments/assets/36f9cf3a-9252-4fb5-bc58-ebca82ae8b89" />
<img width="200" height="425" alt="Screenshot_20251014_132342" src="https://github.com/user-attachments/assets/8727fcb7-6c90-411a-b3c2-ccd3a616f875" />
<img width="200" height="425" alt="Screenshot_20251014_132352" src="https://github.com/user-attachments/assets/961fdad0-8acc-449a-80c4-9a7e38d3c018" />
<img width="200" height="425" alt="Screenshot_20251014_132358" src="https://github.com/user-attachments/assets/0a836ac5-a684-4d41-abd1-c8aea803fec5" />
