package com.experoinc.vp;

import org.apache.tinkerpop.gremlin.process.computer.*;
import org.apache.tinkerpop.gremlin.process.computer.util.AbstractVertexProgramBuilder;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class OutDegreeVP implements VertexProgram<Long> {

    private OutDegreeVP() {}

    @Override
    public void setup(Memory memory) {
        //
    }

    @Override
    public Set<VertexComputeKey> getVertexComputeKeys() {
        return new HashSet(Arrays.asList(VertexComputeKey.of("edge_count", false)));
    }

    @Override
    public void execute(Vertex vertex, Messenger<Long> messenger, Memory memory) {
        if (memory.isInitialIteration()) {
            final long edgeCount = IteratorUtils.count(vertex.edges(Direction.BOTH));
            messenger.sendMessage(MessageScope.Local.of(__::outE), edgeCount);
        } else {
            if (messenger.receiveMessages().hasNext()) {
                long edgeCount = IteratorUtils.reduce(messenger.receiveMessages(), 0l, (a, b) -> a + b);
                vertex.property(VertexProperty.Cardinality.single, "edge_count", edgeCount);
            }
        }
    }

    @Override
    public boolean terminate(Memory memory) {
        if (memory.getIteration() == 2) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Set<MessageScope> getMessageScopes(Memory memory) {
        final Set<MessageScope> set = new HashSet<>();
        set.add(MessageScope.Local.of(__::outE));
        return set;
    }

    @Override
    public OutDegreeVP clone() {
        try {
            final OutDegreeVP clone = (OutDegreeVP) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GraphComputer.ResultGraph getPreferredResultGraph() {
        return null;
    }

    @Override
    public GraphComputer.Persist getPreferredPersist() {
        return null;
    }

    public static OutDegreeVP.Builder build() {
        return new OutDegreeVP.Builder();
    }

    public final static class Builder extends AbstractVertexProgramBuilder<OutDegreeVP.Builder> {

        private Builder() {
            super(OutDegreeVP.class);
        }
    }
}
