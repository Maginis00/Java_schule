package com.example.ecs;

import com.example.component.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Hält alle Entities (nur IDs) und ihre Components.
 * Speicherung: pro Component-Typ eine Map EntityId -> Component.
 */
public class World {
    private int nextId;
    private final Deque<Integer> freeIds = new ArrayDeque<>(); // gelöschte IDs zum Recyceln
    private final Set<Integer> alive = new TreeSet<>();
    // TreeMap: Iteration immer in ID-Reihenfolge -> deterministische Zeichenreihenfolge
    private final Map<Class<?>, Map<Integer, Component>> stores = new HashMap<>();
    private final Map<Class<?>, Object> resources = new HashMap<>();

    // --- Entities ---

    public int createEntity() {
        int id = freeIds.isEmpty() ? nextId++ : freeIds.pop();
        alive.add(id);
        return id;
    }

    /** Entfernt alle Components der Entity und gibt die ID zur Wiederverwendung frei. */
    public void destroyEntity(int id) {
        if (!alive.remove(id)) {
            return;
        }
        for (Map<Integer, Component> store : stores.values()) {
            store.remove(id);
        }
        freeIds.push(id);
    }

    public int getEntityCount() {
        return alive.size();
    }

    // --- Components ---

    public void add(int id, Component component) {
        if (!alive.contains(id)) {
            throw new IllegalArgumentException("Entity " + id + " existiert nicht");
        }
        Map<Integer, Component> store = stores.get(component.getClass());
        if (store == null) {
            store = new TreeMap<>();
            stores.put(component.getClass(), store);
        }
        store.put(id, component);
    }

    public void remove(int id, Class<? extends Component> type) {
        Map<Integer, Component> store = stores.get(type);
        if (store != null) {
            store.remove(id);
        }
    }

    /** Liefert die Component oder null, wenn die Entity sie nicht besitzt. */
    public <T extends Component> T get(int id, Class<T> type) {
        Map<Integer, Component> store = stores.get(type);
        return store == null ? null : type.cast(store.get(id));
    }

    public boolean has(int id, Class<?> type) {
        Map<Integer, Component> store = stores.get(type);
        return store != null && store.containsKey(id);
    }

    // --- Query ---

    public Iterable<Integer> query(Class<?>... all) {
        return query(all, new Class<?>[0]);
    }

    /**
     * Alle Entities, die JEDEN Typ aus "all" und KEINEN Typ aus "exclude" besitzen.
     * Bewusst simpel: die kleinste Component-Map liefert die Kandidaten,
     * alle anderen Typen werden per Lookup gefiltert. Das Ergebnis ist eine Kopie,
     * damit Systeme beim Iterieren Entities erzeugen/löschen dürfen.
     */
    public Iterable<Integer> query(Class<?>[] all, Class<?>[] exclude) {
        Set<Integer> candidates = alive; // ohne "all"-Typen: alle lebenden Entities
        for (Class<?> type : all) {
            Map<Integer, Component> store = stores.get(type);
            if (store == null || store.isEmpty()) {
                return Collections.emptyList();
            }
            if (candidates == alive || store.size() < candidates.size()) {
                candidates = store.keySet();
            }
        }

        List<Integer> result = new ArrayList<>();
        for (int id : candidates) {
            if (hasAll(id, all) && hasNone(id, exclude)) {
                result.add(id);
            }
        }
        return result;
    }

    private boolean hasAll(int id, Class<?>[] types) {
        for (Class<?> type : types) {
            if (!has(id, type)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasNone(int id, Class<?>[] types) {
        for (Class<?> type : types) {
            if (has(id, type)) {
                return false;
            }
        }
        return true;
    }

    // --- Resources (bewusst nur für KeyState und RenderTarget gedacht) ---

    public <T> void setResource(Class<T> type, T value) {
        resources.put(type, value);
    }

    public <T> T getResource(Class<T> type) {
        return type.cast(resources.get(type));
    }
}
