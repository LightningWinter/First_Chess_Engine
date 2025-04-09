package Chess;
import Chess.Coordinates;

import java.util.HashMap;

public record Move(Coordinates startingSquare, Coordinates endingSquare, int PieceID, boolean isCaptureMove, Coordinates captureSquare, boolean isCastle) {

    private static HashMap<Coordinates, Coordinates[]> castlingMovesLookup;

    public Move(Coordinates startingSquare, Coordinates endingSquare, int PieceID, boolean isCaptureMove, Coordinates captureSquare, boolean isCastle){
        this.startingSquare = startingSquare;
        this.endingSquare = endingSquare;
        this.PieceID = PieceID;
        this.isCaptureMove = isCaptureMove;
        this.captureSquare = captureSquare;
        this.isCastle = isCastle;

    }

    private static void createCastlingMovesLookup(){
        //this should only be called when setting up a new game.
        Coordinates[] temp = new Coordinates[2];
        temp[0] = new Coordinates(0,7);
        temp[1] = new Coordinates(0,5);
        castlingMovesLookup.put(new Coordinates(0,6), temp);

        temp[0] = new Coordinates(0,0);
        temp[1] = new Coordinates(0,3);
        castlingMovesLookup.put(new Coordinates(0,2),temp);

        temp[0] = new Coordinates(7,7);
        temp[1] = new Coordinates(7,5);
        castlingMovesLookup.put(new Coordinates(7,6),temp);

        temp[0] = new Coordinates(7,0);
        temp[1] = new Coordinates(7,3);
        castlingMovesLookup.put(new Coordinates(7,2),temp);
    }
    public Coordinates[] getRookSquaresForCastling(Coordinates kingEndLocation){
        return castlingMovesLookup.get(kingEndLocation);
    }

    public String toString(){
        String out = "";
        return out;
    }
}