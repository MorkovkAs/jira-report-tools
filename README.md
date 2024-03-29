<p align="center">
    <img alt="GitHub Workflow Status (branch)" src="https://img.shields.io/github/workflow/status/MorkovkAs/jira-report-tools/build_master/master">
    <a href="https://github.com/MorkovkAs/jira-report-tools/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/MorkovkAs/jira-report-tools"></a>
    <a href="https://github.com/MorkovkAs/jira-report-tools/blob/master/LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/MorkovkAs/jira-report-tools"></a>
</p>

# Jira report tool

It is an app for getting Jira issues, generating reports on them, generating release notes.

## Quick look
https://jira-report-tools.herokuapp.com/

![](report-service-ui/src/assets/Screenshot_1.jpg)

## Installation

Check [Gradle](https://gradle.org/) and [Node.js with npm](https://nodejs.org/en/download/) are installed and configured

Clone the source locally:
```
$ git clone https://github.com/MorkovkAs/jira-report-tools/
```
Build and run project:
```
$ cd jira-report-tools/
then
$ ./gradlew build
$ ./gradlew bootRun
or 
$ ./gradlew bootJar
$ nohup java -jar report-service-0.4.0.jar > /dev/null 2>&1 &
$ nohup java -jar -DLogback.configurationFile=logback.xml report-service-0.4.0.jar > /dev/null 2>&1 &
or
$ docker compose up -d
```
Go to [localhost](http://localhost:8081) page.

## Modules
1. `report-service` containing server side logic such as request processing, Jira integration, generating release notes. [Details](report-service/README.md)
2. `report-service-ui` containing client side logic. [Details](report-service-ui/README.md)

## Thanks!
Any questions or problems give me a shout on email avklimakov@gmail.com

## License
Copyright 2020 Anton Klimakov\
Licensed under the Apache License, Version 2.0