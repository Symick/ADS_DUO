package maze_escape;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PrimMazeEscapeMainTest {

    @Test
    void pathSearchesFindValidPath() {
        Maze maze = new Maze(100, 100);
        maze.generateRandomizedPrim();
        maze.getAllVertices(maze.getStartNode());
        Maze.GPath path = maze.depthFirstSearch(maze.getStartNode(), maze.getExitNode());
        assertNotNull(path);
    }

    @Test
    void pathSearchesReturnDifferentPathsForDifferentSearchMethods() {
        Maze maze = new Maze(100, 100);
        maze.generateRandomizedPrim();
        Maze.GPath dfsPath = maze.depthFirstSearch(maze.getStartNode(), maze.getExitNode());
        Maze.GPath bfsPath = maze.breadthFirstSearch(maze.getStartNode(), maze.getExitNode());
        Maze.GPath dijkstraPath = maze.dijkstraShortestPath(maze.getStartNode(), maze.getExitNode(),
                maze::manhattanTime);
        assertNotEquals(dfsPath, bfsPath);
        assertNotEquals(dfsPath, dijkstraPath);
        assertNotEquals(bfsPath, dijkstraPath);
    }

    @Test
    void testMainMethods() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalOut = System.out;
        System.setOut(printStream);

        PrimMazeEscapeMain.main(new String[]{});

        System.out.flush();
        System.setOut(originalOut);
        String output = outputStream.toString();
        assertTrue(output.contains("Welcome to the HvA Maze Escape"));
        assertTrue(output.contains("Maze-Graph contains"));
        assertTrue(output.contains("Depth First Search"));
        assertTrue(output.contains("Breadth First Search"));
        assertTrue(output.contains("Dijkstra Shortest Path"));
    }

    @Test
    void testEdgeCases() {
        Maze maze = new Maze(1, 1);
        maze.generateRandomizedPrim();
        maze.configureInnerEntry();
        maze.removeRandomWalls(0);

        assertEquals(1, maze.getWidth());
        assertEquals(1, maze.getHeight());
        assertEquals(0, maze.getStartNode());
    }
}
