import csv
import os
import pickle
from tqdm import tqdm
import spacy
nlp = spacy.load('en_core_web_sm')

def get_data_collection():
	permission = {'Device Address':['address', 'location', 'geographic', 'gps', 'geolocation', 'position'], 'Device Country and Postal Code':['zipcode', 'zip code', 'address', 'location', 'postal code', 'geographic', 'gps','geolocation', 'country code', 'position', 'postalcode'], 'Email Address':['email', 'e-mail', 'mail'], 'First Name':['name'], 'Full Name':['name'], 'Location Services':['address', 'location' ,'geographic', 'gps','geolocation', 'position'], 'Mobile Number':['phone number', 'telephone', 'phone']}
	permit = {}
	name = {}
	policy = {}
	f = open('data/skill_with_permission.csv')
	reader = csv.reader(f)
	for row in reader:
		name[row[0]] = row[1]
		policy[row[0]] = row[2]
		permit[row[0]] = row[3]
	f.close()
	data_collection = {}
	for skill in permit:
		for data in permission:
			if data in permit[skill]:
				if skill in data_collection:
					data_collection[skill].append(permission[data])
				else:
					data_collection[skill] = [permission[data]]
	lacking_policy = []
	for skill in permit:
		if policy[skill] == '':
			lacking_policy.append(skill)
	return data_collection, lacking_policy, policy, name, permit


def get_incomplete_policy(data_collection):
	verb = open('data/verb.txt').read().split('\n')[0].split(',')
	pol = {}
	with tqdm(total = len(data_collection)) as pbar:
		for skill in data_collection:
			try:
				pol[skill] = nlp(open('data/skill_privacy_policy/' + skill + '.txt', encoding = 'unicode_escape').read().lower().replace('\n',' '))
			except:
				continue
			pbar.update(1)
	result = {}
	for skill in data_collection:
		try:
			doc = pol[skill]
		except:
			result[skill] = 0
			continue
		sentence_with_word = []
		for sent in doc.sents:
			for data in data_collection[skill]:
				sentence = sent.text
				if 'address' in data and ('email address' in sentence or 'ip address' in sentence):
					sentence = sentence.replace('ip address','').replace('email address','')
				if any (word in sentence for word in data) and any (word in sentence for word in verb):
					sentence_with_word.append(sent)
		have_permission = []
		for sent in sentence_with_word:
			for data in data_collection[skill]:
				if 'address' in data and ('email address' in sent.text or 'ip address' in sent.text):
					sentence = nlp(sent.text.replace('ip address','').replace('email address',''))
				else:
					sentence = sent
				m = 0
				n = 0
				if 'zip code' in data or 'phone number' in data:
					for l in data:
						if l in doc.text:
							m = 1
				for l in sentence:
					if l.lemma_ in data and 'NN' in l.tag_:
						m = 1
					if l.lemma_ in verb and 'VB' in l.tag_:
						n = 1
				if m == 1 and n == 1:
						have_permission.append(data)
		result[skill] = 1
		for data in data_collection[skill]:
			if data not in have_permission:
				result[skill] = 0
#	get_accuracy(result)			
	incomplete_policy = []
	for skill in result:
		if result[skill] == 0:
			incomplete_policy.append(skill)
	return incomplete_policy



def get_accuracy(result):
	data = open('data/permission_label.txt').read().split('\n')[:-1]
	label = {}
	for line in data:
		skillid, label_result = line.split('\t')
		label[skillid]=int(label_result)
	tp = 0
	fp = 0
	tn = 0
	fn = 0
	for skill in result:
		if result[skill] == 1 and label[skill] == 1:
			tp = tp + 1
		if result[skill] == 1 and label[skill] == 0:
			fp = fp + 1	
		if result[skill] == 0 and label[skill] == 1:
			fn = fn + 1
		if result[skill] == 0 and label[skill] == 0:
			tn = tn + 1

	precision = tp/(tp + fp)
	recall = tp/(tp + fn)
	f1 = 2/(1/precision + 1/recall)
	accuracy = (tp + tn)/len(result)

	print("\nDetecting complete policy: ")
	print("Precison: " + str(precision))
	print("Recall: " + str(recall))
	print("F1-Score: " + str(f1))
	print("Accuracy: " + str(accuracy))
	print("\nDetecting incomplete policy: ")
	print("Precison: " + str(tn/(tn + fn)))
	print("Recall: " + str(tn/(tn + fp)))
	print("F1-Score: " + str(2 * tn/(2 * tn + fn + fp)))
	print("Accuracy: " + str(accuracy))


def get_inconsistent_policy(data_collection):
	inconsistent_policy=[]
	remove_keywords=['child', 'age', 'under', '13', '18', 'unless', 'other than', 'except', 'financial information', 'only', 'categories']
	for skill in data_collection:
		try:
			data_practices = pickle.load(open('data/data_practice/' + skill + '.pickle', 'rb'), encoding='utf-8')
			negative_data_practices = pickle.load(open('data/negative_data_practice/' + skill + '.pickle', 'rb'), encoding='utf-8')
			not_collected_data = []
			for negative in negative_data_practices['negations']:
				for data_practice in data_practices:
					if data_practice[4] != 'collect':
						continue
					if data_practice[3] != negative:
						continue
					if any (word in data_practice[3].lower() for word in remove_keywords):
						continue
					if data_practice[2] != 'datum' and data_practice[2] != 'information' and 'person' not in data_practice[2]:
						continue
					not_collected_data.append(data_practice)
			if len(not_collected_data) > 0:
				inconsistent_policy.append(skill)
		except:
			continue
	return inconsistent_policy


def write_result(violation_type, skills, name, policy, permit):
	with open('result/skill_3_Collectdatawithpermission_privacy_policy_issue.csv', 'a', newline = '') as csvfile:
		fieldnames = ['Order', 'Category', 'Violated_policy', 'Skill_id', 'Skill_name', 'Skill_output']
		writer = csv.DictWriter(csvfile, fieldnames = fieldnames)
#		writer.writeheader()
		for skill in skills:
			writer.writerow({'Order': '3', 'Category': 'Collect_data_with_permission', 'Violated_policy': violation_type, 'Skill_id': skill, 'Skill_name': name[skill], 'Skill_output': permit[skill]})


def get_data_collection_permission():
	data_collection, lacking_policy, policy, name, permit = get_data_collection()
	incomplete_policy = get_incomplete_policy(data_collection)
	inconsistent_policy = get_inconsistent_policy(data_collection)
	write_result('lacking_policy', lacking_policy, name, policy, permit)
	write_result('incomplete_policy', incomplete_policy, name, policy, permit)
	write_result('inconsistent_policy', inconsistent_policy, name, policy, permit)

