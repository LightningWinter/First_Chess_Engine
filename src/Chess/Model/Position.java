package Chess.Model;
import static Chess.Model.HelperClass.getBitboard;
import Chess.Coordinates;
import Chess.LookupsFillers.PsudolegalMovesGenerator;
import Chess.Move;
import Chess.LookupsFillers.BaseMovesFiller;
import java.util.ArrayList;


public class Position {
    //TODO: add transposition table
    private long zobristHash;
    private boolean player1ToMove;
    private Player playerMoving;
    private Player playerNotMoving;
    private Piece[][] board;
    private Player whitePlayer;
    private Player blackPlayer;
    private long checkLines;
    //I know it says "squares" in the name but there will only ever be one en passant square at any given time
    //I just don't want to go through the trouble of changing the name
    private long enPassantSquares;
    private static PsudolegalMovesGenerator movesGenerator;
    private long blockerBitboard;
    private ArrayList<Long> pinLines;


    public Position(boolean player1ToMove){
        this.zobristHash = 0;
        this.player1ToMove = player1ToMove;
        this.whitePlayer = new Player(true);
        this.blackPlayer = new Player(false);
        if (player1ToMove){
            this.playerMoving = this.whitePlayer;
            this.playerNotMoving = this.blackPlayer;
        }
        else {
            this.playerMoving = this.blackPlayer;
            this.playerNotMoving = this.whitePlayer;
        }
        this.checkLines = 0xFFFFFFFFFFFFFFFFL;
        this.pinLines = new ArrayList<>();
        this.enPassantSquares = 0;
    }

    //Copy Constructor
    //IMPORTANT: This constructor sets this.player1ToMove to the opposite of the parent.
    public Position(Position parent) {
        this.player1ToMove = !parent.player1ToMove;
        this.zobristHash = parent.zobristHash;
        //The Player copier creates new piece objects
        this.whitePlayer = new Player(parent.whitePlayer, parent.blockerBitboard);
        this.blackPlayer = new Player(parent.blackPlayer, parent.blockerBitboard);
        if (this.player1ToMove) {
            this.playerMoving = this.whitePlayer;
            this.playerNotMoving = this.blackPlayer;
        } else {
            this.playerMoving = this.blackPlayer;
            this.playerNotMoving = this.whitePlayer;
        }
        this.checkLines = parent.checkLines;
        this.pinLines = parent.pinLines;
        this.enPassantSquares = parent.enPassantSquares;
        this.blockerBitboard = parent.blockerBitboard;

        //creating the new board, filling it with the new piece objects we created
        for (Piece piece : this.whitePlayer.getPieces()) {
            this.board[piece.getLocation().row()][piece.getLocation().col()] = piece;
        }
        for (Piece piece : this.blackPlayer.getPieces()) {
            this.board[piece.getLocation().row()][piece.getLocation().col()] = piece;
        }
    }

    public void updateBlockerBitboard(){
        this.blockerBitboard = this.whitePlayer.getPiecesBitboard() | this.blackPlayer.getPiecesBitboard();
    }

