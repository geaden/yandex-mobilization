# Yandex Mobilization Sample App

[![Build Status](https://travis-ci.org/geaden/yandex-mobilization.svg?branch=ci)](https://travis-ci.org/geaden/yandex-mobilization) [![Coverage Status](https://coveralls.io/repos/github/geaden/yandex-mobilization/badge.svg?branch=ci)](https://coveralls.io/github/geaden/yandex-mobilization?branch=ci)

A sample app for applying to [Yandex Mobilization](https://www.yandex.ru/mobilization/) as an Android Developer.

Inspired by:

 - [Android Testing Code Labs](https://codelabs.developers.google.com/codelabs/android-testing/)
 - [Model-View-Presenter](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter) pattern in Android
 - Clean Architecture
 - Dagger 2 
 - Test-driven development.
 
## Screenshots

<img src="https://raw.githubusercontent.com/geaden/yandex-mobilization/master/screenshots/artists-grid.png" width="400" alt="Artists Grid" /> <img src="https://raw.githubusercontent.com/geaden/yandex-mobilization/master/screenshots/artists-detail.png" width="400" alt="Artists Detail" />

## Download

[![Download](https://disk.yandex.net/qr/?text=https%3A%2F%2Fyadi.sk%2Fd%2FVrwklSbRrAhpo)](https://yadi.sk/d/VrwklSbRrAhpo)
 
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
 - [Building an Android Project in Travis](https://docs.travis-ci.com/user/languages/android)
 - [Retrofit 2 - Mocking HTTP Responses](http://riggaroo.co.za/retrofit-2-mocking-http-responses/)
 - [Android TDD Playground](https://github.com/pestrada/android-tdd-playground)
 - [Square Island about Android Development](http://blog.sqisland.com/)
 - [Fragment Navigation Drawer](https://github.com/codepath/android_guides/wiki/Fragment-Navigation-Drawer)
 - [Android Dev Summit Architecture Demo](https://github.com/yigit/dev-summit-architecture-demo)
 - [Upload file to Yandex Disk](http://www.influunt.ru/backup2yandex-disk-with-rest-api)
 - [Continuous integration on Android with travis CI](http://panavtec.me/continous-integration-on-android-with-travis-ci)

Gennady Denisov, 2016.