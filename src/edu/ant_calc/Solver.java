package edu.ant_calc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Dmitry on 21.03.2017.
 */
public class Solver {

    private Double tempValue;
    private Operation operation;
    private static HashMap<String,Operation> plugins;

    Solver() {
        plugins = new HashMap<>();
        tempValue = null;
    }

    void defineOperations(HashSet<String> pluginNames) throws Exception{
        Iterator it = plugins.keySet().iterator();
        while (it.hasNext()) {
            if (!pluginNames.contains(it.next()))
                it.remove();
        }
        for (String plugin : pluginNames) {
            if (!plugins.containsKey(plugin)) {
                plugins.put(plugin, getClassInstance(plugin));
            }
        }
    }

    void clear() {
        tempValue = null;
        operation = null;
    }

    boolean setOperation(String operation, Double arg) {
        this.operation = plugins.get(operation);
        this.tempValue = arg;
        return this.operation instanceof SingleArgOperation;
    }

    double calc (double arg) {
        if (operation == null) {
            return arg;
        } else if (operation instanceof SingleArgOperation) {
            double res = ((SingleArgOperation) operation).calc(arg);
            clear();
            return res;
        } else if (operation instanceof DoubleArgOperation){
            double res = ((DoubleArgOperation) operation).calc(tempValue, arg);
            clear();
            return res;
        } else return Double.NaN;
    }

    private static Operation getClassInstance(String className) throws IOException, ClassNotFoundException, IllegalAccessException,InstantiationException{
        String dir = new File(".").getAbsolutePath();
//        File myJar = new File("D:\\Java Projects\\JAR\\" + className + ".jar");
        File myJar = new File(dir.substring(0,dir.length()-1) + className + ".jar");
        URLClassLoader urlClassLoader = new URLClassLoader (new URL[]{myJar.toURI().toURL()});
        Class classToLoad = Class.forName ("edu.ant_calc.oper." + className, true, urlClassLoader);
        return (Operation) classToLoad.newInstance();
    }
}
