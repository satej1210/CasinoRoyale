import CR.card;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

/**
 * Created by satejmhatre on 11/4/16.
 */
public class CardFunctions {
    public static card PickCard(CardAndDeck d) {
        Stack cardList = new Stack<CR.card>();
        cardList.addAll(d.c);
        CR.card c = (CR.card)cardList.pop();
        ArrayList t = new ArrayList(cardList);
        d.c = t;
        d.card = c;
        return c;
    }

    public static int GetValue(char b) {
        if (b == 'K' || b == 'Q' || b == 'J' || b == 'T') {
            return 10;
        } else {
            return b - 48;
        }
    }

    public static void PrintCard(CR.card card) {
        System.out.println("Card:\nSuite:" + card.suite + "\nBase Value:" + card.base_value + "\nVisible:" + card.visible);
    }

    public static void PrintCards(ArrayList<CR.card> cards) {
        for (int i = 0; i < cards.size(); ++i) {
            if (cards.get(i).base_value != 0) {
                PrintCard(cards.get(i));
            }
        }
    }
    public static void PrintDeck(ArrayList<CR.card> cards) {
        for (int i = 0; i < cards.size(); ++i) {
            if(i%52==0)System.out.println("Deck");
            String s;
            s = Character.toString(cards.get(i).suite);
            s += Character.toString(cards.get(i).base_value);
            System.out.println(s);
        }
    }

    public static void GenerateDeck(ArrayList<CR.card> cards) {
        int count=0;

        char suites[] = {'C', 'H', 'D', 'S'};
        char otherCards[] = {'2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'};
        for (int i = 0; i < Dealer.DECKS; ++i) {
            for(int k = 0; k < 4; ++k) {
                for(int j = 0; j < otherCards.length; ++j){
                    cards.add(new card(suites[k], otherCards[j], false));
                }
            }
        }

    }

    public static ArrayList<card> ShuffleCards(ArrayList<card> array) {
        Random rgen = new Random();  // Random number generator

        for (int i = 0; i < array.size(); i++) {
            int randomPosition = rgen.nextInt(array.size());
            CR.card temp = array.get(i);
            array.set(i, array.get(randomPosition));
            array.set(randomPosition, temp);
        }

        return array;
    }
}
