package Chess.LookupsFillers;

import Chess.Coordinates;
import Chess.Model.HelperClass;

import java.io.*;
import java.util.HashMap;

public class ZobristHashMapFiller {

    public static HashMap<String,Long> generateZobristHashMap(){
        HashMap<String,Long> map = new HashMap<>();
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                Coordinates currentCoords = new Coordinates(i,j);
                map.put("N"+currentCoords.toString(), HelperClass.generateRandomZobristHash());
                map.put("R"+currentCoords.toString(), HelperClass.generateRandomZobristHash());
                map.put("B"+currentCoords.toString(), HelperClass.generateRandomZobristHash());
                map.put("Q"+currentCoords.toString(), HelperClass.generateRandomZobristHash());
                map.put("K"+currentCoords.toString(), HelperClass.generateRandomZobristHash());
            }
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
        readZobristHashMapFromFile();
    }
}
