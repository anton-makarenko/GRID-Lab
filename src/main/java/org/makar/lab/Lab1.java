package org.makar.lab;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class Lab1 {
    final static double H = 0.25;
    final static double p = 500;
    final static double pLiquid = 1000;
    final static double alpha = Math.PI / 6;
    final static double t0 = 0;
    final static double x05 = 0.05;
    final static double x10 = 0.1;
    final static double x15 = 0.15;
    final static double tMax = 10;
    final static double f = 15;
    final static double g = 9.81;
    final static double h0 = H * Math.pow(p / pLiquid, 1.0 / 3);
    final static double m = (Math.PI * p) / 3 * Math.pow(Math.tan(alpha), 2) * Math.pow(H, 3);
    final static int rounds = 500;
    final static double h = tMax / rounds;
    final static String fileName05 = "lab105.csv";
    final static String fileName10 = "lab110.csv";
    final static String fileName15 = "lab115.csv";

    public static void main(String[] args) throws IOException {
        calculate(t0, x05, 0, fileName05);
        calculate(t0, x10, 0, fileName10);
        calculate(t0, x15, 0, fileName15);
    }

    static double U(double t, double x, double z) {
        double arg1 = Math.PI * Math.pow(Math.tan(alpha), 2) * Math.pow(h0 - x, 2) / m;
        double arg2 = pLiquid * g / 3 * (h0 - x) - f / Math.sin(alpha) * z;
        return arg1 * arg2 - g;
    }

    static double[] kurt4Round(double t, double x, double z) {
        double q0 = U(t, x, z);
        double k0 = z;

        double q1 = U(t + h / 2, x + k0 * h / 2, z + q0 * h / 2);
        double k1 = z + q0 * h / 2;

        double q2 = U(t + h / 2, x + k1 * h / 2, z + q1 * h / 2);
        double k2 = z + q1 * h / 2;

        double q3 = U(t + h, x + k2 * h, z + q2 * h);
        double k3 = z + q2 * h;

        double newT = t + h;
        double newX = x + h / 6 * (k0 + 2 * k1 + 2 * k2 + k3);
        double newZ = z + h / 6 * (q0 + 2 * q2 + 2 * q2 + q3);

        return new double[] { newT, newX, newZ };
    }

    static void calculate(double t0, double x0, double z0, String fileName) throws IOException {
        double[][] storage = new double[rounds + 1][];
        double[] values = new double[]{t0, x0, z0};
        log.info("Starting calculations for t0 = {}, x0 = {}, v0 = {}", t0, x0, z0);
        for (int i = 0; i <= rounds; i++) {
            storage[i] = values;
            values = kurt4Round(values[0], values[1], values[2]);
        }
        writeToCsv(storage, fileName);
        log.info("Results are written to the file {}", new File(fileName).getAbsolutePath());
    }

    static void writeToCsv(double[][] values, String fileName) throws IOException {
        try (FileWriter writer = new FileWriter(fileName);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.EXCEL)) {
            for (double[] value : values)
                printer.printRecord(value[0], value[1], value[2]);
        }
    }
}
