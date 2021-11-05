import csv
import os
import re
import spacy
nlp = spacy.load("en_core_web_lg")

from tqdm import tqdm
import string


def get_output_skill():
	filename = ['output/skill']
	number = 0
	name = {}
	output = {}
	skillid = {}
	for folder in filename:
		b = os.listdir(folder)
		for file in b:
			if file[-4:] == '.csv':
				f = open( folder + '/' + file, 'r')
				reader = csv.reader(f)
				for row in reader:
					number = number + 1
					name[number] = row[0].replace('Open ', '')
					output[number] = row[1].lower()
					skillid[number] = row[5]
				f.close()
	return output, name, skillid


def get_output_action():
	filename = ['output/action']
	number = 0
	name = {}
	output = {}
	skillid = {}
	for folder in filename:
		b = os.listdir(folder)
		for file in b:
			if file[-4:] == '.csv':
				f = open( folder + '/' + file,'r')
				reader = csv.reader(f)
				for row in reader:
					number = number+1
					name[number] = row[0].replace('Open ', '')
					output[number] = row[1].lower()
					skillid[number] = row[5].split('/')[-1]
				f.close()
	return output, name, skillid


def get_dataset_skill():
	skillname = {}
	skillpolicy = {}
	skilldescription = {}
	number = 0
	f = open('data/dataset_9_13_68846.csv', 'r')
	reader = csv.reader(f)
	for row in reader:
		skillname[row[0]] = row[1]
		skilldescription[row[0]] = row[7]
		if row[8] == 'null':
			continue
		skillpolicy[row[0]] = row[8]
	return skillname, skillpolicy, skilldescription


def get_dataset_action():
	skillname = {}
	skillpolicy = {}
	skilldescription = {}
	number = 0
	f = open('data/action_16002.csv', 'r')
	reader = csv.reader(f)
	for row in reader:
		action_id = row[10].split('/')[-1]
		skillname[action_id] = row[0]
		skilldescription[action_id] = row[3]
		if row[4] == '':
			continue
		skillpolicy[action_id] = row[4]
	return skillname, skillpolicy, skilldescription


def get_data_collection(output):
	noun = [ 'address', 'name', 'email', 'email address', 'birthday', 'age', 'gender', 'location', 'contact', 'phonebook', 'profession', 'income', 'ssn', 'zipcode', 'ethnicity', 'affiliation', 'orientation', 'affiliation', 'postal code', 'zip code', 'first name', 'last name', 'full name', 'phone number', 'social security number', 'passport number', 'driver license', 'bank account number', 'debit card numbers']
	noun2 = [word.split()[-1] for word in noun]
	add_sentences = {"how old are you": 'age', "when were you born": 'age', "where do you live":'location' ,"where are you from": 'location', "what can i call you": 'name', 'male or female': 'gender'}
	skills = []
	for output_id in output:
		if len(output[output_id]) > 1000:
			continue
		if output[output_id][0:4] == 'here':
			continue
		if 'jesus' in output[output_id]:
			continue
		sentences = re.split(r' *[\n\,\.!][\'"\)\]]* *', output[output_id])
		for sentence in sentences:
			if 'here\'s what you need to know' in sentence:
				continue
			if 'your name is' in sentence:
				continue
			if any (word in sentence for word in noun) and 'your' in sentence:
				doc = nlp(sentence)
			for word in noun:
				if word not in sentence or 'your' not in sentence:
					continue
				if word == 'name' and 'your name' not in sentence:
					continue
				if word == 'address' and 'email address' in sentence:
					continue
				if word == 'phone number' and 'dial your local emergency' in sentence:
					continue
				for l in doc:
					if l.text == 'your' and l.head.text in noun2 and l.head.text in word:
						if 'name' in word:
							skills.append((output_id, 'collect data name'))
						else:
							skills.append((output_id, 'collect data ' + word))
			for sent in add_sentences:
					if sent in sentence.translate(str.maketrans('', '', string.punctuation)):
						skills.append((output_id, 'collect data ' + add_sentences[sent]))
	return skills


def get_website(output):
	skills = []
	for output_id in output:
		if 'amazon.com' not in output[output_id] and ('www.' in output[output_id] or ('.com' in output[output_id] and '@' not in output[output_id])):
			skills.append((output_id, "contains out website"))
		if 'dot' in output[output_id] and ' com ' in output[output_id]:        
			if 'com' in output[output_id][output[output_id].find('dot')-100:output[output_id].find('dot') + 100]:
				skills.append((output_id, "contains out website"))
	return skills


def get_mature_content(output):
	skills = []
	for output_id in output:
		if 'mature content' in output[output_id]:
			skills.append((output_id, "contains mature content"))
	return skills


def get_positive_rating(output):
	skills = []
	keywords = ['5 star review', 'five star review', '5-star review', '5 star rating', 'five star rating', '5-star rating']
	with tqdm(total = len(output)) as pbar:
		for output_id in output:
			if any (word in output[output_id].lower() for word in keywords):
				doc = nlp(output[output_id].lower())  
				for sentence in doc.sents:
					if any (word in sentence.text for word in keywords):
						for word in sentence:
							if word.text == 'star' and (word.head.head.lemma_ == 'leave' or word.head.head.lemma_ == 'give'):
								skills.append((output_id, "contains asking for positive rating"))
			pbar.update(1)
	return skills


def get_sentences(output):
	sentences = {}
	for skill_id in output:
		try:
			for sent in tokenizer.tokenize(output[skill_id].lower()):
				if sent in sentences:
					sentences[sent].append(skill_id)
				else:
					sentences[sent] = [skill_id]
		except:
			continue
	return sentences



