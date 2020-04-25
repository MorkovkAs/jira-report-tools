# Jira report tool

It is an app for getting Jira issues, generating reports on them, generating release notes.

## Installation

Clone the source locally:
```
$ git clone https://github.com/MorkovkAs/jira-report-tools/
```
Fill your Jira auth data `jira.auth.basic` in `report-service/application.yml` file. See [details](report-service/README.md#jiraauthbasic) in `report-service` module.

Check Gradle is installed and configured

Build and install with gradle
```
$ cd jira-report-tools/
$ ./gradlew build
$ ./gradlew bootRun
```

## Modules
1. `report-service` containing server side logic such as request processing, Jira integration, generating release notes. [Details](report-service/README.md)
2. `report-service-ui` containing client side logic. [Details](report-service/README.md)

## To do list
* [x] commit `report-service` package with server side
* [ ] commit `report-service-ui` package with client side on Vue.js
* [ ] some cool things:)

## Thanks!
Any questions or problems give me a shout on email avklimakov@gmail.com

## License
Copyright 2020 Anton Klimakov\
Licensed under the Apache License, Version 2.0