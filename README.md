# Packaging and Running Tests

`mvn clean package`

# Deploying to JanusGraph/Spark

1. Build the vertex program jar: `mvn clean package`.
1. Copy the jar to your JanusGraph lib directory: `cp target/tp-vp-example-1.0-SNAPSHOT.jar janusgraph-0.4.1-hadoop2/lib`
1. Start your JanusGraph server. This assumes you're trying this feature out locally: `./bin/janusgraph.sh start`.
1. Open the Gremlin shell: `./bin/gremlin.sh`.
1. If your graph is empty, add a bit of data:
    ```groovy
    gremlin> :remote connect tinkerpop.server conf/remote.yaml session
    ==>Configured localhost/127.0.0.1:8182, localhost/0:0:0:0:0:0:0:1:8182-[bc402af6-9f27-45e3-9a32-51c75b06a3bd]
    gremlin> :remote console
    ==>All scripts will now be sent to Gremlin Server - [localhost/127.0.0.1:8182, localhost/0:0:0:0:0:0:0:1:8182]-[bc402af6-9f27-45e3-9a32-51c75b06a3bd] - type ':remote console' to return to local mode
    gremlin> g.V()
    gremlin> g.addV('Account').property('name', 'A').as('a').addV('Account').property('name', 'B').addE('transcatedWith').from('a')
    ==>e[1lp-3c8-2dx-35s][4328-transcatedWith->4096]
    gremlin> g.V().valueMap()
    ==>{name=[B]}
    ==>{name=[A]}
    gremlin> g.tx().commit()
    ==>null
    gremlin> :remote console
    ==>All scripts will now be evaluated locally - type ':remote console' to return to remote mode for Gremlin Server - [localhost/127.0.0.1:8182, localhost/0:0:0:0:0:0:0:1:8182]-[bc402af6-9f27-45e3-9a32-51c75b06a3bd]
    gremlin>
    ```
1. Run a Spark Gremlin traversal to test connectivity.  
    ```groovy
    gremlin> graph = GraphFactory.open('conf/hadoop-graph/read-cassandra-3.properties')
    ==>hadoopgraph[cassandra3inputformat->gryooutputformat]
    gremlin> a = graph.traversal().withComputer(SparkGraphComputer)
    ==>graphtraversalsource[hadoopgraph[cassandra3inputformat->gryooutputformat], sparkgraphcomputer]
    gremlin> a.V().count()
    13:50:11 WARN  com.datastax.driver.core.Cluster  - You listed localhost/0:0:0:0:0:0:0:1:9042 in your contact points, but it wasn't found in the control host's system.peers at startup
    13:50:13 WARN  com.datastax.driver.core.Cluster  - You listed localhost/0:0:0:0:0:0:0:1:9042 in your contact points, but it wasn't found in the control host's system.peers at startup
    13:50:17 WARN  org.janusgraph.diskstorage.cassandra.astyanax.AstyanaxStoreManager  - Astyanax is deprecated and will be removed with JanusGraph 0.5.0. Please switch to the CQL backend.
    13:50:27 WARN  org.janusgraph.diskstorage.cassandra.astyanax.AstyanaxStoreManager  - Astyanax is deprecated and will be removed with JanusGraph 0.5.0. Please switch to the CQL backend.
    13:50:27 WARN  com.datastax.driver.core.Cluster  - You listed localhost/0:0:0:0:0:0:0:1:9042 in your contact points, but it wasn't found in the control host's system.peers at startup
    13:50:27 WARN  com.datastax.driver.core.Cluster  - You listed localhost/0:0:0:0:0:0:0:1:9042 in your contact points, but it wasn't found in the control host's system.peers at startup
    13:50:27 WARN  com.datastax.driver.core.Cluster  - You listed localhost/0:0:0:0:0:0:0:1:9042 in your contact points, but it wasn't found in the control host's system.peers at startup
    13:50:27 WARN  com.datastax.driver.core.Cluster  - You listed localhost/0:0:0:0:0:0:0:1:9042 in your contact points, but it wasn't found in the control host's system.peers at startup
    13:50:29 WARN  org.janusgraph.diskstorage.cassandra.astyanax.AstyanaxStoreManager  - Astyanax is deprecated and will be removed with JanusGraph 0.5.0. Please switch to the CQL backend.
    ==>2
    ```
1. Run your vertex program:
    ```groovy
    gremlin> import com.experoinc.vp.*
    gremlin> a.V().program(OutDegreeVP.build().create()).valueMap()
    13:53:47 WARN  com.datastax.driver.core.Cluster  - You listed localhost/0:0:0:0:0:0:0:1:9042 in your contact points, but it wasn't found in the control host's system.peers at startup
    13:53:49 WARN  com.datastax.driver.core.Cluster  - You listed localhost/0:0:0:0:0:0:0:1:9042 in your contact points, but it wasn't found in the control host's system.peers at startup
    13:53:54 WARN  org.janusgraph.diskstorage.cassandra.astyanax.AstyanaxStoreManager  - Astyanax is deprecated and will be removed with JanusGraph 0.5.0. Please switch to the CQL backend.
    13:54:04 WARN  org.janusgraph.diskstorage.cassandra.astyanax.AstyanaxStoreManager  - Astyanax is deprecated and will be removed with JanusGraph 0.5.0. Please switch to the CQL backend.
    13:54:04 WARN  com.datastax.driver.core.Cluster  - You listed localhost/0:0:0:0:0:0:0:1:9042 in your contact points, but it wasn't found in the control host's system.peers at startup
    13:54:04 WARN  com.datastax.driver.core.Cluster  - You listed localhost/0:0:0:0:0:0:0:1:9042 in your contact points, but it wasn't found in the control host's system.peers at startup
    13:54:04 WARN  com.datastax.driver.core.Cluster  - You listed localhost/0:0:0:0:0:0:0:1:9042 in your contact points, but it wasn't found in the control host's system.peers at startup
    13:54:04 WARN  com.datastax.driver.core.Cluster  - You listed localhost/0:0:0:0:0:0:0:1:9042 in your contact points, but it wasn't found in the control host's system.peers at startup
    13:54:07 WARN  org.janusgraph.diskstorage.cassandra.astyanax.AstyanaxStoreManager  - Astyanax is deprecated and will be removed with JanusGraph 0.5.0. Please switch to the CQL backend.
    13:54:17 WARN  org.janusgraph.diskstorage.cassandra.astyanax.AstyanaxStoreManager  - Astyanax is deprecated and will be removed with JanusGraph 0.5.0. Please switch to the CQL backend.
    13:54:17 WARN  com.datastax.driver.core.Cluster  - You listed localhost/0:0:0:0:0:0:0:1:9042 in your contact points, but it wasn't found in the control host's system.peers at startup
    ==>[name:[B],edge_count:[1]]
    ==>[name:[A]]
    ```
