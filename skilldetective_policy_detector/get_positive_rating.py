import violation
import csv
import os


def get_dataset_skill_non_kids():
	skillname={}
	skillpolicy={}
	skilldescription={}
	number=0
	f=open('data/dataset_9_13_68846.csv','r')
	reader=csv.reader(f)
	for row in reader:
		if 'kids' not in row[2].lower():
			skillname[row[0]]=row[1]
			skilldescription[row[0]]=row[7]
			if row[8]=='null':
				continue
			skillpolicy[row[0]]=row[8]
	return skillname, skillpolicy, skilldescription


def write_result_output(skills, skillid, name, output, filename):
	with open('result/' + filename + '.csv', 'w', newline='') as csvfile:
		fieldnames = ['Order', 'Category', 'Violated_policy', 'Skill_id', 'Skill_name', 'Skill_output']
		writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
#		writer.writeheader()
		for skill in skills:
			writer.writerow({'Order': '5', 'Category': 'General', 'Violated_policy': skill[1], 'Skill_id': skillid[skill[0]], 'Skill_name': name[skill[0]], 'Skill_output': output[skill[0]].replace('\n',' ')})


def write_result_description(skills, skillid, skillname, skilldescription, filename):
	with open('result/' + filename + '.csv', 'a', newline='') as csvfile:
		fieldnames = ['Order', 'Category', 'Violated_policy', 'Skill_id', 'Skill_name', 'Skill_output']
		writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
#		writer.writeheader()
		for skill in skills:
			writer.writerow({'Order': '5', 'Category': 'General', 'Violated_policy': skill[1], 'Skill_id': skill[0], 'Skill_name': skillname[skill[0]], 'Skill_output': skilldescription[skill[0]].replace('\n',' ')})


def get_positive_rating():
	skillname, skillpolicy, skilldescription = get_dataset_skill_non_kids()
	output, name, skillid = violation.get_output_skill()

	skills_from_output = violation.get_positive_rating(output) 
	write_result_output(skills_from_output, skillid, name, output, 'skill_5_General_positive_rating')

	skills_from_description = violation.get_positive_rating(skilldescription)
	write_result_description(skills_from_description, skillid, skillname, skilldescription, 'skill_5_General_positive_rating')



