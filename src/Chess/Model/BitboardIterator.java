package Chess.Model;

public class BitboardIterator {
    private long value;

    public BitboardIterator(long input){
        this.value = input;
    }

    public boolean hasNext(){
        if (value != 0){
            return false;
        }
        return true;
    }
    public int next(){

        //get index of highest on bit
        int index = 63-Long.numberOfLeadingZeros(value);

        //turn off the relevant bit
        value &= ~(Long.MIN_VALUE >>> (63-index));

        //return result
        return index;
    }
}
