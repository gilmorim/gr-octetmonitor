import jnr.ffi.annotations.In;
import org.snmp4j.agent.mo.MOMutableTableModel;
import org.snmp4j.agent.mo.MOScalar;

import java.util.HashMap;
import java.util.Map;

public class UniversalVariables {
    // static variable single_instance of type Singleton
    private static UniversalVariables single_instance = null;
    private Map<Integer,MOTableBuilder> ID_TableImage_Builder = new HashMap<Integer, MOTableBuilder>();


    // variable of type String
    public String cms;
    public String porta;
    MOScalar ms1;
    MOScalar ms2;
    MOScalar ms3;
    MOScalar msta1;
    MOScalar msta2;
    MOScalar msta3;

    MOTableBuilder builder_s;
    MOMutableTableModel m3;
    Agent agent;
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
    public void Put_porta (String numporta){
        this.porta=numporta;
    }

    public String Get_porta (){
        return porta;
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
    public  void Put_escalar_param_3(MOScalar ms3_new){
        this.ms3 = ms3_new;
    }
    public MOScalar Get_escalar_param_3(){
        return ms3;
    }
    public  void  Put_escalar_status_1(MOScalar mstatus1_new){this.msta1=mstatus1_new;}
    public MOScalar Get_escalar_status_1 (){
        return msta1;
    }
    public  void  Put_escalar_status_2(MOScalar mstatus2_new){this.msta2=mstatus2_new;}
    public MOScalar Get_escalar_status_2 (){
        return msta2;
    }
    public  void  Put_escalar_status_3(MOScalar mstatus3_new){this.msta3=mstatus3_new;}
    public MOScalar Get_escalar_status_3 (){
        return msta3;
    }
    public void Put_Table_3 (MOMutableTableModel table){
        this.m3 = table;
    }
    public MOMutableTableModel Get_Table_3(){
        return m3;
    }
    public void Put_Table_3_Build (MOTableBuilder bui){ this.builder_s=bui; }
    public MOTableBuilder Get_Table_3_Build (){ return builder_s;}

    public void Put_Agente (Agent new_agente){
        this.agent=new_agente;
    }
    public Agent Get_Agente (){
        return agent;
    }

}
