package HelloWorldData;

import org.opensplice.dds.dcps.Utilities;

public final class MsgTypeSupportHelper
{

    public static HelloWorldData.MsgTypeSupport narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MsgTypeSupport) {
            return (HelloWorldData.MsgTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static HelloWorldData.MsgTypeSupport unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MsgTypeSupport) {
            return (HelloWorldData.MsgTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
