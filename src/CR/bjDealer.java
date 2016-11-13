package CR;

public final class bjDealer {

    public int uuid;
    public int seqno;
    public int active_players;
    public CR.player_status[] players = new CR.player_status[6];
    public CR.bjd_action action = CR.bjd_action.from_int(0);
    public CR.card[] cards = new CR.card[21];
    public int target_uuid;

    public bjDealer() {
        for(int i1 = 0; i1 < 6; i1++) {
            players[i1] = new CR.player_status();
        }
        for(int i1 = 0; i1 < 21; i1++) {
            cards[i1] = new CR.card();
        }
    }

    public bjDealer(
        int _uuid,
        int _seqno,
        int _active_players,
        CR.player_status[] _players,
        CR.bjd_action _action,
        CR.card[] _cards,
        int _target_uuid)
    {
        uuid = _uuid;
        seqno = _seqno;
        active_players = _active_players;
        players = _players;
        action = _action;
        cards = _cards;
        target_uuid = _target_uuid;
    }

}
