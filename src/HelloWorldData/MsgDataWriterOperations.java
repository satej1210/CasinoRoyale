package HelloWorldData;

public interface MsgDataWriterOperations extends
    DDS.DataWriterOperations
{

    long register_instance(
            HelloWorldData.Msg instance_data);

    long register_instance_w_timestamp(
            HelloWorldData.Msg instance_data, 
            DDS.Time_t source_timestamp);

    int unregister_instance(
            HelloWorldData.Msg instance_data, 
            long handle);

    int unregister_instance_w_timestamp(
            HelloWorldData.Msg instance_data, 
            long handle, 
            DDS.Time_t source_timestamp);

    int write(
            HelloWorldData.Msg instance_data, 
            long handle);

    int write_w_timestamp(
            HelloWorldData.Msg instance_data, 
            long handle, 
            DDS.Time_t source_timestamp);

    int dispose(
            HelloWorldData.Msg instance_data, 
            long instance_handle);

    int dispose_w_timestamp(
            HelloWorldData.Msg instance_data, 
            long instance_handle, 
            DDS.Time_t source_timestamp);
    
    int writedispose(
            HelloWorldData.Msg instance_data, 
            long instance_handle);

    int writedispose_w_timestamp(
            HelloWorldData.Msg instance_data, 
            long instance_handle, 
            DDS.Time_t source_timestamp);

    int get_key_value(
            HelloWorldData.MsgHolder key_holder, 
            long handle);
    
    long lookup_instance(
            HelloWorldData.Msg instance_data);

}
