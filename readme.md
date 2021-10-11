SkillDetective Policy Detector

The policy detector works with python3.

1 install the “spacy” library with:

pip install -U pip setup tools wheel
pip install -U spacy
python -m spacy download en_core_web_sm

2 download and unzip "data" file from: "https://drive.google.com/file/d/1VIw_Er8vLVEvzpt1RX24uqnDR0hdXGIG/view?usp=sharing". Put it under "skilldetective_policy_detector" and name it as "data".

3 output of skills and actions should be put in folder “output/skill” and “output/action” as "csv" file.

4 run the code “python policy_detector.py”

5 the results are in folder “result”. Summary of policy-violating skills and actions are in files ”all_problematic_skills_result.csv” and “all_problematic_actions_result.csv”. More details about each category are shown in “result/details” folder.