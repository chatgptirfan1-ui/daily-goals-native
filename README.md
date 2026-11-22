# Daily Goals — Android Native starter

This is the initial native Android project scaffold based on the uploaded Daily Goals HTML.

## What was found in the original HTML
- Daily habit tracker with monthly records (`dailyGoals.v1.YYYY-MM`)
- Weekly Goals with date-specific checks and editable definitions (`dailyGoals.weekly.v1`, `dailyGoals.weekly.defs.v1`)
- Main Goals and achievement history
- To-Do List with deadline, priority matrix, notes and subtasks (`dailyGoalsTodoList_v1`)
- Gym dashboard and workout records
- Progress charts, streak calendar, quotes and periodic goals

## Current native starter
- Kotlin + Jetpack Compose UI shell
- Room database schema for daily checks, weekly goals/checks, and gym sessions
- Starter screens for Home, Weekly, Gym and To-Do
- Seed weekly goals based on the original file: Bersihkan kamar, Makan 2 kali sehari, Kerumah bapak

IMPORTANT: This is a starter project, not a complete feature-for-feature conversion yet. The original HTML contains ~695 KB of code and multiple independent data stores. The remaining screens, interactions, export/import and full data migration must be implemented and tested before replacing the original app. Do not uninstall the existing browser/PWA app or clear its browser data during development.

## Build APK using GitHub Actions (phone-only workflow)
1. Create a GitHub account and a new empty repository named `daily-goals-native`.
2. Upload the contents of this folder to the repository (not the outer folder itself).
3. Open the repository's Actions tab and enable Actions if prompted.
4. The workflow `Android APK` runs on pushes and builds `app-debug.apk`.
5. Open the completed workflow run, scroll to Artifacts, and download `DailyGoals-debug-apk`.
6. Extract the ZIP on your Android phone, tap the APK, and allow installation from that source if Android asks.

Build requirements: JDK 17, Android Gradle Plugin 8.7.3, Gradle 8.9, compile SDK 35. GitHub-hosted runner provides the Android SDK.
