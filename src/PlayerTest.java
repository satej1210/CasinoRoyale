import junit.framework.TestCase;

/**
 * Created by Satej on 12/6/2016.
 */
public class PlayerTest extends TestCase {
    public void testCheckSum() throws Exception {
        Player p = new Player();
        assertEquals("BLACKJACK!", p.CheckSum(21, 1));
        assertEquals("BLACKJACK!", p.CheckSum(21, 0));
        assertEquals("BLACKJACK!", p.CheckSum(31, 1));
        assertEquals("BLACKJACK!", p.CheckSum(41, 2));
        assertEquals("L21", p.CheckSum(22, 2));
    }

}