#!/bin/bash

gradle build

rm -f ~/.minecraft-1.21/mods/livingadventuretrails*
yes | cp build/libs/livingadventuretrails-1.21.1.1.jar ~/.minecraft-1.21/mods/
