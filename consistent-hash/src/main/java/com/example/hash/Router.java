package com.example.hash;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static java.util.concurrent.ThreadLocalRandom.current;

public class Router {

    private SortedMap<Long, VirtualNode> ring = new TreeMap<>();
    private Hash hash = new Hash();

    public void addNode(Node node) {
        for (int i = 0; i < node.getReplica(); i++) {
            var virtualNode = new VirtualNode();
            virtualNode.setIp(randomIp());
            virtualNode.setPhysicalNode(node);
            var key = hash.hash(virtualNode.getIp());
            ring.put(key, virtualNode);
        }
    }

    public List<VirtualNode> removeNode(Node node) {
        var iterator = ring.values().iterator();
        var removed = new ArrayList<VirtualNode>();
        while (iterator.hasNext()) {
            var virtualNode = iterator.next();
            if (virtualNode.getPhysicalNode().equals(node)) {
                iterator.remove();
                removed.add(virtualNode);
            }
        }
        return removed;
    }

    public Node route(String ip) {
        var key = hash.hash(ip);
        SortedMap<Long, VirtualNode> tailMap = ring.tailMap(key);
        key = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        return ring.get(key).getPhysicalNode();
    }

    String randomIp() {
        return String.format(
            "%d.%d.%d.%d",
            current().nextInt(1, 255),
            current().nextInt(1, 255),
            current().nextInt(1, 255),
            current().nextInt(1, 255));
    }

}
