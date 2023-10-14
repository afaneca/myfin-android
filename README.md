[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/afaneca/myfin-android?include_prereleases)
[<img style="height: 20px;" src="https://img.shields.io/endpoint?url=https://apt.izzysoft.de/fdroid/api/v1/shield/com.afaneca.myfin">](https://apt.izzysoft.de/fdroid/index/apk/com.afaneca.myfin/)
[<img style="height: 20px;" src="https://img.shields.io/badge/Google_Play-414141?style=for-the-badge&logo=google-play&logoColor=white">](https://play.google.com/store/apps/details?id=com.afaneca.myfin)


# Android App for [MyFin - Personal Finances Manager platform](https://github.com/aFaneca/myfin)
- [About MyFin](#about-myfin)
  - [Features](#features)
  - [Roadmap](#roadmap)
  - [App Architecture](#app-architecture)
- [Getting Started](#getting-started)
- [Contributing](#contributing)

![image](/img/feature0.png)
 <p align="center">
  <img src="/img/0.png" width="15%"></img>
  <img src="/img/1.png" width="15%"></img>
  <img src="/img/2.png" width="15%"></img>
  <img src="/img/3.png" width="15%"></img>
  <img src="/img/4.png" width="15%"></img>
  <img src="/img/5.png" width="15%"></img>
  <img src="/img/6.png" width="15%"></img>
  <img src="/img/7.png" width="15%"></img>
  <img src="/img/8.png" width="15%"></img>
  <img src="/img/9.png" width="15%"></img>
  <img src="/img/10.png" width="15%"></img> 
</p>

# About MyFin
MyFin originated as my own passion project in 2020. At that point, I'd already tried a bunch of other FPM's, but all of them lacked in atleast one of the following points:
- They were not user-friendly
- Their features lacked a full-fledged budgeting tool
- I was never in control of my own data

MyFin is my <u>attempt</u> to solve all of these issues. It has helped me manage my finances for a while now and I hope it can be useful for you as well.

## Features
Here are the main features of MyFin:
- **Authentication** - Full-fledged authentication system integrated with the MyFin API, with support for fingerprint auth
- **Transactions** - You can see and filter through all your transactions
- **Accounts** - You can track all of your accounts, including their transactions and balances
- **Budgets** - Taking a Boonzi-style approach to budgeting, our budget tool allows you to budget for your present and future, month by month
- **Stats** - this one's for the data nerds. Here you have an overview of your patrimony's evolution across each month and get a forecast of your financial future for the years to come 


## Roadmap
Here's some of the features currently in development or planned for the near future:
- **Goals** - Record and keep track of your goals to keep yourself motivated at all times
- **Better Account Management** - allowing the user to change its data (email, profile photo...)
- **Better Stats** - add more complex & interesting stats
- **Investing** - add a module specific to keeping track of your investments
- & much more...

## App Architecture
- 100% in Kotlin
- Standard [MVVM](https://developer.android.com/jetpack/guide) arch
- Dependency Injection w/ [Dagger-Hilt](https://dagger.dev/hilt/)
- Retrofit for HTTP communication with [MyFin API](https://github.com/aFaneca/myfin)
- [Room DB](https://developer.android.com/training/data-storage/room) (encrypted with [SQLCypher](https://github.com/sqlcipher/sqlcipher)) & [EncryptedSharedPreferences](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences) for local data persistence
- [Jetpack](https://developer.android.com/jetpack)-focused (including [Jetpack Navigation](https://developer.android.com/guide/navigation) for navigation flows)

# Getting Started
This mobile app is already available on [**Play Store**](https://play.google.com/store/apps/details?id=com.afaneca.myfin) for closed testing and will be available shortly for the general public.
For a demo, you can use the following auth data:
- Username: demo
- Password: demo

# Contributing
This was never meant to be anything other than a little passion project of mine. However, if you're interested in taking this project and make it your own or add something to it, you're more than welcome to do so. Just get in touch :)
