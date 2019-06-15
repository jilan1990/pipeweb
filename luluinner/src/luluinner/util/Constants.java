package luluinner.util;

public class Constants {

    public static final String OUTER_IP_KEY = "outer_ip";
    public static final String OUTER_IP_DEFAULT = "127.0.0.1";

    public static final String OUTER_MSG_PORT_KEY = "outer_msg_port";
    public static final int OUTER_MSG_PORT_DEFAULT = 5273;

    public static final String OUTER_DATA_PORT_KEY = "outer_data_port";
    public static final int OUTER_DATA_PORT_DEFAULT = 5266;

    public final static int BUFFER_LENGTH = 5 * 1024 * 1024;

    public final static long RESTART_PERIOD = 30 * 60 * 1000;
}
