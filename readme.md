# SkillDetective: Automated Policy-Violation Detection of Voice Assistant Applications in the Wild

## Introduction

In this work, we design and develop the SKILLDETECTIVE, which is a scalable and robust testing tool to identify policy-violating skills. SKILLDETECTIVE significantly extends skill testing capabilities in a broader context. We make the following contributions:

* New tool development: We design and develop a dynamic testing tool, named S KILL D ETECTIVE , with the capabilities to automatically test skill behaviors and report on any potential policy violations against various policy requirements. 

* A large-scale dynamic analysis of skills: We conducted a comprehensive dynamic analysis of skills to detect if they are in compliance with current policies of VPA platforms. After over a year of development and testing, we have tested 54,055 Amazon Alexa skills and 5,583 Google Assistant actions, and gathered 518,385 textual outputs, 10,370 audio files and 311,000 images in total from skill interactions. Such a wide-range and large-scale policy violation detection of skills has not previously been reported.

* Findings: We identified 5,994 skills and 184 actions violating at least one policy requirement. 830 skills and 22 actions violate more than one policy. In the Kids category, we identified 282 policy-violating skills. 82% of skills and 67% of actions in the Health category violate at least one policy. 1,149 skills and 32 actions violate policies related to personal data collection. 


## Major findings

[![Watch the video](https://github.com/skilldetective/skilldetective/blob/master/images/kids_with_permission.png)](https://www.youtube.com/watch?v=tBxdAOCOelg)

### Skills in Kids categories

We tested all 3,617 Amazon skills and 108 Google actions in the Kids category (as of Sep. 2020). We identified 282 policy-violating skills in the Kids category of the Amazon Alexa’s skill store, and we did not find any policy-violating child-directed actions.

Policy Violation | # of Skills | Example |
:---: | :---: | :---:| 
Collecting personal data | 41 | So, first, what is your name? | 
Directing users to outside of Alexa | 21 | Please support us by visiting www.oneoffcoder.com. | 
Explicit mature content | 12 | My bestie contains mature content that may not be suitable for all ages. | 
Requesting for positive rating | 197 | If you enjoyed playing kid chef, leaving us a five star review will help us add more content. | 
Toxic content | 4 | If I had a face like yours, I’d teach my ass to talk | 
Violation in audios/images | 11 | Happy holidays santa’s little helper here. Tell me your name to begin. (in audio) | 

![Kids](https://github.com/skilldetective/skilldetective/blob/master/images/kids_with_permission.png)

### Skills in Health categories
For the Health category, we detected 153 skills out of 2,163 skills and 13 actions out of 207 actions with health-related data collection.

We also found only 467 Amazon skills among 2,163 skills (22%) provide a disclaimer. 79 Google actions among 227 actions (35%) provide a disclaimer and 65% of actions do not provide one.

Policy Violation | # of Skills | # of Actions |
:---: | :---: | :---:| 
Collecting health data | 153 | 13 | 
Collecting health data but not in the Health category| 15 | 0 | 
Lacking a disclaimer | 1696 (78%) | 148 (65%) | 


### Skills with data collection

For the general (non-Kids and non-Health) categories, we identified 597 skills and 35 actions collecting personal data  without using permission APIs (they collect user data through the conversation interface). There are also 1,369 skills collecting personal data using permission APIs in the Amazon Alexa platform. Among all these skills with data collection, 1,149 skills and 32 actions violate at last one policy, such as lacking a privacy policy, having an incomplete or deceptive privacy policy.

Policy Violation | Skills Collect Data ThroughPermission APIs | Skills Collect Data Without Using Permission APIs | Action |
:---: | :---: | :---:| :---:| 
Collecting data | 1369 | 597 | 35|
Lacking a privacy policy| 1 | 266 | 0 |
Having an ncomplete privacy policy | 587 | 188 |26|
Having a deceptive privacy policy |148|35|1|
Should ask for permission |-|340|12|

![nopolicy](https://github.com/skilldetective/skilldetective/blob/master/images/permission_no_policy2.png)
![deceptivepolicy](https://github.com/skilldetective/skilldetective/blob/master/images/deceptive_policy.png)


###  Explicitly requests that users leave a positive rating of the skill

We confirmed this policy violation in 3,157 skills (there were some skills requesting for positive rating in both the description and skill output). We also found that the developer “Appbly.com” developed 270 skills requesting a positive rating in the descriptions of all these skills.


###  Policy violations of content safety in skill outputs

71 Alexa skills contain toxic content

3 skills predict gender

1 skill promotes gambling

### Policy violations in audio/images

We have detected 19 skills with policy violations hidden in the audio or image files. Eleven of the skills in the Kids category. Six non-kid skills contain data collection, while 3 of them lack a privacy policy and 3 more have incomplete privacy policies. The skill "Shape Game" asks "what is your name" in the audio file output, but does not provide a privacy policy. Another 2 skills contain toxic content in the media outputs.

![Summary](https://github.com/skilldetective/skilldetective/blob/master/images/media_violation.png)

## Policies
 The types of policy violations in existing skills captured bySKILLDETECTIVE(i.e., there exists at least one skill violatingone of these 13 policies).
![ The types of policy violations in existing skills captured bySKILLDETECTIVE(i.e., there exists at least one skill violatingone of these 13 policies).](https://github.com/skilldetective/skilldetective/blob/master/images/policies.png)

Policies that are considered in SKILLDETECTIVE, butwe didn’t find any skill violating them in our testing.

Provide life-saving assistance, Cure all diseases, Black market sale, Prescription drugs, Offers a separate skills store, Recommend skills, Offer compensation for using skills, Solicit donations, Extreme gore, Decapitations, Unsettling content, Excessive violence, Organized crime,  Terrorism, Illegal activities, Forced marriages, Purchasable husbands, Purchasable wives, Promote hate speech, Incite racial hatred, Incite gender hatred, Nazi symbols, Promote Ku Klux Klan, Contact emergency responder, Contact 911, Illegal downloading, Pirated software, Join illegal organization, Illegal lifestyle, Prostitution, Create dangerous materials, Build bomb, Build meth lab, Build silencer, Promote terrorism, Praise terrorism, Recruit members for terrorist, Gambling, Excessive alcohol, Underage alcohol|


## Summary of policy violations identified by SKILLDETECTIVE
![Summary](https://github.com/skilldetective/skilldetective/blob/master/images/results.png)
