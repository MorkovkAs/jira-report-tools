<p align="center">
    <img alt="GitHub Workflow Status (branch)" src="https://img.shields.io/github/workflow/status/MorkovkAs/jira-report-tools/reportServiceCI/master">
    <a href="https://github.com/MorkovkAs/jira-report-tools/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/MorkovkAs/jira-report-tools"></a>
    <a href="https://github.com/MorkovkAs/jira-report-tools/blob/master/LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/MorkovkAs/jira-report-tools"></a>
</p>

# Jira report tool

It is an app for getting Jira issues, generating reports on them, generating release notes.

## Installation

Clone the source locally:
```
$ git clone https://github.com/MorkovkAs/jira-report-tools/
```

Check [Gradle](https://gradle.org/) and [Node.js with npm](https://nodejs.org/en/download/) are installed and configured

Build and install `report-service` module. See [instructions](report-service/README.md#installation)

Build and install `report-service-ui` module. See [instructions](report-service-ui/README.md#installation)

## Modules
1. `report-service` containing server side logic such as request processing, Jira integration, generating release notes. [Details](report-service/README.md)
2. `report-service-ui` containing client side logic. [Details](report-service-ui/README.md)

## To do list
* [x] commit `report-service` package with server side
* [x] commit `report-service-ui` package with basic implementation of client side on Vue.js
* [x] upgrade client side to support sending requests of all types
* [ ] conversion of the release notes, in accordance with our project template
* [ ] creating tasks with release info
* [ ] adding UI form validation
* [ ] some cool things:)

## Thanks!
Any questions or problems give me a shout on email avklimakov@gmail.com

## License
Copyright 2020 Anton Klimakov\
Licensed under the Apache License, Version 2.0