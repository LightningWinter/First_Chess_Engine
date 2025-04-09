package Chess.Model;
import Chess.Coordinates;
import Chess.Move;

import java.util.ArrayList;

public class Game {
    private ArrayList<Position> gameLog;
    private Position currentPosition;
    private boolean whiteIsPlayer;
    private boolean blackIsPlayer;
    private boolean whiteToMove;

    public Game(boolean whiteIsPlayer, boolean blackIsPlayer){
        this.gameLog = new ArrayList<>();
        this.currentPosition = new Position(true);
        this.whiteIsPlayer = whiteIsPlayer;
        this.blackIsPlayer = blackIsPlayer;
        this.whiteToMove = true;


    }


}
