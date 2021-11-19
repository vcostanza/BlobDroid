## Overview
BlobDroid is a simple library containing helper classes that I use to make Android development easier.
Some of these features include:
- OpenGL shader, layer, and drawable object system
- Easier shared preference management
- More easily customizable preference views
- Threads that can be used for logic processing at a steady interval
- SDK compatibility methods

## Requirements
- Android 6.0 Marshmallow or newer

## Class Reference

### Preferences

[**SimplePreferences**](lib/src/main/java/software/blob/android/preference/util/SimplePreferences.kt)  
Preferences wrapper that simplifies preference management by utilizing operator overrides

[**PreferenceView**](lib/src/main/java/software/blob/android/preference/view/PreferenceView.kt)  
Preference view that supports customization of the bottom widget frame (used by `SeekBarPreferenceView`)

[**SeekBarPreferenceView**](lib/src/main/java/software/blob/android/preference/view/SeekBarPreferenceView.kt)  
Preference for selecting an integer or floating point number using a seek bar

[**ColorPreferenceView**](lib/src/main/java/software/blob/android/preference/view/ColorPreferenceView.kt)  
Preference for selecting a color using a dialog

[**PreferenceLinkView**](lib/src/main/java/software/blob/android/preference/view/PreferenceLinkView.kt)  
Preference that links to another preference layout via `xmlId` attribute

[**GenericPreferenceFragment**](lib/src/main/java/software/blob/android/preference/fragment/GenericPreferenceFragment.kt)  
Preference fragment that only requires the preference layout XML ID

### OpenGL

[**GLShader**](lib/src/main/java/software/blob/android/opengl/shader/GLShader.kt)  
Shader program helper class that accepts vertex and fragment shader sources

[**GLBasicShader**](lib/src/main/java/software/blob/android/opengl/shader/GLBasicShader.kt)  
Basic implementation of `GLShader` that supports color modulation and textures

[**GLDrawable**](lib/src/main/java/software/blob/android/opengl/drawable/GLDrawable.kt)  
Interface for an object that is drawn with OpenGL using a `GLShader`

[**GLShape**](lib/src/main/java/software/blob/android/opengl/drawable/GLShape.kt)  
Implementation of `GLDrawable` that defines a shape using vertices, textures, and transformation parameters

[**GLRectangle**](lib/src/main/java/software/blob/android/opengl/drawable/GLRectangle.kt)  
Shape that has helper methods for adding basic rectangles

[**GLTextRect**](lib/src/main/java/software/blob/android/opengl/drawable/GLTextRect.kt)  
Texture-based text that is rendered in a rectangle

[**GLLayer**](lib/src/main/java/software/blob/android/opengl/layer/GLLayer.kt)  
Used for rendering a set of `GLDrawable` objects

[**GLBasicLayer**](lib/src/main/java/software/blob/android/opengl/layer/GLBasicLayer.kt)  
Implementation of `GLLayer` that only accepts a `GLBasicShader` for rendering

[**GLBasic2DLayer**](lib/src/main/java/software/blob/android/opengl/layer/GLBasic2DLayer.kt)  
Implementation of `GLBasicShader` for rendering objects in 2D

[**GLCachedTextLayer**](lib/src/main/java/software/blob/android/opengl/layer/GLCachedTextLayer.kt)  
Layer for rendering `GLTextRect` objects that are dynamically cached by their text content or a key

### Threading

[**IntervalThread**](lib/src/main/java/software/blob/android/thread/IntervalThread.kt)  
Thread that runs at a steady interval for logic processing (in milliseconds or FPS)

[**BasicIntervalThread**](lib/src/main/java/software/blob/android/thread/BasicIntervalThread.kt)  
Basic implementation of `IntervalThread` that accepts a lambda in its constructor

### SDK Compatibility

[**SDKCompat**](lib/src/main/java/software/blob/android/compatibility/SDKCompat.kt)  
Defines several class-injected methods with the suffix `Compat` for making SDK cross compatibility easier