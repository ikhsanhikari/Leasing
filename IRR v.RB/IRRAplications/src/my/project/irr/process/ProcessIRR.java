/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.project.irr.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author junjun
 */
public class ProcessIRR {

    Map<Integer, ArrayList<Double>> mapList = new HashMap<>();
    Map<Integer, Double> mapNPV = new HashMap<>();
    double hargaTunai;
    double dp;
    int angsuran;
    double cicilan;

    public ProcessIRR(double hargaTunai, double dp, int angsuran, double cicilan) {
        this.hargaTunai = hargaTunai;
        this.dp = dp;
        this.angsuran = angsuran;
        this.cicilan = cicilan;
    }
    
    public void hitungIRR() {
        double hargaPokok = hargaTunai - dp;
        double npv = 1;
        int bunga = 5;
        while (npv > 0) {
            ArrayList<Double> list = new ArrayList<>();
            double sumOfPV = 0;
            for (int i = 0; i < angsuran; i++) {
                double pv = getPV(bunga / 100.0, i + 1);
                sumOfPV += pv;
                list.add(pv);
            }
            npv = sumOfPV - hargaPokok;
            list.add(sumOfPV);
            list.add(hargaPokok);
            list.add(npv);
            mapList.put(bunga, list);
            mapNPV.put(bunga, npv);
            if (bunga == 5) {
                bunga += 5;
            } else {
                bunga += 10;
            }
        }
    }
    
    private double getPV(double bunga, int angsuran) {
        double pembagi = Math.pow(1 + (bunga / 12.0), angsuran);
        return Math.round(cicilan / pembagi);
    }

    public Map<Integer, ArrayList<Double>> getHasil() {
        return mapList;
    }

    public Map<Integer, Double> getSummary() {
        return mapNPV;
    }

    public int getAngsuran() {
        return angsuran;
    }

    public double getHargaTunai() {
        return hargaTunai;
    }

    public double getDP() {
        return dp;
    }
    
    public double getCicilan(){
        return cicilan;
    }
    
}
