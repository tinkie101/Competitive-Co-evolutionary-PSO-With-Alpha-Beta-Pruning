package Main.XMLParser;

/**
 * Created by tinkie101 on 2015/08/05.
 */
public class Algorithm {
    private String _id;
    private String _class;

    public Algorithm(String _id, String _class){
        this._id = _id;
        this._class = _class;
    }

    public String get_Id(){
        return _id;
    }

    public String get_Class(){
        return _class;
    }
}
