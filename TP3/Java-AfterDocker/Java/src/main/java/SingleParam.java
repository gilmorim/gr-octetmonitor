import java.util.HashMap;
import java.util.Map;

public class SingleParam {
    // static variable single_instance of type Singleton
    private static SingleParam single_instance = null;


    // variable of type String
    public String indexp_stored;
    public String indImagep_stored;
    public String flagp_stored;
    int id_CONT_criar;
    // private constructor restricted to this class itself
    private SingleParam()
    {
    }

    // static method to create instance of Singleton class
    public static SingleParam getInstance()
    {
        if (single_instance == null)
            single_instance = new SingleParam();

        return single_instance;
    }

    public void Put_Indexp (String indexp){
        this.indexp_stored=indexp;    }

    public String Get_Indexp(){
      return indexp_stored;
    }
    public void Put_indImagep (String indImagep){
        this.indImagep_stored=indImagep;    }
    public String Get_indImagep(){
        return indImagep_stored;
    }

    public void Put_flagp(String flagp){
        this.flagp_stored=flagp;    }
    public String Get_flagp(){
        return flagp_stored;
    }

    public  void Put_id_snmpset(int id){
        this.id_CONT_criar=id;
    }
    public int Get_id_snmpset_param(){
        return id_CONT_criar;
    }
}
