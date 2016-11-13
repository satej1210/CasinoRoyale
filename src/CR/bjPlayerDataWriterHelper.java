package CR;

import org.opensplice.dds.dcps.Utilities;

public final class bjPlayerDataWriterHelper
{

    public static CR.bjPlayerDataWriter narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjPlayerDataWriter) {
            return (CR.bjPlayerDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static CR.bjPlayerDataWriter unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof CR.bjPlayerDataWriter) {
            return (CR.bjPlayerDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
