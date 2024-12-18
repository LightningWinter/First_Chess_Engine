package Chess.LookupsFillers;
import Chess.Model.HelperClass;
import Chess.Coordinates;

import java.io.*;
import java.util.HashMap;

public class BaseMovesFiller {

    private static final long startingPositiveDiagonal = 0x0102040810204080L;
    private static final long startingNegativeDiagonal = 0x8040201008040201L;

    public static long calculateKnightMovesBitboard(Coordinates location){
        long result = 0x0000142200221400L;
        int verticalShiftNum = location.row()-3;
        if (verticalShiftNum < 0){
            result = result >>> (Math.abs(verticalShiftNum * 8));
        }
        else{
            result = result << (verticalShiftNum * 8);
        }
        int horizontalShiftNum = 3-location.col();
        result = HelperClass.edgeSafeBitShift(result,horizontalShiftNum);

        return result;
    }

    public static long calculateRookMovesBitboard(Coordinates location){
        long result = 0x0101010101010101L << location.col();
        result = result | (0x00000000000000FFL << ((location.row())*8));
        result ^= HelperClass.getBitboard(location);
        return result;
    }

    public static long calculateBishopMovesBitboard(Coordinates location){
        long positiveDiagonal = startingPositiveDiagonal;
        int verticalShiftNum = location.row()-4;
        verticalShiftNum += (-(3-location.col()));
        if (verticalShiftNum < 0){
            positiveDiagonal >>>= Math.abs(verticalShiftNum * 8);
        }
        else{
            positiveDiagonal <<= (verticalShiftNum * 8);
        }

        long negativeDiagonal = startingNegativeDiagonal;
        verticalShiftNum = location.row()-4;
        verticalShiftNum += (4-location.col());
        if (verticalShiftNum < 0){
            negativeDiagonal >>>= Math.abs(verticalShiftNum * 8);
        }
        else{
            negativeDiagonal <<= (verticalShiftNum * 8);
        }

        long result = positiveDiagonal | negativeDiagonal;

        result = result ^ HelperClass.getBitboard(location);
        return result;
    }
    public static long calculateQueenMovesBitboard(Coordinates location){
        return calculateBishopMovesBitboard(location) | calculateRookMovesBitboard(location);
    }
    public static long calculateKingMovesBitboard(Coordinates location){
        long result = 0x0000001C141C0000L;
        int verticalShiftNum = location.row()-3;
        if (verticalShiftNum < 0){
            result = result >>> (Math.abs(verticalShiftNum * 8));
        }
        else{
            result = result << (verticalShiftNum * 8);
        }
        int horizontalShiftNum = 3-location.col();
        result = HelperClass.edgeSafeBitShift(result,horizontalShiftNum);

        return result;
    }


    public static HashMap<String,Long> createBaseMovesHashMap(){
        HashMap<String,Long> map = new HashMap<>();
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                Coordinates currentCoords = new Coordinates(i,j);
                map.put("N"+currentCoords.toString(),calculateKnightMovesBitboard(currentCoords));
                map.put("R"+currentCoords.toString(),calculateRookMovesBitboard(currentCoords));
                map.put("B"+currentCoords.toString(),calculateBishopMovesBitboard(currentCoords));
                map.put("Q"+currentCoords.toString(),calculateQueenMovesBitboard(currentCoords));
                map.put("K"+currentCoords.toString(),calculateKingMovesBitboard(currentCoords));
            }
        }
        return map;
    }

    public static void writeBaseMovesHashMapToFile(){
        try{
            HashMap<String,Long> map = createBaseMovesHashMap();
            FileOutputStream f = new FileOutputStream(new File("data/BaseMoves"));
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(map);

            f.close();
            o.close();

        }catch (IOException e){
            System.out.println("Error initializing stream");
        }
    }
    public static void readBaseMovesHashMapFromFile() {
        try {
            FileInputStream fi = new FileInputStream(new File("data/BaseMoves"));
            ObjectInputStream oi = new ObjectInputStream(fi);
            HashMap<String,Long> map = (HashMap<String,Long>) oi.readObject();
            for (String key : map.keySet()){
                System.out.println("Key: " + key);
                HelperClass.showBitboard(map.get(key));
            }
            fi.close();
            oi.close();

        } catch (IOException e) {
            System.out.println("Error reading file contents");
        } catch (ClassNotFoundException e){
            System.out.println("type of Object in file does not match the type the object is cast to");
        }
    }
    public static void main(String[] args){
        readBaseMovesHashMapFromFile();
    }
}
