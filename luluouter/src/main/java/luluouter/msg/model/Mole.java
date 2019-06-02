package luluouter.msg.model;


public class Mole {
    private final Pigeon pigeon;
    private final int coverPort;
    private final String ip;
    private final int port;

    public Mole(Pigeon pigeon, int coverPort, String ip, int port) {
        this.pigeon = pigeon;
        this.coverPort = coverPort;
        this.ip = ip;
        this.port = port;
    }

    public Pigeon getPigeon() {
        return pigeon;
    }

    public int getCoverPort() {
        return coverPort;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + coverPort;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + ((pigeon == null) ? 0 : pigeon.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Mole other = (Mole) obj;
        if (coverPort != other.coverPort)
            return false;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (pigeon == null) {
            if (other.pigeon != null)
                return false;
        } else if (!pigeon.equals(other.pigeon))
            return false;
        if (port != other.port)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Mole [pigeon=" + pigeon + ", coverPort=" + coverPort + ", ip=" + ip + ", port=" + port + "]";
    }
}
