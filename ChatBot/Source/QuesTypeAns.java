/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;

import java.util.*;

public class QuesTypeAns {

    enum QuestionType {
        YesNo, Instrution, Selection, WH, Mix, Unsupport
    }
    private Tree parseTree;
    private String question;
    private QuestionType questionType;
    List<QuestionType> qTypes = new ArrayList<QuestionType>();
    private static List<HashMap<String, String>> virtualUsers = new ArrayList<HashMap<String, String>>();

    QuesTypeAns(String question) {
        this.question = question;
        this.parseTree = getParseTree();
    }

    public QuestionType getQuestionType() {
//        System.out.println(parseTree);
        String treeString = parseTree.toString();
        String qusLowerCase = question.toLowerCase();

        // Identify Instruction questions
        if (treeString.contains("VB") || treeString.contains("VBG") || treeString.contains("VBP")) {
            if (qusLowerCase.contains("say") || qusLowerCase.contains("ask") || qusLowerCase.contains("saying") || qusLowerCase.contains("asking")) {
                qTypes.add(QuestionType.Instrution);
            }
        }

        // Identify Selection questions
        if (treeString.contains("CC")) {
            qTypes.add(QuestionType.Selection);
        }

        // Identify Wh questions
        List<String> whTag = new ArrayList<String>(Arrays.asList("WDT", "WHADJP", "WHADVP", "WHNP"));
        if (!qTypes.contains(QuestionType.Instrution)) {
            for (String s : whTag) {
                if (treeString.contains(s)) {
                    qTypes.add(QuestionType.WH);
                    break;
                }
            }
        }

        // Identify Yes/No questions
        if (treeString.contains("SQ")) {
            if (!qTypes.contains(QuestionType.Selection) && !qTypes.contains(QuestionType.WH)) {
                qTypes.add(QuestionType.YesNo);
            }
        }

        if (qTypes.size() > 1) {
            System.out.println("Mix question includes: " + qTypes);
            this.questionType = QuestionType.Mix;
        }
        if (qTypes.size() == 1) {
            this.questionType = qTypes.get(0);
        }
        if (qTypes.size() < 1) {
            this.questionType = QuestionType.Unsupport;
        }
        return this.questionType;
    }

    public List<String> getAnswer() {
        String qusLowerCase = question.toLowerCase();
        if (questionType == null) {
            getQuestionType();
            return new ArrayList<String>();
        } else {
            // Answer Yes/No question
            if (questionType == QuestionType.YesNo) {
                return answerYesNoQuestion();
            }

            // Answer Instruction question
            if (questionType == QuestionType.Instrution) {
                return answerInstructionQuestion(qusLowerCase);
            }

            // Answer Selection question
            if (questionType == QuestionType.Selection) {
                return answerSelectionQuestion();
            }

            // Answer WH question
            if (questionType == QuestionType.WH) {
                return answerWH(qusLowerCase, 0);
            }

            // Answer Mix question
            if (questionType == QuestionType.Mix) {
                return answerMixQuestion(qusLowerCase);
            }
        }
        return new ArrayList<String>();
    }

    private List<String> answerYesNoQuestion() {
        return new ArrayList<String>(Arrays.asList("Yes", "No"));
    }

    private List<String> answerInstructionQuestion(String question) {
        if (question.contains("say")) {
            String answer = removeSymbols(question.split("say")[1]);
            return new ArrayList<String>(Arrays.asList(answer));
        }
        if (question.contains("saying")) {
            String answer = removeSymbols(question.split("say")[1]);
            return new ArrayList<String>(Arrays.asList(answer));
        }
        if (question.contains("ask")) {
            String answer = removeSymbols(question.split("say")[1]);
            return new ArrayList<String>(Arrays.asList(answer));
        }
        if (question.contains("asking")) {
            String answer = removeSymbols(question.split("say")[1]);
            return new ArrayList<String>(Arrays.asList(answer));
        }
        return new ArrayList<String>();
    }

    private List<String> answerSelectionQuestion() {
        Tree tmpTree = parseTree;
        Queue<Tree> queue;
        queue = new ArrayDeque<Tree>();
        queue.add(tmpTree);
        Tree cur;
        Tree optionTree = null;
        while (!queue.isEmpty()) {
            cur = queue.peek();
            if (cur.label().toString().equals("CC")) {
                optionTree = cur.parent(parseTree);
                break;
            }
            queue.poll();
            queue.addAll(cur.getChildrenAsList());
        }
        List<String> ansArr = new ArrayList<String>();
        for (Tree t : optionTree.getChildrenAsList()) {
            String tmpStr = t.getLeaves().toString();
            tmpStr = tmpStr.replaceAll("\\pP", "");
            tmpStr = tmpStr.trim();
            if (!tmpStr.equals("or") && tmpStr.length() > 1) {
                ansArr.add(tmpStr);
            }
        }
        return ansArr;
    }

    private List<String> answerMixQuestion(String question) {
        List<String> answerArr = new ArrayList<String>();
        if (qTypes.contains(QuestionType.YesNo)) {
            answerArr.addAll(answerYesNoQuestion());
        }
        if (qTypes.contains(QuestionType.Instrution) && qTypes.contains(QuestionType.Selection)) {
            answerArr.addAll(answerSelectionQuestion());
        }
        if (qTypes.contains(QuestionType.Instrution) && !qTypes.contains(QuestionType.Selection)) {
            answerArr.addAll(answerInstructionQuestion(question));
        }
        if (!qTypes.contains(QuestionType.Instrution) && qTypes.contains(QuestionType.Selection)) {
            answerArr.addAll(answerSelectionQuestion());
        }
        return answerArr;
    }

