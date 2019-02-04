import org.snmp4j.agent.mo.MOScalar;

public class UniversalVariables {
    // static variable single_instance of type Singleton
    private static UniversalVariables single_instance = null;

    // variable of type String
    public String cms;
    MOScalar ms1;
    MOScalar ms2;
    MOScalar ms3;
    // private constructor restricted to this class itself
    private UniversalVariables()
    {
    }

    // static method to create instance of Singleton class
    public static UniversalVariables getInstance()
    {
        if (single_instance == null)
            single_instance = new UniversalVariables();

        return single_instance;
    }

    public void Put_CMS (String cmss){
        this.cms=cmss;
    }

    public String Get_CMS (){
        return cms;
    }

    public void Put_escalar_param_1(MOScalar ms_new){
        this.ms1=ms_new;
    }
    public MOScalar Get_escalar_param_1 (){
        return ms1;
    }
    public  void Put_escalar_param_2(MOScalar ms2_new){
        this.ms2 = ms2_new;
    }
    public MOScalar Get_escalar_param_2 (){
        return ms2;
    }

}
