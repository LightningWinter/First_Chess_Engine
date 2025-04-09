package Chess.Model;

import Chess.Coordinates;
import Chess.Move;
import java.util.Random;
public class HelperClass {
    public static long getBitboard(int row, int col){
        int bitboardIndex = row*8 + col;
        return (Long.MIN_VALUE >>> (63-bitboardIndex));
    }
    public static long getBitboard(int bitboardIndex){
        return (Long.MIN_VALUE >>> (63-bitboardIndex));
    }
    public static long getBitboard(Coordinates coordinates){
        return getBitboard(coordinates.row(), coordinates.col());
    }

    public static Coordinates coordsFromBitBoardIndex(int bitboardIndex){
        return new Coordinates(bitboardIndex / 8, bitboardIndex % 8);
    }

    public static String getBinaryRepr(long num){
        String out = Long.toBinaryString(num);
        for (int i = out.length(); i < 64; i++){
            out = "0"+out;
        }
        return out;
    }

    public static void showBitboard(long bitboard){
        String binaryString = getBinaryRepr(bitboard);
        String out = "  76543210\n  ________\n";
        for (int i = 0; i < 8; i++){
            out += (7-i)+"|";
            out += binaryString.substring(i * 8, (i*8)+ 8);
            out += "\n";
        }
        System.out.println(out);
    }
    public static long getSingleColumnMask(int columnIndex){
        //input index should be 0-7 inclusive, leftmost column is 0, rightmost column is 7
        return 0x8080808080808080L >>> columnIndex;
    }
    public static long getMultiColumnMask(int numColumnsFromLeft){
        //numColumnsFromLeft should range from 1-8
        //returns a multi-column mask that starts from the left and extends numColumnsFromLeft columns to the right
        long mask = 0;
        for (int i = 0; i < numColumnsFromLeft; i++){
            mask = mask | getSingleColumnMask(i);
        }
        return mask;
    }

    public static long edgeSafeBitShift(long input, int movement){
         if (movement < 0){
             return ((input & ~getMultiColumnMask(Math.abs(movement))) << Math.abs(movement));
         }
         if (movement > 0){
             return ((input & getMultiColumnMask(8-movement)) >>> movement);
         }
         //if movement is 0
         return input;
    }
    public static long verticalFlip(long inputBitboard){
        long out = ( (inputBitboard << 56)                           ) |
                ( (inputBitboard << 40) & (0x00FF000000000000L) ) |
                ( (inputBitboard << 24) & (0x0000FF0000000000L) ) |
                ( (inputBitboard <<  8) & (0x000000FF00000000L) ) |
                ( (inputBitboard >>>  8) & (0x00000000FF000000L) ) |
                ( (inputBitboard >>> 24) & (0x0000000000FF0000L) ) |
                ( (inputBitboard >>> 40) & (0x000000000000FF00L) ) |
                ( (inputBitboard >>> 56) );
        return out;
    }

    public static long generateRandomZobristHash(){
        Random random = new Random();
        long min = Long.MIN_VALUE;
        long max = Long.MAX_VALUE;
        long randomNum = random.nextLong();
        return randomNum;
    }
    public static int log2(long num){
        //IMPORTANT: this rounds the result down to the nearest smaller integer
        return (int)(Math.log(num) / Math.log(2));
    }

    public static boolean onlyOneBitOn(long bitboard){
        if (((bitboard & (bitboard - 1)) == 0) & bitboard != 0){
            return true;
        }
        return false;
    }

    public static void main(String args[]){
        long test = 0xF69301FCAA8309FFL;
        showBitboard(test);
        showBitboard(verticalFlip(test));
        System.out.println(generateRandomZobristHash());
    }
}
