package CR;

import org.opensplice.dds.dcps.Utilities;

public final class bjPlayerTypeSupportHelper
{

    public static CR.bjPlayerTypeSupport narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjPlayerTypeSupport) {
            return (CR.bjPlayerTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static CR.bjPlayerTypeSupport unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjPlayerTypeSupport) {
            return (CR.bjPlayerTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
