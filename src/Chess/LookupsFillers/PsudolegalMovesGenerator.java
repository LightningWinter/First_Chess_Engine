package Chess.LookupsFillers;
import Chess.Coordinates;
import Chess.Model.HelperClass;
import Chess.Model.Piece;

public class PsudolegalMovesGenerator {

    public static long generatePsudolegalMovesRook(Coordinates location, long blockerBitboard){
        //calculating necessary values
        long pieceLocationBitboard = HelperClass.getBitboard(location);
        long baseMovement = BaseMovesFiller.calculateRookMovesBitboard(location);
        long verticalMask = (0x0101010101010101L << location.col()) & baseMovement;
        long horizontalMask = (0x00000000000000FFL << ((location.row())*8)) & baseMovement;

        long movementVerticalPortion = baseMovement & verticalMask;
        long movementHorizontalPortion = baseMovement & horizontalMask;
        long blockersVerticalPortion = blockerBitboard & verticalMask;
        long blockersHorizontalPortion = blockerBitboard & horizontalMask;

        //solving the vertical portion
        //next line is a spot to look at if bugs appear
        //this logic makes sure the log function
        //only considers blockers at a smaller bitboard index than the piece's location
        long relevantBlockersMask = ((pieceLocationBitboard >> 1)-1) | (pieceLocationBitboard >> 1);
        int closestBlocker = HelperClass.log2(blockersVerticalPortion & relevantBlockersMask);
        long removalMask = -HelperClass.getBitboard(closestBlocker);
        movementVerticalPortion &= removalMask;
        //THIS LINE IS A POTENTIAL ERROR SOURCE
        //the << 1 is included to make sure that the first blocker used for the first removal
        //is also removed from the blockers bitboard
        blockersVerticalPortion &= removalMask << 1;

        movementVerticalPortion = HelperClass.verticalFlip(movementVerticalPortion);
        blockersVerticalPortion = HelperClass.verticalFlip(blockersVerticalPortion);

        closestBlocker = HelperClass.log2(blockersVerticalPortion);

        removalMask = -HelperClass.getBitboard(closestBlocker);
        movementVerticalPortion &= removalMask;

        movementVerticalPortion = HelperClass.verticalFlip(movementVerticalPortion);



        //solving the horizontal portion
        long horizontalPortion = 0;
        for (int i = 1; i <= 7-location.col(); i++){
            long currentLocationBitboard = HelperClass.getBitboard(location.row(), i+location.col());
            horizontalPortion |= currentLocationBitboard;
            if ((currentLocationBitboard & blockersHorizontalPortion) != 0){
                break;
            }
        }
        for (int i = 1; i <= location.col(); i++) {
            long currentLocationBitboard = HelperClass.getBitboard(location.row(), location.col() - i);
            horizontalPortion |= currentLocationBitboard;
            if ((currentLocationBitboard & blockersHorizontalPortion) != 0) {
                break;
            }
        }

        return horizontalPortion | movementVerticalPortion;

    }

    public static long generatePsudolegalMovesBishop(Coordinates location, long blockerBitboard){
        long finalMovement = 0;
        int distanceFromPieceToTop = 7-location.row();
        int distanceFromPieceToLeft = 7-location.col();
        int distanceFromPieceToBottom = location.row();
        int distanceFromPieceToRight = location.col();
        System.out.println(distanceFromPieceToTop);
        System.out.println(distanceFromPieceToLeft);
        System.out.println(distanceFromPieceToBottom);
        System.out.println(distanceFromPieceToRight);
        //up and left movement
        for (int i = 1; i <= Math.min(distanceFromPieceToTop,distanceFromPieceToLeft); i++){
            long currentLocationBitboard = HelperClass.getBitboard(location.row()+i,location.col()+i);
            finalMovement |= currentLocationBitboard;
            if (((currentLocationBitboard) & blockerBitboard) != 0){
                break;
            }
        }

        //up and right movement
        for (int i = 1; i <= Math.min(distanceFromPieceToTop,distanceFromPieceToRight); i++){
            long currentLocationBitboard = HelperClass.getBitboard(location.row()+i,location.col()-i);
            finalMovement |= currentLocationBitboard;
            if (((currentLocationBitboard) & blockerBitboard) != 0){
                break;
            }
        }
        //down and left movement
        for (int i = 1; i <= Math.min(distanceFromPieceToBottom,distanceFromPieceToLeft); i++){
            long currentLocationBitboard = HelperClass.getBitboard(location.row()-i,location.col()+i);
            finalMovement |= currentLocationBitboard;
            if (((currentLocationBitboard) & blockerBitboard) != 0){
                break;
            }
        }

        //down and right movement
        for (int i = 1; i <= Math.min(distanceFromPieceToBottom,distanceFromPieceToRight); i++){
            long currentLocationBitboard = HelperClass.getBitboard(location.row()-i,location.col()-i);
            finalMovement |= currentLocationBitboard;
            if (((currentLocationBitboard) & blockerBitboard) != 0){
                break;
            }
        }

        return finalMovement;
    }
    public static long generatePsudolegalMovesKnight(Coordinates location, long blockerBitboard){
        return BaseMovesFiller.calculateKnightMovesBitboard(location);
    }
    public static long generatePsudolegalMovesKing(Coordinates location, long blockerBitboard){
        return BaseMovesFiller.calculateKingMovesBitboard(location);
    }
    public static long generatePsudolegalMovesQueen(Coordinates location, long blockerBitboard){
        return generatePsudolegalMovesRook(location, blockerBitboard) | generatePsudolegalMovesBishop(location, blockerBitboard);
    }

