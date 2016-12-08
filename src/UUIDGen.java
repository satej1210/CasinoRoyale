import java.util.Random;

/**
 * Created by satejmhatre on 11/2/16.
 */
public class UUIDGen {
    public static int generate_UUID(){
        Random rand = new Random();
        return Math.abs(rand.nextInt() % 10000);
    }
}
