package edu.ant_calc.oper;

import edu.ant_calc.DoubleArgOperation;

/**
 * Created by Dmitry on 21.03.2017.
 */
public class Plus implements DoubleArgOperation {
    @Override
    public double calc(double arg1, double arg2) {
        return arg1 + arg2;
    }
}
