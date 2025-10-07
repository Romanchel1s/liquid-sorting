import org.example.Main;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @Test
    public void testSimplePuzzle() {
        String[][] initial = {
                {"R", "G", "."},
                {"G", "R", "."},
                {".", ".", "."}
        };

        List<Main.Move> moves = Main.solve(initial);
        assertNotNull(moves, "Решение должно существовать");
        assertFalse(moves.isEmpty(), "Ходов должно быть хотя бы несколько");
    }

    @Test
    public void testAlreadySolved() {
        String[][] initial = {
                {"R", "R", "."},
                {"G", "G", "."},
                {".", ".", "."}
        };

        List<Main.Move> moves = Main.solve(initial);
        // Уже решено → шагов быть не должно
        assertNotNull(moves);
        assertTrue(moves.isEmpty(), "Решение уже в целевом состоянии, ходов быть не должно");
    }

    @Test
    public void testUnsolvable() {
        // Тут нет пустой пробирки, решить невозможно
        String[][] initial = {
                {"R", "G"},
                {"G", "R"}
        };

        List<Main.Move> moves = Main.solve(initial);
        assertNull(moves, "Решение должно отсутствовать");
    }
}
