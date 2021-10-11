#python3
import csv
import os
import re
import string
import violation
import spacy
nlp = spacy.load("en_core_web_sm")


def get_output_media():
	number = 0
	name = {}
	output = {}
	filenames = os.listdir('data/media')
	for folder in filenames:
		try:
			files = os.listdir('data/media/' + folder)
		except:
			continue
		for file in files:
			if file[-4:] == '.csv':
				f = open('data/media/' + folder + '/' + file, 'r')
				reader = csv.reader(f)
				for row in reader:
					number = number + 1
					name[number] = row[0].replace('Open ', '')
					output[number] = row[1].lower()
				f.close()
	return name, output


def get_unique_output(output):
	unique_output = {}
	for i in output:
		if output[i] in unique_output:
			unique_output[output[i]].append(i)
		else:
			unique_output[output[i]] = [i]
	return unique_output
	

def get_data_collection_media(unique_output):
	noun = [ 'address', 'name', 'email', 'email address', 'birthday', 'age', 'gender', 'location', 'contact', 'phonebook', 'profession', 'income', 'ssn', 'zipcode', 'ethnicity', 'affiliation', 'orientation', 'affiliation', 'postal code', 'zip code', 'first name', 'last name', 'full name', 'phone number', 'social security number', 'passport number', 'driver license', 'bank account number', 'debit card numbers']
	noun2 = [word.split()[-1] for word in noun]
	add_sentences = {"how old are you": 'age', "when were you born": 'age', "where do you live":'location' ,"where are you from": 'location', "what can i call you": 'name', 'male or female': 'gender'}
	skills = []
	for output in unique_output:
		if len(output) > 1000:
			continue
		if output[0:4] == 'here':
			continue
		if 'jesus' in output:
			continue
		sentences = re.split(r' *[\n\,\.!][\'"\)\]]* *', output)
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
							skills.append((output, 'collect data name'))
						else:
							skills.append((output, 'collect data ' + word))
			for sent in add_sentences:
					if sent in sentence.translate(str.maketrans('', '', string.punctuation)):
						skills.append((output, 'collect data ' + add_sentences[sent]))
	return skills


def write_result_output_data_collection(skills, name, unique_output, output, filename):
	with open('result/' + filename + '.csv', 'w', newline='') as csvfile:
		fieldnames = ['Order', 'Category', 'Violated_policy', 'Skill_id', 'Skill_name', 'Skill_output']
		writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
#		writer.writeheader()
		for skill in skills:
			for output_id in unique_output[skill[0]]:
				writer.writerow({'Order': '7', 'Category': 'Media', 'Violated_policy': skill[1], 'Skill_id': ' ', 'Skill_name': name[output_id], 'Skill_output': output[output_id].replace('\n',' ')})


def write_result_output(skills, name, output, filename):
	with open('result/' + filename + '.csv', 'a', newline='') as csvfile:
		fieldnames = ['Order', 'Category', 'Violated_policy', 'Skill_id', 'Skill_name', 'Skill_output']
		writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
#		writer.writeheader()
		for skill in skills:
			if 'www.dhcat.com' in output[skill[0]] or 'skornorth.com' in output[skill[0]] or 'sudsies.com' in output[skill[0]] or 'caribbeanpot.com' in output[skill[0]]:
				continue
			writer.writerow({'Order': '7', 'Category': 'Media', 'Violated_policy': skill[1], 'Skill_id': ' ', 'Skill_name': name[skill[0]], 'Skill_output': output[skill[0]].replace('\n',' ')})


def get_media_violation():
	name, output = get_output_media()
	unique_output = get_unique_output(output)

	data_collection_skills_media = get_data_collection_media(unique_output)
	website_skills_media = violation.get_website(output)
	mature_content_skills_media = violation.get_mature_content(output)
	positive_rating_skills_media = violation.get_positive_rating(output)

	write_result_output_data_collection( data_collection_skills_media, name, unique_output, output, 'skill_7_Media_violation')
	write_result_output( website_skills_media + mature_content_skills_media + positive_rating_skills_media, name, output, 'skill_7_Media_violation')


def get_media():
	get_media_violation()



