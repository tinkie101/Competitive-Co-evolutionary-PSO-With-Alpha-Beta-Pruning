package Main.XMLParser;

/**
 * Created by tinkie101 on 2015/08/05.
 */
public class Simulation {
    private int _samples;
    private Algorithm _algorithm;
    private Problem _problem;
    private Measurements _measurements;
    private String _output;

    public Simulation(int _samples, Algorithm _algorithm, Problem _problem, Measurements _measurements, String _output){
        this._samples = _samples;
        this._algorithm = _algorithm;
        this._problem = _problem;
        this._measurements = _measurements;
        this._output = _output;
    }

    public int get_samples(){
        return _samples;
    }

    public Algorithm get_algorithm(){
        return _algorithm;
    }

    public Problem get_problem(){
        return _problem;
    }

    public Measurements get_measurements(){
        return _measurements;
    }

    public String get_output(){
        return _output;
    }
}
