package Chess.Model;
import Chess.Coordinates;
import java.util.ArrayList;

public class Player {
    private boolean isPlayer1;
    private ArrayList<Piece> pieces;
    private long piecesBitboard;
    private Coordinates kingLocation;
    private long squaresAttacking;
}
