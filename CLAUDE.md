# CLAUDE.md

## Preferences
すべての応答は日本語でお願いします。コードに関する説明や提案も日本語で記述してください。

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Kotlin-based Android TODO application project using MVVM architecture. The project includes task management functionality with the ability to add new tasks and toggle their completion status.

## Common Development Commands

### Android Project Commands:
- `./gradlew build` - Build the project
- `./gradlew test` - Run unit tests
- `./gradlew connectedAndroidTest` - Run instrumented tests
- `./gradlew assembleDebug` - Build debug APK
- `./gradlew installDebug` - Install debug APK on connected device

## Architecture

The project follows MVVM (Model-View-ViewModel) architecture:

- **Model**: `Task.kt` - Data class representing a task with id, title, and completion status
- **ViewModel**: `TaskViewModel.kt` - Manages task list state and business logic using LiveData
- **View**: `MainActivity.kt` + XML layouts - UI layer with RecyclerView for task display
- **Adapter**: `TaskAdapter.kt` - RecyclerView adapter using ListAdapter with DiffUtil

## Key Components

- **Task Management**: Add new tasks and toggle completion status
- **UI Features**: EditText for input, Button for adding, RecyclerView for task list
- **Visual Feedback**: Strikethrough text for completed tasks
- **Input Handling**: Support for both button click and Enter key to add tasks

## Project Structure

```
src/main/
├── java/com/example/todo/
│   ├── Task.kt              # Task data class
│   ├── TaskViewModel.kt     # ViewModel with LiveData
│   ├── TaskAdapter.kt       # RecyclerView adapter
│   └── MainActivity.kt      # Main activity
├── res/
│   ├── layout/
│   │   ├── activity_main.xml    # Main layout
│   │   └── item_task.xml        # Task item layout
│   └── values/              # Strings, colors, themes
└── AndroidManifest.xml
```
