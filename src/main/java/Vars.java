import javax.print.DocFlavor;

public class Vars {
    public static final int SUCCESS = 0;
    public static final int INVALID_IP = 1;
    public static final int INVALID_PORT = 2;
    public static final int INVALID_VERSION = 3;
    public static final int INVALID_COMMUNITY_STRING = 4;
    public static final int INVALID_TIMEOUT = 5;
    public static final int INVALID_RETRIES = 6;
    public static final int FILE_DOES_NOT_EXIST = 7;

    public static final String SUCCESS_MESSAGE = "Success reading configuration file";
    public static final String INVALID_IP_MESSAGE = "Invalid IP address";
    public static final String INVALID_PORT_MESSAGE = "Invalid port value";
    public static final String INVALID_VERSION_MESSAGE = "Invalid version value";
    public static final String INVALID_COMMUNITY_STRING_MESSAGE = "Invalid community string";
    public static final String INVALID_TIMEOUT_MESSAGE = "Invalid timeout value";
    public static final String INVALID_RETRIES_MESSAGE = "Invalid retries value";
    public static final String FILE_DOES_NOT_EXIST_MESSAGE = "Configuration file not found";

    public static final String[] LOG_MESSAGES = {
            SUCCESS_MESSAGE,
            INVALID_IP_MESSAGE,
            INVALID_PORT_MESSAGE,
            INVALID_VERSION_MESSAGE,
            INVALID_COMMUNITY_STRING_MESSAGE,
            INVALID_TIMEOUT_MESSAGE,
            INVALID_RETRIES_MESSAGE,
            FILE_DOES_NOT_EXIST_MESSAGE
    };

    public static final String  INTERFACE_COUNT_OID = "1.3.6.1.2.1.2.1";
    public static final String  INTERFACE_INDEX_OID = "1.3.6.1.2.1.2.2.1.1";
    public static final String  INTERFACE_DESCRIPTION_OID = ".1.3.6.1.2.1.2.2.1.2";
    public static final String  INTERFACE_MACADDRESS_OID = "1.3.6.1.2.1.2.2.1.6";
    public static final String  INTERFACE_STATUS_OID = "1.3.6.1.2.1.2.2.1.7";
    public static final String  INTERFACE_INCOMING_OCTETS_OID = "1.3.6.1.2.1.2.2.1.10";
    public static final String  INTERFACE_OUTGOING_OCTETS_OID = "1.3.6.1.2.1.2.2.1.16";

    public static final String[] OID_LIST ={
        INTERFACE_INDEX_OID,
        INTERFACE_DESCRIPTION_OID,
        INTERFACE_MACADDRESS_OID,
        INTERFACE_STATUS_OID,
        INTERFACE_INCOMING_OCTETS_OID,
        INTERFACE_OUTGOING_OCTETS_OID
    };

    public static final int  INDEX = 1;
    public static final int  DESCRIPTION = 2;
    public static final int  MACADDRESS = 6;
    public static final int  STATUS = 7;
    public static final int  INCOMING_OCTETS = 10;
    public static final int  OUTGOING_OCTETS = 16;


}
