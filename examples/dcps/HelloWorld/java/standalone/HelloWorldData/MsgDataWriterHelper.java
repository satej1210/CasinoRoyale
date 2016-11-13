package HelloWorldData;

import org.opensplice.dds.dcps.Utilities;

public final class MsgDataWriterHelper
{

    public static HelloWorldData.MsgDataWriter narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MsgDataWriter) {
            return (HelloWorldData.MsgDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static HelloWorldData.MsgDataWriter unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MsgDataWriter) {
            return (HelloWorldData.MsgDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
