

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * This file demonstrates the useage of the NN
 * @author jeffreyyoung
 */
public class Test {

    public static void main(String[] args) throws IOException {

        NN n = new NN();
        DataCheck dC = new DataCheck();

        ArrayList<ArrayList<String>> data = new ArrayList<>(); //stores inputs
        input(data, "Data/TrainNNFinal");

        ArrayList<ArrayList<String>> sW = new ArrayList<>(); //stores inputs
        input(sW, "Data/stopwords");

        Sim s = new Sim(data, sW);

        ArrayList<ArrayList<Double>> Vdata = s.getVecData(data, 1);
        for (int i = 0; i < Vdata.size() - 1; i++) {
            double gain = dC.getEntropy(Vdata.get(Vdata.size() - 1)) - dC.getGain(Vdata.get(Vdata.size() - 1), Vdata.get(i));
            double coor = dC.correlation(Vdata.get(i), Vdata.get(Vdata.size() - 1));
            System.out.println(gain + " :Gain");
            System.out.println(coor + " :Coor");

        }
        
        
        n.printD(Vdata);
        n.run(Vdata);

        while (true) {

            Scanner in = new Scanner(System.in);
            System.out.println("Question: ");
            String ques = in.nextLine();
            ArrayList<ArrayList<String>> test = new ArrayList<>(); //stores inputs
            test.add(new ArrayList<>());
            test.get(0).add(ques);
            ArrayList<ArrayList<Double>> Vtest = s.getVecData(test, 0);
            n.printD(Vtest);
            ArrayList<Double> X = new ArrayList<>();
            for (int i = 0; i < Vtest.size(); i++) {
                X.add(Vtest.get(i).get(0));
            }
            
            double cl = n.predict(X);
            System.out.println(cl);
            System.out.println("A1: " + s.classes.get(Math.ceil(cl)));
            System.out.println("A2: " + s.classes.get(Math.floor(cl)));

        }

    }

    public static void input(ArrayList<ArrayList<String>> data, String name) throws IOException {

        ArrayList<ArrayList<XSSFCell>> cells = new ArrayList<>();

        File myFile = new File("//Pathway to folder/Data/" + name + ".xlsx");
        FileInputStream fis = null;

        fis = new FileInputStream(myFile);

        XSSFWorkbook wb = null;

        wb = new XSSFWorkbook(fis);

        XSSFSheet sheet = wb.getSheetAt(0);

        XSSFRow row;
        XSSFCell cell = null;

        int rows; // No of rows
        rows = sheet.getPhysicalNumberOfRows();

        System.out.println("rows = " + rows);
        int cols = 0; // No of columns
        int tmp = 0;

        // This trick ensures that we get the data properly even if it doesn't start from first few rows
        for (int i = 0; i < 10 || i < rows; i++) {
            row = sheet.getRow(i);
            if (row != null) {
                tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                if (tmp > cols) {
                    cols = tmp;
                }
            }
        }
        for (int n = 0; n < cols; n++) {
            cells.add(new ArrayList<>()); //fills arraylists for number of columns
            data.add(new ArrayList<>());
        }

        System.out.println("rows 2: " + rows);
        System.out.println("cols: " + cols);
        for (int r = 0; r < rows * 2; r++) { //*2 to fix halfing problem
            row = sheet.getRow(r);
            if (row != null) {
                for (int c = 0; c < cols; c++) {
                    cell = row.getCell((short) c);
                    if (cell != null) {
                        cells.get(c % cols).add(cell);
                    }
                }
            }
        }

        for (int i = 0; i < cells.size(); i++) {
            System.out.println("Cell " + i + " contain n = : " + cells.get(i).size());
            for (int j = 0; j < cells.get(i).size(); j++) { //adjust to isolate years
                cells.get(i).get(j).setCellType(CellType.STRING); //convert cell to numeric
                data.get(i).add(cells.get(i).get(j).getStringCellValue()); //convert cell to double and add to arraylist
            }
        }
        //-------------------input data end-------------------------------------
    }
}
