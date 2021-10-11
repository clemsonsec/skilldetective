import csv
import os

import get_kids
import get_health
import get_data_collection_permission
import get_data_collection
import get_positive_rating
import get_toxic
import get_media



def generate_all_results():
	files = os.listdir('result')
	files.sort()
	os.system('mkdir result/details')
	skills = []
	actions = []
	for file in files:
		if 'perspective' in file:
			x = os.system('mv result/' + file + ' result/details/' + file)
			continue
		if 'skill' in file:
			with open('result/' + file) as f:
				reader = csv.reader(f)
				for row in reader:
					skills.append(row)
		if 'action' in file:
			with open('result/' + file) as f:
				reader = csv.reader(f)
				for row in reader:
					actions.append(row)
		x = os.system('mv result/' + file + ' result/details/' + file)
	unique_skills = []
	unique_actions = []
	for skill in skills:
		if skill in unique_skills:
			continue
		unique_skills.append(skill)
	for action in actions:
		if action in unique_actions:
			continue
		unique_actions.append(action)	
	with open('result/all_problematic_skills_result.csv', 'w', newline='') as csvfile:
		fieldnames = ['Order', 'Category', 'Violated_policy', 'Skill_id', 'Skill_name', 'Skill_output']
		writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
		writer.writeheader()
		for skill in unique_skills:
			x = writer.writerow({'Order': skill[0], 'Category': skill[1], 'Violated_policy': skill[2], 'Skill_id': skill[3], 'Skill_name': skill[4], 'Skill_output': skill[5]  })
	with open('result/all_problematic_actions_result.csv', 'w', newline='') as csvfile:
		fieldnames = ['Order', 'Category', 'Violated_policy', 'Action_id', 'Action_name', 'Action_output']
		writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
		writer.writeheader()
		for action in unique_actions:
			x = writer.writerow({'Order': action[0], 'Category': action[1], 'Violated_policy': action[2], 'Action_id': action[3], 'Action_name': action[4], 'Action_output': action[5]  })


def main():
    print('\n-------------Detecting Kids skills-------------\n')
    get_kids.get_kids()
    print('\n-------------Detecting Health skills-------------\n')
    get_health.get_health()
    print('\n-------------Detecting skills with Data Collection with permission-------------\n')
    get_data_collection_permission.get_data_collection_permission()
    print('\n-------------Detecting skills with Data Collection without permission-------------\n')
    get_data_collection.get_data_collection()
    print('\n-------------Detecting skills with Asking for positive rating-------------\n')
    get_positive_rating.get_positive_rating()
    print('\n-------------Detecting skills with Toxic content-------------\n')
    get_toxic.get_toxic()
    print('\n-------------Detecting skills with violation in Media-------------\n')
    get_media.get_media()
    generate_all_results()


if __name__ == "__main__":
	main()
