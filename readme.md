# Simple Android AI Realtime Object Detection

This is a simple Android app that uses the TensorFlow Object Detection API and a pre-trained model to detect objects in real time. 

![CI Build Status](https://github.com/oalee/ai-android-object-detector/actions/workflows/gradle-publish.yml/badge.svg)

### Requirements
- JDK 17

### How to build
1. Clone the repository
2. Run `./gradlew assembleDebug` to build the project

Alternatively, you can import the project into Android Studio and build it from there.

### Built With
- [TensorFlow Lite](https://www.tensorflow.org/lite)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [CameraX](https://developer.android.com/training/camerax)
- [ModelViewViewModel (MVVM)](https://developer.android.com/jetpack/guide)
- [Navigation Component](https://developer.android.com/guide/navigation)
- [RoomDB Persistence Library](https://developer.android.com/training/data-storage/room)
