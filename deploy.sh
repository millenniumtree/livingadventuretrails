#!/bin/bash

mod="livingadventuretrails"
modversion=$(grep mod_version gradle.properties |cut -d'=' -f2)
mcversion=$(grep mod_version gradle.properties |cut -d'=' -f2 |cut -d'.' -f1-3)
echo "Deploying mod $mod version $modversion to folder .minecraft-$mcversion"

gradle build

rm -f ~/.minecraft-$mcversion/mods/$mod*
yes | cp build/libs/$mod-$modversion.jar ~/.minecraft-$mcversion/mods/
