import violation
import csv
import os


def get_dataset_skill_kids():
	skillname={}
	skillpolicy={}
	skilldescription={}
	number=0
	f=open('data/dataset_9_13_68846.csv','r')
	reader=csv.reader(f)
	for row in reader:
		if 'kids' in row[2].lower():
			skillname[row[0]]=row[1]
			skilldescription[row[0]]=row[7]
			if row[8]=='null':
				continue
			skillpolicy[row[0]]=row[8]
	return skillname, skillpolicy, skilldescription


def get_dataset_action_kids():
	skillname={}
	skillpolicy={}
	skilldescription={}
	number=0
	f=open('data/action_16002.csv','r')
	reader=csv.reader(f)
	for row in reader:
		if 'kids' in row[2].lower():
			action_id=row[10].split('/')[-1]
			skillname[action_id]=row[0]
			skilldescription[action_id]=row[3]
			if row[4]=='':
				continue
			skillpolicy[action_id]=row[4]
	return skillname, skillpolicy, skilldescription


def get_output_kids(output, skillid, skillname):
	output2 = {}
	for i in output:
		if skillid[i] in skillname:
			output2[i] = output[i]
	return output2


def write_result_output(skills, skillid, name, output, filename):
	with open('result/' + filename + '.csv', 'w', newline='') as csvfile:
		fieldnames = ['Order', 'Category', 'Violated_policy', 'Skill_id', 'Skill_name', 'Skill_output']
		writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
#		writer.writeheader()
		for skill in skills:
			writer.writerow({ 'Order': '1', 'Category': 'Kids', 'Violated_policy': skill[1], 'Skill_id': skillid[skill[0]], 'Skill_name': name[skill[0]], 'Skill_output': output[skill[0]].replace('\n',' ')})


def write_result_description(skills, name, output, filename):
	with open('result/' + filename + '.csv', 'a', newline='') as csvfile:
		fieldnames = ['Order', 'Category', 'Violated_policy', 'Skill_id', 'Skill_name', 'Skill_output']
		writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
#		writer.writeheader()
		for skill in skills:
			writer.writerow({ 'Order': '1', 'Category': 'Kids', 'Violated_policy': skill[1], 'Skill_id': skill[0], 'Skill_name': name[skill[0]], 'Skill_output': output[skill[0]].replace('\n',' ')})


def get_kids_violation_output(platform='skill'):
	if platform=='skill':
		output, name, skillid = violation.get_output_skill()
		skillname, skillpolicy, skilldescription = get_dataset_skill_kids()
		output_kids = get_output_kids(output, skillid, skillname)
		output = output_kids
	else:
		output, name, skillid = violation.get_output_action()
		skillname, skillpolicy, skilldescription = get_dataset_action_kids()
		output_kids = get_output_kids(output, skillid, skillname)
		output = output_kids

	data_collection_skills = violation.get_data_collection(output)
	website_skills = violation.get_website(output)
	mature_content_skills = violation.get_mature_content(output)
	positive_rating_skills = violation.get_positive_rating(output)

	write_result_output(data_collection_skills + website_skills + mature_content_skills + positive_rating_skills, skillid, name, output, platform + '_1_Kids_violation_output')


def get_kids_violation_description(platform='skill'):
	if platform=='skill':
		skillname, skillpolicy, skilldescription = get_dataset_skill_kids()
		positive_rating_skills = violation.get_positive_rating(skilldescription)
		mature_content_skills = violation.get_mature_content(skilldescription)
		write_result_description( mature_content_skills + positive_rating_skills, skillname, skilldescription, platform + '_1_Kids_violation_description')
	else:
		skillname, skillpolicy, skilldescription = get_dataset_action_kids()
		mature_content_skills = violation.get_mature_content(skilldescription)
		write_result_description( mature_content_skills, skillname, skilldescription, platform + '_1_kids_violation_description')


def get_kids():
	get_kids_violation_output(platform='skill')
	get_kids_violation_description(platform='skill')


