/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author jeffreyyoung
 */
public class S5 {

    static int file = 1;
    //config data
    static String UserName, PW, URL, Direct;

    public static void main(String[] args) throws InterruptedException, IOException {
        //gets data from config file
        getConfig();
        Classifier c = new Classifier(Direct);

        //log in------------------------------------------------------------------
        WebDriver driver = new FirefoxDriver();
        //URL to testing console
        String baseUrl = URL;
        driver.get(baseUrl);
        //Testing console sign in credentials
        driver.findElement(By.id("ap_email")).sendKeys(UserName);
        driver.findElement(By.id("ap_password")).sendKeys(PW);
        driver.findElement(By.id("signInSubmit")).click();

        //wait for testing console to load
        Thread.sleep(30000);
        //enable logging on Amazon
        driver.findElement(By.id("deviceLevel-label")).click();

        //import skill names to explore
        ArrayList<ArrayList<String>> names = new ArrayList<>();
        input(names, "Skills");

        //Store maps in list
        ArrayList<Map> maps = new ArrayList<>();
        int id = 0;
        //iterate through names
        for (String n : names.get(0)) {

            //creates open term
            String name = "Open " + n.replaceAll("\\p{Punct}", "");
            System.out.println("Input: ");
            Thread.sleep(5000);

            //add Root to 1st map
            maps.add(new Map());
            maps.get(0).addToNodes(name);

            Set hs = new HashSet();
            //index is for nameing convention of output, control and invar 
            //control how many interactions are allowed; change invar to allow
            // more or less interactions
            int index = 0, control = 0, invar = 25;
            //controls when bot stops 
            boolean isNull = maps.isEmpty();
            outerloop:
            while (!isNull) {

                //Output data
                //-----------------------------------------------------------------
                ArrayList<ArrayList<String>> out = new ArrayList<>();
                try {
                    input(out, "output");
                } catch (IOException ex) {
                    System.out.println("Error In");
                }
                //copies old output
                ArrayList<ArrayList<String>> output = new ArrayList<>();
                for (int j = 0; j < out.size(); j++) {
                    output.add(new ArrayList<>());
                    for (int k = 0; k < out.get(j).size(); k++) {
                        output.get(j).add(out.get(j).get(k));
                    }
                }
                //-----------------------------------------------------------------

                //sends skill name and clicks enter
                name = maps.get(0).getNodes().get(index++);
                driver.findElement(By.xpath("//*[@id=\"astro-tabs-1-panel-0\"]/div[1]/div[2]/input")).sendKeys(name);
                Thread.sleep(5000);
                driver.findElement(By.xpath("//*[@id=\"astro-tabs-1-panel-0\"]/div[1]/div[2]/input")).sendKeys(Keys.ENTER);

                //waits for repsonse 5 seconds
                Thread.sleep(5000);

                //gets all questions from console
                //must iterate to get all outputs---------------------------------
                String question = driver.findElement(By.xpath("/html/body/div[1]/div/div/div/div/div[2]/div/div/section[2]/div[2]/div[2]/div[1]/p")).getText(), testQ = "";
                if (question.equals("<Audio only response>") || question.equals("<Short audio>")) {
                    getAudio(hs, driver);
                }
                output.get(output.size() - 6).add(n);
                output.get(output.size() - 5).add(question);
                output.get(output.size() - 4).add("000");
                output.get(output.size() - 3).add("1");
                output.get(output.size() - 2).add(" ");
                output.get(output.size() - 1).add(names.get(1).get(id));

                int times = 0;
                while (times < 2500) {
                    testQ = question;
                    question = driver.findElement(By.xpath("/html/body/div[1]/div/div/div/div/div[2]/div/div/section[2]/div[2]/div[2]/div[1]/p")).getText();
                    if (question.equals(testQ)) {
                        times++;
                    } else {
                        System.out.println("The question is: " + question + " = " + times);
                        if (question.equals("<Audio only response>") || question.equals("<Short audio>")) {
                            getAudio(hs, driver);
                        }
                        output.get(output.size() - 6).add(n);
                        output.get(output.size() - 5).add(question);
                        output.get(output.size() - 4).add("000");
                        output.get(output.size() - 3).add("1");
                        output.get(output.size() - 2).add(" ");
                        output.get(output.size() - 1).add(names.get(1).get(id));
                    }
                }
                //----------------------------------------------------------------

                //breaks outer while if condition met
                if (question.contains("Sorry, I'm having trouble accessing your")) {
                    maps.remove(0);
                    try {
                        outputToFile(output);
                    } catch (IOException ex) {
                        System.out.println("Error Out");
                    }
                    break;
                }

                //----------------------------------------------------------------
                String answer = "";
                try {
                    //if question is terminal, break
                    //classifier here..............
                    if (maps.get(0).getBranches().contains(question)) {
                        System.out.println("Question is already in Map");
                    } else {
                        QuesTypeAns qa = new QuesTypeAns(c.Break(question));
                        if (qa.getQuestionType().equals(QuesTypeAns.QuestionType.Unsupport)) {
                            System.out.println("Restart>>>>>>1");
                            exit(driver);
                            maps.remove(0);
                            isNull = maps.isEmpty();
                            index = 0;
                            try {
                                outputToFile(output);
                            } catch (IOException ex) {
                                System.out.println("Error Out");
                            }
                        } else {
                            maps.get(0).addToBranches(question);

                            if (qa.getAnswer().size() == 1) {
                                maps.get(0).addToNodes(qa.getAnswer().get(0));
                            } else if (qa.getAnswer().size() > 1) {
                                //adds to maps
                                //copy maps here....
                                for (int i = 1; i < qa.getAnswer().size(); i++) {
                                    maps.add(new Map());
                                    maps.get(maps.size() - 1).copy(maps.get(0), maps.get(maps.size() - 1));
                                    maps.get(maps.size() - 1).addToNodes(qa.getAnswer().get(i));
                                    System.out.println("Added: " + qa.getAnswer().get(i));
                                }
                                System.out.println("Added Last: " + qa.getAnswer().get(0));
                                maps.get(0).addToNodes(qa.getAnswer().get(0));
                            } else {
                                System.out.println("Restart>>>>>>>>>2");
                                exit(driver);
                                maps.remove(0);
                                isNull = maps.isEmpty();
                                index = 0;
                            }
                        }
                    }

                    if (!isNull && maps.size() > 0) {
                        answer = maps.get(0).getNodes().get(index);
                        System.out.println("S: " + answer);
                        output.get(output.size() - 6).add(n);
                        output.get(output.size() - 5).add(question);
                        output.get(output.size() - 4).add("000");
                        output.get(output.size() - 3).add("1");
                        output.get(output.size() - 2).add(answer);
                        output.get(output.size() - 1).add(names.get(1).get(id));
                    }
                } catch (java.lang.IllegalStateException ex) {
                    System.out.println("Error......State");
                    maps.remove(0);
                    isNull = true;
                } catch (java.lang.NullPointerException ex) {
                    System.out.println("Error......NULL");
                    maps.remove(0);
                    isNull = true;
                } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                    System.out.println("Error......Type");
                    maps.remove(0);
                    isNull = true;
                } catch (java.lang.OutOfMemoryError ex) {
                    System.out.println("Error......Heap");
                    maps.remove(0);
                    isNull = true;
                }

                //----------------------------------------------------------------
                //gets links for images and other media
                try {
                    outputToFile(output);
                } catch (IOException ex) {
                    System.out.println("Error Out");
                }
                //this prevents exploring every possible interaction
                if (control++ > invar) {
                    isNull = true;
                }
            } //end of while loop
            //gets links for images and other media
            getLinks(hs, driver);
            //outputs all the media
            outputMedia(hs, n, names.get(1).get(id));
            //Skill id index
            id++;
        }

    }

    //outputs all the media content gethered throughout the interaction
    public static void outputMedia(Set hs, String name, String id) {
        ArrayList<String> logs = new ArrayList<>(hs);
        for (String link : logs) {

            if (link.length() > 3) {
                String end = link.charAt(link.length() - 3) + "" + link.charAt(link.length() - 2) + "" + link.charAt(link.length() - 1);

                try {
                    FileUtils.copyURLToFile(
                            new URL(link),
                            //the following path must be set to the media folder
                            new File(Direct + "Data/Media/" + name + "-" + id + "-" + file++ + "." + end));

                } catch (java.io.FileNotFoundException e) {
                    System.out.println("Error file type. All is well!");
                } catch (java.io.IOException ex) {
                    System.out.println("IO Exception");
                }
            }
        }

    }

    public static void getLinks(Set<String> hs, WebDriver driver) {

        //gets links
        List<WebElement> list = driver.findElements(By.xpath("//*[@href or @src]"));
        for (WebElement e : list) {
            String link = null;
            try {
                link = e.getAttribute("href");
            } catch (org.openqa.selenium.StaleElementReferenceException ex) {

                System.out.println("Stale href....");
            }

            if (null == link) {
                try {
                    link = e.getAttribute("src");
                } catch (org.openqa.selenium.StaleElementReferenceException ex) {
                    System.out.println("Stale href....");
                }
            }

            try {
                System.out.println(e.getTagName() + " = " + link);

                if (e.getTagName().equals("img")) {
                    hs.add(link);
                }
            } catch (org.openqa.selenium.StaleElementReferenceException ex) {
                System.out.println("Stale href....");
            }
        }
    }

    public static void getAudio(Set<String> hs, WebDriver driver) {
        List<WebElement> list = driver.findElements(By.xpath("//*[@href or @src]"));
        for (WebElement e : list) {
            String link = null;
            try {
                link = e.getAttribute("href");
            } catch (org.openqa.selenium.StaleElementReferenceException ex) {

                System.out.println("Stale href....");
            }

            if (null == link) {
                try {
                    link = e.getAttribute("src");
                } catch (org.openqa.selenium.StaleElementReferenceException ex) {
                    System.out.println("Stale href....");
                }
            }

            try {
                System.out.println(e.getTagName() + " = " + link);

                if (e.getTagName().equals("audio")) {
                    hs.add(link);
                }
            } catch (org.openqa.selenium.StaleElementReferenceException ex) {
                System.out.println("Stale href....");
            }
        }
    }

    public static void exit(WebDriver driver) throws InterruptedException {
        driver.findElement(By.xpath("//*[@id=\"astro-tabs-1-panel-0\"]/div[1]/div[2]/input")).sendKeys("Stop");
        driver.findElement(By.xpath("//*[@id=\"astro-tabs-1-panel-0\"]/div[1]/div[2]/input")).sendKeys(Keys.ENTER);
        Thread.sleep(1500);
        driver.findElement(By.xpath("//*[@id=\"astro-tabs-1-panel-0\"]/div[1]/div[2]/input")).sendKeys("Exit");
        driver.findElement(By.xpath("//*[@id=\"astro-tabs-1-panel-0\"]/div[1]/div[2]/input")).sendKeys(Keys.ENTER);
    }

    //Answer start----------------------------------------------------------------
    public static String ans(String q) throws IOException {
        ArrayList<ArrayList<String>> data = new ArrayList<>(); //stores inputs

        input(data, "Data/TrainNNFinal");

        ArrayList<ArrayList<String>> sW = new ArrayList<>(); //stores inputs

        input(sW, "Data/stopwords");

        //question
        ArrayList<String> Q = new ArrayList<>(data.get(0));
        //answer
        ArrayList<String> A = new ArrayList<>(data.get(1));

        System.out.println("QUES: " + q);
        ArrayList<String> a = new ArrayList<>();
        split(q, a);
        double sim = 0.0;
        int n = 0;
        for (int i = 0; i < Q.size(); i++) {
            ArrayList<String> b = new ArrayList<>();
            split(Q.get(i), b);

            double sum = 0.0;
            for (int j = 0; j < a.size(); j++) {
                double max = 0.0;
                for (int k = 0; k < b.size(); k++) {
                    double sm = similarity(a.get(j), b.get(k));
                    if (sm > max) {
                        max = sm;

                    }
                }

                sum += max;

            }

            if (sum > sim) {
                sim = sum;
                n = i;
            }
        }
        return A.get(n);
    }

    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     *
     * @param s1
     * @param s2
     */
    public static double similarity(String s1, String s2) {

        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
            /* both strings are zero length */ }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }
