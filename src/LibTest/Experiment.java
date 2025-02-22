/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LibTest;

import static LibTest.TestParallel.*;
import PetriObjParallel.ExceptionInvalidNetStructure;
import PetriObjParallel.PetriObjModel;
import PetriObjParallel.PetriSim;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

/**
 *
 * @author innastetsenko
 */
public class Experiment extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        ArrayList<XYChart.Series> series = new ArrayList<>();

        warmedUp(); // розігрів
        series.add(getSeriesComplThreadAndLimitImpact(10000, 400, 2.0, 1.0, 3, 20)); 
        series.add(getSeriesComplThreadAndLimitImpact(10000, 400, 2.0, 1.0, 10, 20));
        series.add(getSeriesComplThreadAndLimitImpact(10000, 400, 2.0, 1.0, 100, 20));
        series.add(getSeriesComplThreadAndLimitImpact(10000, 400, 2.0, 1.0, 1000, 20));
          
//        series.add(getSeriesNumThreadsAndLimitImpact(10000, 400, 2.0, 1.0, 3, 4));
//        series.add(getSeriesNumThreadsAndLimitImpact(10000, 400, 2.0, 1.0, 10, 4));
//        series.add(getSeriesNumThreadsAndLimitImpact(10000, 400, 2.0, 1.0, 100, 4));
//        series.add(getSeriesComplThreadAndLimitImpact(10000, 400, 2.0, 1.0, 1000, 4));
//        series.add(getSeriesComplThreadAndLimitImpact(10000, 400, 2.0, 1.0, 10000, 4));
        createScene(stage, "Complexity of thread impact", "Events per thread", "Performance time, ms", series);
//         createScene(stage, "Complexity of thread impact", "Number of threads", "Performance time, ms", series);
        
// complexity of threads impact
//
//        series.add(getSeriesComplThreadImpact(10000, 200, 2.0, 1.0, 4));
//        series.add(getSeriesComplThreadImpact(10000, 400, 2.0, 1.0, 4));
//        series.add(getSeriesComplThreadImpact(10000, 800, 2.0, 1.0, 4));
//        series.add(getSeriesComplThreadImpact(10000, 1000, 2.0, 1.0, 4));
//        createScene(stage, "Complexity of thread impact", "Events per thread", "Performance time, ms", series);

// number of threads impact      
//        series.add(getSeriesNumThreadsImpact(10000, 200, 2.0, 1.0, 4));
//                series.add(getSeriesNumThreadsImpact(10000, 400, 2.0, 1.0, 4));
//                        series.add(getSeriesNumThreadsImpact(10000, 800, 2.0, 1.0, 4));
//                        series.add(getSeriesNumThreadsImpact(10000, 1000, 2.0, 1.0, 4));
//////      
////  //  series.add(getSeriesNumThreadsImpact(10000, 200, 1.0, 1.0, 4)); // зсувається на 20 оптимум
//       createScene(stage, "Number of threads impact", "Number of threads", "Performance time", series);
        // limit buffer
//           series.add(getSeriesLimitImpact(10000, 200, 2.0, 1.0, 4));
//           series.add(getSeriesLimitImpact(10000, 400, 2.0, 1.0, 4));
//           series.add(getSeriesLimitImpact(10000, 800, 2.0, 1.0, 4));
//           series.add(getSeriesLimitImpact(10000, 1000, 2.0, 1.0, 4));
//
//           createScene(stage, "Limit buffer of external events impact", "Limit", "Performance time", series);
//             
        // formula
//             series = getSeriesFormula();
//        createScene(stage, "Thread complexity impact", "Events per thread", "Computational complexity", series);
        // experiment
        // series = getSeriesAlgPerformance();
