package HelloWorldData;

import org.opensplice.dds.dcps.Utilities;

public final class MdsgDataReaderHelper
{

    public static HelloWorldData.MdsgDataReader narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MdsgDataReader) {
            return (HelloWorldData.MdsgDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static HelloWorldData.MdsgDataReader unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MdsgDataReader) {
            return (HelloWorldData.MdsgDataReader)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
