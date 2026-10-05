package com.example.system;

import com.example.ecs.System;
import com.example.ecs.World;
import com.example.recording.WorldSnapshot;
import com.example.resource.Recorder;

/**
 * UpdateSet: nimmt pro Logik-Schritt den Zustand aller Figuren auf bzw. spielt ihn ab. Läuft nach
 * dem InputSet, damit ein Frame schon das Ziehen und die Dialog-Änderungen dieses Schritts enthält.
 */
public class RecorderSystem implements System {
    @Override
    public void update(World world, double dt) {
        Recorder recorder = world.getResource(Recorder.class);

        if (recorder.isRecording()) {
            recorder.addFrame(WorldSnapshot.capture(world), dt);
        } else if (recorder.isPlaying()) {
            WorldSnapshot frame = recorder.nextFrame();
            if (frame != null) {
                frame.apply(world);
            } else {
                // Ende der Aufnahme: Welt wieder so wie vor dem Abspielen
                recorder.stopPlayback().apply(world);
            }
        }
    }
}
