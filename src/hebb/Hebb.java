/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hebb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author aaugustyniak
 */
public class Hebb {

    private static final Random outerR = new Random(345);

    public static class Neuron {

        private static final Random r = new Random(3455);

        public Double[] in = {0.0, 0.0, 0.0, 0.0, 0.0};
        public Double[] w = new Double[5];

        public Neuron() {

            for (int i = 0; i < w.length; i++) {
                w[i] = r.nextDouble();
            }
        }

        public void in(Double[] input) {
            in = input;

        }

        public double weightsSum() {
            double sum = 0.0;
            for (int i = 0; i < w.length; i++) {
                sum += w[i];

                if (Double.isNaN(sum)) {
                    throw new RuntimeException("NaN kurwa");
                }

            }

            return sum;
        }

        public double inputSum() {
            double sum = 0.0;
            for (int i = 0; i < w.length; i++) {
                sum += in[i];

                if (Double.isNaN(sum)) {
                    throw new RuntimeException("NaN kurwa");
                }

            }

            return sum;
        }

        public double netForce() {
            double sum = 0.0;
            for (int i = 0; i < w.length; i++) {
                sum += in[i] * w[i];

                if (Double.isNaN(sum)) {
                    throw new RuntimeException("NaN kurwa");
                }

            }

            return sum;
        }

        public double out() {
            return Math.tanh(netForce());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        /**
         * fly, swim, hair, beak, egg mammal fish bird
         */
        Double[][] tSet = {
            {-1.0, -1.0, 1.0, -1.0, -1.0},
            {-1.0, 1.0, -1.0, -1.0, 0.55},
            {1.0, -1.0, -1.0, 1.0, 1.0}
        };

        Double[][] expectedSet = {
            {1.0, -1.0, -1.0},
            {-1.0, 1.0, -1.0},
            {-1.0, -1.0, 1.0}
        };

        /**
         * outputs mammal fish, bird
         */
        Neuron[] net = {new Neuron(), new Neuron(), new Neuron()};

        for (int k = 0; k < 40; k++) {
            int i = outerR.nextInt(tSet.length);
            netInput(net, tSet[i]);
            //deltaRule(net, expectedSet[i]);
            hebbanRule(net);

        }

        Double[] platypus = {-1.0, -1.0, 1.0, 0.9, 0.2};

        netInput(net, platypus);
        //netInput(net, tSet[0]);
        System.out.println(netResp(net));
        System.out.println(interpret(netResp(net)));

    }

    public static void netInput(Neuron[] net, Double[] input) {
        for (Neuron net1 : net) {
            net1.in(input);
        }

    }

    private static void hebbanRule(Neuron[] net) {
        int p = 2;
        for (int j = 0; j < net.length; j++) {

            double sub = 0.0;

            for (int k = 0; k < net[j].in.length; k++) {
                /**
                 * Oja
                 */

                sub += Math.pow(net[j].w[k] + 0.3 * net[j].out() * net[j].in[k], p);

            }
            for (int i = 0; i < net[j].in.length; i++) {
                /**
                 * Oja
                 */
                net[j].w[i] = (net[j].w[i] + 0.3 * net[j].out() * net[j].in[i]) / Math.pow(sub, 1.0 / p);

            }
        }

    }

    public static void deltaRule(Neuron[] net, Double[] expectedOut) {

        for (int j = 0; j < net.length; j++) {
            Double neuronError = Math.pow(net[j].out() - expectedOut[j], 2) / 2.0;
            for (int i = 0; i < net[j].in.length; i++) {
                Double delta = neuronError * dtanh(net[j].netForce()) * net[j].in[i];
                if (neuronError > 0.001) {
                    net[j].w[i] += 0.3 * delta;
                }
            }
        }
    }

    public static double dtanh(double x) {
        return 1.0 - Math.pow(Math.tanh(x), 2);
    }

    public static String interpret(List<Double> resp) {

        Map<String, Double> d = new HashMap<>();
        d.put("mammal", resp.get(0));
        d.put("fish", resp.get(1));
        d.put("bird", resp.get(2));

        Map.Entry<String, Double> maxEntry = null;

        for (Map.Entry<String, Double> entry : d.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }

        return maxEntry.getKey();
    }

    public static List<Double> netResp(Neuron[] net) {

        Double[] resp = new Double[net.length];
        for (int j = 0; j < net.length; j++) {
            resp[j] = net[j].out();
        }
        return Arrays.asList(resp);
    }

}
