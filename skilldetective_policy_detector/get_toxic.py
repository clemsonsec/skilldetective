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


