package Chess.Model;
import Chess.Coordinates;

import java.util.HashMap;

import static Chess.Model.HelperClass.getBitboard;

public class Piece {
    public static HashMap<String,Long> psudoLegalMovesLookupTable;
    private Coordinates location;
    private int ID;
    private long zobristHash;
    private long positionBitboard;
    private int bitboardIndex;


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

    public Coordinates getLocation(){return this.location;}
    public int getID(){return this.ID;}
    public int getBitboardIndex(){return this.bitboardIndex;}
    public long getPositionBitboard(){return positionBitboard;}
    public long getZobristHash(){return zobristHash;}



}
