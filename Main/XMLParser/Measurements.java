package Main.XMLParser;

import java.util.LinkedList;

/**
 * Created by tinkie101 on 2015/08/05.
 */
public class Measurements {

    private String _id;
    private String _class;
    private int _resolution;
    private LinkedList<String> _measurements;

    public Measurements(String _id, String _class, int _resolution){
        this._id = _id;
        this._class = _class;
        this._resolution = _resolution;
        this._measurements = new LinkedList<>();
    }

    public void addMeasurement(String measurement){
        _measurements.add(measurement);
    }

    public String get_id(){
        return _id;
    }

    public String get_Class(){
        return _class;
    }

    public int get_resolution(){
        return _resolution;
    }

    public LinkedList<String> get_measurements(){
        return _measurements;
    }
}
