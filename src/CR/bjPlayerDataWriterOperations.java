package CR;

public interface bjPlayerDataWriterOperations extends
    DDS.DataWriterOperations
{

    long register_instance(
            CR.bjPlayer instance_data);

    long register_instance_w_timestamp(
            CR.bjPlayer instance_data, 
            DDS.Time_t source_timestamp);

    int unregister_instance(
            CR.bjPlayer instance_data, 
            long handle);

    int unregister_instance_w_timestamp(
            CR.bjPlayer instance_data, 
            long handle, 
            DDS.Time_t source_timestamp);

    int write(
            CR.bjPlayer instance_data, 
            long handle);

    int write_w_timestamp(
            CR.bjPlayer instance_data, 
            long handle, 
            DDS.Time_t source_timestamp);

    int dispose(
            CR.bjPlayer instance_data, 
            long instance_handle);

    int dispose_w_timestamp(
            CR.bjPlayer instance_data, 
            long instance_handle, 
            DDS.Time_t source_timestamp);
    
    int writedispose(
            CR.bjPlayer instance_data, 
            long instance_handle);

    int writedispose_w_timestamp(
            CR.bjPlayer instance_data, 
            long instance_handle, 
            DDS.Time_t source_timestamp);

    int get_key_value(
            CR.bjPlayerHolder key_holder, 
            long handle);
    
    long lookup_instance(
            CR.bjPlayer instance_data);

}
