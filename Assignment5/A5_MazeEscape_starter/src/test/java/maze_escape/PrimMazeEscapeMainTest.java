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
    void mainOutputShouldContainRightValues() {
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
    void edgeCasesShouldBeAccountedFor() {
        Maze maze = new Maze(1, 1);
        maze.generateRandomizedPrim();
        maze.configureInnerEntry();
        maze.removeRandomWalls(0);

        assertEquals(1, maze.getWidth());
        assertEquals(1, maze.getHeight());
        assertEquals(0, maze.getStartNode());
    }

    @Test
    void mainOutputShouldContainExpectedResults() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalOut = System.out;

        System.setOut(printStream);
        PrimMazeEscapeMain.main(new String[]{});
        System.out.flush();
        System.setOut(originalOut);

        // Replace \r\n with \n to make sure the test passes on Windows.
        String output = outputStream.toString().replace("\r\n", "\n");

        String expectedOutput = "Welcome to the HvA Maze Escape\n" +
                "\n" +
                "Created 100x100 Randomized-Prim-Maze(20231113) with 250 walls removed\n" +
                "Maze-Graph contains 5428 connected vertices in 10000 cells\n" +
                "\n" +
                "Results from 'Depth First Search' in 100x100 maze from vertex '6666' to '96':\n" +
                "Depth First Search: Weight=463.00 Length=162 visited=5167 (6666, 6563, 6663, 6665, 6765, 6766, 6767, 6768, 6769, 7069, ..., 788, 790, 692, 693, 493, 393, 293, 193, 194, 96)\n" +
                "Depth First Search return: Weight=1976.00 Length=709 visited=3197 (96, 97, 197, 194, 193, 293, 393, 395, 495, 493, ..., 6974, 6973, 6972, 7070, 7069, 6769, 6768, 6767, 6766, 6666)\n" +
                "\n" +
                "Results from 'Breadth First Search' in 100x100 maze from vertex '6666' to '96':\n" +
                "Breadth First Search: Weight=226.00 Length=79 visited=5126 (6666, 6563, 6462, 6460, 6459, 6359, 6259, 6157, 6057, 5756, ..., 788, 790, 692, 693, 493, 393, 293, 193, 194, 96)\n" +
                "Breadth First Search return: Weight=226.00 Length=79 visited=2940 (96, 194, 193, 293, 393, 493, 693, 692, 790, 788, ..., 5756, 6057, 6157, 6259, 6359, 6459, 6460, 6462, 6563, 6666)\n" +
                "\n" +
                "Results from 'Dijkstra Shortest Path' in 100x100 maze from vertex '6666' to '96':\n" +
                "Dijkstra Shortest Path: Weight=226.00 Length=79 visited=5250 (6666, 6563, 6462, 6460, 6459, 6359, 6259, 6157, 6057, 5756, ..., 788, 790, 692, 693, 493, 393, 293, 193, 194, 96)\n" +
                "Dijkstra Shortest Path return: Weight=226.00 Length=79 visited=3335 (96, 194, 193, 293, 393, 493, 693, 692, 790, 788, ..., 5756, 6057, 6157, 6259, 6359, 6459, 6460, 6462, 6563, 6666)" +
                "\n";

        // Replace \r\n with \n to make sure the test passes on Windows.
        expectedOutput = expectedOutput.replace("\r\n", "\n");

        assertEquals(expectedOutput, output);
    }
}
