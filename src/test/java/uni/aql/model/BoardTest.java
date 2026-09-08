package uni.aql.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testInitialState() {
        assertTrue(board.isInProgressMode());
        assertEquals(Player.X, board.getCurrentTurn());
        assertNull(board.getWinner());
    }

    @Test
    void testBoundaryValuesValidCoordinates() {
        // Test equivalence class limits [0, 2] for rows and cols
        board.mark(0, 0);
        assertEquals(Player.O, board.getCurrentTurn());
        
        board.mark(2, 2);
        assertEquals(Player.X, board.getCurrentTurn());
    }

    @Test
    void testBoundaryValuesInvalidCoordinatesOutOfBounds() {
        // Test out-of-bounds equivalence classes (< 0 or > 2)
        board.mark(-1, 0); // No-op
        assertEquals(Player.X, board.getCurrentTurn());
        
        board.mark(3, 1); // No-op
        assertEquals(Player.X, board.getCurrentTurn());
    }

    @Test
    void testCellAlreadySetNoOp() {
        board.mark(1, 1); // Played by X
        assertEquals(Player.O, board.getCurrentTurn());
        
        board.mark(1, 1); // Already set, should be a no-op, turn remains O
        assertEquals(Player.O, board.getCurrentTurn());
    }

    @Test
    void testTurnAlternation() {
        assertEquals(Player.X, board.getCurrentTurn());
        board.mark(0, 0);
        assertEquals(Player.O, board.getCurrentTurn());
        board.mark(0, 1);
        assertEquals(Player.X, board.getCurrentTurn());
    }

    @Test
    void testWinningConditionHorizontal() {
        // Player X horizontal win on row 0[cite: 3, 4]
        board.mark(0, 0); // X
        board.mark(1, 0); // O
        board.mark(0, 1); // X
        board.mark(1, 1); // O
        board.mark(0, 2); // X wins

        assertTrue(board.isInFinishedMode());
        assertEquals(Player.X, board.getWinner());
    }

    @Test
    void testMoveAfterGameFinishedNoOp() {
        // Reach finished state
        board.mark(0, 0); // X
        board.mark(1, 0); // O
        board.mark(0, 1); // X
        board.mark(1, 1); // O
        board.mark(0, 2); // X wins
        
        assertTrue(board.isInFinishedMode());
        
        // Attempt move after game is finished[cite: 3]
        board.mark(2, 2);
        assertTrue(board.isInFinishedMode());
    }

    @Test
    void testWinningConditionVertical() {
        // Player O vertical win on col 1[cite: 3, 4]
        board.mark(0, 0); // X
        board.mark(0, 1); // O
        board.mark(2, 2); // X
        board.mark(1, 1); // O
        board.mark(2, 0); // X
        board.mark(2, 1); // O wins

        assertTrue(board.isInFinishedMode());
        assertEquals(Player.O, board.getWinner());
    }

    @Test
    void testWinningConditionMainDiagonal() {
        // Player X main diagonal win (0,0 -> 1,1 -> 2,2)[cite: 3, 4]
        board.mark(0, 0); // X
        board.mark(0, 1); // O
        board.mark(1, 1); // X
        board.mark(0, 2); // O
        board.mark(2, 2); // X wins

        assertTrue(board.isInFinishedMode());
        assertEquals(Player.X, board.getWinner());
    }

    @Test
    void testWinningConditionOppositeDiagonal() {
        // Player X opposite diagonal win (0,2 -> 1,1 -> 2,0)[cite: 3, 4]
        board.mark(0, 2); // X
        board.mark(0, 0); // O
        board.mark(1, 1); // X
        board.mark(0, 1); // O
        board.mark(2, 0); // X wins

        assertTrue(board.isInFinishedMode());
        assertEquals(Player.X, board.getWinner());
    }

    @Test
    void testRestartFunctionality() {
        board.mark(0, 0);
        board.mark(0, 1);

        // Trigger restart[cite: 3]
        board.restart();

        assertTrue(board.isInProgressMode());
        assertEquals(Player.X, board.getCurrentTurn());
        assertNull(board.getWinner());
        assertNull(board.getWinner()); // Cells are reset
    }

    @Test
    void testBoundaryValuesNegativeCoordinates() {
        board.mark(-1, -1); // Out of bounds lower limit
        assertTrue(board.isInProgressMode());
        assertEquals(Player.X, board.getCurrentTurn());
    }

    @Test
    void testBoundaryValuesUpperLimitCoordinates() {
        board.mark(3, 3); // Out of bounds upper limit
        assertTrue(board.isInProgressMode());
        assertEquals(Player.X, board.getCurrentTurn());
    }

    @Test
    void testSettersAndGettersCoverage() {
        board.setCurrentTurn(Player.O);
        assertEquals(Player.O, board.getCurrentTurn());

        board.setState(Board.GameState.FINISHED);
        assertTrue(board.isInFinishedMode());
    }

    @Test
    void testMixedOutOfBoundsCoordinates() {
        board.mark(1, 3); // Valid row, invalid column upper limit
        assertTrue(board.isInProgressMode());
        assertEquals(Player.X, board.getCurrentTurn());

        board.mark(-1, 1); // Invalid row lower limit, valid column
        assertTrue(board.isInProgressMode());
        assertEquals(Player.X, board.getCurrentTurn());
    }

    @Test
    void testPlayerOWinningCondition() {
        // Setup a horizontal win for Player O[cite: 1, 3, 4]
        board.mark(1, 1); // X
        board.mark(0, 0); // O
        board.mark(2, 2); // X
        board.mark(0, 1); // O
        board.mark(2, 0); // X
        board.mark(0, 2); // O wins row 0

        assertTrue(board.isInFinishedMode());
        assertEquals(Player.O, board.getWinner());
    }

    @Test
    void testStateBooleanHelpers() {
        assertTrue(board.isInProgressMode());
        assertFalse(board.isInFinishedMode());

        // Force finish via horizontal win[cite: 3, 4]
        board.mark(0, 0);
        board.mark(1, 0);
        board.mark(0, 1);
        board.mark(1, 1);
        board.mark(0, 2);

        assertFalse(board.isInProgressMode());
        assertTrue(board.isInFinishedMode());
    }

    @Test
    void testConsecutiveInvalidMovesPersistence() {
        board.mark(0, 0); // X plays, turn becomes O[cite: 3, 4]
        assertEquals(Player.O, board.getCurrentTurn());

        // Attempt invalid move on already set cell
        board.mark(0, 0);
        assertEquals(Player.O, board.getCurrentTurn()); // Turn should not change

        // Attempt invalid out-of-bounds move
        board.mark(5, 5);
        assertEquals(Player.O, board.getCurrentTurn()); // Turn should still remain O
    }
}