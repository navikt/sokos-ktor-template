#!/bin/bash

default="sokos-ktor-template"
defaultArtifactName="prosjektnavn"

# Define a variable for the red color
red='\033[0;31m'
# No Color
NC='\033[0m'

echo '**** Setup for sokos-ktor-template ****'
echo

while true; do
    read -p 'Project name (eg: sokos-ktor-template): ' projectName
    if [[ $projectName =~ ^[a-z0-9\-]+$ ]]; then
        break
    else
        echo -e "${red}Project name can only contain lowercase letters, numbers, and hyphens.${NC}"
    fi
done

while true; do
    read -p 'Artifact name (ktor.template): ' artifactName
    if [[ $artifactName =~ ^[a-z0-9\.]+$ ]]; then
        break
    else
        echo -e "${red}Artifact name can only contain lowercase letters, numbers, and periods.${NC}"
    fi
done
echo

# Replace hyphens with underscores for METRICS_NAMESPACE
projectNameWithUnderScore=$(echo $projectName | tr '-' '_')

artifactNamePath=$(echo $artifactName | tr '.' '/')

# Create new directories before running sed commands
mkdir -p "src/main/kotlin/no/nav/sokos/$artifactNamePath"
mkdir -p "src/test/kotlin/no/nav/sokos/$artifactNamePath"

# Combine grep commands
grep -rl --exclude=setupTemplate.sh --exclude-dir=.git -e $default -e $defaultArtifactName -e "sokos_ktor_template" -e "sokos_ktor_template_type" | xargs -I@ sed -i '' -e "s|$default|$projectName|g" -e "s|$defaultArtifactName|$artifactName|g" -e "s|sokos_ktor_template|$projectNameWithUnderScore|g" -e "s|sokos_ktor_template_type|${projectNameWithUnderScore}_type|g" @

# Move files from the old directory to the new one
mv src/main/kotlin/no/nav/sokos/prosjektnavn/* "src/main/kotlin/no/nav/sokos/$artifactNamePath"
mv src/test/kotlin/no/nav/sokos/prosjektnavn/* "src/test/kotlin/no/nav/sokos/$artifactNamePath"

# Remove the old directories
rmdir src/main/kotlin/no/nav/sokos/prosjektnavn
rmdir src/test/kotlin/no/nav/sokos/prosjektnavn
