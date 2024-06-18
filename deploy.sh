#!/bin/bash

gradle build

rm -f ~/.minecraft-1.20/mods/livingadventuretrails*
yes | cp build/libs/livingadventuretrails-1.20.4.4.jar ~/.minecraft-1.20/mods/