    public void updateCheckLines(){

        //If there are no checks, simply set checklines to 0xFFFFFFFFFFFFFFFF and return
        if ((playerMoving.getSquaresAttacking() & HelperClass.getBitboard(playerNotMoving.getKingLocation())) != 0){
            this.checkLines = 0xFFFFFFFFFFFFFFFFL;
            return;
        }

        long diagonalsFromKing = playerNotMoving.getDiagonalsFromKing();
        long straightsFromKing = playerNotMoving.getStraightsFromKing();
        long knightMovesFromKing = playerMoving.getKnightMovesFromKing();
        long playerMovingPiecesBitboard = playerMoving.getPiecesBitboard();

        //calculate diagonal lines first
        //note: it's impossible for there to be check lines on more than one diagonal at once. This is NOT the case for straights!

        long diagonalCheckLines =0xFFFFFFFFFFFFFFFFL;
        long piecesInDiagonal = playerMovingPiecesBitboard & diagonalsFromKing;
        BitboardIterator piecesInDiagonalIterator = new BitboardIterator(piecesInDiagonal);
        while (piecesInDiagonalIterator.hasNext()){
            int currentPieceIndex = piecesInDiagonalIterator.next();
            Coordinates currentPieceCoords = HelperClass.coordsFromBitBoardIndex(currentPieceIndex);
            Piece currentPiece = this.board[currentPieceCoords.row()][currentPieceCoords.col()];
            //if the below statement is true, there is a check.
            if (Math.abs(currentPiece.getID()) == 3 || Math.abs(currentPiece.getID()) == 5){
                long diagonalsFromPiece = PsudolegalMovesGenerator.generatePsudolegalMovesBishop(currentPieceCoords,this.blockerBitboard);
                diagonalCheckLines = diagonalsFromKing & diagonalsFromPiece;
                break;
            }
        }

        //straights second
        long straightCheckLines = 0xFFFFFFFFFFFFFFFFL;
        long piecesInStraights = playerMovingPiecesBitboard & straightsFromKing;
        BitboardIterator piecesInStraightsIterator = new BitboardIterator(piecesInStraights);
        while (piecesInStraightsIterator.hasNext()){
            int currentPieceIndex = piecesInStraightsIterator.next();
            Coordinates currentPieceCoords = HelperClass.coordsFromBitBoardIndex(currentPieceIndex);
            Piece currentPiece = this.board[currentPieceCoords.row()][currentPieceCoords.col()];
            //if the below statement is true, there is a check.
            if (Math.abs(currentPiece.getID()) == 3 || Math.abs(currentPiece.getID()) == 5){
                long straightsFromPiece = PsudolegalMovesGenerator.generatePsudolegalMovesRook(currentPieceCoords,this.blockerBitboard);
                straightCheckLines &= straightsFromKing & straightsFromPiece;
            }
        }
        this.checkLines = diagonalCheckLines & straightCheckLines;

        //handle knight checks
        long piecesInKnightSpots = playerMovingPiecesBitboard & knightMovesFromKing;
        BitboardIterator piecesInKnightMovementIterator = new BitboardIterator(piecesInKnightSpots);
        while (piecesInKnightMovementIterator.hasNext()){
            int currentPieceIndex = piecesInKnightMovementIterator.next();
            Coordinates currentPieceCords = HelperClass.coordsFromBitBoardIndex(currentPieceIndex);
            Piece currentPiece = this.board[currentPieceCords.row()][currentPieceCords.col()];;
            //if the statement below is true, there is a check
            if (Math.abs(currentPiece.getID()) == 2){
                this.checkLines &= HelperClass.getBitboard(currentPieceIndex);
            }
        }

        //handle pawn checks
        Coordinates kingLocation = this.playerNotMoving.getKingLocation();
        long spacesAroundKing = this.board[kingLocation.row()][kingLocation.col()].getSquaresAttackingBitboard();
        long piecesAroundKing = playerMovingPiecesBitboard & spacesAroundKing;
        BitboardIterator piecesInKingMovementIterator = new BitboardIterator(piecesAroundKing);
        while (piecesInKingMovementIterator.hasNext()){
            int currentPieceIndex = piecesInKingMovementIterator.next();
            Coordinates currentPieceCords = HelperClass.coordsFromBitBoardIndex(currentPieceIndex);
            Piece currentPiece = this.board[currentPieceCords.row()][currentPieceCords.col()];
            if (Math.abs(currentPiece.getID()) == 1){
                if ((currentPiece.getSquaresAttackingBitboard() & HelperClass.getBitboard(kingLocation)) != 0){
                    this.checkLines &= HelperClass.getBitboard(currentPieceCords);
                }
            }
        }
    }

