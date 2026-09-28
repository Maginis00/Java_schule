package com.example.ecs;

import java.util.ArrayList;
import java.util.List;

/** Benannte, geordnete Gruppe von Systems; läuft in Einfügereihenfolge und ist abschaltbar. */
public class SystemSet {
    private final String name;
    private final List<System> systems = new ArrayList<>();
    private boolean enabled = true;

    public SystemSet(String name) {
        this.name = name;
    }

    public SystemSet add(System system) {
        systems.add(system);
        return this;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    void run(World world, double dt) {
        if (!enabled) {
            return;
        }
        for (System system : systems) {
            system.update(world, dt);
        }
    }
}
