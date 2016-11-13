package CR;

import org.opensplice.dds.dcps.Utilities;

public class bjd_action {

    private int __value;
    private static int __size = 5;
    private static CR.bjd_action[] __array = new CR.bjd_action[__size];

    public static final int _shuffling = 0;
    public static final CR.bjd_action shuffling = new CR.bjd_action(_shuffling);

    public static final int _waiting = 1;
    public static final CR.bjd_action waiting = new CR.bjd_action(_waiting);

    public static final int _dealing = 2;
    public static final CR.bjd_action dealing = new CR.bjd_action(_dealing);

    public static final int _collecting = 3;
    public static final CR.bjd_action collecting = new CR.bjd_action(_collecting);

    public static final int _paying = 4;
    public static final CR.bjd_action paying = new CR.bjd_action(_paying);

    public int value ()
    {
        return __value;
    }

    public static CR.bjd_action from_int (int value)
    {
        if (value >= 0 && value < __size) {
            return __array[value];
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_OPERATION, null);
        }
    }

    protected bjd_action (int value)
    {
        __value = value;
        __array[__value] = this;
    }
}
