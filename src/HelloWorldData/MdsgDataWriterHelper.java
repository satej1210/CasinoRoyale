package HelloWorldData;

import org.opensplice.dds.dcps.Utilities;

public final class MdsgDataWriterHelper
{

    public static HelloWorldData.MdsgDataWriter narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MdsgDataWriter) {
            return (HelloWorldData.MdsgDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static HelloWorldData.MdsgDataWriter unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MdsgDataWriter) {
            return (HelloWorldData.MdsgDataWriter)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
