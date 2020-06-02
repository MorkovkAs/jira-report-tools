# Jira report tool service

It is a REST service for getting Jira issues, generating reports on them, generating release notes.

## Installation

Fill the `jira.url` in `report-service/application.yml` file. See [details](#jiraurl) below

Fill the `jira.auth.basic` with your Jira auth data in `report-service/application.yml` file. See [details](#jiraauthbasic) below

Check Gradle is installed and configured

Build and install with gradle
```
$ cd jira-report-tools/report-service/
$ ./gradlew build
$ ./gradlew bootRun
```

## Usage preparation

Fill the `jira.url` in `report-service/application.yml` file. See [details](#jiraurl) below

Fill the `jira.auth.basic` with your Jira auth data in `report-service/application.yml` file. See [details](#jiraauthbasic) below

## Params

### application.yml

##### jira.url

*Required*\
Type: `String`

Url for Jira.

##### jira.auth.basic

*Required*\
Type: `String`

Your auth data for Jira. It should be the result of `"Basic " + base64 (login + ":" + password)`\
For example: 
```
jira.auth.basic=Basic TW9ya292a0E6bXlTdXBlclBhc3M=
```

##### jira.search.default.project

*Required*\
Type: `String`

Default name of the project for Jira.

##### jira.search.default.limit

*Required*\
Type: `Int`

Default limit on the number of returned issues from Jira.

##### jira.search.default.comment.test.case.start

*Required*\
Type: `String`

String to search test cases in issue comments, used as `comment.startsWith("\${jira.comment.test.case.start}")`\
For example: 
```
jira.comment.test.case.start=Сценарий для тестирования на препроде
```

##### jira.search.default.comment.deploy.instruction.start

*Required*\
Type: `String`

String to search deploy instructions in issue comments, used as `comment.startsWith("\${jira.comment.deploy.instruction.start}")`\
For example: 
```
jira.comment.deploy.instruction.start=Инструкция по установке
```

## Usage

There are examples of common requests in `jira-report-tools/src/test/kotlin/ru/morkovka/report/requestExample/service/`

##### Get issues by jql search

Type: `GET`\
Url: `/task/byJql?jql={jql}&limit={limit}`\
Returns a list of representation of the issues for the given jql (Jira query language) search.

##### Get issue by issueIdOrKey

Type: `GET`\
Url: `/task/byKey?jiraKey={issueIdOrKey}`\
Returns a representation of the issue for the given issue key.

##### Get issues by fixVersion

Type: `GET`\
Url: `/task/byRelease?jiraRelease={fixVersion}&limit={limit}`\
Returns a list of representation of the issues for the given fixVersion.

##### Get simple instructions for issues by fixVersion

Type: `GET`\
Url: `/release/infoByRelease?jiraRelease={fixVersion}&limit={limit}`\
Returns test cases and deploy instructions of the issues for the given fixVersion.

##### Get release report by jiraRelease

Type: `GET`\
Url: `/release/getReleaseNote?jiraRelease={fixVersion}&limit={limit}`\
Returns release report data for the given fixVersion.

## Thanks!
Any questions or problems give me a shout on email avklimakov@gmail.com

## License
Copyright 2020 Anton Klimakov\
Licensed under the Apache License, Version 2.0
