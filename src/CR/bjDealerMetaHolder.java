package CR;

public final class bjDealerMetaHolder
{

    public static java.lang.String metaDescriptor[] = { "<MetaData version=\"1.0.0\"><Module name=\"CR\"><Struct name=\"card\"><Member name=\"suite\"><Char/></Member><Member name=\"base_value\"><Char/></Member><Member name=\"visible\"><Boolean/></Member></Struct><Enum name=\"bjd_action\"><Element name=\"shuffling\" value=\"0\"/><Element name=\"waiting\" value=\"1\"/><Element name=\"dealing\" value=\"2\"/><Element name=\"collecting\" value=\"3\"/><Element name=\"paying\" value=\"4\"/></Enum><Struct name=\"player_status\"><Member name=\"uuid\"><Long/></Member><Member name=\"wager\"><Long/></Member><Member name=\"cards\"><Array size=\"21\"><Type name=\"card\"/></Array></Member></Struct><Struct name=\"bjDealer\"><Member name=\"uuid\"><Long/></Member><Member name=\"seqno\"><Long/></Member><Member name=\"active_players\"><Long/></Member><Member name=\"players\"><Array size=\"6\"><Type name=\"player_status\"/></Array></Member><Member name=\"action\"><Type name=\"bjd_action\"/></Member><Member name=\"cards\"><Array size=\"21\"><Type name=\"card\"/></Array></Member><Member name=\"target_uuid\"><Long/></Member></Struct></Module></MetaData>" };

    public bjDealerMetaHolder()
    {
    }

}
