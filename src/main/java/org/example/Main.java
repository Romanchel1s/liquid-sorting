package org.example;

import java.util.*;


public class Main {

    public record Move(int from, int to) {
    }


    public static List<Move> solve(String[][] initial) {
        int N = initial.length;
        if (N == 0) return Collections.emptyList();
        int V = initial[0].length;
        List<Deque<String>> tubes = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            Deque<String> d = new ArrayDeque<>();
            for (int j = 0; j < V; j++) {
                String c = initial[i][j];
                if (c != null && !c.equals(".")) d.addLast(c);
            }
            tubes.add(d);
        }

        String startKey = encodeState(tubes, V);
        if (isSolved(tubes, V)) return Collections.emptyList();
        int maxDepthLimit = 2000;
        for (int maxDepth = 1; maxDepth <= maxDepthLimit; maxDepth++) {
            Set<String> visited = new HashSet<>();
            List<Move> path = new ArrayList<>();
            visited.add(startKey);
            if (dfs(tubes, V, path, visited, maxDepth)) return path;
        }
        return null;
    }

    private static boolean dfs(List<Deque<String>> tubes, int V, List<Move> path, Set<String> visited, int depthLeft) {
        if (isSolved(tubes, V)) return true;
        if (depthLeft == 0) return false;
        int N = tubes.size();
        for (int a = 0; a < N; a++) {
            if (tubes.get(a).isEmpty()) continue;
            String topColor = tubes.get(a).peekLast();
            int runA = 0;
            for (Iterator<String> it = descendingIterator(tubes.get(a)); it.hasNext(); ) {
                if (it.next().equals(topColor)) runA++;
                else break;
            }
            for (int b = 0; b < N; b++) {
                if (a == b) continue;
                if (tubes.get(b).size() == V) continue; // full
                if (!tubes.get(b).isEmpty() && !tubes.get(b).peekLast().equals(topColor)) continue;


                int cap = V - tubes.get(b).size();
                int moveCount = Math.min(runA, cap);

                if (tubes.get(b).isEmpty() && moveCount == tubes.get(a).size())
                    continue;

                List<String> moved = new ArrayList<>();
                for (int k = 0; k < moveCount; k++) moved.add(tubes.get(a).removeLast());
                for (int k = moved.size() - 1; k >= 0; k--) tubes.get(b).addLast(moved.get(k));


                String key = encodeState(tubes, V);
                boolean ok = false;
                if (!visited.contains(key)) {
                    visited.add(key);
                    path.add(new Move(a, b));
                    ok = dfs(tubes, V, path, visited, depthLeft - 1);
                    if (ok) return true;
                    path.remove(path.size() - 1);
                }


                for (int k = 0; k < moveCount; k++) tubes.get(b).removeLast();
                for (int k = moved.size() - 1; k >= 0; k--) tubes.get(a).addLast(moved.get(k));
            }
        }
        return false;
    }

    private static void printSolution(List<Move> moves) {
        if (moves == null) {
            System.out.println("No solution found within limits.");
            return;
        }
        System.out.println("Solution in " + moves.size() + " moves:");
        int cnt = 0;
        for (Move m : moves) {
            System.out.printf("(%2d, %2d) ", m.from, m.to);
            cnt++;
            if (cnt % 8 == 0) System.out.println();
        }
        if (cnt % 8 != 0) System.out.println();
    }

    private static Iterator<String> descendingIterator(Deque<String> d) {
        return new Iterator<String>() {
            private final Iterator<String> it = d.descendingIterator();

            public boolean hasNext() {
                return it.hasNext();
            }

            public String next() {
                return it.next();
            }
        };
    }

    private static String encodeState(List<Deque<String>> tubes, int V) {
        StringBuilder sb = new StringBuilder();
        for (Deque<String> d : tubes) {
            if (d.isEmpty()) sb.append(".");
            else {
                for (String s : d) sb.append(escape(s));
            }
            sb.append('|');
        }
        return sb.toString();
    }

    private static String escape(String s) {
        return s.replace("|", "\\|") + ",";
    }

    private static boolean isSolved(List<Deque<String>> tubes, int V) {
        for (Deque<String> d : tubes) {
            if (d.isEmpty()) continue;
            String color = null;
            for (String s : d) {
                if (color == null) color = s;
                else if (!color.equals(s)) return false;
            }
            if (d.size() != V) {
                for (Deque<String> other : tubes) {
                    if (other == d) continue;
                    for (String s : other) if (s.equals(color)) return false;
                }
            }
        }
        return true;
    }


    private static void printTubes(List<Deque<String>> tubes, int V) {
        for (int i = 0; i < tubes.size(); i++) {
            Deque<String> d = tubes.get(i);
            System.out.print("Tube " + i + ": ");
            List<String> list = new ArrayList<>(d);
            for (int j = 0; j < list.size(); j++) {
                System.out.print(list.get(j) + " ");
            }
            for (int j = list.size(); j < V; j++) {
                System.out.print(". ");
            }
            System.out.println();
        }
        System.out.println("------");
    }




    public static void main(String[] args) {
        String[][] example = new String[][]{
                {"R", "G", "G", "R"},
                {"B", "R", "B", "G"},
                {"B", "B", "R", "G"},
                {".", ".", ".", "."}
        };
        List<Move> solution = solve(example);
        printSolution(solution);

        if (solution != null) {
            int N = example.length;
            int V = example[0].length;
            List<Deque<String>> tubes = new ArrayList<>(N);
            for (int i = 0; i < N; i++) {
                Deque<String> d = new ArrayDeque<>();
                for (int j = 0; j < V; j++) {
                    String c = example[i][j];
                    if (c != null && !c.equals(".")) d.addLast(c);
                }
                tubes.add(d);
            }

            System.out.println("Initial state:");
            printTubes(tubes, V);

            int step = 1;
            for (Move m : solution) {
                Deque<String> from = tubes.get(m.from());
                Deque<String> to = tubes.get(m.to());
                String color = from.peekLast();
                while (!from.isEmpty() && from.peekLast().equals(color) && to.size() < V) {
                    to.addLast(from.removeLast());
                }
                System.out.println("Step " + (step++) + ": move (" + m.from() + " → " + m.to() + ")");
                printTubes(tubes, V);
            }
        }
    }
}