    public void updatePinLines(){
        //clear the array list so that old pin lines are removed
        this.pinLines.clear();

        long baseDiagonalsFromKing = BaseMovesFiller.calculateBishopMovesBitboard(playerNotMoving.getKingLocation());
        long baseStraightsFromKing = BaseMovesFiller.calculateRookMovesBitboard(playerNotMoving.getKingLocation());

        //diagonals first
        BitboardIterator piecesAlongBaseDiagonals = new BitboardIterator(baseDiagonalsFromKing & playerMoving.getPiecesBitboard());
        while (piecesAlongBaseDiagonals.hasNext()){
            int currentPieceIndex = piecesAlongBaseDiagonals.next();
            Coordinates currentPieceLocation = HelperClass.coordsFromBitBoardIndex(currentPieceIndex);
            Piece currentPiece = this.board[currentPieceLocation.row()][currentPieceLocation.col()];
            if (Math.abs(currentPiece.getID()) == 3 || Math.abs(currentPiece.getID()) == 5){
                long psudolegalBishopMovesFromKing = PsudolegalMovesGenerator.generatePsudolegalMovesBishop(playerNotMoving.getKingLocation(), this.blockerBitboard);
                long checkForPinnedPiece = PsudolegalMovesGenerator.generatePsudolegalMovesBishop(currentPieceLocation, this.blockerBitboard) & psudolegalBishopMovesFromKing;
                if (HelperClass.onlyOneBitOn(checkForPinnedPiece)){
                    long tempBlockers = this.blockerBitboard ^ HelperClass.getBitboard(currentPieceIndex);
                    long pinLine = PsudolegalMovesGenerator.generatePsudolegalMovesBishop(currentPieceLocation,tempBlockers) & PsudolegalMovesGenerator.generatePsudolegalMovesBishop(playerNotMoving.getKingLocation(), tempBlockers);
                    this.pinLines.add(pinLine);
                }
            }
        }

        //straights next
        BitboardIterator piecesAlongBaseStraights = new BitboardIterator(baseDiagonalsFromKing & playerMoving.getPiecesBitboard());
        while (piecesAlongBaseStraights.hasNext()){
            int currentPieceIndex = piecesAlongBaseStraights.next();
            Coordinates currentPieceLocation = HelperClass.coordsFromBitBoardIndex(currentPieceIndex);
            Piece currentPiece = this.board[currentPieceLocation.row()][currentPieceLocation.col()];
            if (Math.abs(currentPiece.getID()) == 4 || Math.abs(currentPiece.getID()) == 5){
                long psudolegalRookMovesFromKing = PsudolegalMovesGenerator.generatePsudolegalMovesRook(playerNotMoving.getKingLocation(), this.blockerBitboard);
                long checkForPinnedPiece = PsudolegalMovesGenerator.generatePsudolegalMovesRook(currentPieceLocation, this.blockerBitboard) & psudolegalRookMovesFromKing;
                if (HelperClass.onlyOneBitOn(checkForPinnedPiece)){
                    long tempBlockers = this.blockerBitboard ^ HelperClass.getBitboard(currentPieceIndex);
                    long pinLine = PsudolegalMovesGenerator.generatePsudolegalMovesRook(currentPieceLocation,tempBlockers) & PsudolegalMovesGenerator.generatePsudolegalMovesRook(playerNotMoving.getKingLocation(), tempBlockers);
                    this.pinLines.add(pinLine);
                }
            }
        }
    }

