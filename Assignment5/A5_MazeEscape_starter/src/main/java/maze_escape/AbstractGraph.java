package maze_escape;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public abstract class AbstractGraph<V> {

    /**
     * Graph representation:
     * this class implements graph search algorithms on a graph with abstract vertex
     * type V
     * for every vertex in the graph, its neighbours can be found by use of abstract
     * method getNeighbours(fromVertex)
     * this abstraction can be used for both directed and undirected graphs
     **/

    public AbstractGraph() {
    }

    /**
     * retrieves all neighbours of the given fromVertex
     * if the graph is directed, the implementation of this method shall follow the
     * outgoing edges of fromVertex
     *
     * @param fromVertex
     * @return
     */
    public abstract Set<V> getNeighbours(V fromVertex);

    /**
     * retrieves all vertices that can be reached directly or indirectly from the
     * given firstVertex
     * if the graph is directed, only outgoing edges shall be traversed
     * firstVertex shall be included in the result as well
     * if the graph is connected, all vertices shall be found
     *
     * @param firstVertex the start vertex for the retrieval
     * @return
     */
    public Set<V> getAllVertices(V firstVertex) {
        Set<V> vertices = new HashSet<>();
        getAllVerticesRecursively(firstVertex, vertices);
        return vertices; // replace by a proper outcome
    }

    /**
     * Recursive traverse over all connected vertices and adding it to the set
     *
     * @param currentVertex the current vertex that is being traverse
     * @param vertices      a set of vertices already traversed
     */
    private void getAllVerticesRecursively(V currentVertex, Set<V> vertices) {
        if (vertices.contains(currentVertex))
            return;
        vertices.add(currentVertex);

        for (V neighbour : getNeighbours(currentVertex)) {
            getAllVerticesRecursively(neighbour, vertices);
        }
    }

    /**
     * Formats the adjacency list of the subgraph starting at the given firstVertex
     * according to the format:
     * vertex1: [neighbour11,neighbour12,…]
     * vertex2: [neighbour21,neighbour22,…]
     * …
     * Uses a pre-order traversal of a spanning tree of the sub-graph starting with
     * firstVertex as the root
     * if the graph is directed, only outgoing edges shall be traversed
     * , and using the getNeighbours() method to retrieve the roots of the child
     * subtrees.
     *
     * @param firstVertex
     * @return
     */
    public String formatAdjacencyList(V firstVertex) {
        StringBuilder stringBuilder = new StringBuilder("Graph adjacency list:\n");
        formatAdjacencyListRecursive(firstVertex, stringBuilder, new HashSet<>());
        return stringBuilder.toString();
    }

    /**
     * Helper function to print out adjacency list for all vertices
     *
     * @param currentVertex the current vertex to print the adjacency list for
     * @param stringBuilder the stringbuilder to create the whole adjacency list
     * @param visited       a list to track all vertices already printed out, they
     *                      should not be visited again
     */
    private void formatAdjacencyListRecursive(V currentVertex, StringBuilder stringBuilder, Set<V> visited) {
        if (visited.contains(currentVertex))
            return;

        visited.add(currentVertex);
        // print current
        stringBuilder.append(currentVertex).append(": [");
        Set<V> neighbours = getNeighbours(currentVertex);

        // print list of neighbours
        for (V neighbour : neighbours) {
            stringBuilder.append(neighbour).append(",");
        }
        if (stringBuilder.charAt(stringBuilder.length() - 1) == ',') {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append("]\n");

        // recursively solve for neighbours
        for (V neighbour : neighbours) {
            formatAdjacencyListRecursive(neighbour, stringBuilder, visited);
        }
    }

    /**
     * represents a directed path of connected vertices in the graph
     */
    public class GPath {
        private Deque<V> vertices = new LinkedList<>();
        private double totalWeight = 0.0;
        private Set<V> visited = new HashSet<>();

        /**
         * representation invariants:
         * 1. vertices contains a sequence of vertices that are neighbours in the graph,
         * i.e. FOR ALL i: 1 < i < vertices.length:
         * getNeighbours(vertices[i-1]).contains(vertices[i])
         * 2. a path with one vertex equal start and target vertex
         * 3. a path without vertices is empty, does not have a start nor a target
         * totalWeight is a helper attribute to capture total path length from a
         * function on two neighbouring vertices
         * visited is a helper set to be able to track visited vertices in searches,
         * only for analysis purposes
         **/
        private static final int DISPLAY_CUT = 10;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(
                    String.format("Weight=%.2f Length=%d visited=%d (",
                            this.totalWeight, this.vertices.size(), this.visited.size()));
            String separator = "";
            int count = 0;
            final int tailCut = this.vertices.size() - 1 - DISPLAY_CUT;
            for (V v : this.vertices) {
                // limit the length of the text representation for long paths.
                if (count < DISPLAY_CUT || count > tailCut) {
                    sb.append(separator).append(v.toString());
                    separator = ", ";
                } else if (count == DISPLAY_CUT) {
                    sb.append(separator).append("...");
                }
                count++;
            }
            sb.append(")");
            return sb.toString();
        }

        /**
         * recalculates the total weight of the path from a given weightMapper that
         * calculates the weight of
         * the path segment between two neighbouring vertices.
         *
         * @param weightMapper
         */
        public void reCalculateTotalWeight(BiFunction<V, V, Double> weightMapper) {
            this.totalWeight = 0.0;
            V previous = null;
            for (V v : this.vertices) {
                // the first vertex of the iterator has no predecessor and hence no weight
                // contribution
                if (previous != null)
                    this.totalWeight += weightMapper.apply(previous, v);
                previous = v;
            }
        }

        public Queue<V> getVertices() {
            return this.vertices;
        }

        public double getTotalWeight() {
            return this.totalWeight;
        }

        public Set<V> getVisited() {
            return this.visited;
        }
    }

    /**
     * Uses a depth-first search algorithm to find a path from the startVertex to
     * targetVertex in the subgraph
     * All vertices that are being visited by the search should also be registered
     * in path.visited
     *
     * @param startVertex
     * @param targetVertex
     * @return the path from startVertex to targetVertex
     *         or null if target cannot be matched with a vertex in the sub-graph
     *         from startVertex
     */
    public GPath depthFirstSearch(V startVertex, V targetVertex) {
        if (startVertex == null || targetVertex == null)
            return null;

        GPath path = new GPath();
        path.vertices = depthFirstSearch(startVertex, targetVertex, path.visited);
        // no path was found
        if (path.vertices == null)
            return null;

        return path;
    }

    /**
     * Recursive function handling the depth first search
     *
     * @param currentVertex the current index the traversal is at
     * @param targetVertex  the target which the dfs should find
     * @param visited       the vertices already visited by dfs
     * @return a queue representing the path to take to get from the current to the
     *         target. Is recursively build.
     */
    private Deque<V> depthFirstSearch(V currentVertex, V targetVertex, Set<V> visited) {
        // base case if vertex doesn't result in finding the target return
        // Ensures only vertices used to find the path are added to the path and no dead
        // ends.
        if (visited.contains(currentVertex))
            return null;
        visited.add(currentVertex);

        // if current is target Return path
        if (currentVertex.equals(targetVertex)) {
            Deque<V> path = new LinkedList<>();
            path.addLast(targetVertex);
            return path;
        }

        // traverse over neighbours
        for (V neighbour : getNeighbours(currentVertex)) {
            Deque<V> path = depthFirstSearch(neighbour, targetVertex, visited);

            if (path != null) {
                path.addFirst(currentVertex);
                return path;
            }
        }
        return null;
    }

    /**
     * Uses a breadth-first search algorithm to find a path from the startVertex to
     * targetVertex in the subgraph
     * All vertices that are being visited by the search should also be registered
     * in path.visited
     *
     * @param startVertex
     * @param targetVertex
     * @return the path from startVertex to targetVertex
     *         or null if target cannot be matched with a vertex in the sub-graph
     *         from startVertex
     */
    public GPath breadthFirstSearch(V startVertex, V targetVertex) {

        if (startVertex == null || targetVertex == null)
            return null;
        GPath path = new GPath();

        if (startVertex.equals(targetVertex)) {
            path.vertices.addLast(targetVertex);
            path.visited.add(startVertex);
            return path;
        }

        Queue<V> stillToVisited = new LinkedList<>();
        Map<V, V> visitedFrom = new HashMap<>();

        // setup the start
        visitedFrom.put(startVertex, null);
        path.visited.add(startVertex);
        V current = startVertex;

        while (current != null) {
            for (V neighbour : this.getNeighbours(current)) {
                path.visited.add(neighbour);

                if (neighbour.equals(targetVertex)) {
                    path.vertices.addLast(targetVertex);
                    while (current != null) {
                        path.vertices.addFirst(current);
                        current = visitedFrom.get(current);
                    }
                    return path;
                } else if (!visitedFrom.containsKey(neighbour)) {
                    visitedFrom.put(neighbour, current);
                    stillToVisited.offer(neighbour);
                }
            }
            current = stillToVisited.poll();
        }
        return null;
    }

    // helper class to build the spanning tree of visited vertices in dijkstra's
    // shortest path algorithm
    // your may change this class or delete it altogether follow a different
    // approach in your implementation
    private class MSTNode implements Comparable<MSTNode> {
        protected V vertex; // the graph vertex that is concerned with this MSTNode
        protected V parentVertex = null; // the parent's node vertex that has an edge towards this node's vertex
        protected boolean marked = false; // indicates DSP processing has been marked complete for this vertex
        protected double weightSumTo = Double.MAX_VALUE; // sum of weights of current shortest path towards this node's
                                                         // vertex

        private MSTNode(V vertex) {
            this.vertex = vertex;
        }

        // comparable interface helps to find a node with the shortest current path,
        // sofar
        @Override
        public int compareTo(MSTNode otherMSTNode) {
            return Double.compare(weightSumTo, otherMSTNode.weightSumTo);
        }
    }

    /**
     * Calculates the edge-weighted shortest path from the startVertex to
     * targetVertex in the subgraph
     * according to Dijkstra's algorithm of a minimum spanning tree
     *
     * @param startVertex  the start vertex of the path
     * @param targetVertex the target vertex of the path
     * @param weightMapper provides a function(v1,v2) by which the weight of an edge
     *                     from v1 to v2
     *                     can be retrieved or calculated
     * @return the shortest path from startVertex to targetVertex
     *         or null if target cannot be matched with a vertex in the sub-graph
     *         from startVertex
     */
    public GPath dijkstraShortestPath(V startVertex, V targetVertex,
            BiFunction<V, V, Double> weightMapper) {
        if (startVertex == null || targetVertex == null)
            return null;

        // initialise the result path of the search
        GPath path = new GPath();
        path.visited.add(startVertex);

        // easy target
        if (startVertex.equals(targetVertex)) {
            path.vertices.add(startVertex);
            return path;
        }

        Map<V, MSTNode> minimumSpanningTree = new HashMap<>();
        MSTNode nearestMSTNode = new MSTNode(startVertex);
        nearestMSTNode.weightSumTo = 0.0;
        minimumSpanningTree.put(startVertex, nearestMSTNode);

        // Priority queue to keep track of the nearest node.
        PriorityQueue<MSTNode> priorityQueue = new PriorityQueue<>();

        // Add the first node to the queue.
        priorityQueue.offer(nearestMSTNode);

        while (!priorityQueue.isEmpty()) {
            nearestMSTNode = priorityQueue.poll();
            nearestMSTNode.marked = true;
            path.visited.add(nearestMSTNode.vertex);

            if (nearestMSTNode.vertex.equals(targetVertex)) {
                // build the path from the MST
                while (nearestMSTNode != null) {
                    path.vertices.addFirst(nearestMSTNode.vertex);
                    nearestMSTNode = minimumSpanningTree.get(nearestMSTNode.parentVertex);
                }

                path.totalWeight = minimumSpanningTree.get(targetVertex).weightSumTo;

                return path;
            }

            for (V neighbour : getNeighbours(nearestMSTNode.vertex)) {
                if (minimumSpanningTree.containsKey(neighbour))
                    continue;

                MSTNode neighbourMSTNode = new MSTNode(neighbour);
                neighbourMSTNode.parentVertex = nearestMSTNode.vertex;
                neighbourMSTNode.weightSumTo = nearestMSTNode.weightSumTo + weightMapper.apply(nearestMSTNode.vertex,
                        neighbour);
                minimumSpanningTree.put(neighbour, neighbourMSTNode);
                priorityQueue.offer(neighbourMSTNode);
            }
        }

        return null;
    }
}
