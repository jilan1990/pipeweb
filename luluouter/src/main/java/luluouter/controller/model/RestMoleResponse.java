package luluouter.controller.model;

public class RestMoleResponse {

    private int coverPort;
    private String pigeonCode;
    private String pigeonAddress;
    private String moleIp;
    private int molePort;
    private boolean status;

    public int getCoverPort() {
        return coverPort;
    }

    public void setCoverPort(int coverPort) {
        this.coverPort = coverPort;
    }

    public String getPigeonCode() {
        return pigeonCode;
    }

    public void setPigeonCode(String pigeonCode) {
        this.pigeonCode = pigeonCode;
    }

    public String getPigeonAddress() {
        return pigeonAddress;
    }

    public void setPigeonAddress(String pigeonAddress) {
        this.pigeonAddress = pigeonAddress;
    }

    public String getMoleIp() {
        return moleIp;
    }

    public void setMoleIp(String moleIp) {
        this.moleIp = moleIp;
    }

    public int getMolePort() {
        return molePort;
    }

    public void setMolePort(int molePort) {
        this.molePort = molePort;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
