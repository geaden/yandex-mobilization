# Yandex Mobilization Sample App

[![Build Status](https://travis-ci.org/geaden/yandex-mobilization.svg?branch=ci)](https://travis-ci.org/geaden/yandex-mobilization)

A sample app for applying to [Yandex Mobilization](https://www.yandex.ru/mobilization/) as an Android Developer.

Inspired by:

 - [Android Testing Code Labs](https://codelabs.developers.google.com/codelabs/android-testing/)
 - [Model-View-Presenter](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter) pattern in Android
 - Clean Architecture
 - Dagger 2 
 - Test-driven development.
 
## Lessons learned
 
1. Manually query database
 
```bash
adb pull /data/data/com.geaden.android.mobilization.app/databases/artists.db
sqlite3 artists.db
sqlite> .schema
```


## Links

 - [Handling-Scrolls-with-CoordinatorLayout](https://guides.codepath.com/android/Handling-Scrolls-with-CoordinatorLayout)
 - [Material Design Icons](https://design.google.com/icons/)
 - [Dependency Injection with Dagger 2](https://github.com/codepath/android_guides/wiki/Dependency-Injection-with-Dagger-2)
 - [DBFlow Guide](https://guides.codepath.com/android/DBFlow-Guide)
 - [Building and Android Project in Travis](https://docs.travis-ci.com/user/languages/android)
 - [Retrofit 2 - Mocking HTTP Responses](http://riggaroo.co.za/retrofit-2-mocking-http-responses/)
 - [Android TDD Playground](https://github.com/pestrada/android-tdd-playground)

Gennady Denisov, 2016.