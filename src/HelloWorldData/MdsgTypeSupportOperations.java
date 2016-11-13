package HelloWorldData;

public interface MdsgTypeSupportOperations extends
    DDS.TypeSupportOperations
{

    int register_type(
            DDS.DomainParticipant participant, 
            java.lang.String type_name);

}
