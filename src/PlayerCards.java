import java.util.ArrayList;

/**
 * Created by Satej on 12/5/2016.
 */
public class PlayerCards {
    long uuid;
    ArrayList<CR.card> playerCards;

    PlayerCards(long uuid) {
        this.uuid = uuid;
        playerCards = new ArrayList<>();
    }
}
