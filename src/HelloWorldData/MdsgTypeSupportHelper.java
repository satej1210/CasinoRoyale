package HelloWorldData;

import org.opensplice.dds.dcps.Utilities;

public final class MdsgTypeSupportHelper
{

    public static HelloWorldData.MdsgTypeSupport narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MdsgTypeSupport) {
            return (HelloWorldData.MdsgTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

    public static HelloWorldData.MdsgTypeSupport unchecked_narrow(java.lang.Object obj)
    {
        if (obj == null) {
            return null;
        } else if (obj instanceof HelloWorldData.MdsgTypeSupport) {
            return (HelloWorldData.MdsgTypeSupport)obj;
        } else {
            throw Utilities.createException(Utilities.EXCEPTION_TYPE_BAD_PARAM, null);
        }
    }

}
