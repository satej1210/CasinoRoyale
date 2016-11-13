/**
 * Created by satejmhatre on 11/4/16.
 */
public class CardAndDeck {
    public CR.card c[];
    public CR.card card;
    CardAndDeck(CR.card _c[], CR.card d){
        c = _c.clone();
        card = d;
    }
}