    //This method does NOT create a new Position. It modifies the current one.
    public void updateFromMove(Move move){
        //Get the piece that's moving
        Piece pieceMoving = this.board[move.startingSquare().row()][move.startingSquare().col()];

        //Move piece and update Player and Piece object data
        this.playerMoving.movePiece(pieceMoving, move.startingSquare(), move.endingSquare());


        //NOTE: The reason the capture must be checked first is that otherwise
        //the square the capturing piece lands on will be set to null AFTER the capturing piece
        //is placed there.

        //if the move was a capture, remove piece and update this.board
        if (move.isCaptureMove()){
            Piece pieceCaptured = this.board[move.captureSquare().row()][move.captureSquare().col()];
            this.playerMoving.removePiece(pieceCaptured);
            this.board[move.captureSquare().row()][move.captureSquare().col()] = null;
        }

        //update this.board to reflect the movement of the piece moving
        this.board[move.startingSquare().row()][move.startingSquare().col()] = null;
        this.board[move.endingSquare().row()][move.endingSquare().col()] = pieceMoving;



        //update blocker bitboard
        this.updateBlockerBitboard();

        //if the piece moved was a king, update the corresponding player's diagonal and straight bitboards from king
        if (Math.abs(pieceMoving.getID()) == 6){
            this.playerMoving.updateKingLines(this.blockerBitboard);
        }
        //update squaresAttacking bitboards for BOTH players
        this.whitePlayer.updateSquaresAttackingBitboard(this.blockerBitboard);
        this.blackPlayer.updateSquaresAttackingBitboard(this.blockerBitboard);

        //TODO: update castling rights

        //Update check lines
        this.updateCheckLines();

        //Update pin lines
        this.updatePinLines();

        //update En Passant squares
        this.enPassantSquares = 0;
        if (Math.abs(pieceMoving.getID()) == 1){
            //if a pawn moves by two rows, an en passant square is present
            if (Math.abs(move.startingSquare().row() - move.endingSquare().row()) == 2){
                //the average of the starting and ending rows is the row we want
                //taking the average accounts for both white and black pawns.
                int enPassantRow = (move.startingSquare().row() - move.endingSquare().row()) / 2;
                this.enPassantSquares = HelperClass.getBitboard(enPassantRow, move.startingSquare().col());
            }
        }

        //update playerMoving and playerNotMoving
        Player temp = this.playerMoving;
        this.playerMoving = this.playerNotMoving;
        this.playerNotMoving = temp;

        //switch player1ToMove
        this.player1ToMove = !this.player1ToMove;

        //TODO: update zobrist hash

    }

    public Position createChildPosition(Move move){
        //initialize child position
        Position childPosition = new Position(this);

        //update child position
        childPosition.updateFromMove(move);

        return childPosition;
    }

    //PRECONDITION: assumes that the coordinates provided point to a Piece object on the board
    public ArrayList<Move> generateLegalMoves(Coordinates location){
        ArrayList<Move> legalMoves = new ArrayList<>();

        Piece pieceMoving = this.board[location.row()][location.col()];
        int pieceMovingID = pieceMoving.getID();

        long psudolegalMoves = PsudolegalMovesGenerator.getPsudolegalMoves(pieceMoving, this.blockerBitboard);
        long legalMovesBitboard;
        if (Math.abs(pieceMovingID) == 6){
            legalMovesBitboard = psudolegalMoves & (~this.playerNotMoving.getSquaresAttacking()) & (~this.playerMoving.getPiecesBitboard());
        }
        else{
            legalMovesBitboard = psudolegalMoves & (~this.playerMoving.getPiecesBitboard()) & this.checkLines;
            for (long pinLine : this.pinLines){
                if ((pieceMoving.getPositionBitboard() & pinLine) != 0) {
                    legalMovesBitboard &= pinLine;
                    break;
                }
            }
        }
        BitboardIterator movesIterator = new BitboardIterator(legalMovesBitboard);
        while (movesIterator.hasNext()){
            int currentMoveIndex = movesIterator.next();
            //TODO: Finish
        }
        return legalMoves;
    }

    public void displayBoard(){
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (board[i][j] != null){
                    System.out.print(board[i][j].getPieceString()+" ");
                }
                else{
                    System.out.print("+ ");
                }
            }
            System.out.println();
        }
    }




}
