package CR;

import org.opensplice.dds.dcps.Utilities;

public final class bjDealerTypeSupportHelper
{

    public static CR.bjDealerTypeSupport narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjDealerTypeSupport) {
            return (CR.bjDealerTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static CR.bjDealerTypeSupport unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjDealerTypeSupport) {
            return (CR.bjDealerTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
