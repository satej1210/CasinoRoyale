package CR;

import org.opensplice.dds.dcps.Utilities;

public final class bjPlayerDataReaderHelper
{

    public static CR.bjPlayerDataReader narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjPlayerDataReader) {
            return (CR.bjPlayerDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static CR.bjPlayerDataReader unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjPlayerDataReader) {
            return (CR.bjPlayerDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
