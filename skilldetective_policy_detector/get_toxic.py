import violation
import csv
import os
import json


def get_perspective_result():
	data = open('data/toxic_result_from_perspective.txt','r').read().split('\n')[:-1]
	perspective={}
	for line in data:
		perspective[line.split('\t')[0]]=json.loads(line.split('\t')[1])
	result = {}
	for sentence in perspective:
		try:
			max = 0
			for attribute in perspective[sentence]['attributeScores'].keys():
				if attribute == 'TOXICITY_FAST':
					continue
				value = perspective[sentence]['attributeScores'][attribute]['summaryScore']['value']
				if value > 0.9 and value > max:
					max = value
			if max > 0:
				result[sentence] = max
		except:
			continue
	return result


def write_result(result):
	with open('result/skill_6_General_toxic_result_from_perspective.csv', 'w', newline='') as csvfile:
		fieldnames = ['sentence']
		writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
		writer.writeheader()
		for data in result:
			writer.writerow({'sentence': data})


def get_toxic():
	result = get_perspective_result()
	write_result(result)
	x = os.system('cp data/toxic_after_manually_checking.csv result/skill_6_General_toxic_after_manually_checking.csv')
	get_kids_toxic()


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


def get_kids_toxic():
	f = open('result/skill_6_General_toxic_after_manually_checking.csv')
	reader = csv.reader(f)
	skills = []
	for row in reader:
		skills.append(row)
	skillname, skillpolicy, skilldescription = get_dataset_skill_kids()
	toxic_skills = []
	for skill in skills:
		if skill[3] in skillname:
			toxic_skills.append(skill)
	with open('result/skill_1_Kids_violation_output.csv', 'a', newline='') as csvfile:
		fieldnames = ['Order', 'Category', 'Violated_policy', 'Skill_id', 'Skill_name', 'Skill_output']
		writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
#		writer.writeheader()
		for skill in toxic_skills:
			writer.writerow({ 'Order': '1', 'Category': 'Kids', 'Violated_policy': skill[2], 'Skill_id': skill[3], 'Skill_name': skill[4], 'Skill_output': skill[5]})


