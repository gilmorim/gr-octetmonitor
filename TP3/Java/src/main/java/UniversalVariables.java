public class UniversalVariables {
    // static variable single_instance of type Singleton
    private static UniversalVariables single_instance = null;

    // variable of type String
    public String cms;

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
}
