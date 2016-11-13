package CR;

public final class player_status {

    public int uuid;
    public int wager;
    public CR.card[] cards = new CR.card[21];

    public player_status() {
        for(int i1 = 0; i1 < 21; i1++) {
            cards[i1] = new CR.card();
        }
    }

    public player_status(
        int _uuid,
        int _wager,
        CR.card[] _cards)
    {
        uuid = _uuid;
        wager = _wager;
        cards = _cards;
    }

}
