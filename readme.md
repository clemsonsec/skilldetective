# SkillDetective: Automated Policy-Violation Detection of Voice Assistant Applications in the Wild

## Introduction

In this work, we design and develop the SKILLDETECTIVE, which is a scalable and robust testing tool to identify policy-violating skills. SKILLDETECTIVE significantly extends skill testing capabilities in a broader context. We make the following contributions:

* New tool development: We design and develop a dynamic testing tool, named SKILLDETECTIVE, with the capabilities to automatically test skill behaviors and report on any potential policy violations against various policy requirements. 

* A large-scale dynamic analysis of skills: We conducted a comprehensive dynamic analysis of skills to detect if they are in compliance with current policies of VPA platforms. After over a year of development and testing, we have tested 54,055 Amazon Alexa skills and 5,583 Google Assistant actions, and gathered 518,385 textual outputs, 2,070 unique audio files and 31,100 images in total from skill interactions. Such a wide-range and large-scale policy violation detection of skills has not previously been reported.

* Findings: We identified 6,079 skills and 175 actions violating at least one policy requirement. 590 skills and 24 actions violate more than one policy. In the Kids category, we identified 244 policy-violating skills. 80% of skills and 68% of actions in the Health category violate at least one policy. 623 skills and 25 actions violate policies related to personal data collection. 


## Major findings

### Here is a demo for the automated skill testing. 

[![Watch the video](https://github.com/skilldetective/skilldetective/blob/master/images/youtube.png)](https://www.youtube.com/watch?v=tBxdAOCOelg)

### The detailed list of the identified policy-violating skills/actions can be found in ./[policy_detector](https://github.com/skilldetective/skilldetective/tree/master/policy_detector)

### Skills in Kids categories

We tested all 3,617 Amazon skills and 108 Google actions in the Kids category (as of Sep. 2020). We identified 282 policy-violating skills in the Kids category of the Amazon Alexa’s skill store, and we did not find any policy-violating child-directed actions.

Policy Violation | # of Skills | Example |
:---: | :---: | :---:| 
Collecting personal data | 34 | So, first, what is your name? | 
Directing users to outside of Alexa | 21 | Please support us by visiting www.oneoffcoder.com. | 
Explicit mature content | 12 | My bestie contains mature content that may not be suitable for all ages. | 
Requesting for positive rating | 177 | If you enjoyed playing kid chef, leaving us a five star review will help us add more content. | 
Toxic content | 4 | If I had a face like yours, I’d teach my ass to talk | 
Violation in audios/images | 4 | Happy holidays santa’s little helper here. Tell me your name to begin. (in audio) | 

![Kids](https://github.com/skilldetective/skilldetective/blob/master/images/kids_with_permission.png)

### Skills in Health categories
For the Health category, we detected 146 skills out of 2,162 skills and 13 actions out of 207 actions with health-related data collection.

We also found only 453 Amazon skills among 2,162 skills (21%) provide a disclaimer. 78 Google actions among 227 actions (34%) provide a disclaimer and 66% of actions do not provide one.

Policy Violation | # of Skills | # of Actions |
:---: | :---: | :---:| 
Collecting health data | 146 | 13 | 
Collecting health data but not in the Health category| 13 | 0 | 
Lacking a disclaimer | 1,709 (79%) | 149 (66%) | 


### Skills with data collection

For the general (non-Kids and non-Health) categories, we identified 480 skills and 61 actions collecting personal data without using permission APIs (they collect user data through the conversation interface). There are also 1,369 skills collecting personal data using permission APIs in the Amazon Alexa platform. Among all these skills with data collection, 623 skills and 25 actions violate at last one policy, such as lacking a privacy policy, having an incomplete or deceptive privacy policy.

Policy Violation | Skills Collect Data Through Permission APIs | Skills Collect Data Without Using Permission APIs | Action |
:---: | :---: | :---:| :---:| 
Collecting data | 1,369 | 480 | 61|
Lacking a privacy policy| 1 | 171 | 0 |
Having an ncomplete privacy policy | 330 | 104 |8|
Having a deceptive privacy policy |38|12|2|
Should ask for permission |-|-|17|

![nopolicy](https://github.com/skilldetective/skilldetective/blob/master/images/permission_no_policy2.png)
![deceptivepolicy](https://github.com/skilldetective/skilldetective/blob/master/images/deceptive_policy.png)


###  Explicitly requests that users leave a positive rating of the skill

We confirmed this policy violation in 3,464 skills (there were 9 skills requesting for positive rating in both the description and skill output). We also found that the developer “Appbly.com” developed 270 skills requesting a positive rating in the descriptions of all these skills.


###  Policy violations of content safety in skill outputs

177 Alexa skills contain toxic content

3 skills predict gender

### Policy violations in audio/images

We have detected 8 skills with policy violations hidden in the audio or image files. 4 of the skills in the Kids category. 4 non-kid skills contain data collection, while 2 of them lack a privacy policy and 2 more have incomplete privacy policies. The skill "Shape Game" asks "what is your name" in the audio file output, but does not provide a privacy policy.

![Summary](https://github.com/skilldetective/skilldetective/blob/master/images/media_violation.png)

## Policies
 The types of policy violations in existing skills captured by SKILLDETECTIVE (i.e., there exists at least one skill violating one of these 13 policies).
![ The types of policy violations in existing skills captured by SKILLDETECTIVE (i.e., there exists at least one skill violating one of these 13 policies).](https://github.com/skilldetective/skilldetective/blob/master/images/policies.png)

## Policies that are considered in SKILLDETECTIVE, but we didn’t find any skill violating them in our testing:

Provide life-saving assistance, Cure all diseases, Black market sale, Prescription drugs, Offers a separate skills store, Recommend skills, Offer compensation for using skills, Solicit donations, Extreme gore, Decapitations, Unsettling content, Excessive violence, Organized crime,  Terrorism, Illegal activities, Forced marriages, Purchasable husbands, Purchasable wives, Promote hate speech, Incite racial hatred, Incite gender hatred, Nazi symbols, Promote Ku Klux Klan, Contact emergency responder, Contact 911, Illegal downloading, Pirated software, Join illegal organization, Illegal lifestyle, Prostitution, Create dangerous materials, Build bomb, Build meth lab, Build silencer, Promote terrorism, Praise terrorism, Recruit members for terrorist, Promote Gambling, Excessive alcohol, Underage alcohol


## Summary of policy violations identified by SKILLDETECTIVE
![Summary](https://github.com/skilldetective/skilldetective/blob/master/images/results.png)
