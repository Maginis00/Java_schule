package com.example.system;

import com.example.ecs.System;
import com.example.ecs.World;
import com.example.input.KeyState;
import com.example.input.MouseState;

/** Letztes System im InputSet: nicht verbrauchte Klicks und Tastendrücke verwerfen. */
public class InputFrameEndSystem implements System {
    @Override
    public void update(World world, double dt) {
        world.getResource(MouseState.class).endFrame();
        world.getResource(KeyState.class).endFrame();
    }
}
