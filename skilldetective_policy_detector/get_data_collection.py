import violation
import csv
import os
import re
import pickle
from tqdm import tqdm
import spacy
nlp = spacy.load('en_core_web_sm')


def get_policy(skill_id):
	try:
		try:
			with open('/data/skill_privacy_policy/' + skill_id + '.txt') as f:
				data = f.read()
		except:
			with open('/data/action_privacy_policy/' + skill_id + '.txt') as f:
				data = f.read()			
	except:
		data = ''
	return data


def get_lacking_policy(data_collection, skillid, skillpolicy):
	lacking_policy = []
	for skill in data_collection:
		if skillid[skill[0]] not in skillpolicy:
			lacking_policy.append(skill)
	return lacking_policy


def get_incomplete_policy(data_collection, skillid, skillpolicy):
	similar_data = {}
	similar_data['address'] = ['address', 'location', 'geographic', 'gps', 'geolocation', 'position']
	similar_data['location'] = similar_data['address']
	similar_data['zipcode'] = ['zipcode', 'zip code', 'address', 'location', 'postal code', 'geographic', 'gps','geolocation', 'country code', 'position', 'postalcode']
	similar_data['zip code'] = similar_data['zipcode']
	similar_data['postal code'] = similar_data['zipcode']
	similar_data['email'] = ['email', 'e-mail', 'mail']
	similar_data['email addres'] = similar_data['email']
	similar_data['phone number'] = ['phone number', 'telephone', 'phone']
	verb = open('data/verb.txt').read().split('\n')[0].split(',')
	no_privacy_word = []
	getpolicyfailed = []
	incomplete_policy = []
	with tqdm(total = len(data_collection)) as pbar:
		for skill in data_collection:
			if skill[1] in similar_data:
				data = similar_data[skill[1]]
			else:
				data = [skill[1]]
			if skillid[skill[0]] not in skillpolicy:
				continue
			try:
				policy = get_policy(skillid[skill[0]]).lower()
			except:
				getpolicyfailed.append(skill)
				continue
			if any (word in policy for word in data) == False:
				no_privacy_word.append(skill)
				continue
			doc = nlp(policy)
			sent_with_word = []
			for sent in doc.sents:
				sentence = sent.text
				if 'address' in data and ('email address' in sentence or 'ip address' in sentence):
					sentence=sentence.replace('ip address', '').replace('email address', '')
				if any (word in sentence for word in data) and any (word in sentence for word in verb):
					sent_with_word.append(sent)
			have_data = 0
			for sent in sent_with_word:
				if 'address' in data and ('email address' in j.text or 'ip address' in j.text):
					sentence = nlp(sent.text.replace('ip address','').replace('email address',''))
				else:
					sentence = sent
				m = 0
				n = 0
				if 'zip code' in data or 'phone number' in data or 'email address':
					for l in data:
						if l in doc.text:
							m = 1
				for l in sentence:
					if l.lemma_ in data and 'NN' in l.tag_:
						m = 1
					if l.lemma_ in verb and 'VB' in l.tag_:
						n = 1
				if m == 1 and n == 1:
					have_data = 1
			if have_data != 1:
				incomplete_policy.append(skill)
			pbar.update(1)
	return no_privacy_word, incomplete_policy, getpolicyfailed


def get_inconsistent_policy(data_collection, skillid, skillpolicy):
	inconsistent_policy = []
	remove_keywords = ['child', 'age','under','13','18','unless','other than','except','financial information','only','categories']
	for skill in data_collection:
		try:
			data_practices = pickle.load(open('/data/data_practice/' + skillid[skill[0]]+'.pickle', 'rb'), encoding='utf-8')
			negative_data_practices = pickle.load(open('/data/data_practice//' + skillid[skill[0]]+'.pickle', 'rb'), encoding='utf-8')		
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


def get_des_inconsistent(data_collection, skilldescription, skillid):
	verb=['collect','obtain','need','require','ask']
	noun=['address', 'name', 'email', 'birthday', 'age', 'gender', 'location', 'data', 'contact', 'phonebook', 'SMS', 'call', 'profession', 'income', 'ssn','zipcode','number','code','account']
	result=[]
	for skill in data_collection:
		sentences = re.split(r' *[\n\,\.!][\'"\)\]]* *', skilldescription[skillid[skill[0]]])
		for sent in sentences:
			if 'no' not in sent or 'you' not in sent:
				continue
			doc = nlp(sent)
			for word in doc:
				if word.pos_ == 'NOUN' and word.text in noun and word.head.text in verb:
					result.append(skill)
	return result


def get_wrong_configure_google(data_collection, output):
	permission = ['name','address','location']
	skills = []
	for skill in data_collection:
		if skill[1] in permission:
			if 'can i share' not in output[skill[0]] and 'google' not in output[skill[0]]:
				skills.append(skill)
	return skills


def write_result(violation_type, skills, output, skillid, name, platform):
	with open('result/' + platform + '_4_Collectdatawithoutpermission_privacy_policy_issue.csv', 'a', newline = '') as csvfile:
		fieldnames = ['Order', 'Category', 'Violated_policy', 'Skill_id', 'Skill_name', 'Skill_output']
		writer = csv.DictWriter(csvfile, fieldnames = fieldnames)
#		writer.writeheader()
		for skill in skills:
			writer.writerow({ 'Order': '4', 'Category': 'Collect_data_without_permission', 'Violated_policy': violation_type + ' ' + skill[1], 'Skill_id': skillid[skill[0]], 'Skill_name': name[skill[0]].replace('Talk to ', ''), 'Skill_output': output[skill[0]].replace('\n','')})


def get_privacy_policy_result(platform='skill'):
	if platform == 'skill':
		skillname, skillpolicy, skilldescription = violation.get_dataset_skill()
		output, name, skillid = violation.get_output_skill()
	else:
		skillname, skillpolicy, skilldescription = violation.get_dataset_action()
		output, name, skillid = violation.get_output_action()

	data_collection = violation.get_data_collection(output)
	lacking_policy = get_lacking_policy(data_collection, skillid, skillpolicy)
	no_privacy_word, incomplete_policy, getpolicyfailed = get_incomplete_policy(data_collection, skillid, skillpolicy)
	inconsistent_policy = get_inconsistent_policy(data_collection, skillid,skillpolicy)
	des_inconsistent = get_des_inconsistent(data_collection, skilldescription, skillid)

	write_result('lacking_privacy_policy', lacking_policy, output, skillid, name, platform)
	write_result('incomplete_privacy_policy', no_privacy_word + incomplete_policy, output, skillid, name, platform)
	write_result('inconsistent_privacy_policy', inconsistent_policy, output, skillid, name, platform)

	if platform == 'action':
		wrong_configure = get_wrong_configure_google(data_collection, output)
		write_result('wrong_configure_action', wrong_configure, output, skillid, name, platform)


def get_data_collection():
	get_privacy_policy_result(platform = 'skill')
	get_privacy_policy_result(platform = 'action')


