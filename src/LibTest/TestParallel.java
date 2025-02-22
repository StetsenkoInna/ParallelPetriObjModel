/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LibTest;

import static LibNet.NetLibrary.CreateNetGenerator;
import static LibNet.NetLibrary.CreateNetSMOgroup;
import static LibNet.NetLibrary.CreateNetSMOwithoutQueue;
import static LibTest.Experiment.getTimePerformance;
import static LibTest.Experiment.warmedUp;
import PetriObjParallel.ExceptionInvalidNetStructure;
import PetriObjParallel.PetriObjModel;
import PetriObjParallel.PetriP;
import PetriObjParallel.PetriSim;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Inna
 */
public class TestParallel {

    public static void main(String[] args) throws ExceptionInvalidNetStructure {

        double time = 10000;
        int totalNum = 200;
        int complThread = 4;
        int limit = 50;
        int numObj = totalNum / complThread;

        testResult(time, totalNum, totalNum / complThread, limit,2.0, 1.0);
        
        
//        warmedUp();
//        System.out.println(" algorithm time performance    " + Experiment.getTimePerformance(time, totalNum, totalNum / complThread, limit, 2.0, 1.0));

    }
    
    public static void testResult(double timeMod, int totalNum, int numObj, int limit, double tGen, double tServ) throws ExceptionInvalidNetStructure{ //02.2025
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
        
        TestParallel.printResultsForAllObj(model); //02.2025
        
    }
    

    public static void printResultsForAllObj(PetriObjModel model) {
        model.getListObj().stream().forEach((PetriSim e) -> {
            System.out.println("for SMO " + e.getName() + ":  tLocal " + e.getTimeLocal());
            if (e.getPreviousObj() != null) {
                System.out.println(" tExternalInput size " + e.getTimeExternalInput().size());
                if (!e.getTimeExternalInput().isEmpty()) {
                    System.out.println(" tExternalInput first " + e.getTimeExternalInput().get(0)
                            + ", tExternalInput last " + e.getTimeExternalInput().get(e.getTimeExternalInput().size() - 1));
                }
                for (int j = 0; j < e.getNet().getListP().length / 2; j++) {
                    System.out.println("mean queue in SMO " + e.getName() + "  " + e.getNet().getListP()[2 * j].getMean()
                            + ", current number in queue " + e.getNet().getListP()[2 * j].getMark());
                }
            }
            e.printState();
        });
    }

    public static void printResultsForLastObj(PetriObjModel model) {
        PetriSim e = model.getListObj().get(model.getListObj().size() - 1);
        System.out.println("for SMO " + e.getName() + ":  tLocal " + e.getTimeLocal() + ", tMin " + e.getTimeMin());
        if (e.getPreviousObj() != null) {
            System.out.println(" tExternalInput size " + e.getTimeExternalInput().size());
            if (!e.getTimeExternalInput().isEmpty()) {
                System.out.println(" tExternalInput first " + e.getTimeExternalInput().get(0)
                        + ", tExternalInput last " + e.getTimeExternalInput().get(e.getTimeExternalInput().size() - 1));
            }

            for (int j = 0; j < e.getNet().getListP().length / 2; j++) {
                System.out.println("mean queue in SMO " + e.getName() + "  " + e.getNet().getListP()[2 * j].getMean()
                        + ", current number in queue " + e.getNet().getListP()[2 * j].getMark());
            }
        }
        e.printState();
    }

