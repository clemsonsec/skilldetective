/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * Path to input folder must be set below!!!!!
 */


import edu.stanford.nlp.simple.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Classifier {

    static String Direct;
    public Classifier(String Direct) {

        Classifier.Direct = Direct;
        System.out.println("Classifier created");
    }

    public String Break(String ques) {

        char[] c = ques.toCharArray();
        String s = "";
        int index = 0;
        for (int i = c.length - 2; i >= 0; i--) {
            if (c[i] == '.' || c[i] == '!') {
                System.out.println(c[i]);
                index = (i + 1);
                break;
            }
        }

        System.out.println(index);
        if (index != 0) {
            for (int i = index + 1; i < ques.length(); i++) {
                s += c[i];
            }
        } else {
            s = ques;
        }

        System.out.println("S: " + s);

        return s;
    }

    public String classify(String ques) {
        // Create a document. No computation is done yet.
        ArrayList<ArrayList<String>> in = new ArrayList<>();
        try {
            input(in);
        } catch (IOException ex) {
            System.out.println("Error In");
        }

        ArrayList<ArrayList<String>> q = new ArrayList<>();

        for (String st : in.get(0)) {
            q.add(new ArrayList<>());
            Document doc = new Document(st);
            for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences
                for (String s : sent.posTags()) {
                    q.get(q.size() - 1).add(s);
                }
            }
        }

        Document doc = new Document(Break(ques));
        ArrayList<String> test = new ArrayList<>();

        try {
            for (String s : doc.sentence(0).posTags()) {

                System.out.println(s);
                test.add(s);
            }
        } catch (java.lang.IndexOutOfBoundsException ex) {
            System.out.println("Out of bounds in classifier.");
        }

        int sim = 0, i = 0, index = 0;
        for (ArrayList<String> a : q) {
            int count = 0;
            String ts = "";
            for (String s : a) {
                ts += s;
                String ts2 = "";
                for (String s2 : test) {
                    ts2 += s2;
                }
                System.out.println(ts2 + " " + ts);
                if (ts2.contains(ts)) {
                    count++;
                }
            }
            if (count > sim) {
                sim = count;
                index = i;
            }
            i++;

        }

        System.out.println("The question is classified as: " + in.get(1).get(index));

        return in.get(1).get(index);

    }
    public static void input(ArrayList<ArrayList<String>> data) throws IOException {

        ArrayList<ArrayList<XSSFCell>> cells = new ArrayList<>();

        //This pathway must be set!!!!
        File myFile = new File(Direct + "/Data/Data/QClassified.xlsx");
        
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
