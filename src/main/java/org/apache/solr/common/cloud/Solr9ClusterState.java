package org.apache.solr.common.cloud;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

/**
 * Adaption layer between SolrJ 4.5.x and Solr 9.x ClusterState API.
 *
 * The adaption layer is used in the original ClusterState load(...) methods to
 */
public class Solr9ClusterState {

    private final SolrZkClient zkClient;
    private final Set<String> liveNodes;

    public Solr9ClusterState(SolrZkClient zkClient, Set<String> liveNodes) {
        this.zkClient = Objects.requireNonNull(zkClient);
        this.liveNodes = Objects.requireNonNull(liveNodes);
    }

    public ClusterState buildClusterState() {
        try {

            // get collections from Solr
            final Collection<String> availableCollections = getCollections();
            final Map<String, DocCollection> collections = new LinkedHashMap<>(availableCollections.size());
            Integer version = null;

            // for each collection get the state from ZK
            for (String collection : availableCollections) {
                final CollectionState collectionState = getCollectionState(collection);
                collections.put(collectionState.getName(), collectionState.toDocCollection());

                // always keep the lowest version found
                if (version == null || collectionState.getVersion() < version) {
                    version = collectionState.getVersion();
                }
            }

            return new ClusterState(version, liveNodes, collections);
        } catch (Exception e) {
            throw new RuntimeException("Error building Solr9 compatible ClusterState from Zookeeper: " + e.getMessage(),
                    e);
        }
    }

    private CollectionState getCollectionState(String collection) throws KeeperException, InterruptedException {
        Objects.requireNonNull(collection);

        final Stat stat = new Stat();
        final byte[] state = zkClient.getData("/collections/" + collection + "/state.json", null, stat, true);

        // this is ugly but this is the expected format in Zookeeper
        return new CollectionState(stat, collection, ZkStateReader.fromJSON(state));
    }

    /**
     * reads the list of current collections from ZK
     */
    private Collection<String> getCollections() throws Exception {
        return zkClient.getChildren("/collections", null, true);
    }

    static class CollectionState {
        private final Stat version;
        private final String name;
        private final Map<String, Map<String, Object>> state;

        public CollectionState(Stat version, String name, Object state) {
            this.version = Objects.requireNonNull(version);
            this.name = Objects.requireNonNull(name);
            this.state = (Map<String, Map<String, Object>>) state;
        }

        public DocCollection toDocCollection() {
            return ClusterState.collectionFromObjects(name, state.get(name));
        }

        public Integer getVersion() {
            return version.getVersion();
        }

        public String getName() {
            return name;
        }
    }

}
