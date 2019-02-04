import java.util.HashMap;
import java.util.Map;

public class SingleCointainerTable {
    private static SingleCointainerTable single_instance = null;
    public int size;

    private Map<Integer,String> ID_CointainerTable_Index = new HashMap<Integer, String>();
    private Map<String,String> ID_CointainerTable_Name = new HashMap<String, String>();
    private Map<String,String> ID_CointainerTable_Image = new HashMap<String, String>();
    private Map<String,String> ID_CointainerTable_Status = new HashMap<String, String>();
    private Map<String,String> ID_CointainerTable_Processor = new HashMap<String, String>();


    // private constructor restricted to this class itself
    private SingleCointainerTable()
    {
    }

    // static method to create instance of Singleton class
    public static SingleCointainerTable getInstance()
    {
        if (single_instance == null)
            single_instance = new SingleCointainerTable();

        return single_instance;
    }

    public void Put_ID_CointainerTable_Index(int ID, String Index){
        ID_CointainerTable_Index.put(ID,Index);
    }
    public String Get_Index_by_the_ID(int ID){
        return ID_CointainerTable_Index.get(ID);
    }
    public void Put_ID_CointainerTable_Name(String ID, String Name){
        ID_CointainerTable_Name.put(ID,Name);
    }
    public String Get_Name_by_ID(String ID){
        return ID_CointainerTable_Name.get(ID);
    }
    public void Put_ID_CointainerTable_Image(String ID, String Image){
        ID_CointainerTable_Image.put(ID,Image);
    }
    public String Get_Image_by_ID(String ID){
        return ID_CointainerTable_Image.get(ID);
    }
    public void Put_ID_CointainerTable_Status(String ID, String Status){
        ID_CointainerTable_Status.put(ID,Status);
    }
    public String Get_Status_by_ID(String ID){
        return ID_CointainerTable_Status.get(ID);
    }
    public void Put_ID_CointainerTable_Processor(String ID, String Processor){
        ID_CointainerTable_Processor.put(ID,Processor);
    }
    public String Get_Processor_by_ID(String ID){
        return ID_CointainerTable_Processor.get(ID);
    }
    public void Put_size(int size_new){
        this.size=size_new;
    }

    public int Get_size(){
        return size;
    }
}
