import CR.bjDealer;
import CR.bjPlayer;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Satej on 11/14/2016.
 */
public class Table {
    UUID u;
    bjDealer dealer;
    ArrayList<bjPlayer> players;

    Table(bjDealer d) {
        if (u == null)
            u = UUID.randomUUID();
        dealer = d;
        if (players == null)
            players = new ArrayList<>();
    }

    Table(bjDealer d, bjPlayer p) {
        if (u == null)
            u = UUID.randomUUID();
        dealer = d;
        if (players == null)
            players = new ArrayList<>();
        CheckSize();
        players.add(p);

    }

    UUID getUUID() {
        return u;
    }

    void CheckSize() {
        if (players.size() >= 6) {
            System.out.println("Players full");
        }
    }

    @Override
    public String toString() {
        String s;
        s = "Table " + getUUID() + "\n";
        s += "Dealer: \n" + new Dealer(dealer) + "\n";
        int j = 0;
        for (bjPlayer p : players) {
            s += "Player " + ++j + ":\n\t" + p.uuid + "\n";
        }
        return s;
    }

}
