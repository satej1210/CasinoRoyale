import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Created by Satej on 11/14/2016.
 */
public class CardFunctionsTest extends TestCase {
    public void testGenerateDeck() throws Exception {
        ArrayList<CR.card> test = new ArrayList<>();

        CardFunctions.GenerateDeck(test);
        boolean arrayHasNull = false;
        int size = 0;
        for (CR.card c : test) {
            if (c == null)
                arrayHasNull = true;
            size++;
        }
        assertEquals("Array has all element not equal to null", arrayHasNull, false);
        assertEquals("Array has size DECKS*52", size, Dealer.DECKS * 52);
    }

    public void testShuffleCards() throws Exception {
        ArrayList<CR.card> test = new ArrayList<>();

        CardFunctions.GenerateDeck(test);
        ArrayList<CR.card> org = new ArrayList<>(test);
        CardFunctions.ShuffleCards(test);
        assertNotSame("Arrays should not be equal : ", test, org);
    }



}