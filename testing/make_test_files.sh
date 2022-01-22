#!/bin/bash
# Are we on Linux? Mac? Cygwin? Something else?

unameOut="$(uname -s)"
case "${unameOut}" in
    Linux*)     machine=Linux;;
    Darwin*)    machine=Mac;;
    CYGWIN*)    machine=Cygwin;;
    MINGW*)     machine=MinGw;;
    *)          machine="UNKNOWN:${unameOut}"
esac

case "${machine}" in
    # Mac is weird - need to use lowercase letters for file sizes
    Mac*)
        testFileSizes=(100m 200m 400m 800m 1600m)
        ;;
    *)
        testFileSizes=(100M 200M 400M 800M 1600M)
        ;;
esac


for fileSize in "${testFileSizes[@]}"
do
	dd if=/dev/zero of=testing/source/$(uuidgen)-$fileSize.dat bs=$fileSize count=1
done
