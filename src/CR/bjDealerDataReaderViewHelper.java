package CR;

import org.opensplice.dds.dcps.Utilities;

public final class bjDealerDataReaderViewHelper
{

    public static CR.bjDealerDataReaderView narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjDealerDataReaderView) {
            return (CR.bjDealerDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static CR.bjDealerDataReaderView unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjDealerDataReaderView) {
            return (CR.bjDealerDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