//        createScene(stage, "Thread complexity impact", "Events per thread", "Performance time, ms", series);
    
    
  }

    public Scene createScene(Stage stage, String title, String xLabel, String yLabel, ArrayList<XYChart.Series> series) {
        stage.setTitle(title);
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xLabel);// "Events per obj");
        yAxis.setLabel(yLabel);//"Performance time");
        //creating the chart
        final LineChart<Number, Number> lineChart
                = new LineChart<>(xAxis, yAxis);
        for (XYChart.Series s : series) {
            lineChart.getData().add(s);
        }
        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.show();
        return scene;
    }

    public XYChart.Series getSeriesLimitImpact(double timeMod, int totalComplexity, double tGen, double tServ, int progon) throws ExceptionInvalidNetStructure {

        XYChart.Series seriesA = new XYChart.Series();
        seriesA.setName("Total comlexity = " + totalComplexity);

        double[] x = {2, 5, 10, 20, 50, 100, 500, 1000};
        int length = x.length;
        double[] y = new double[length];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < progon; j++) {
                y[i] += getTimePerformance(timeMod, totalComplexity, 20, (int) x[i], tGen, tServ); // numObj=20
            }
        }
        for (int i = length - 1; i >= 0; i--) { // у зворотному порядку, щоб гарантувати позбавлення ефекту розігріву
            for (int j = 0; j < progon; j++) { // 
                y[i] += getTimePerformance(timeMod, totalComplexity, 20, (int) x[i], tGen, tServ); // numObj=20
            }
        }

        for (int i = 0; i < length; i++) {
            y[i] = y[i] / 2 / progon;
        }

        System.out.println("x " + "\t "
                + " y");
        for (int j = 0; j < x.length; j++) {
            System.out.println((int) x[j] + "\t"
                    + y[j]);
        }

        for (int j = 0; j < x.length; j++) {

            seriesA.getData().add(new XYChart.Data(x[j], y[j]));

        }
        return seriesA;
    }

    public XYChart.Series getSeriesComplThreadImpact(double timeMod, int totalComplexity, double tGen, double tServ, int progon) throws ExceptionInvalidNetStructure {

        XYChart.Series seriesA = new XYChart.Series();
        int limit = 200;
        seriesA.setName("Total comlexity = " + totalComplexity);
        double[] x ={5, 6, 7, 8, 9, 10, 11, 12, 15, 17};// {2, 3, 4, 5, 6,7,8,9,10, 11, 12}; //  //{5, 10, 20, 50, 100};// complThread
 PetriObjModel model;
        int length = x.length;
        double[] y = new double[length];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < progon; j++) {
                model = TestParallel.getModelSMOgroupByThreadCompl(totalComplexity, (int) x[i], 2.0, 1.0);  //[0]-thread complexity
                y[i] += getTimePerformance(model, 10000, limit); // 10000 was in previous expriments
              //  y[i] += getTimePerformance(timeMod, totalComplexity, totalComplexity / (int) x[i], limit, tGen, tServ); //[0]-thread complexity

            }
        }
         for (int i = length - 1; i >= 0; i--) { // у зворотному порядку, щоб гарантувати позбавлення ефекту розігріву
            for (int j = 0; j < progon; j++) { // 
                 model = TestParallel.getModelSMOgroupByThreadCompl(totalComplexity, (int) x[i], 2.0, 1.0);  //[0]-thread complexity
                y[i] += getTimePerformance(model, 10000, limit); // 10000 was in previous expriments
//                y[i] += getTimePerformance(timeMod, totalComplexity, totalComplexity / (int) x[i],limit, tGen, tServ);

            }
        }
           for (int i = 0; i < length; i++) {
            y[i] = y[i] / 2 / progon;
        }

        System.out.println("x " + "\t "
                + " y");
        for (int j = 0; j < x.length; j++) {
            System.out.println((int) x[j] + "\t"
                    + y[j]);
        }

        for (int j = 0; j < x.length; j++) {

            seriesA.getData().add(new XYChart.Data(x[j], y[j]));

        }
        return seriesA;
    }
    
    public XYChart.Series getSeriesNumThreadsAndLimitImpact(double timeMod, int totalComplexity, double tGen, double tServ, int limit, int progon) throws ExceptionInvalidNetStructure {

        XYChart.Series seriesA = new XYChart.Series();
        seriesA.setName("Limit Buffer = " + limit);
        double[] x ={2, 3, 4, 5, 6, 7, 8, 9, 11, 13, 15, 17};//  number of threads
 PetriObjModel model;
        int length = x.length;
        double[] y = new double[length];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < progon; j++) {
                y[i] += getTimePerformance(timeMod, totalComplexity, (int) x[i], limit, tGen, tServ); //[0]-thread complexity

            }
        }
         for (int i = length - 1; i >= 0; i--) { // у зворотному порядку, щоб гарантувати позбавлення ефекту розігріву
            for (int j = 0; j < progon; j++) { // 
              y[i] += getTimePerformance(timeMod, totalComplexity, (int) x[i],limit, tGen, tServ);

            }
        }
           for (int i = 0; i < length; i++) {
            y[i] = y[i] / 2 / progon;
        }

        System.out.println("x " + "\t "
                + " y");
        for (int j = 0; j < x.length; j++) {
            System.out.println((int) x[j] + "\t"
                    + y[j]);
        }

        for (int j = 0; j < x.length; j++) {

            seriesA.getData().add(new XYChart.Data(x[j], y[j]));

        }
        return seriesA;
    }
    
    

    public XYChart.Series getSeriesComplThreadAndLimitImpact(double timeMod, int totalComplexity, double tGen, double tServ, int limit, int progon) throws ExceptionInvalidNetStructure {

        XYChart.Series seriesA = new XYChart.Series();
        seriesA.setName("Limit Buffer = " + limit);
        double[] x ={3, 4, 6, 8, 10, 12, 15};// {2, 3, 4, 5, 6,7,8,9,10, 11, 12}; //  //{5, 10, 20, 50, 100};// complThread
 PetriObjModel model;
        int length = x.length;
        double[] y = new double[length];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < progon; j++) {
                model = TestParallel.getModelSMOgroupByThreadCompl(totalComplexity, (int) x[i], 2.0, 1.0);  //[0]-thread complexity
                y[i] += getTimePerformance(model, 10000, limit); // 10000 was in previous expriments
//                y[i] += getTimePerformance(timeMod, totalComplexity, totalComplexity / (int) x[i], limit, tGen, tServ); //[0]-thread complexity

            }
        }
         for (int i = length - 1; i >= 0; i--) { // у зворотному порядку, щоб гарантувати позбавлення ефекту розігріву
            for (int j = 0; j < progon; j++) { // 
                 model = TestParallel.getModelSMOgroupByThreadCompl(totalComplexity, (int) x[i], 2.0, 1.0);  //[0]-thread complexity
                y[i] += getTimePerformance(model, 10000, limit); // 10000 was in previous expriments
//              y[i] += getTimePerformance(timeMod, totalComplexity, totalComplexity / (int) x[i],limit, tGen, tServ);

            }
        }
           for (int i = 0; i < length; i++) {
            y[i] = y[i] / 2 / progon;
        }

        System.out.println("x " + "\t "
                + " y");
        for (int j = 0; j < x.length; j++) {
            System.out.println((int) x[j] + "\t"
                    + y[j]);
        }

        for (int j = 0; j < x.length; j++) {

            seriesA.getData().add(new XYChart.Data(x[j], y[j]));

        }
        return seriesA;
    }
    
    public XYChart.Series getSeriesNumThreadsImpact(double timeMod, int totalComplexity, double tGen, double tServ, int progon) throws ExceptionInvalidNetStructure {

        XYChart.Series seriesA = new XYChart.Series();
        seriesA.setName("Total comlexity = " + totalComplexity);
        double[] x = {4, 5, 10, 15, 20, 25, 50, 75, 100};//{5, 10, 20, 50, 100}; // numObj
//      double[] x =  {4,5,6,7,8,9,10};
        int length = x.length;
        double[] y = new double[length];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < progon; j++) {
                y[i] += getTimePerformance(timeMod, totalComplexity, (int) x[i], 3, tGen, tServ); // limit=3

            }
        }
        for (int i = length - 1; i >= 0; i--) { // у зворотному порядку, щоб гарантувати позбавлення ефекту розігріву
            for (int j = 0; j < progon; j++) { // 
                y[i] += getTimePerformance(timeMod, totalComplexity, (int) x[i], 3, tGen, tServ); // limit=3

            }
        }
        for (int i = 0; i < length; i++) {
            y[i] = y[i] / 2 / progon;
        }

        System.out.println("x " + "\t "
                + " y");
        for (int j = 0; j < x.length; j++) {
            System.out.println((int) x[j] + "\t"
                    + y[j]);
        }

        for (int j = 0; j < x.length; j++) {

            seriesA.getData().add(new XYChart.Data(x[j], y[j]));

        }
        return seriesA;
    }

    public ArrayList<XYChart.Series> getSeriesXY() {
        ArrayList<XYChart.Series> series = new ArrayList<>();
        double[] x = {5, 10, 20, 50, 100};
        double[][] y = {{12536.023000605053, 5410.805000196245, 6338.125000231299, 11850.708000559118, 21869.878001123183},
        {8152.191000318455, 3085.502000048997, 3277.6260000423276, 6199.187000195732, 11327.869000514353},
        {5755.442000161724, 1976.8089999914228, 1792.7689999894615, 3260.5920000314595, 11269.660000510723},
        {4761.625000095421, 1425.4089999955063, 1025.2419999953727, 3256.690000030698, 11326.357000509508}

        };

        int[] cores = {2, 4, 8, 16};
        XYChart.Series seriesA;
        for (int i = 0; i < 4; i++) {
            seriesA = new XYChart.Series();
            seriesA.setName("Cores:" + cores[i]);

            for (int j = 0; j < x.length; j++) {

                seriesA.getData().add(new XYChart.Data(x[j], y[i][j]));

            }
            series.add(seriesA);
        }

        return series;
    }

    public ArrayList<XYChart.Series> getSeriesAlgPerformance() {
        ArrayList<XYChart.Series> series = new ArrayList<>();
        double[] x = {2, 5, 10, 50, 100};
        // experimental results in case 2 core
        double[][] y = {{2339.25, 1184.75, 806, 2391.5, 5625.75},
        {2712, 1450.5, 1367, 3940.75, 8434.75},
        {6291.25, 2978, 2711.25, 6996.25, 14688.5},
        {8318.75, 3710.5, 3287.25, 8644.5, 17390.25}

        };

        int[] complexity = {200, 400, 800, 1000};
        XYChart.Series seriesA;
        for (int i = 0; i < 4; i++) {
            seriesA = new XYChart.Series();
            seriesA.setName("The total number of events: " + complexity[i]);

            for (int j = 0; j < x.length; j++) {

                seriesA.getData().add(new XYChart.Data(x[j], y[i][j]));

            }
            series.add(seriesA);
        }

        return series;
    }
    
    public static long getTimePerformance(PetriObjModel model, double timeMod, int limitBuffer) throws ExceptionInvalidNetStructure {
      
//      model = getModelSMOgroupForTestParallel(numObj+1,totalNum/(numObj), tGen, tServ); // для випадку цілого ділення на кількість об'єктів

        model.setTimeMod(timeMod);
        PetriSim.setLimitArrayExtInputs(limitBuffer); // після вдосконалення стало працювати для будь-якого великого ліміту подій 

        long startTime = System.nanoTime(); // в замір часу входить тільки час виконання, створення моделі НЕ входить  
        ArrayList<Thread> threads = new ArrayList<>();
        model.getListObj().forEach((PetriSim e) -> {  // as model.goParallel()

            Thread petriObj = new Thread(e);

            threads.add(petriObj);

            petriObj.start();

        });
        threads.forEach((thread) -> {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(TestParallel.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        long stopTime = System.nanoTime();
        //   System.out.println("Total execution time: " + ((stopTime - startTime) / 1_000_000) + " ms.");
        return (stopTime - startTime) / 1_000_000; // timeMod in ms
    }

    
    
    public static long getTimePerformance(double timeMod, int totalNum, int numObj, int limit, double tGen, double tServ) throws ExceptionInvalidNetStructure {
        PetriObjModel model = getModelSMOgroupGeneral(totalNum, numObj, tGen, tServ); // частка від ділення додається по одному у перші групи
    
//      model = getModelSMOgroupForTestParallel(numObj+1,totalNum/(numObj), tGen, tServ); // для випадку цілого ділення на кількість об'єктів

        model.setTimeMod(timeMod);
        PetriSim.setLimitArrayExtInputs(limit);  // при 30 теж спрацьовує

        long startTime = System.nanoTime(); // в замір часу входить тільки час виконання, створення моделі НЕ входить  
        ArrayList<Thread> threads = new ArrayList<>();
        model.getListObj().forEach((PetriSim e) -> {  // as model.goParallel()

            Thread petriObj = new Thread(e);

            threads.add(petriObj);

            petriObj.start();

        });
        threads.forEach((thread) -> {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(TestParallel.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        long stopTime = System.nanoTime();
        //   System.out.println("Total execution time: " + ((stopTime - startTime) / 1_000_000) + " ms.");
        return (stopTime - startTime) / 1_000_000; // timeMod in ms
    }

    public static void warmedUp() {
        double w = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                try {
                    w += getTimePerformance(10000, 200, 20, 100, 2.0, 1.0); // numObj=20
                } catch (ExceptionInvalidNetStructure ex) {
                    Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            w = w / 4;
        }
    }

    public static void main(String[] args) {
        launch("null");
    }

}
