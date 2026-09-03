package com.jobaggregator.service.dedup;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Union-find over job IDs, used to cluster transitively-similar jobs into duplicate groups. */
final class UnionFind {

    private final Map<UUID, UUID> parent = new HashMap<>();

    void add(UUID id) {
        parent.putIfAbsent(id, id);
    }

    UUID find(UUID id) {
        UUID root = id;
        while (!parent.get(root).equals(root)) {
            root = parent.get(root);
        }
        UUID current = id;
        while (!current.equals(root)) {
            UUID next = parent.get(current);
            parent.put(current, root);
            current = next;
        }
        return root;
    }

    void union(UUID a, UUID b) {
        UUID rootA = find(a);
        UUID rootB = find(b);
        if (!rootA.equals(rootB)) {
            parent.put(rootA, rootB);
        }
    }
}
