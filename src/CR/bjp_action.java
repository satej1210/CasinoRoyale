package CR;

import org.opensplice.dds.dcps.Utilities;

public class bjp_action {

    private int __value;
    private static int __size = 5;
    private static CR.bjp_action[] __array = new CR.bjp_action[__size];

    public static final int _none = 0;
    public static final CR.bjp_action none = new CR.bjp_action(_none);

    public static final int _joining = 1;
    public static final CR.bjp_action joining = new CR.bjp_action(_joining);

    public static final int _exiting = 2;
    public static final CR.bjp_action exiting = new CR.bjp_action(_exiting);

    public static final int _wagering = 3;
    public static final CR.bjp_action wagering = new CR.bjp_action(_wagering);

    public static final int _requesting_a_card = 4;
    public static final CR.bjp_action requesting_a_card = new CR.bjp_action(_requesting_a_card);

    public int value ()
    {
        return __value;
    }

    public static CR.bjp_action from_int (int value)
    {
        if (value >= 0 && value < __size) {
            return __array[value];
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_OPERATION, null);
        }
    }

    protected bjp_action (int value)
    {
        __value = value;
        __array[__value] = this;
    }
}
