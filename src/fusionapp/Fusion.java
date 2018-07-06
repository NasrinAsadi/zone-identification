/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusionapp;

/**
 *
 * @author ZeytoonCo
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Range;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.AddID;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToNominal;


/**
 *
 * @author ZeytoonCo
 */
public class Fusion {
    

    String forPrediction = null;

    public Fusion() {
    }

    @SuppressWarnings("empty-statement")
    public String train(String filename, Classifier classifier) throws FileNotFoundException, Exception {
if(!filename.endsWith("arff")){
    System.out.println("--------------------input data should be a file with format ARFF---------------------");
    return "";
}
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        Instances data = new Instances(reader);

        reader.close();

        data.setClassIndex(data.numAttributes() - 1);
         System.out.println("................................Sentence Based Classification.........................");
    // String text =  firstCrossValidation(classifier, data, 1);
        System.out.println("Done.........................");
         DecreaseFusionCrossValidationSum("D:\\pred", data, 1,3);
          System.out.println("................................Window with Increasing size (Majority Voting).........................");
    //    IncreaseFusionCrossValidation(  "D:\\pred", data,2,0);
   /*      System.out.println("................................Window with Decreasing size (Sum rule).........................");
        DecreaseFusionCrossValidation("D:\\pred", data, 1,3);
         System.out.println("................................Window with Fixed size (Majority Voting).........................");
        FixedFusionCrossValidation("D:\\pred", data, 3);
    */
 //   return text;
   return null;
    }


