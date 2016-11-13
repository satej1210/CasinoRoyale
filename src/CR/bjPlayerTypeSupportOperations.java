package CR;

public interface bjPlayerTypeSupportOperations extends
    DDS.TypeSupportOperations
{

    int register_type(
            DDS.DomainParticipant participant, 
            java.lang.String type_name);

}
