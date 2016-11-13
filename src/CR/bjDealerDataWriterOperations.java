package CR;

public interface bjDealerDataWriterOperations extends
    DDS.DataWriterOperations
{

    long register_instance(
            CR.bjDealer instance_data);

    long register_instance_w_timestamp(
            CR.bjDealer instance_data, 
            DDS.Time_t source_timestamp);

    int unregister_instance(
            CR.bjDealer instance_data, 
            long handle);

    int unregister_instance_w_timestamp(
            CR.bjDealer instance_data, 
            long handle, 
            DDS.Time_t source_timestamp);

    int write(
            CR.bjDealer instance_data, 
            long handle);

    int write_w_timestamp(
            CR.bjDealer instance_data, 
            long handle, 
            DDS.Time_t source_timestamp);

    int dispose(
            CR.bjDealer instance_data, 
            long instance_handle);

    int dispose_w_timestamp(
            CR.bjDealer instance_data, 
            long instance_handle, 
            DDS.Time_t source_timestamp);
    
    int writedispose(
            CR.bjDealer instance_data, 
            long instance_handle);

    int writedispose_w_timestamp(
            CR.bjDealer instance_data, 
            long instance_handle, 
            DDS.Time_t source_timestamp);

    int get_key_value(
            CR.bjDealerHolder key_holder, 
            long handle);
    
    long lookup_instance(
            CR.bjDealer instance_data);

}
