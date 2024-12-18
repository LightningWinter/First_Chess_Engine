package Chess;
import Chess.Coordinates;
public record Move(Coordinates startingSquare, Coordinates endingSquare, int PieceID, boolean isCaptureMove, Coordinates captureSquare) {

    public Move(Coordinates startingSquare, Coordinates endingSquare, int PieceID, boolean isCaptureMove, Coordinates captureSquare){
        this.startingSquare = startingSquare;
        this.endingSquare = endingSquare;
        this.PieceID = PieceID;
        this.isCaptureMove = isCaptureMove;
        this.captureSquare = captureSquare;
    }

    public String toString(){
        String out = "";
        return out;
    }
}
