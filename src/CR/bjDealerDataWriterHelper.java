package CR;

import org.opensplice.dds.dcps.Utilities;

public final class bjDealerDataWriterHelper
{

    public static CR.bjDealerDataWriter narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjDealerDataWriter) {
            return (CR.bjDealerDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static CR.bjDealerDataWriter unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjDealerDataWriter) {
            return (CR.bjDealerDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
