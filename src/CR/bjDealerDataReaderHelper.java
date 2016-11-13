package CR;

import org.opensplice.dds.dcps.Utilities;

public final class bjDealerDataReaderHelper
{

    public static CR.bjDealerDataReader narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjDealerDataReader) {
            return (CR.bjDealerDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static CR.bjDealerDataReader unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjDealerDataReader) {
            return (CR.bjDealerDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
