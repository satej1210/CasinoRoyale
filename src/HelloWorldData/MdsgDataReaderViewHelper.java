package HelloWorldData;

import org.opensplice.dds.dcps.Utilities;

public final class MdsgDataReaderViewHelper
{

    public static HelloWorldData.MdsgDataReaderView narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MdsgDataReaderView) {
            return (HelloWorldData.MdsgDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static HelloWorldData.MdsgDataReaderView unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MdsgDataReaderView) {
            return (HelloWorldData.MdsgDataReaderView)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