    public static PetriObjModel getModelSMOgroupForTestParallel(int numGroups, int numInGroup, double gen, double delay) throws ExceptionInvalidNetStructure {
        ArrayList<PetriSim> list = new ArrayList<>();
        int numSMO = numGroups - 1;

        list.add(new PetriSim(CreateNetGenerator(gen)));
        for (int i = 0; i < numSMO; i++) {

            list.add(new PetriSim(CreateNetSMOgroup(numInGroup, 1, delay, "group_" + i))); //   group1,group2,group3...         
        }
        list.get(0).getNet().getListP()[1] = list.get(1).getNet().getListP()[0]; //gen = > group1
        list.get(0).addOutT(list.get(0).getNet().getListT()[0]);
        list.get(1).addInT(list.get(1).getNet().getListT()[0]);
        list.get(0).setNextObj(list.get(1));
        //  list.get(0).setAccess(true); // першим починає працювати
        list.get(1).setPreviousObj(list.get(0));
        // list.get(1).setAccess(false); //очікувати подію від попереднього об єкта
        if (numSMO > 1) {
            for (int i = 2; i <= numSMO; i++) {
                int last = list.get(i - 1).getNet().getListP().length - 1;

                //   list.get(i-1).getNet().getListP()[last] = list.get(i).getNet().getListP()[0]; //group1 = > group2, group2 = > group3,...
                list.get(i).getNet().getListP()[0] = list.get(i - 1).getNet().getListP()[last]; //group1 = > group2, group2 = > group3,...
                //  list.get(i-1).getListPositionsForStatistica().remove(list.get(i-1).getNet().getListP()[last]); // так не виходить! корегування списку позицій для статистики

                int lastT = list.get(i - 1).getNet().getListT().length - 1;
                list.get(i - 1).addOutT(list.get(i - 1).getNet().getListT()[lastT]);
                list.get(i).addInT(list.get(i).getNet().getListT()[0]);
                list.get(i - 1).setNextObj(list.get(i));
                list.get(i).setPreviousObj(list.get(i - 1));

                //  list.get(i).setAccess(false);//очікувати подію від попереднього об єкта
            }
        }

        //корегування списку розицій для статистики
        for (int i = 1; i <= numSMO; i++) {
            ArrayList<PetriP> positionForStatistics = new ArrayList<>();
            PetriP[] listP = list.get(i).getNet().getListP();
            for (int j = 0; j < listP.length - 1; j++) { //окрім останньої, наприклад
                positionForStatistics.add(listP[j]);
            }

            list.get(i).setListPositionsForStatistica(positionForStatistics);
        }

        PetriObjModel model = new PetriObjModel(list);
        return model;
    }
   // ще не тестовано ніде... 
    public static PetriObjModel getModelSMOgroupByThreadCompl(int totalNum, int complThread, double gen, double delay) throws ExceptionInvalidNetStructure {
        ArrayList<PetriSim> list = new ArrayList<>();
        int numObj = totalNum /complThread;
        int remain = totalNum % complThread;

        list.add(new PetriSim(CreateNetGenerator(gen)));
        if (remain < numObj) { // остачу можна розподілити по 1 між усіма об'єктами
            for (int i = 0; i < numObj; i++) {
                if (i < remain) {
                    list.add(new PetriSim(CreateNetSMOgroup(complThread + 1, 1, delay, "group_" + i))); //   group1,group2,group3...         
                } else {
                    list.add(new PetriSim(CreateNetSMOgroup(complThread, 1, delay, "group_" + i))); //   group1,group2,group3...         
                }

            }
        } else {
            int s = remain/numObj; // по скільки додати, щоб приблизно порівну додавати до усіх об'єктів
            for (int i = 0; i < numObj-1; i++) {
                 list.add(new PetriSim(CreateNetSMOgroup(complThread + s, 1, delay, "group_" + i))); //
            }
            list.add(new PetriSim(CreateNetSMOgroup(complThread + (remain-s*(numObj-1)), 1, delay, "group_" + numObj))); //
        }
        list.get(0).getNet().getListP()[1] = list.get(1).getNet().getListP()[0]; //gen = > group1
        list.get(0).addOutT(list.get(0).getNet().getListT()[0]);
        list.get(1).addInT(list.get(1).getNet().getListT()[0]);
        list.get(0).setNextObj(list.get(1));
        list.get(1).setPreviousObj(list.get(0));
        if (numObj > 1) {
            for (int i = 2; i <= numObj; i++) {
                int last = list.get(i - 1).getNet().getListP().length - 1;

                list.get(i).getNet().getListP()[0] = list.get(i - 1).getNet().getListP()[last]; //group1 = > group2, group2 = > group3,...             
                int lastT = list.get(i - 1).getNet().getListT().length - 1;
                list.get(i - 1).addOutT(list.get(i - 1).getNet().getListT()[lastT]);
                list.get(i).addInT(list.get(i).getNet().getListT()[0]);
                list.get(i - 1).setNextObj(list.get(i));
                list.get(i).setPreviousObj(list.get(i - 1));

            }
        }

        //корегування списку розицій для статистики
        for (int i = 1; i <= numObj; i++) {
            ArrayList<PetriP> positionForStatistics = new ArrayList<>();
            PetriP[] listP = list.get(i).getNet().getListP();
            for (int j = 0; j < listP.length - 1; j++) { //окрім останньої, наприклад
                positionForStatistics.add(listP[j]);
            }

            list.get(i).setListPositionsForStatistica(positionForStatistics);
        }

        PetriObjModel model = new PetriObjModel(list);
        return model;
    }
    
    

