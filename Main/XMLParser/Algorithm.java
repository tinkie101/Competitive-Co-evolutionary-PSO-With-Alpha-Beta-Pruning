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

    public double getProbability()throws Exception{
        double result = 0.0d;

        switch(_id){
            case "random1": result = 0.1d;
                    break;
            case "random3": result = 0.3d;
                break;
            case "random5": result = 0.5d;
                break;
            case "random7": result = 0.7d;
                break;
            case "random9": result = 0.9d;
                break;
        }

        return result;
    }

    public Boolean getAlphaBeta() throws Exception{
        Boolean result = false;

        switch(_id){
            case "normal": result = false;
                break;
            case "alphabeta": result = true;
                break;
            case "random1":
            case "random3":
            case "random5":
            case "random7":
            case "random9": result = null;
                break;
            default: throw new Exception("Invalid algorithm!");
        }

        return result;
    }
}
