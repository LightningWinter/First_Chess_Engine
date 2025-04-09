package Chess.Model;
import Chess.Coordinates;
import Chess.LookupsFillers.PsudolegalMovesGenerator;

import java.util.HashMap;

import static Chess.Model.HelperClass.getBitboard;

public class Piece {
    public static HashMap<String,Long> psudoLegalMovesLookupTable;
    private static HashMap<String,Long> ZobristHashesLookupTable;
    private Coordinates location;
    private int ID;
    private long zobristHash;
    private long positionBitboard;
    private int bitboardIndex;
    private String pieceString;
    private static PsudolegalMovesGenerator movesGenerator;
    private long squaresAttackingBitboard;

    public Piece (Coordinates location, int ID){
        this.location = location;
        this.ID = ID;
        this.bitboardIndex = (this.location.row() * 8) + this.location.col();
        this.positionBitboard = getBitboard(bitboardIndex);
        switch (ID){
            case 1: pieceString = "P";
            case 2: pieceString = "N";
            case 3: pieceString = "B";
            case 4: pieceString = "R";
            case 5: pieceString = "Q";
            case 6: pieceString = "K";
            case -1: pieceString = "p";
            case -2: pieceString = "n";
            case -3: pieceString = "b";
            case -4: pieceString = "r";
            case -5: pieceString = "q";
            case -6: pieceString = "k";
        }
    }

    public Piece (int bitboardIndex, int ID){
        this.location = new Coordinates(bitboardIndex / 8, bitboardIndex % 8);
        this.ID = ID;
        this.bitboardIndex = bitboardIndex;
        this.positionBitboard = getBitboard(bitboardIndex);
        switch (ID){
            case 1: pieceString = "P";
            case 2: pieceString = "N";
            case 3: pieceString = "B";
            case 4: pieceString = "R";
            case 5: pieceString = "Q";
            case 6: pieceString = "K";
            case -1: pieceString = "p";
            case -2: pieceString = "n";
            case -3: pieceString = "b";
            case -4: pieceString = "r";
            case -5: pieceString = "q";
            case -6: pieceString = "k";
        }
    }

    //Copy Constructor
    public Piece (Piece parent){
        this.location = new Coordinates(parent.getLocation().row(), parent.getLocation().col());
        this.ID = parent.ID;
        this.bitboardIndex = parent.getBitboardIndex();
        this.positionBitboard = getBitboard(this.bitboardIndex);
        this.pieceString = parent.getPieceString();
        this.squaresAttackingBitboard = parent.getSquaresAttackingBitboard();
    }

    public static void setPsudoLegalMovesLookupTable(HashMap<String,Long> map){
        psudoLegalMovesLookupTable = map;
    }
    public static void setZobristHashesLookupTable(HashMap<String,Long> map){
        ZobristHashesLookupTable = map;
    }

    public Coordinates getLocation(){return this.location;}
    public int getID(){return this.ID;}
    public int getBitboardIndex(){return this.bitboardIndex;}
    public long getPositionBitboard(){return positionBitboard;}
    public long getZobristHash(){return zobristHash;}
    public String getPieceString(){return pieceString;}
    public long getSquaresAttackingBitboard(){return this.squaresAttackingBitboard;}
    public long getPsudolegalMoves(long blockers){
        return movesGenerator.getPsudolegalMoves(this,blockers);
    }

    public void updateSquaresAttackingBitboard(long blockers){
        this.squaresAttackingBitboard = getPsudolegalMoves(blockers);
    }

    public void updateLocation(Coordinates newLocation){
        this.location = newLocation;
        this.bitboardIndex = (this.location.row() * 8) + this.location.col();
        this.positionBitboard = getBitboard(bitboardIndex);
    }


    public String toString(){
        return this.pieceString + this.location.toString();
    }

}
