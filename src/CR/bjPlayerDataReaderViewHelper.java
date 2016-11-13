package CR;

import org.opensplice.dds.dcps.Utilities;

public final class bjPlayerDataReaderViewHelper
{

    public static CR.bjPlayerDataReaderView narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjPlayerDataReaderView) {
            return (CR.bjPlayerDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static CR.bjPlayerDataReaderView unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjPlayerDataReaderView) {
            return (CR.bjPlayerDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
