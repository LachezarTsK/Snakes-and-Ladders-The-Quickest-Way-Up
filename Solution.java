import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Scanner;

public class Solution {

  private static final int BOARD_SIZE = 100;
  private static final int START = 1;
  private static final int GOAL = 100;

  //The adjacency list that stores the graph.
  private static List<Integer>[] nodesAndEdges;
  
  //The map is applied to trace back the path with minimum number of rolls.
  private static Map<Integer, Integer> path;

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    int numberOfTestCases = scanner.nextInt();

    for (int i = 0; i < numberOfTestCases; i++) {

      initialize_nodesAndEdges();

      int numberOfLadders = scanner.nextInt();
      for (int i_ladder = 0; i_ladder < numberOfLadders; i_ladder++) {
        int start_ladder = scanner.nextInt();
        int end_ladder = scanner.nextInt();
        replaceNeighbours_with_snakeOrLadder(start_ladder, end_ladder);
      }

      int numberOfSnakes = scanner.nextInt();
      for (int i_snake = 0; i_snake < numberOfSnakes; i_snake++) {
        int start_snake = scanner.nextInt();
        int end_snake = scanner.nextInt();
        replaceNeighbours_with_snakeOrLadder(start_snake, end_snake);
      }

      int result = find_minimumNumberOfRolls_toWin();
      System.out.println(result);
    }
    scanner.close();
  }

  /**
   * Searches for the path leading to a win, i.e. beginning at START and landing exactly on 
   * the square of the GOAL, with minimum number of rolls of the dice. 
   * At each roll, it is possible to have: 1,2,3,4,5,6.
   *
   * @return A positive integer, representing the minimum number of rolls, if the GOAL can be reached.
   *         Otherwise, it returns -1;
   */
  private static int find_minimumNumberOfRolls_toWin() {
    boolean pathExists = breadthFirstSearch_findPath_withMinimumRolls();
    return pathExists ? traceBackPath_to_findMinimumRolls() : -1;
  }

  /**
   * Traces back the path from GOAL to START and sums up the minumum number of rolls.
   *
   * @return An integer, representing the minimum number of rolls.
   */
  private static int traceBackPath_to_findMinimumRolls() {
    int current = GOAL;
    int rolls = 0;

    while (current != START) {
      rolls++;
      current = path.get(current);
    }
    return rolls;
  }

  /**
   * Applying Breadth First Search to find the path with minimum number of rolls from START to GOAL.
   *
   * @return 'true' if there is a path from START to GOAL. Otherwise, it returns 'false'.
   */
  private static boolean breadthFirstSearch_findPath_withMinimumRolls() {

    path = new HashMap<Integer, Integer>();
    boolean[] visited = new boolean[BOARD_SIZE + 1];
    LinkedList<Integer> queue = new LinkedList<Integer>();
    queue.add(START);

    while (!queue.isEmpty()) {

      int current = queue.removeFirst();
      if (current == GOAL) {
        return true;
      }
      
      // Of course (!visited[current]) is also possible but the tiny '!' could be easily overlooked:))
      if (visited[current] == false) {
        visited[current] = true;

        /**
         * It is essential that the neigbours are visited backwards, so that the path 
         * with minimum number of rolls from GOAL to START can be easily traced.
         *
         * Example: 
         * 1=>7, 1=>6, 1=>5, 1=>4, 1=>3, 1=>2 
         * instead of 
         * 1=>2, 1=>3, 1=>4, 1=>5, 1=>6, 1=>7
         */
        for (int i = nodesAndEdges[current].size() - 1; i >= 0; i--) {

          if (visited[nodesAndEdges[current].get(i)] == false) {
            path.put(nodesAndEdges[current].get(i), current);
            queue.add(nodesAndEdges[current].get(i));
          }
        }
      }
    }
    return false;
  }

  /**
   * Initializes the adjacency list that stores the board. 
   * It is initialized as a directed graph, where each node has and edge 
   * that leads only to the next 1 to 6 nodes (squares). 
   * Example: 1 => 7, 6, 5, 4, 3, 2
   *
   * Bakcward movement is possible only if the node is a start of a snake.
   *
   * Values at index '0' are not applied in the solution, so that each index corresponds
   * to a node (square) value.
   */
  @SuppressWarnings("unchecked")
  private static void initialize_nodesAndEdges() {

    nodesAndEdges = new List[BOARD_SIZE + 1];
    for (int i = 1; i < BOARD_SIZE + 1; i++) {

      nodesAndEdges[i] = new ArrayList<Integer>();
      for (int neighbour = i + 1; neighbour <= i + 6 && neighbour < BOARD_SIZE + 1; neighbour++) {
        nodesAndEdges[i].add(neighbour);
      }
    }
  }

  /**
   * Replaces the start node of a snake/ladder with the corresponding end node of a snake/ladder.
   * The adjacency list of the start node of the snake/ladder is set to null.
   *
   * Example: start of ladder = 6, end of ladder = 27.
   *
   * Before replacement: 
   * 1 =>  7, 6, 5, 4, 3, 2
   * 2 =>  8, 7, 6, 5, 4, 3 
   * 3 =>  9, 8, 7, 6, 5, 4 
   * 4 => 10, 9, 8, 7, 6, 5 
   * 5 => 11, 10, 9, 8, 7, 6 
   * 6 => 12, 11, 10, 9, 8, 7
   *
   * After replacement: 
   * 1 =>  7, 27, 5, 4, 3, 2 
   * 2 =>  8, 7, 27, 5, 4, 3 
   * 3 =>  9, 8, 7, 27, 5, 4 
   * 4 => 10, 9, 8, 7, 27, 5 
   * 5 => 11, 10, 9, 8, 7, 27 
   * 6 => null
   */
  private static void replaceNeighbours_with_snakeOrLadder(int start_snakeOrLadder, int end_snakeOrLadder) {

    nodesAndEdges[start_snakeOrLadder].clear();
    int neighbour = start_snakeOrLadder - 1;
    int index = 0;

    while (neighbour >= start_snakeOrLadder - 6 && neighbour > 0) {

      if (!nodesAndEdges[neighbour].isEmpty()) {
        nodesAndEdges[neighbour].set(index, end_snakeOrLadder);
      }
      neighbour--;
      index++;
    }
  }
}
