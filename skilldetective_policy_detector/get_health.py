import violation
import csv
import os
import re
import string
import spacy
nlp = spacy.load('en_core_web_sm')

def get_dataset_skill_health(health = True):
	skillname = {}
	skillpolicy = {}
	skilldescription = {}
	number = 0
	f = open('data/dataset_9_13_68846.csv','r')
	reader = csv.reader(f)
	for row in reader:
		if health:
			if 'health' in row[2].lower():
				skillname[row[0]] = row[1]
				skilldescription[row[0]] = row[7]
				if row[8] == 'null':
					continue
				skillpolicy[row[0]] = row[8]
		else:
			if 'health' not in row[2].lower():
				skillname[row[0]] = row[1]
				skilldescription[row[0]] = row[7]
				if row[8] == 'null':
					continue
				skillpolicy[row[0]] = row[8]
	return skillname, skillpolicy, skilldescription


def get_dataset_action_health(health = True):
	skillname = {}
	skillpolicy = {}
	skilldescription = {}
	number = 0
	f = open('data/action_16002.csv','r')
	reader = csv.reader(f)
	for row in reader:
		action_id = row[10].split('/')[-1]
		if health:
			if 'health' in row[2].lower():
				skillname[action_id] = row[0]
				skilldescription[action_id] = row[3]
				if row[4] == '':
					continue
				skillpolicy[row[0]] = row[8]
		else:
			if 'health' not in row[2].lower():
				skillname[action_id] = row[0]
				skilldescription[action_id] = row[3]
				if row[4] == '':
					continue
				skillpolicy[row[0]] = row[8]
	return skillname, skillpolicy, skilldescription


def get_output_category(output, skillid, skillname):
	output2 = {}
	for i in output:
		if skillid[i] in skillname:
			output2[i] = output[i]
	return output2


def get_output_data_collection(output, health, health2, add_sentences):
	skills = []
	for i in output:
		if len(output[i]) > 1000:
				continue
		if output[i][0:4] == 'here':
			continue
		if 'jesus' in output[i]:
			continue
		sentences = re.split(r' *[\n\,\.!][\'"\)\]]* *', output[i])
		for j in sentences:
			if 'here\'s what you need to know' in j:
				continue
			if any (word in j for word in health) and 'your' in j:
				doc = nlp(j)
			for l in health:
				if l not in j or 'your' not in j:
					continue
				for k in doc:
					if k.text == 'your' and k.head.text in health2 and k.head.text in l:
						skills.append((i, 'collect data ' + l))
			for l in add_sentences:
					if l in j.translate(str.maketrans('', '', string.punctuation)):
						skills.append((i, 'collect data ' +  add_sentences[l]))
	return skills


def get_description_data_collection(skilldescription, health, health2, add_sentences, healthornot):
	if healthornot == True:
		noun = health + [ 'address', 'name', 'email', 'email address', 'birthday', 'age', 'gender', 'location', 'contact', 'phonebook', 'profession', 'income', 'ssn', 'zipcode', 'ethnicity', 'affiliation', 'orientation', 'affiliation', 'postal code', 'zip code', 'first name', 'last name', 'full name', 'phone number', 'social security number', 'passport number', 'driver license', 'bank account number', 'debit card numbers']
		add_sentences={"how old are you": 'age', "when were you born": 'age', "where do you live":'location' ,"where are you from": 'location', "what can i call you": 'name', 'male or female':'gender'}
	else:
		noun = health
	noun2 = [word.split()[-1] for word in noun]
	skills = []
	for des_id in skilldescription:
		sentences = re.split(r' *[\n\,\.!][\'"\)\]]* *', skilldescription[des_id])
		for sentence in sentences:
			if 'here\'s what you need to know' in sentence:
				continue
			for word in health:
				if word in sentence and 'your' in sentence:
					doc = nlp(sentence)
					for k in doc:
						if k.text == 'your' and k.head.text in health2 and k.head.text in word:
							skills.append((des_id,  'collect data ' + word, sentence))
			for word in add_sentences:
					if word in sentence.translate(str.maketrans('', '', string.punctuation)):
						skills.append((des_id,  'collect data ' + add_sentences[word], sentence))
	return skills


