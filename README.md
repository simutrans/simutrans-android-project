# Simutrans Android Project

Project to compile Simutrans for Android with gradle and CMake.

## Requirements

Use the setup_android.sh for the simutrans tools folder to install the dependencies.

## Compilation

use ./build_only.sh from the simutrans tools folder.

If the compilation fails, the libary submodules likely needs an update. For SDL, go to the SDL folder and run "git pull origin SDL2" If that fails due to local changes (even though there should not be any, run "git reset --hard" before. The other submodules can be updated in the same way.