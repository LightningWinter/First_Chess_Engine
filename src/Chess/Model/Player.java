package Chess.Model;
import Chess.Coordinates;
import Chess.LookupsFillers.PsudolegalMovesGenerator;

import java.util.ArrayList;
import static Chess.Model.HelperClass.getBitboard;

public class Player {
    private final boolean isPlayer1;
    private ArrayList<Piece> pieces;
    private long piecesBitboard;
    private Coordinates kingLocation;
    private long squaresAttacking;
    private boolean castlingRightsShort;
    private boolean castlingRightsLong;
    private long diagonalsFromKing;
    private long straightsFromKing;
    private long knightMovesFromKing;


    public Player(boolean isPlayer1){
        this.isPlayer1 = isPlayer1;
        this.pieces = new ArrayList<>();
        this.piecesBitboard = 0;
        this.kingLocation = null;
        this.squaresAttacking = 0;
        this.castlingRightsShort = true;
        this.castlingRightsLong = true;
    }

    //Copy constructor
    public Player(Player parent, long blockers){
        this.isPlayer1 = parent.isPlayer1;
        this.pieces = new ArrayList<>();
        for (Piece piece : parent.getPieces()){
            this.addPiece(new Piece(piece)); //this creates a new piece object based off the old piece.
        }
        this.squaresAttacking = parent.squaresAttacking;
        this.castlingRightsShort = parent.castlingRightsShort;
        this.castlingRightsLong = parent.castlingRightsLong;
        this.kingLocation = parent.kingLocation;
        this.diagonalsFromKing = parent.diagonalsFromKing;
        this.straightsFromKing = parent.straightsFromKing;
    }

    //getters
    public boolean isPlayer1(){return this.isPlayer1;}
    public ArrayList<Piece> getPieces(){return this.pieces;}
    public long getPiecesBitboard(){return this.piecesBitboard;}
    public Coordinates getKingLocation(){return this.kingLocation;}
    public long getSquaresAttacking(){return this.squaresAttacking;}
    public long getDiagonalsFromKing(){return this.diagonalsFromKing;}
    public long getStraightsFromKing(){return this.straightsFromKing;}
    public long getKnightMovesFromKing(){return this.knightMovesFromKing;}
    public void addPiece(Piece piece){
        this.pieces.add(piece);
        if (Math.abs(piece.getID()) == 6){
            this.kingLocation = piece.getLocation();
        }
        this.piecesBitboard |= piece.getPositionBitboard();

    }
    public void removePiece(Piece piece){
        this.pieces.remove(piece);
        this.piecesBitboard ^= piece.getPositionBitboard();
    }

    public void updateKingLines(long blockers){
        this.diagonalsFromKing = PsudolegalMovesGenerator.generatePsudolegalMovesBishop(this.kingLocation,blockers);
        this.straightsFromKing = PsudolegalMovesGenerator.generatePsudolegalMovesRook(this.kingLocation,blockers);
        this.knightMovesFromKing = PsudolegalMovesGenerator.generatePsudolegalMovesKnight(this.kingLocation,blockers);
    }

    //piece is in this.pieces
    public void movePiece(Piece piece, Coordinates startingLocation, Coordinates endingLocation){
        piece.updateLocation(endingLocation);
        this.piecesBitboard ^= getBitboard(startingLocation);
        this.piecesBitboard |= getBitboard(endingLocation);

        //if the piece is the king, update the king location
        if (Math.abs(piece.getID()) == 6){
            this.kingLocation = endingLocation;
        }
    }

    public void updateSquaresAttackingBitboard(long blockers){
        this.squaresAttacking = 0;
        for (Piece piece : pieces){
            piece.updateSquaresAttackingBitboard(blockers);
            this.squaresAttacking |= piece.getSquaresAttackingBitboard();
        }
    }
    
}
