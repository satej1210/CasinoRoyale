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

    public void testGetValue() throws Exception {
        assertEquals(2, CardFunctions.GetValue('2'));
        assertEquals(3, CardFunctions.GetValue('3'));
        assertEquals(4, CardFunctions.GetValue('4'));
        assertEquals(5, CardFunctions.GetValue('5'));
        assertEquals(6, CardFunctions.GetValue('6'));
        assertEquals(7, CardFunctions.GetValue('7'));
        assertEquals(8, CardFunctions.GetValue('8'));
        assertEquals(9, CardFunctions.GetValue('9'));
        assertEquals(10, CardFunctions.GetValue('T'));
        assertEquals(10, CardFunctions.GetValue('K'));
        assertEquals(10, CardFunctions.GetValue('Q'));
        assertEquals(10, CardFunctions.GetValue('J'));
        assertEquals(11, CardFunctions.GetValue('A'));
    }

}