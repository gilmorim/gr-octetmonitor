import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigFileParser {
    String ipAddress;
    int port;
    int version;
    String communityString;
    int timeout;
    int retries;

    public ConfigFileParser(){
        ipAddress = "";
        port = 0;
        version = 0;
        communityString = "";
        timeout = 0;
        retries = 0;
    }

    public String getIpAddress() {
        return ipAddress;
    }
    public int getPort() {
        return port;
    }

    public int getVersion() {
        return version;
    }

    public String getCommunityString() {
        return communityString;
    }


    public int getTimeout() {
        return timeout;
    }

    public int getRetries() {
        return retries;
    }

    public int openConfigFile() throws IOException {
        String fileDirectory = "./src/main/resources/target.cfg";
        File file = new File(fileDirectory);
        int readStatus = 0;
        if(!file.exists()){
            return Vars.FILE_DOES_NOT_EXIST;
        }else {
            readStatus = readConfigFile(file);
        }
        return readStatus;
    }

    private int readConfigFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int status = 0;

        while((line = reader.readLine()) != null) {
            if(!line.startsWith("#") || !line.startsWith("\n")){
                status = parseLine(line);
                if(status != Vars.SUCCESS)
                    return status;
            }
        }

        return status;
    }

    private int parseLine(String line) {
        String[] words = line.split(":");
        int status = 0;

        switch (words[0]){
            case "IP" :
                if(validateIp(words[1]) == true) {
                    ipAddress = words[1];
                    status = Vars.SUCCESS;
                }else {
                    status = Vars.INVALID_IP;
                }
                break;
            case "PT" :
                int portFromFile = Integer.parseInt(words[1]);
                if(portFromFile < 0)
                    status = Vars.INVALID_PORT;
                else{
                    port = portFromFile;
                    status = Vars.SUCCESS;
                }
                break;
            case "VE" :
                int versionFromFile = Integer.parseInt(words[1]);
                if(versionFromFile < 1 || versionFromFile > 3)
                    status = Vars.INVALID_VERSION;
                else {
                    version = versionFromFile;
                    status = Vars.SUCCESS;
                }
                break;
            case "CS" :
                communityString = words[1];
                break;
            case "TO" :
                int timeoutFromFile = Integer.parseInt(words[1]);
                if(timeoutFromFile < 0)
                    status = Vars.INVALID_TIMEOUT;
                else {
                    timeout = timeoutFromFile;
                    status = Vars.SUCCESS;
                }
                break;
            case "RT" :
                int retriesFromFile = Integer.parseInt(words[1]);
                if(retriesFromFile < 1)
                    status = Vars.INVALID_RETRIES;
                else {
                    retries = retriesFromFile;
                    status = Vars.SUCCESS;
                }
                break;
            default:
                break;
        }

        return status;
    }

    private boolean validateIp(String ipAddress){
        Pattern pattern = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.find();
    }

    public String toString(){
        return "IP Address + " + ipAddress
                + " \nPort: " + port
                + " \nVersion: " + version
                + " \nCommunity String: " + communityString
                + " \nTimeout: " + timeout
                + " \nRetries: " + retries;
    }
}
