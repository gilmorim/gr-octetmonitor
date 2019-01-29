import java.util.HashMap;
import java.util.Map;

public class SingleTableImage {

    private static SingleTableImage single_instance = null;

    private Map<String,String> ID_TableImage_imagem = new HashMap<String, String>();
    public int size;


    // private constructor restricted to this class itself
    private SingleTableImage()
    {
    }

    // static method to create instance of Singleton class
    public static SingleTableImage getInstance()
    {
        if (single_instance == null)
            single_instance = new SingleTableImage();

        return single_instance;
    }

    public void Put_ID_Image (String Id, String Image) {
        ID_TableImage_imagem.put(Id, Image);
    }

    public String Get_Image_by_id (String Id){
        String Image_new = ID_TableImage_imagem.get(Id);
        return Image_new;
    }

    public void Put_size(int size_new){
        this.size=size_new;
    }

    public int Get_size(){
        return size;
    }

}
