import java.util.HashMap;
import java.util.Map;

public class SingleTableImage {

    private static SingleTableImage single_instance = null;

    private Map<String,String> ID_TableImage_imagem = new HashMap<String, String>();
    private Map<Integer,String> Intseq_TableImgage_ID = new HashMap<Integer, String>();
    private Map<String,String> Imagem_TableImage_ID = new HashMap<String, String>();

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
        Imagem_TableImage_ID.put(Image,Id);
    }

    public String Get_Image_by_id (String Id){
        String Image_new = ID_TableImage_imagem.get(Id);
        return Image_new;
    }
    public String Get_ID_by_Image (String image_new){
        String ID_new = Imagem_TableImage_ID.get(image_new);
        return  ID_new;
    }

    public void Put_Intseq_ID(int inteiro, String Id){
        Intseq_TableImgage_ID.put(inteiro,Id);
    }
    public String Get_ID_by_inteiroseq (int inteiro){
        String ID_new = Intseq_TableImgage_ID.get(inteiro);
        return ID_new;
    }


    public void Put_size(int size_new){
        this.size=size_new;
    }

    public int Get_size(){
        return size;
    }

}