//implementation of the Levenshtein Edit Distance

    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }
    //Answer stop-----------------------------------------------------------------

    public static void split(String s, ArrayList<String> a) {

        String temp = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ') {
                if (s.charAt(i) != ',' && s.charAt(i) != '.' && s.charAt(i) != '?') {
                    temp += s.charAt(i);
                }
            } else {
                a.add(temp.replaceAll("\\p{Punct}", ""));
                temp = "";
            }
        }
        //adds the last word in the statement
        a.add(temp.replaceAll("\\p{Punct}", ""));

    }

    //Used to input Excel files
    public static void input(ArrayList<ArrayList<String>> data, String name) throws IOException {

        ArrayList<ArrayList<XSSFCell>> cells = new ArrayList<>();

        File myFile = new File(Direct + "/Data/" + name + ".xlsx");
        FileInputStream fis = null;

        fis = new FileInputStream(myFile);

        XSSFWorkbook wb = null;

        wb = new XSSFWorkbook(fis);

        XSSFSheet sheet = wb.getSheetAt(0);

        XSSFRow row;
        XSSFCell cell = null;

        int rows; // No of rows
        rows = sheet.getPhysicalNumberOfRows();

        int cols = 0; // No of columns
        int tmp = 0;

        // This trick ensures that question get the data properly even if it doesn't start from first few rows
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
            for (int j = 0; j < cells.get(i).size(); j++) { //adjust to isolate years
                cells.get(i).get(j).setCellType(CellType.STRING); //convert cell to numeric
                data.get(i).add(cells.get(i).get(j).getStringCellValue()); //convert cell to double and add to arraylist
            }
        }
        //-------------------input data end-------------------------------------
    }

    //Method used to output an Excel file
    public static void outputToFile(ArrayList<ArrayList<String>> list) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Java Books");

        Object[][] bookData = new Object[list.get(0).size()][list.size()];

        for (int i = 0; i < list.get(1).size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                bookData[i][j] = list.get(j).get(i);
            }
        }

        int rowCount = 0;

        for (Object[] aBook : bookData) {
            Row row = sheet.createRow(rowCount++);

            int columnCount = 0;

            for (Object field : aBook) {
                Cell cell = row.createCell(columnCount++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }

        }

        try (FileOutputStream outputStream = new FileOutputStream(Direct + "/Data/output.xlsx")) {
            workbook.write(outputStream);
        }
        System.out.println("file was output");
    }
    public static void getConfig() {
        try {
            File myObj = new File("src/config.txt");
            Scanner myReader = new Scanner(myObj);
            int n = 1;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (n == 1) {
                    UserName = data;
                } else if (n == 2) {
                    PW = data;
                } else if (n == 3) {
                    URL = data;
                } else if(n == 4){
                    Direct = data;
                }   
                n++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
