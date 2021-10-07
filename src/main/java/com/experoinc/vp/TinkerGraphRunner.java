package com.experoinc.vp;

import org.apache.tinkerpop.gremlin.process.computer.ComputerResult;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;

import java.util.concurrent.ExecutionException;

public class TinkerGraphRunner {

    public static void main(String... args) throws ExecutionException, InterruptedException {
        final Graph graph = TinkerFactory.createModern();
        final ComputerResult result = graph.compute().program(OutDegreeVP.build().create()).submit().get();
        result.graph().traversal().V().valueMap().toList().forEach(System.out::println);
    }
}
