package HelloWorldData;

import org.opensplice.dds.dcps.Utilities;

public final class MsgDataReaderViewHelper
{

    public static HelloWorldData.MsgDataReaderView narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MsgDataReaderView) {
            return (HelloWorldData.MsgDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static HelloWorldData.MsgDataReaderView unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MsgDataReaderView) {
            return (HelloWorldData.MsgDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
