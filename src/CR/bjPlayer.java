package CR;

public final class bjPlayer {

    public int uuid;
    public int seqno;
    public int credits;
    public int wager;
    public int dealer_id;
    public CR.bjp_action action = CR.bjp_action.from_int(0);

    public bjPlayer() {
    }

    public bjPlayer(
        int _uuid,
        int _seqno,
        int _credits,
        int _wager,
        int _dealer_id,
        CR.bjp_action _action)
    {
        uuid = _uuid;
        seqno = _seqno;
        credits = _credits;
        wager = _wager;
        dealer_id = _dealer_id;
        action = _action;
    }

}