    private Tree getParseTree() {
        Sentence sent = new Sentence(question);
        return sent.parse();
    }

    private static String removeSymbols(String str) {
        str = str.replaceAll("\\pP", "");
        str = str.trim();
        return str;

    }

    private ArrayList<String> answerWH(String qus, int idx) {
//        loadInfo();
        if (qus.contains("name")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("name")));
        }
        if (qus.contains("gender")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("gender")));
        }
        if (qus.contains("race")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("race")));
        }
        if (qus.contains("birthday")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("birthday")));
        }
        if (qus.contains("social security number") || qus.contains("SSN")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("SSN")));
        }
        if (qus.contains("street")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("street")));
        }
        if (qus.contains("city")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("city")));
        }
        if (qus.contains("state")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("state")));
        }
        if (qus.contains("zip code")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("zip_code")));
        }
        if (qus.contains("phone number") || qus.contains("mobile number")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("phone_number")));
        }
        if (qus.contains("email")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("email")));
        }
        if (qus.contains("height")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("height")));
        }
        if (qus.contains("weight")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("weight")));
        }
        if (qus.contains("hair color")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("hair_color")));
        }
        if (qus.contains("blood type")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("blood_type")));
        }
        if (qus.contains("mather") && qus.contains("name")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("mother_maiden_name")));
        }
        if (qus.contains("civil status")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("civil_status")));
        }
        if (qus.contains("education")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("education_background")));
        }
        if (qus.contains("diver license")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("diver_license")));
        }
        if (qus.contains("employment status")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("employment_status")));
        }
        if (qus.contains("job title")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("job_title")));
        }
        if (qus.contains("salary")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("monthly_salary")));
        }
        if (qus.contains("company") && qus.contains("name")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("company_name")));
        }
        if (qus.contains("company") && qus.contains("size")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("company_size")));
        }
        if (qus.contains("industry")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("industry")));
        }
        if (qus.contains("credit card type")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("credit_card_type")));
        }
        if (qus.contains("credit card number")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("credit_card_number")));
        }
        if (qus.contains("cvv")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("cvv2")));
        }
        if (qus.contains("expires")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("expires")));
        }
        if (qus.contains("vehicle")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("vehicle")));
        }
        if (qus.contains("license plate")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("car_license_plate")));
        }
        if (qus.contains("favorite color")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("favorite_color")));
        }
        if (qus.contains("favorite movie")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("favorite_movie")));
        }
        if (qus.contains("favorite music")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("favorite_music")));
        }
        if (qus.contains("favorite song")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("favorite_song")));
        }
        if (qus.contains("favorite book")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("favorite_book")));
        }
        if (qus.contains("favorite sports")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("favorite_sports")));
        }
        if (qus.contains("favorite tv")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("favorite_tv")));
        }
        if (qus.contains("favorite food")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("favorite_food")));
        }
        if (qus.contains("personality")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("personality")));
        }
        if (qus.contains("personal style")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("personal_style")));
        }
        if (qus.contains("username")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("username")));
        }
        if (qus.contains("password")) {
            return new ArrayList<String>(Arrays.asList(virtualUsers.get(idx).get("password")));
        }

        return new ArrayList<String>();
    }

    static {
        List<HashMap<String, String>> infoList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> user0 = new HashMap<String, String>();
        user0.put("name", "James C Washington");
        user0.put("gender", "male");
        user0.put("race", "white");
        user0.put("birthday", "6/19/1980");
        user0.put("SSN", "066-80-6240");
        user0.put("street", "357 Bottom Lane");
        user0.put("city", "Buffalo");
        user0.put("state", "NY");
        user0.put("state_full", "New York");
        user0.put("zip_code", "14214");
        user0.put("phone_number", "716-903-8835");
        user0.put("email", "7mcjmqil0l@payspun.com");
        user0.put("height", "6'0");
        user0.put("weight", "200.2 pounds");
        user0.put("hair_color", "black");
        user0.put("blood_type", "A");
        user0.put("mother_maiden_name", "Brooks");
        user0.put("civil_status", "Married, with children");
        user0.put("education_background", "Bachelor's degree");
        user0.put("diver_license", "685-549-815");
        user0.put("employment_status", "full-time work");
        user0.put("monthly_salary", "$3000");
        user0.put("job_title", "Water and waitress");
        user0.put("company_name", "Personal & Corporate Design");
        user0.put("company_size", "11-50 employees");
        user0.put("industry", "Food Preparation and Serving Related");
        user0.put("credit_card_type", "MasterCard");
        user0.put("credit_card_number", "5417027168183647");
        user0.put("cvv2", "025");
        user0.put("expires", "10/2023");
        user0.put("vehicle", "2012 Audi RS3");
        user0.put("car_license_plate", "2DJ F99");
        user0.put("favorite_color", "violet");
        user0.put("favorite_movie", "The Big Lebowski(1998)");
        user0.put("favorite_music", "Gospel music");
        user0.put("favorite_song", "I'm An Albatraoz(by AronChupa)");
        user0.put("favorite_book", "Frostbite (Vampire Academy) --by RichelleLes Misérables");
        user0.put("favorite_sports", "Diving");
        user0.put("favorite_tv", "Limitless CBS");
        user0.put("favorite_food", "Pasta");
        user0.put("personality", "Philosophic");
        user0.put("personal_style", "Jeans and t-shirt");
        user0.put("username", "arshia_karikator");
        user0.put("password", "iRaetuuf7ai");
        QuesTypeAns.virtualUsers.add(user0);
    }
}
