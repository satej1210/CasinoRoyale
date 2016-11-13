package HelloWorldData;

public interface MdsgDataWriterOperations extends
    DDS.DataWriterOperations
{

    long register_instance(
            HelloWorldData.Mdsg instance_data);

    long register_instance_w_timestamp(
            HelloWorldData.Mdsg instance_data, 
            DDS.Time_t source_timestamp);

    int unregister_instance(
            HelloWorldData.Mdsg instance_data, 
            long handle);

    int unregister_instance_w_timestamp(
            HelloWorldData.Mdsg instance_data, 
            long handle, 
            DDS.Time_t source_timestamp);

    int write(
            HelloWorldData.Mdsg instance_data, 
            long handle);

    int write_w_timestamp(
            HelloWorldData.Mdsg instance_data, 
            long handle, 
            DDS.Time_t source_timestamp);

    int dispose(
            HelloWorldData.Mdsg instance_data, 
            long instance_handle);

    int dispose_w_timestamp(
            HelloWorldData.Mdsg instance_data, 
            long instance_handle, 
            DDS.Time_t source_timestamp);
    
    int writedispose(
            HelloWorldData.Mdsg instance_data, 
            long instance_handle);

    int writedispose_w_timestamp(
            HelloWorldData.Mdsg instance_data, 
            long instance_handle, 
            DDS.Time_t source_timestamp);

    int get_key_value(
            HelloWorldData.MdsgHolder key_holder, 
            long handle);
    
    long lookup_instance(
            HelloWorldData.Mdsg instance_data);

}
