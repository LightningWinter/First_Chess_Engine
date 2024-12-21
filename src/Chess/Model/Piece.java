package Chess.Model;
import Chess.Coordinates;

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


    public Piece (Coordinates location, int ID){
        this.location = location;
        this.ID = ID;
        this.bitboardIndex = (this.location.row() * 8) + this.location.col();
        this.positionBitboard = getBitboard(bitboardIndex);

    }

    public Piece (int bitboardIndex, int ID){
        this.location = new Coordinates(bitboardIndex / 8, bitboardIndex % 8);
        this.ID = ID;
        this.bitboardIndex = bitboardIndex;
        this.positionBitboard = getBitboard(bitboardIndex);

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



}
