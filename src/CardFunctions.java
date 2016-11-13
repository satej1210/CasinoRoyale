import CR.card;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

/**
 * Created by satejmhatre on 11/4/16.
 */
public class CardFunctions {
    public static CardAndDeck PickCard(CR.card cards[]){
        Stack cardList = new Stack<CR.card>();
        cardList.addAll(Arrays.asList(cards));
        CR.card c = (CR.card)cardList.pop();
        ArrayList t = new ArrayList(cardList);

        CR.card newDeck[] = new CR.card[t.size()];
        newDeck = (CR.card[])t.toArray(newDeck);

        return new CardAndDeck(newDeck, c);
    }

    public static void PrintDeck(CR.card cards[]){
        for(int i = 0; i < cards.length; ++i) {
            if(i%52==0)System.out.println("Deck");
            System.out.println(cards[i]);
        }
    }
    public static void GenerateDeck(CR.card cards[]){
        int count=0;

        char suites[] = {'C', 'H', 'D', 'S'};
        char otherCards[] = {'2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'};
        for(int i = 0; i < Dealer.NUMBER_OF_DECKS; ++i){
            for(int k = 0; k < 4; ++k) {
                for(int j = 0; j < otherCards.length; ++j){
                    cards[count++] = new card(suites[k], otherCards[j], false);
                }
            }
        }

    }
    public static CR.card[] ShuffleCards(CR.card[] array){
        Random rgen = new Random();  // Random number generator

        for (int i=0; i<array.length; i++) {
            int randomPosition = rgen.nextInt(array.length);
            CR.card temp = array[i];
            array[i] = array[randomPosition];
            array[randomPosition] = temp;
        }

        return array;
    }
}
