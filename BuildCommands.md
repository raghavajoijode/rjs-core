# Building and Maintaining Project

This is a project template for AEM-based applications. It is intended as a best-practice set of examples as well as a potential starting point to develop your own functionality.

## Versioning
Use `versions:set` from the versions-maven plugin:

`mvn versions:set -DnewVersion=2.50.1-SNAPSHOT -DprocessAllModules`
It will adjust all pom versions, parent versions and dependency versions in a multi-module project.

If you made a mistake, do `mvn versions:revert` afterwards, or `mvn versions:commit -DprocessAllModules`


## Release

Removes snapshot from a current version and creates tag and creates a new snapshot upversion

`mvn release:clean release:prepare`

`mvn release:clean release:prepare release:perform -DreleaseVersion=${releaseVersion} -DdevelopmentVersion=${developmentVersion}`


## Building and Deploying 
Use profile `buildFor64` to build project deployable in 6.4 Instance.

`mvn clean install -PbuildFor64 -PautoInstallSinglePackage`
