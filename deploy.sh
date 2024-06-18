#!/bin/bash

gradle build

rm -f ~/.minecraft-1.20/mods/livingadventuretrails*
yes | cp build/libs/livingadventuretrails-1.20.4.3.jar ~/.minecraft-1.20/mods/
