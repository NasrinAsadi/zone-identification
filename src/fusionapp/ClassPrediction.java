/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fusionapp;

/**
 *
 * @author Nasrin
 */
public class ClassPrediction {

    int classID;
    double prediction;
    double actualClass = 0.0;
    int winSize =0;

    public ClassPrediction(int classID, double prediction, double actual) {
        this.classID = classID;
        this.prediction = prediction;
        this.actualClass = actual;
    }

    public ClassPrediction() {
    }
    
     public ClassPrediction( double prediction, double actual ,int wins , String s) {
        this.winSize = wins;
        this.prediction = prediction;
        this.actualClass = actual;
    }

    public double getActualClass() {
        return actualClass;
    }

    public void setActualClass(double actualClass) {
        this.actualClass = actualClass;
    }

    public int getClassID() {
        return classID;
    }

    public double getPrediction() {
        return prediction;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public void setPrediction(double prediction) {
        this.prediction = prediction;
    }

    @Override
    public String toString() {
        return "ClassPrediction{" + "classID=" + classID + "prediction=" + prediction + '}';
    }

    public ClassPrediction add(ClassPrediction cp) {
        this.prediction += cp.prediction;
        this.classID = cp.classID;
        return this;
    }

    public ClassPrediction copyCP() {

        ClassPrediction tmpPred = new ClassPrediction();
        tmpPred.actualClass = this.actualClass;
        tmpPred.classID = this.classID;
        tmpPred.prediction = this.prediction;

        return tmpPred;
    }

}
