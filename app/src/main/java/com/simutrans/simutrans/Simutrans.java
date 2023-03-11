package com.simutrans.simutrans;

import org.libsdl.app.SDLActivity;

/**
* A sample wrapper class that just calls SDLActivity
*/
public class Simutrans extends SDLActivity { 

	@Override
    protected String[] getLibraries() {
        return new String[] {
            "SDL2",
            "simutrans"
        };
    }

} 
