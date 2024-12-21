package Chess.LookupsFillers;

import Chess.Coordinates;
import Chess.Model.HelperClass;

import java.io.*;
import java.util.HashMap;

public class ZobristHashMapFiller {
    //this insures that all hash values in the zobrist hash map are unique
    public static HashMap<String,Long> addItemToZobristHashMap(HashMap<String,Long> map, String key){
        boolean uniqueHashInserted = false;
        while (!uniqueHashInserted){
            long zobristHash = HelperClass.generateRandomZobristHash();
            boolean matchFound = false;
            for (String k : map.keySet()){
                if (map.get(k) == zobristHash){
                    matchFound = true;
                }
            }
            if (!matchFound){
                map.put(key,zobristHash);
                uniqueHashInserted = true;
            }
        }
        return map;
    }
    public static HashMap<String,Long> generateZobristHashMap(){
        HashMap<String,Long> map = new HashMap<>();
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                Coordinates currentCoords = new Coordinates(i,j);
                addItemToZobristHashMap(map,"WP"+currentCoords.toString());
                addItemToZobristHashMap(map,"BP"+currentCoords.toString());
                addItemToZobristHashMap(map,"WN"+currentCoords.toString());
                addItemToZobristHashMap(map,"BN"+currentCoords.toString());
                addItemToZobristHashMap(map,"WR"+currentCoords.toString());
                addItemToZobristHashMap(map,"BR"+currentCoords.toString());
                addItemToZobristHashMap(map,"WB"+currentCoords.toString());
                addItemToZobristHashMap(map,"BB"+currentCoords.toString());
                addItemToZobristHashMap(map,"WQ"+currentCoords.toString());
                addItemToZobristHashMap(map,"BQ"+currentCoords.toString());
                addItemToZobristHashMap(map,"WK"+currentCoords.toString());
                addItemToZobristHashMap(map,"BK"+currentCoords.toString());
            }
        }
        addItemToZobristHashMap(map,"WhiteCastleShort");
        addItemToZobristHashMap(map,"WhiteCastleLong");
        addItemToZobristHashMap(map,"BlackCastleShort");
        addItemToZobristHashMap(map,"BlackCastleLong");
        addItemToZobristHashMap(map,"BlackToMove");
        addItemToZobristHashMap(map,"WhiteToMove");
        for (int i = 0; i < 8; i++){
            addItemToZobristHashMap(map,"EP"+Integer.toString(i));
        }

        return map;
    }

    public static void writeZobristHashMapToFile(){
        try{
            HashMap<String,Long> map = generateZobristHashMap();
            FileOutputStream f = new FileOutputStream(new File("data/ZobristValuesHashMap"));
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(map);

            f.close();
            o.close();

        }catch (IOException e){
            System.out.println("Error initializing stream");
        }
    }

    public static void readZobristHashMapFromFile(){
        try {
            FileInputStream fi = new FileInputStream(new File("data/ZobristValuesHashMap"));
            ObjectInputStream oi = new ObjectInputStream(fi);
            HashMap<String,Long> map = (HashMap<String,Long>) oi.readObject();
            for (String key : map.keySet()){
                System.out.println("Key: " + key);
                System.out.println(map.get(key));
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
        writeZobristHashMapToFile();
    }
}
