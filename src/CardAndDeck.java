import java.util.ArrayList;

/**
 * Created by satejmhatre on 11/4/16.
 */
public class CardAndDeck {
    public ArrayList<CR.card> c;
    public CR.card card;

    CardAndDeck(ArrayList<CR.card> _c) {
        c = _c;
    }

    @Override
    public String toString() {
        String s = "";
        s += card.suite;
        s += card.base_value;
        return s;
    }
}
