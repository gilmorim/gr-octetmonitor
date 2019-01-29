public class SingleCointainer {
    private static SingleCointainer single_instance = null;


    // variable of type String
    public String indexc_stored;
    public String namec_stored;
    public String imagec_stored;
    public String statusc_stored;
    public String procesorc_stored;
    // private constructor restricted to this class itself
    private SingleCointainer()
    {
    }

    // static method to create instance of Singleton class
    public static SingleCointainer getInstance()
    {
        if (single_instance == null)
            single_instance = new SingleCointainer();

        return single_instance;
    }

    public void Put_Indexc (String indexc){
        this.indexc_stored=indexc;    }

    public String Get_Indexc(){
        return indexc_stored;
    }
    public void Put_namec (String namec){
        this.namec_stored=namec;    }

    public String Get_namec(){
        return namec_stored;
    }

    public void Put_imagec (String imagec){
        this.imagec_stored=imagec;   }

    public String Get_imagec(){
        return imagec_stored;
    }

    public void Put_statusc (String statusc){
        this.statusc_stored=statusc;   }

    public String Get_statuscc(){
        return statusc_stored;
    }

    public void Put_procesorc (String procesorc){
        this.procesorc_stored=procesorc;   }

    public String Get_procesorc(){
        return procesorc_stored;
    }


}
