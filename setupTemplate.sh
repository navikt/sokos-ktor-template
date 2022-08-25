#!/bin/bash

default="sokos-ktor-template"
defaultArtifactName="prosjektnavn"

echo '**** Setup for sokos-ktor-template ****'
echo
read -p 'Project name (sokos-xxxx): ' projectName
read -p 'Artifact name (xxxx): ' artifactName
echo

grep -rl $default --exclude=setupTemplate.sh | xargs -I@ sed -i '' "s|$default|$projectName|g" @
grep -rl $defaultArtifactName --exclude=setupTemplate.sh  | xargs -I@ sed -i '' "s|$defaultArtifactName|$artifactName|g" @
mv src/main/kotlin/no/nav/sokos/prosjektnavn "src/main/kotlin/no/nav/sokos/$artifactName"