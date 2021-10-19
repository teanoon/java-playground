package com.example.hash;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;

import static java.util.concurrent.ThreadLocalRandom.current;

public class Application {

    private void run() {
        var router = new Router();
        var nodes = IntStream.range(0, 10).mapToObj(index -> newNode(index, randomIp())).collect(Collectors.toList());
        var requests = IntStream.range(0, 10_000).mapToObj(v -> randomIp()).collect(Collectors.toList());

        // given a list of nodes
        nodes.forEach(router::addNode);

        // when requested with a bunch of ips
        var distributed = requests.stream()
            .map(request -> Pair.of(router.route(request), request))
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        // then it is distributed
        System.out.println("first");
        distributed.forEach((node, bucket) -> System.out.printf("%45s has %5d\n", node, bucket.size()));

        // when remove one node
        router.removeNode(nodes.get(0));
        System.out.printf("removed %s\n", nodes.get(0));

        // then it is still distributed
        var distributed2 = requests.stream()
            .map(request -> Pair.of(router.route(request), request))
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
        System.out.println("second");
        distributed2.forEach((node, bucket) -> System.out.printf("%45s has %5d\n", node, bucket.size()));

        // and few are moved to the new nodes
        System.out.println("compare");
        distributed2.forEach((node, bucket2) -> {
            var bucket = distributed.get(node);
            var remains = new ArrayList<>(bucket);
            remains.retainAll(bucket2);
            bucket2.removeAll(bucket);
            bucket.removeAll(remains);
            System.out.printf("%45s removed %5d; kept %5d; added %5d\n", node, bucket.size(), remains.size(), bucket2.size());
        });
    }

    private Node newNode(int id, String ip) {
        var node = new Node();
        node.setId(id);
        node.setIp(ip);
        node.setReplica(8);
        return node;
    }

    private String randomIp() {
        return String.format(
            "%d.%d.%d.%d",
            current().nextInt(1, 255),
            current().nextInt(1, 255),
            current().nextInt(1, 255),
            current().nextInt(1, 255));
    }

    public static void main(String[] args) {
        var app = new Application();
        app.run();
    }

}