    public static long generatePsudolegalMovesWhitePawn(Coordinates location, long blockerBitboard){
        //IMPORTANT: This function does NOT account for En Passant. En Passant must be dealt with separately.

        //calculating forward movement
        long finalMovement = 0;
        long pieceBitboardLocation = HelperClass.getBitboard(location);
        if (((pieceBitboardLocation >>> 8) & blockerBitboard) == 0){
            finalMovement |= pieceBitboardLocation >>> 8;
            if (location.row() == 6){
                if (((pieceBitboardLocation >>> 16) & blockerBitboard) == 0) {
                    finalMovement |= pieceBitboardLocation >>> 16;
                }
            }
        }
        if (location.col() != 7){
            long leftCaptureSquare = pieceBitboardLocation >>> 7;
            if ((leftCaptureSquare & blockerBitboard) != 0){
                finalMovement |= leftCaptureSquare;
            }
        }
        if (location.col() != 0){
            long rightCaptureSquare = pieceBitboardLocation >>> 9;
            if ((rightCaptureSquare & blockerBitboard) != 0){
                finalMovement |= rightCaptureSquare;
            }
        }


        return finalMovement;
    }
    public static long generatePsudolegalMovesBlackPawn(Coordinates location, long blockerBitboard){
        //IMPORTANT: This function does NOT account for En Passant. En Passant must be dealt with separately.

        //calculating forward movement
        long finalMovement = 0;
        long pieceBitboardLocation = HelperClass.getBitboard(location);
        HelperClass.showBitboard(pieceBitboardLocation << 8);
        if (((pieceBitboardLocation << 8) & blockerBitboard) == 0){
            System.out.println("Check");
            finalMovement |= pieceBitboardLocation << 8;
            if (location.row() == 1){
                if (((pieceBitboardLocation << 16) & blockerBitboard) == 0) {
                    finalMovement |= pieceBitboardLocation << 16;
                }
            }
        }
        if (location.col() != 7){
            long leftCaptureSquare = pieceBitboardLocation << 9;
            if ((leftCaptureSquare & blockerBitboard) != 0){
                finalMovement |= leftCaptureSquare;
            }
        }
        if (location.col() != 0){
            long rightCaptureSquare = pieceBitboardLocation << 7;
            if ((rightCaptureSquare & blockerBitboard) != 0){
                finalMovement |= rightCaptureSquare;
            }
        }


        return finalMovement;
    }

    public static long getPsudolegalMoves(Piece piece, long blockers){
        if (Math.abs(piece.getID()) == 1){
            if (piece.getID() == 1)
                return generatePsudolegalMovesWhitePawn(piece.getLocation(), blockers);
            if (piece.getID() == -1)
                return generatePsudolegalMovesBlackPawn(piece.getLocation(),blockers);
        }
        switch (Math.abs(piece.getID())){
            case 2:
                return generatePsudolegalMovesKnight(piece.getLocation(),blockers);
            case 3:
                return generatePsudolegalMovesBishop(piece.getLocation(),blockers);
            case 4:
                return generatePsudolegalMovesRook(piece.getLocation(),blockers);
            case 5:
                return generatePsudolegalMovesQueen(piece.getLocation(),blockers);
            case 6:
                return generatePsudolegalMovesKing(piece.getLocation(),blockers);
        }
        return 0L;
    }

    public static void main(String[] args){
        System.out.println("RESULT: ^ ");
        HelperClass.showBitboard(generatePsudolegalMovesBlackPawn(new Coordinates(1,6),0x0C79AFB0923FAD01L));
        HelperClass.showBitboard(0x0C79AFB0923FAD01L);
    }
}
