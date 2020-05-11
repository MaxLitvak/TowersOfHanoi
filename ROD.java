import java.util.ArrayList;

public class ROD {
    public ArrayList<ring> rings;
    public int otherRods[];

    public ROD(int number){
        rings = new ArrayList<ring>();
        otherRods = new int[2];
        if (number == 0){
            otherRods[0]=1;
            otherRods[1]=2;
        }
        if (number == 1){
            otherRods[0]=0;
            otherRods[1]=2;
        }
        if (number == 2){
            otherRods[0]=0;
            otherRods[1]=1;
        }
    }
}