def get_health_data_collection(output_health, skilldescription, healthornot, platform):
	if platform == 'skill':
		noun = open('data/healthdata.txt').read().lower().split('\n')[:-1]
		add_sentences = {}
	else:
		noun = []
		add_sentences = {}
	health = noun + ['medical record number', 'health insurance beneficiary number']
	health2 = [word.split()[-1] for word in health]
	if healthornot == True:
		skills_in_output = get_output_data_collection(output_health, health, health2, add_sentences) + violation.get_data_collection(output_health)
	else:
		skills_in_output = get_output_data_collection(output_health, health, health2, add_sentences)
	skills_in_description = get_description_data_collection(skilldescription, health, health2, add_sentences, healthornot)
	return skills_in_output, skills_in_description


def write_result_output(skills, name, skillid, output, platform):
	with open('result/'  + platform +  '_2_Health_data_collection.csv', 'w', newline='') as csvfile:
		fieldnames = ['Order', 'Category', 'Violated_policy', 'Skill_id', 'Skill_name', 'Skill_output']
		writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
#		writer.writeheader()
		for skill in skills:
			writer.writerow({'Order': '2', 'Category': 'Health', 'Violated_policy': skill[1], 'Skill_id': skillid[skill[0]], 'Skill_name': name[skill[0]], 'Skill_output': output[skill[0]].replace('\n',' ')})


def write_result_description(skills, skillname, output, platform):
	with open('result/'  + platform +  '_2_Health_data_collection.csv', 'a', newline='') as csvfile:
		fieldnames = ['Order', 'Category', 'Violated_policy', 'Skill_id', 'Skill_name', 'Skill_output']
		writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
#		writer.writeheader()
		for skill in skills:
			writer.writerow({'Order': '2', 'Category': 'Health', 'Violated_policy': skill[1],  'Skill_id': skill[0], 'Skill_name': skillname[skill[0]], 'Skill_output': skill[2]})


def get_data_collection( platform = 'skill', health = True):
	if platform == 'skill':
		output, name, skillid = violation.get_output_skill()
		skillname, skillpolicy, skilldescription = get_dataset_skill_health(health)
	else:
		output, name, skillid = violation.get_output_action()
		skillname, skillpolicy, skilldescription = get_dataset_action_health(health)

	output_health = get_output_category(output, skillid, skillname)
	skills_in_output, skills_in_description = get_health_data_collection(output_health, skilldescription, health, platform)
	write_result_output(set(skills_in_output), name, skillid, output_health, platform)
	write_result_description(set(skills_in_description), skillname, output_health, platform)


def get_disclamier(skilldescription, skillname, platform = 'skill'):
	disclamier = 'This tool does not provide medical advice, and is for informational and educational purposes only, and is not a substitute for professional medical advice.'
	doc_dis = nlp(disclamier)
	skills = []
	for des_id in skilldescription:
		if 'medical advice' in skilldescription[des_id].lower() or 'educational purpose' in skilldescription[des_id].lower() or 'informational purpose' in skilldescription[des_id].lower():
			skills.append(des_id)
			continue
		doc = nlp(skilldescription[des_id].lower())
		for sentence in doc.sents:
			if doc_dis.similarity(sentence) > 0.93:
				skills.append(des_id)
	with open('result/'  + platform +  '_2_Health_lack_disclaimer.csv', 'w', newline='') as csvfile:
		fieldnames = ['Order', 'Category', 'Violated_policy', 'Skill_id', 'Skill_name', 'Skill_output']
		writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
#		writer.writeheader()
		for skill in set(skilldescription)-set(skills):
			writer.writerow({'Order': '2', 'Category': 'Health', 'Violated_policy': 'lack a disclaimer', 'Skill_id': skill, 'Skill_name': skillname[skill], 'Skill_output': skilldescription[skill].replace('\n',' ')})


def get_health():
	get_data_collection( platform = 'skill', health = True )
	get_data_collection( platform = 'skill', health = False )

	skillname, skillpolicy, skilldescription = get_dataset_skill_health(health = True)
	get_disclamier(skilldescription, skillname, platform = 'skill')

	get_data_collection( platform = 'action', health = True )
	get_data_collection( platform = 'action', health = False )

	skillname, skillpolicy, skilldescription = get_dataset_action_health(health = True)
	get_disclamier(skilldescription, skillname, platform = 'action')


