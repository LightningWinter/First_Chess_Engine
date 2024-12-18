package Chess;
import Chess.Coordinates;
public record Move(Coordinates startingSquare, Coordinates endingSquare, int PieceID, boolean isCaptureMove) {

    public Move(Coordinates startingSquare, Coordinates endingSquare, int PieceID, boolean isCaptureMove){
        this.startingSquare = startingSquare;
        this.endingSquare = endingSquare;
        this.PieceID = PieceID;
        this.isCaptureMove = isCaptureMove;
    }

    public String toString(){
        String out = "";

        return out;
    }
}
