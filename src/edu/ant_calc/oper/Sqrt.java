package edu.ant_calc.oper;

import edu.ant_calc.SingleArgOperation;

/**
 * Created by Dmitry on 22.03.2017.
 */
public class Sqrt implements SingleArgOperation {
    @Override
    public double calc(double agr1) {
        return Math.sqrt(agr1);
    }
}
