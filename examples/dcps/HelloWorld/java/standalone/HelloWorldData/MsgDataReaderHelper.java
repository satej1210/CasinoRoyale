package HelloWorldData;

import org.opensplice.dds.dcps.Utilities;

public final class MsgDataReaderHelper
{

    public static HelloWorldData.MsgDataReader narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MsgDataReader) {
            return (HelloWorldData.MsgDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static HelloWorldData.MsgDataReader unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MsgDataReader) {
            return (HelloWorldData.MsgDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