    public String DecreaseFusionCrossValidationMV(String filename, Instances data, int r, int windowSize) {
        FileReader fr = null;
        try {
            Object[] objs = (Object[]) ReadObjectFromFile(filename);
            fr = new FileReader("Eval.txt");
            BufferedReader br = new BufferedReader(fr);
            String str = br.readLine();//forprinting
            str = str.substring(str.indexOf("\n") + 1);
          
            double[][] window = new double[windowSize][data.numClasses() + 3];

            double[] prediction = null;
            int[] listID = new int[data.numInstances()];
            double predict, actual;
            for (int i = 0; i < data.numInstances(); i++) {
                str = br.readLine();
                prediction = getPrediction((Prediction) objs[i], data.numClasses());
                int idx = str.indexOf("(");
                String id = str.substring(idx + 1, str.indexOf(")"));
                int idd = Integer.parseInt(id);
                listID[i] = idd;
            }
            int all = 0;
            int correct = 0;
            int saveWinsize = windowSize;
            for (int j = 0; j < data.numInstances() - windowSize; j = j + windowSize) {
                int wc = 0;
                windowSize = saveWinsize;
             //   System.out.println(j);
                for (int i = 0; i < data.numInstances(); i++) {

                    prediction = getPrediction((Prediction) objs[i], data.numClasses());

                    for (int w = j; w < j + windowSize; w++) {
                        if ((listID[i] - 1) == w) {
                            wc++;
                            for (int g = 0; g < prediction.length; g++) {
                                window[w - j][g] = prediction[g];
                            }
                        }
                    }
                    if (wc == windowSize) {
                        break;
                    }
                }

                while (true) {
                    double[] sumPrediction = new double[data.numClasses()];
                    for (int k = 0; k < windowSize; k++) {
                        //MAjority
                        sumPrediction[(int) window[k][1]]++;
                    }

                    ClassPrediction[] cp = findkmax(sumPrediction, 2);

                    if (((cp[0].prediction - cp[1].prediction)) >=(windowSize/2)) {
                        predict = cp[0].classID;

                        for (int h = 0; h < windowSize; h++) {
                            all++;
                            if (window[h][0] == predict) {
                                correct++;
                            }
                        }

                        break;
                    }
                    windowSize--;
                    if (windowSize == 1) {
                        predict = window[0][1];
                        actual = window[0][0];
                        all++;
                        if (predict == actual) {
                            correct++;
                        }
                        break;
                    }
                }
            }
              String text = "Classification Accuracy: "+ correct / (all + 0.0);
              
            System.out.println("Decrease Acc: " + correct / (all + 0.0));
            System.out.println(correct);
            System.out.println(all);
            return text;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
return "";
    }

     public String DecreaseFusionCrossValidationSum(String filename, Instances data, int r, int windowSize) {
        FileReader fr = null;
        try {
            Object[] objs = (Object[]) ReadObjectFromFile(filename);
            fr = new FileReader("Eval.txt");
            BufferedReader br = new BufferedReader(fr);
            String str = br.readLine();//forprinting
            str = str.substring(str.indexOf("\n") + 1);
          
            double[][] window = new double[windowSize][data.numClasses() + 3];

            double[] prediction = null;
            int[] listID = new int[data.numInstances()];
            double predict, actual;
            for (int i = 0; i < data.numInstances(); i++) {
                str = br.readLine();
                prediction = getPrediction((Prediction) objs[i], data.numClasses());
                int idx = str.indexOf("(");
                String id = str.substring(idx + 1, str.indexOf(")"));
                int idd = Integer.parseInt(id);
                listID[i] = idd;
            }
            int all = 0;
            int correct = 0;
            int saveWinsize = windowSize;
            for (int j = 0; j < data.numInstances() - windowSize; j = j + windowSize) {
                int wc = 0;
                windowSize = saveWinsize;
             //   System.out.println(j);
                for (int i = 0; i < data.numInstances(); i++) {

                    prediction = getPrediction((Prediction) objs[i], data.numClasses());

                    for (int w = j; w < j + windowSize; w++) {
                        if ((listID[i] - 1) == w) {
                            wc++;
                            for (int g = 0; g < prediction.length; g++) {
                                window[w - j][g] = prediction[g];
                            }
                        }
                    }
                    if (wc == windowSize) {
                        break;
                    }
                }

                while (true) {
                    double[] sumPrediction = new double[data.numClasses()];
                    for (int k = 0; k < windowSize; k++) {
                        //WMajority
                      for (int p = 3; p < sumPrediction.length; p++) {
                            sumPrediction[p - 3] += window[k][p];
                        }
                        //MAjority
                        //sumPrediction[(int) window[k][1]]++;
                    }

                    ClassPrediction[] cp = findkmax(sumPrediction, 2);

                    if (((cp[0].prediction - cp[1].prediction)) >=(windowSize/2)) {
                        predict = cp[0].classID;

                        for (int h = 0; h < windowSize; h++) {
                            all++;
                            if (window[h][0] == predict) {
                                correct++;
                            }
                        }

                        break;
                    }
                    windowSize--;
                    if (windowSize == 1) {
                        predict = window[0][1];
                        actual = window[0][0];
                        all++;
                        if (predict == actual) {
                            correct++;
                        }
                        break;
                    }
                }
            }
              String text = "Classification Accuracy: "+ correct / (all + 0.0);
              
            System.out.println("Decrease Acc: " + correct / (all + 0.0));
            System.out.println(correct);
            System.out.println(all);
            return text;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
return "";
    }

    public String firstCrossValidation(Classifier classifier, Instances data, int r) {
        Evaluation eval = null;
        try {
            AddID addId = new AddID();
            addId.setInputFormat(data);
            data = Filter.useFilter(data, addId);
            System.out.println("add....");
            Remove rm = new Remove();
            rm.setAttributeIndices("1");

            eval = new Evaluation(data);

            StringBuffer forPredictionsPrinting = new StringBuffer();

            Range attsToOutput = new Range("first");
            Boolean outputDist = new Boolean(false);
            FilteredClassifier fc = new FilteredClassifier();
            fc.setFilter(rm);
            fc.setClassifier(classifier);
            System.out.println("start......");
            eval.crossValidateModel(fc, data, 3, new Random(1), forPredictionsPrinting, attsToOutput, outputDist);

            System.out.println(eval.toSummaryString());
            System.out.println(eval.toClassDetailsString());
            System.out.println(eval.toMatrixString());
            forPrediction = forPredictionsPrinting.toString();

            try (FileWriter fw = new FileWriter("Eval.txt")) {
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(forPrediction);
                bw.close();
            }
            serializePrediction(eval.predictions().toArray(), "D:\\pred");
            // System.out.println(forPredictionsPrinting.toString());
        } catch (Exception ex) {
            Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return eval.toSummaryString();
    }

    public void serializePrediction(Object obj, String filename) {

        FileOutputStream fout = null;
        ObjectOutputStream oos = null;

        try {

            fout = new FileOutputStream(filename);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(obj);
            System.out.println("Done");

        } catch (Exception ex) {

            ex.printStackTrace();

        } finally {

            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public Object ReadObjectFromFile(String filepath) {
        try {
            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            //Object[] obj = new Object[size];
            Object obj = objectIn.readObject();

            System.out.println("The Object has been read from the file");
            objectIn.close();
            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void setForPrediction(String forPred) {
        this.forPrediction = forPred;
    }

    public String getForPrediction() {
        return this.forPrediction;
    }
    
     public String IncreaseFusionCrossValidationMV(String filename, Instances data, int firstWS) {
        FileReader fr = null;
        try {
            Object[] objs = (Object[]) ReadObjectFromFile(filename);
            fr = new FileReader("Eval.txt");

            BufferedReader br = new BufferedReader(fr);
            String str = br.readLine();//forprinting
            //  str = str.substring(str.indexOf("\n") + 1);

            //   ArrayList<double[]> window = new ArrayList<double[]>();
            double[] prediction = null;
            int[] listID = new int[data.numInstances()];
            double predict, actual;
            for (int i = 0; i < data.numInstances(); i++) {
                str = br.readLine();
                prediction = getPrediction((Prediction) objs[i], data.numClasses());
                int idx = str.indexOf("(");
                String id = str.substring(idx + 1, str.indexOf(")"));
                int idd = Integer.parseInt(id);
                listID[i] = idd;
            }
            int correct = 0;
            int all = 0;
            int windowSize = 120;
            int saveWin= firstWS;
            double[][] window = new double[windowSize][data.numClasses() + 3];
            for (int j = 0; j < data.numInstances() - windowSize; j = j + windowSize) {
                int wc = 0;
                windowSize = 120;
             firstWS = saveWin;
                //System.out.println("j  " + j);

                for (int i = 0; i < data.numInstances(); i++) {

                    prediction = getPrediction((Prediction) objs[i], data.numClasses());

                    for (int w = j; w < j + windowSize; w++) {
                        if ((listID[i] - 1) == w) {
                            wc++;
                            for (int g = 0; g < prediction.length - 2; g++) {
                                window[w - j][g] = prediction[g];
                            }

                        }

                    }
                    if (wc == windowSize) {
                        break;
                    }
                }
                windowSize = wc;

                double[] sumPrediction = new double[data.numClasses()];
                int k = 0;
                for (k = 0; k < firstWS; k++) {
                
                   sumPrediction[(int) window[k][1]]++; //Maj

                }
                ClassPrediction[] cp1 = findkmax(sumPrediction, 2);
                while (true) {
                    k++;
                    if (k < windowSize)//Maj
                    {
                        sumPrediction[(int) window[k - 1][1]]++;//Maj
                    }
                  
                    ClassPrediction[] cp2 = findkmax(sumPrediction, 2);

                    if ((k > windowSize) || ((cp1[0].prediction - cp1[1].prediction)) >= (cp2[0].prediction - cp2[1].prediction)) {
                        predict = cp1[0].classID;
                        for (int h = 0; h < firstWS; h++) {
                            all++;
          if (predict == window[h][0]) {
                                correct++;
                            }
                        }
                        // System.out.println("--");
                        windowSize = firstWS;
                        break;
                    } else {
                        firstWS++;
                        for (int i = 0; i < cp1.length; i++) {
                            cp1[i].prediction = cp2[i].prediction;
                            cp1[i].classID = cp2[i].classID;
                        }
                    }

                }
            }
            System.out.println("sssssssss:" + correct / (all + 0.0));
            System.out.println(data.numInstances());
            System.out.println(correct);
            System.out.println(all);
              String text = "Classification Accuracy: "+ correct / (all + 0.0);
              return text;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
return "";
    }

    public String IncreaseFusionCrossValidationSum(String filename, Instances data, int firstWS) {
        FileReader fr = null;
        try {
            Object[] objs = (Object[]) ReadObjectFromFile(filename);
            fr = new FileReader("Eval.txt");

            BufferedReader br = new BufferedReader(fr);
            String str = br.readLine();//forprinting
            //  str = str.substring(str.indexOf("\n") + 1);

            //   ArrayList<double[]> window = new ArrayList<double[]>();
            double[] prediction = null;
            int[] listID = new int[data.numInstances()];
            double predict, actual;
            for (int i = 0; i < data.numInstances(); i++) {
                str = br.readLine();
                prediction = getPrediction((Prediction) objs[i], data.numClasses());
                int idx = str.indexOf("(");
                String id = str.substring(idx + 1, str.indexOf(")"));
                int idd = Integer.parseInt(id);
                listID[i] = idd;
            }
            int correct = 0;
            int all = 0;
            int windowSize = 120;
            int saveWin= firstWS;
            double[][] window = new double[windowSize][data.numClasses() + 3];
            for (int j = 0; j < data.numInstances() - windowSize; j = j + windowSize) {
                int wc = 0;
                windowSize = 120;
             firstWS = saveWin;
                //System.out.println("j  " + j);

                for (int i = 0; i < data.numInstances(); i++) {

                    prediction = getPrediction((Prediction) objs[i], data.numClasses());

                    for (int w = j; w < j + windowSize; w++) {
                        if ((listID[i] - 1) == w) {
                            wc++;
                            for (int g = 0; g < prediction.length - 2; g++) {
                                window[w - j][g] = prediction[g];
                            }

                        }

                    }
                    if (wc == windowSize) {
                        break;
                    }
                }
                windowSize = wc;

                double[] sumPrediction = new double[data.numClasses()];
                int k = 0;
                for (k = 0; k < firstWS; k++) {
                      for (int p =0 ; p < sumPrediction.length ; p++){ //Voting
                    
                    sumPrediction[p] += window[k][p+3];
                    }

                }
                ClassPrediction[] cp1 = findkmax(sumPrediction, 2);
                while (true) {
                    k++;
                    for (int p = 0; p < sumPrediction.length && (k < windowSize); p++) {

                        sumPrediction[p] += window[k-1][p+3]; //Vot
                    }
                    ClassPrediction[] cp2 = findkmax(sumPrediction, 2);

                    if ((k > windowSize) || ((cp1[0].prediction - cp1[1].prediction)) >= (cp2[0].prediction - cp2[1].prediction)) {
                         predict = cp1[0].classID;
                        for (int h = 0; h < firstWS; h++) {
                            all++;
      if (predict == window[h][0]) {
                                correct++;
                            }
                        }
                        windowSize = firstWS;
                        break;
                    } else {
                        firstWS++;
                        for (int i = 0; i < cp1.length; i++) {
                            cp1[i].prediction = cp2[i].prediction;
                            cp1[i].classID = cp2[i].classID;
                        }
                    }

                }
                //    System.out.println(j);

            }
          
            System.out.println("sssssssss:" + correct / (all + 0.0));
            System.out.println(data.numInstances());
            System.out.println(correct);
            System.out.println(all);
              String text = "Classification Accuracy: "+ correct / (all + 0.0);
            return text;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
return ";";
    }

    public String FixedFusionCrossValidationSum(String filename, Instances data, int windowSize) {

        FileReader fr = null;
        try {
            Object[] objs = (Object[]) ReadObjectFromFile(filename);
            fr = new FileReader("Eval.txt");

            BufferedReader br = new BufferedReader(fr);
            String str = br.readLine();//forprinting
            double[] prediction = null;
            int[] listID = new int[data.numInstances()];
            double predict, actual;
            for (int i = 0; i < data.numInstances(); i++) {
                str = br.readLine();
                prediction = getPrediction((Prediction) objs[i], data.numClasses());
                int idx = str.indexOf("(");
                String id = str.substring(idx + 1, str.indexOf(")"));
                int idd = Integer.parseInt(id);
                listID[i] = idd;
            }
            int correct = 0;
            int all = 0;
            
            double[][] window = new double[windowSize][data.numClasses() + 3];
            for (int j = 0; j < data.numInstances() - windowSize; j = j + windowSize) {
                int wc = 0;
                for (int i = 0; i < data.numInstances(); i++) {

                    prediction = getPrediction((Prediction) objs[i], data.numClasses());

                    for (int w = j; w < j + windowSize; w++) {
                        if ((listID[i] - 1) == w) {
                            wc++;
                            for (int g = 0; g < prediction.length - 2; g++) {
                                window[w - j][g] = prediction[g];
                            }
                        }
                    }
                    if (wc == windowSize) {
                        break;
                    }
                }
                windowSize = wc;

                double[] sumPrediction = new double[data.numClasses()];
                int k = 0;
                for (k = 0; k < windowSize; k++) {
                     for (int p =0 ; p < sumPrediction.length ; p++){ //Voting
                    
                    sumPrediction[p] += window[k][p+3];
                    }
                     
                    //sumPrediction[(int) window[k][1]]++; //Maj

                }
                ClassPrediction[] cp1 = findkmax(sumPrediction, 2);

                predict = cp1[0].classID;
                for (int h = 0; h < windowSize; h++) {
                    all++; 
                    if (predict == window[h][0]) {
                        correct++;
                    }
                }
            }
            String text = "Classification Accuracy: "+  correct / (all + 0.0);
            System.out.println("sssssssss:" + correct / (all + 0.0));
            System.out.println(data.numInstances());
            System.out.println(correct);
            System.out.println(all);
            return text;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
    }
public String FixedFusionCrossValidationMV(String filename, Instances data, int windowSize) {

        FileReader fr = null;
        try {
            Object[] objs = (Object[]) ReadObjectFromFile(filename);
            fr = new FileReader("Eval.txt");

            BufferedReader br = new BufferedReader(fr);
            String str = br.readLine();//forprinting
            double[] prediction = null;
            int[] listID = new int[data.numInstances()];
            double predict, actual;
            for (int i = 0; i < data.numInstances(); i++) {
                str = br.readLine();
                prediction = getPrediction((Prediction) objs[i], data.numClasses());
                int idx = str.indexOf("(");
                String id = str.substring(idx + 1, str.indexOf(")"));
                int idd = Integer.parseInt(id);
                listID[i] = idd;
            }
            int correct = 0;
            int all = 0;
            
            double[][] window = new double[windowSize][data.numClasses() + 3];
            for (int j = 0; j < data.numInstances() - windowSize; j = j + windowSize) {
                int wc = 0;
                for (int i = 0; i < data.numInstances(); i++) {

                    prediction = getPrediction((Prediction) objs[i], data.numClasses());

                    for (int w = j; w < j + windowSize; w++) {
                        if ((listID[i] - 1) == w) {
                            wc++;
                            for (int g = 0; g < prediction.length - 2; g++) {
                                window[w - j][g] = prediction[g];
                            }
                        }
                    }
                    if (wc == windowSize) {
                        break;
                    }
                }
                windowSize = wc;

                double[] sumPrediction = new double[data.numClasses()];
                int k = 0;
                for (k = 0; k < windowSize; k++) {
                 /*    for (int p =0 ; p < sumPrediction.length ; p++){ //Voting
                    
                    sumPrediction[p] += window[k][p+3];
                    }*/
                     
                    sumPrediction[(int) window[k][1]]++; //Maj

                }
                ClassPrediction[] cp1 = findkmax(sumPrediction, 2);

                predict = cp1[0].classID;
                for (int h = 0; h < windowSize; h++) {
                    all++; 
                    if (predict == window[h][0]) {
                        correct++;
                    }
                }
            }
               String text = "Classification Accuracy: "+  correct / (all + 0.0);
            System.out.println("sssssssss:" + correct / (all + 0.0));
            System.out.println(data.numInstances());
            System.out.println(correct);
            System.out.println(all);
            return text;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
    }

    public int[] Convertactuals(String str) {
        int[] listactual = new int[str.length()];
        for (int i = 0; i < str.length(); i++) {
            listactual[i] = Integer.parseInt(str.substring(i, i + 1));
        }
        return listactual;
    }

        public void strToNominal(Instances test, String[] options) {
        try {
            StringToNominal stn = new StringToNominal();
            stn.setInputFormat(test);
            //    String[] options4 = {"-R", "1272"};
            stn.setOptions(options);
            test = Filter.useFilter(test, stn);
        } catch (Exception ex) {
            Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
        }

    }



    public double[] findMaxZone(double[][] arr, double zone, int k) {
        double max = 0;
        double inst = 0;
        double[] maxs = new double[k];
        double[] insts = new double[k];
        for (int i = 0; i < arr[0].length; i++) {
            if (arr[0][i] == zone) {

                for (int j = 0; j < k; j++) {
                    if (maxs[j] < arr[1][i]) {
                        for (int x = k - 1; x >= j + 1; x--) {
                            maxs[x] = maxs[x - 1];
                            insts[x] = insts[x - 1];
                        }
                        maxs[j] = arr[1][i];
                        insts[j] = i;
                        break;
                    }
                }
            }
        }
        for (int y = 0; y < k; y++) {
            System.out.println(maxs[y] + " ; " + insts[y]);
        }
        return insts;
    }




    public double calculateAccuracy(FastVector predictions) {
        double correct = 0;

        for (int i = 0; i < predictions.size(); i++) {
            Prediction np = (Prediction) predictions.elementAt(i);
            //   System.out.println(i + " : " + np.actual() + "---->" + np.predicted());
            if (np.predicted() == np.actual()) {
                correct++;
            }
        }

        return 100 * correct / predictions.size();
    }

    public double[] getPrediction(Prediction predictionVector, int num) {
        StringTokenizer st = new StringTokenizer(predictionVector.toString(), " ");
        double[] temp = new double[3 + num];

        int i = 0;
        st.nextToken();
        while (st.hasMoreTokens()) {
            temp[i] = Double.parseDouble(st.nextToken());
            i++;
        }
        return temp;
    }

    public ClassPrediction[] getClassPrediction(double[] predictionVector, int num) {

        ClassPrediction[] cp = new ClassPrediction[num];
        for (int j = 0; j < cp.length; j++) {
            cp[j] = new ClassPrediction();
            cp[j].setClassID(j);//findmax(temp);
            cp[j].setPrediction(predictionVector[j + 3]);
            cp[j].setActualClass(predictionVector[0]);
            //  System.out.println(cp[j].toString());
        }
        return cp;
    }

    
    private ClassPrediction[] findkmax(double[] a, int k) {

        ClassPrediction[] cp = new ClassPrediction[k];
        for (int j = 0; j < cp.length; j++) {
            cp[j] = new ClassPrediction();
        }
        double max1 = a[0];
        int maxID1 = 0;
        double max2 = a[1];
        int maxID2 = 1;
        if (max2 > max1) {
            max1 = a[1];
            maxID1 = 1;
            max2 = a[0];
            maxID2 = 0;
        }
        for (int i = 2; i < a.length; i++) {
            if (a[i] > max1) {
                max2 = max1;
                maxID2 = maxID1;
                max1 = a[i];
                maxID1 = i;
            } else if (a[i] > max2) {
                max2 = a[i];
                maxID2 = i;
            }
        }

        cp[0].setClassID(maxID1);
        cp[0].setPrediction(max1);
        cp[1].setClassID(maxID2);
        cp[1].setPrediction(max2);
        return cp;
    }




    public static void main(String[] args) {
        Fusion fuse = new Fusion();
        try {
            // Classifier.....
             SimpleLogistic classifier1 = new SimpleLogistic();
             // Input file should be a ARFF file. 
            // fuse.train("ART-Dataallnew508.arff", classifier1);
             fuse.train("Fisas-DataF200best.arff", classifier1);
           } catch (Exception ex) {
            Logger.getLogger(Fusion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
