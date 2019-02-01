import java.util.HashMap;
import java.util.Map;

public class SingleStatus {
    private static SingleStatus single_instance = null;
    private Map<Integer,String> Inteiro_TableStatus_ID = new HashMap<Integer, String>();
    private Map<String,String> ID_TableStatus_userIds = new HashMap<String, String>();
    private Map<String,String> ID_TableStatus_TimeBegins = new HashMap<String, String>();
    private Map<String,String> ID_TableStatus_TimeFinals = new HashMap<String, String>();
    private Map<String,Integer> ID_TableStatus_counter = new HashMap<String, Integer>();

    public int size_users;
    public int size_TimeBegins;
    public int size_Timefinal;

    // private constructor restricted to this class itself
    private SingleStatus()
    {
    }

    // static method to create instance of Singleton class
    public static SingleStatus getInstance()
    {
        if (single_instance == null)
            single_instance = new SingleStatus();

        return single_instance;
    }
    public void Put_Int_ID (int in, String ID){
        Inteiro_TableStatus_ID.put(in, ID);
    }
    public String Get_ID_by_inteiro (int in){
        String ID_new = Inteiro_TableStatus_ID.get(in);
        return ID_new;
    }
    public void Put_ID_userIds (String Id, String userIds_new) {
        ID_TableStatus_userIds.put(Id, userIds_new);
    }

    public String Get_userIds_by_id (String Id){
        String userIds_new = ID_TableStatus_userIds.get(Id);
        return userIds_new;
    }
    public void Put_ID_Timebegins(String Id, String Timebegins_new) {
        ID_TableStatus_TimeBegins.put(Id, Timebegins_new);
    }

    public String Get_Timebegins_by_id (String Id){
        String Timebegins_new = ID_TableStatus_TimeBegins.get(Id);
        return Timebegins_new;
    }

    public void Put_ID_Timefinals (String Id, String Timefinal_new) {
        ID_TableStatus_TimeFinals.put(Id, Timefinal_new);
    }

    public String Get_Timefinals_by_id (String Id){
        String Timefinals_new = ID_TableStatus_TimeBegins.get(Id);
        return Timefinals_new;
    }


    public void Put_ID_counter (String Id, int counter_new) {
        ID_TableStatus_counter.put(Id, counter_new);
    }

    public int Get_counter_by_id (String Id){
        int counter_new = ID_TableStatus_counter.get(Id);
        return counter_new;
    }

    public void Put_sizeusers(int size_new){
        this.size_users=size_new;
    }

    public int Get_sizeusers(){
        return size_users;
    }
    public void Put_Sizetimesticksinicial(int size_new){
        this.size_TimeBegins=size_new;
    }

    public int Get_Sizetimesticksinicial(){
        return size_TimeBegins;
    }
    public void Put_Sizetimesticksfinal(int size_new){
        this.size_Timefinal=size_new;
    }

    public int Get_Sizetimesticksfinal(){
        return size_Timefinal;
    }
}