    public static PetriObjModel getModelSMOgroupGeneral(int totalNum, int numObj, double gen, double delay) throws ExceptionInvalidNetStructure {
        ArrayList<PetriSim> list = new ArrayList<>();
        int numInGroup = totalNum / numObj;
        int remain = totalNum % numObj;

        list.add(new PetriSim(CreateNetGenerator(gen)));

        for (int i = 0; i < numObj; i++) {
            if (i < remain) {
                list.add(new PetriSim(CreateNetSMOgroup(numInGroup + 1, 1, delay, "group_" + i))); //   group1,group2,group3...         
            } else {
                list.add(new PetriSim(CreateNetSMOgroup(numInGroup, 1, delay, "group_" + i))); //   group1,group2,group3...         
            }

        }
        list.get(0).getNet().getListP()[1] = list.get(1).getNet().getListP()[0]; //gen = > group1
        list.get(0).addOutT(list.get(0).getNet().getListT()[0]);
        list.get(1).addInT(list.get(1).getNet().getListT()[0]);
        list.get(0).setNextObj(list.get(1));
        list.get(1).setPreviousObj(list.get(0));
        if (numObj > 1) {
            for (int i = 2; i <= numObj; i++) {
                int last = list.get(i - 1).getNet().getListP().length - 1;

                list.get(i).getNet().getListP()[0] = list.get(i - 1).getNet().getListP()[last]; //group1 = > group2, group2 = > group3,...             
                int lastT = list.get(i - 1).getNet().getListT().length - 1;
                list.get(i - 1).addOutT(list.get(i - 1).getNet().getListT()[lastT]);
                list.get(i).addInT(list.get(i).getNet().getListT()[0]);
                list.get(i - 1).setNextObj(list.get(i));
                list.get(i).setPreviousObj(list.get(i - 1));

            }
        }

        //корегування списку розицій для статистики
        for (int i = 1; i <= numObj; i++) {
            ArrayList<PetriP> positionForStatistics = new ArrayList<>();
            PetriP[] listP = list.get(i).getNet().getListP();
            for (int j = 0; j < listP.length - 1; j++) { //окрім останньої, наприклад
                positionForStatistics.add(listP[j]);
            }

            list.get(i).setListPositionsForStatistica(positionForStatistics);
        }

        PetriObjModel model = new PetriObjModel(list);
        return model;
    }

    public static PetriObjModel getModelForTestParallel(int numObj) throws ExceptionInvalidNetStructure {
        ArrayList<PetriSim> list = new ArrayList<>();
        int numSMO = numObj - 1;
        list.add(new PetriSim(CreateNetGenerator(2.0)));
        for (int i = 0; i < numSMO; i++) {

            list.add(new PetriSim(CreateNetSMOwithoutQueue(1, 1.0))); //   SMO1,SMO2,SMO3...         
        }
        list.get(0).getNet().getListP()[1] = list.get(1).getNet().getListP()[0]; //gen = > SMO1
        if (numSMO > 1) {
            for (int i = 2; i <= numSMO; i++) {
                list.get(i - 1).getNet().getListP()[2] = list.get(i).getNet().getListP()[0]; //SMO1 = > SMO2, SMO2 = > SMO3,...
            }
        }
        PetriObjModel model = new PetriObjModel(list);
        return model;
    }

}
