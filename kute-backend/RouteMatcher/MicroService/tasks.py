from celery.decorators import task
from celery import shared_task
from celery.utils.log import get_task_logger
import time
import requests
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import json 
c={
  "type": "service_account",
  "project_id": "kute-ec351",
  "private_key_id": "bdb18465d7a9f2e777982fc3f5d60157ec90c283",
  "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQD0BbYeOKjWXeN+\nEB13AAlcFmlFPVM1OG0m1Z+iZzEIUrMS4KeJw3uRjq6JSkBgmkJ2y9BkbAopNmiC\nrtyB7ddPR50NCUZPglbOosO0aU1gSU2m1q/m5pGTAOBuXe482XyZtljblPeOLB5/\nX7zLm7mbePl6uswcc3lq4d/w7mw3kA/sj3T4NXXZDi9/OHtuQb1flaxt/YhoAyMW\n1jlI09x6lSH9p80+VhOdOJ9WtLToUwf/YpcLep0ng5iBOFRxdiXYmigKO+CFW70p\nFnPpdxfO7vMbu8dJUxNpHe+1GlVAogF8cWYZpOk7KZvD7+2X9HH2QdnKNbVUhIRd\nVnkhTdzPAgMBAAECggEAJsTaMdlQ+GSIoP3WHegWFJjaQcNj+w0gyQOLQ1s/Xtym\nEbuP3U3UTdm24Mi4tgBcIJFHZbpahOtdgFcKto5/NNvNAltfsysjN9zZLfUa83oX\nSbNLKr/6ueRw7mf4UpeVJGYO861VBV6nZmzjSw/4gFB129SzWq59SF4O3QbsOjQB\nkl3sV+qkG7xDnjh2cJu3ILw8tea40wQZOr1FrBN8CFlreFVGwVtU61Bfe5VkJFrs\nfJJArh+A8VFs+ecfiOqqzpfCWj/lgFd35QtDlqMyVOOfW3eOA9FIFT2w3jH0bzb4\nOk6stlkULuZPTYq2eBlCeCSP2SFLEkls2ZFBj9wg3QKBgQD+e0tDYeRwgzdxh+oA\n0Md47jj9TR7x9mimI6aczdLKFulVbwPXej1ql8BDdbgQNHXj1wmePR4u4KNAqlqS\nokcv29+NtK5pr8pTvKGzisobp7AQhupBlsllHZxX+lvZ3FxHJdI5GeRPzU4sEgQu\nehcQTE36piTenbNW5F61MI1tkwKBgQD1enEAclFlsHCH54+Z03yLRAtFW4GSKOtL\nTze/XD8fJ2ThGOGBoLjkhYGI4r94bJocFcqCxduWSauPilmopeS4XeO3loRBIqO+\nV2j80pL7NW3dMuE6vXN6O3LSV7nOrtJFQRDY8exkNTOaYYrwyDx6DmNhYTN2yqNa\nXi2YOPp5VQKBgE+EOwo9BmJZvfNNosLKeenBljEf7fFxK1XugdsxPRJEgnhdjffA\njHxIGp15pR/7JHMi+DBnrIy9SIWmNVLoPhIoQ/xFXtJLSY9Mu8IcNfbaONuRLJV+\nBkQAMqAS7KxwfK0Gll+dRYfiAPEoWAIlyBshnKQbUh31bNpT1XwMRcTdAoGBAIYT\nSTMYPVMQSnZASJOZClY6ZPmN4DhHdzRb4TP4m1VVu+iiIVEeyr2uGbD9P9zzXDzo\nvgItNSFhvX2Z8ByH92OnjF/Sqwu0csDclzA3hyYD6ay+RHxDy5XAcJdoaMj1fU1s\nG1qS0C1vTW8Nxch7ZWS5BRjD8Ur5pL0P4VFaFZw9AoGAAwyLcHFMMqtEa8alXPnz\nSoBQPy24PFww+dcgjbYXMi2sQtnTGOSjpYP/75dktK9ruX9bGYCTYts8BJmtyAWJ\nnWPawetKW0vpFDrnbLnauIeVRnCl9cxiyBf7+bc1CFaCDGnBeZ4UFc6HKO6k2cbS\nexDaVN2Q8Ovgme71MksWB+s=\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-ph2lg@kute-ec351.iam.gserviceaccount.com",
  "client_id": "102846842006987322514",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://accounts.google.com/o/oauth2/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-ph2lg%40kute-ec351.iam.gserviceaccount.com"}

#################### Initialising FireBase ##################
cred = credentials.Certificate(c)
logger = get_task_logger(__name__)
app=firebase_admin.initialize_app(cred, {
		'databaseURL': 'https://kute-ec351.firebaseio.com/'
	})

########################## Task to check whether the two routes are compatible or not  ######################
@task
def checkPath():
	import firebase_admin
	from firebase_admin import credentials
	from firebase_admin import db
	import json 

	########### Importing the Matching Algorithm #########
	from RouteAlgo import isRouteCompatible
	c={
  "type": "service_account",
  "project_id": "kute-ec351",
  "private_key_id": "bdb18465d7a9f2e777982fc3f5d60157ec90c283",
  "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQD0BbYeOKjWXeN+\nEB13AAlcFmlFPVM1OG0m1Z+iZzEIUrMS4KeJw3uRjq6JSkBgmkJ2y9BkbAopNmiC\nrtyB7ddPR50NCUZPglbOosO0aU1gSU2m1q/m5pGTAOBuXe482XyZtljblPeOLB5/\nX7zLm7mbePl6uswcc3lq4d/w7mw3kA/sj3T4NXXZDi9/OHtuQb1flaxt/YhoAyMW\n1jlI09x6lSH9p80+VhOdOJ9WtLToUwf/YpcLep0ng5iBOFRxdiXYmigKO+CFW70p\nFnPpdxfO7vMbu8dJUxNpHe+1GlVAogF8cWYZpOk7KZvD7+2X9HH2QdnKNbVUhIRd\nVnkhTdzPAgMBAAECggEAJsTaMdlQ+GSIoP3WHegWFJjaQcNj+w0gyQOLQ1s/Xtym\nEbuP3U3UTdm24Mi4tgBcIJFHZbpahOtdgFcKto5/NNvNAltfsysjN9zZLfUa83oX\nSbNLKr/6ueRw7mf4UpeVJGYO861VBV6nZmzjSw/4gFB129SzWq59SF4O3QbsOjQB\nkl3sV+qkG7xDnjh2cJu3ILw8tea40wQZOr1FrBN8CFlreFVGwVtU61Bfe5VkJFrs\nfJJArh+A8VFs+ecfiOqqzpfCWj/lgFd35QtDlqMyVOOfW3eOA9FIFT2w3jH0bzb4\nOk6stlkULuZPTYq2eBlCeCSP2SFLEkls2ZFBj9wg3QKBgQD+e0tDYeRwgzdxh+oA\n0Md47jj9TR7x9mimI6aczdLKFulVbwPXej1ql8BDdbgQNHXj1wmePR4u4KNAqlqS\nokcv29+NtK5pr8pTvKGzisobp7AQhupBlsllHZxX+lvZ3FxHJdI5GeRPzU4sEgQu\nehcQTE36piTenbNW5F61MI1tkwKBgQD1enEAclFlsHCH54+Z03yLRAtFW4GSKOtL\nTze/XD8fJ2ThGOGBoLjkhYGI4r94bJocFcqCxduWSauPilmopeS4XeO3loRBIqO+\nV2j80pL7NW3dMuE6vXN6O3LSV7nOrtJFQRDY8exkNTOaYYrwyDx6DmNhYTN2yqNa\nXi2YOPp5VQKBgE+EOwo9BmJZvfNNosLKeenBljEf7fFxK1XugdsxPRJEgnhdjffA\njHxIGp15pR/7JHMi+DBnrIy9SIWmNVLoPhIoQ/xFXtJLSY9Mu8IcNfbaONuRLJV+\nBkQAMqAS7KxwfK0Gll+dRYfiAPEoWAIlyBshnKQbUh31bNpT1XwMRcTdAoGBAIYT\nSTMYPVMQSnZASJOZClY6ZPmN4DhHdzRb4TP4m1VVu+iiIVEeyr2uGbD9P9zzXDzo\nvgItNSFhvX2Z8ByH92OnjF/Sqwu0csDclzA3hyYD6ay+RHxDy5XAcJdoaMj1fU1s\nG1qS0C1vTW8Nxch7ZWS5BRjD8Ur5pL0P4VFaFZw9AoGAAwyLcHFMMqtEa8alXPnz\nSoBQPy24PFww+dcgjbYXMi2sQtnTGOSjpYP/75dktK9ruX9bGYCTYts8BJmtyAWJ\nnWPawetKW0vpFDrnbLnauIeVRnCl9cxiyBf7+bc1CFaCDGnBeZ4UFc6HKO6k2cbS\nexDaVN2Q8Ovgme71MksWB+s=\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-ph2lg@kute-ec351.iam.gserviceaccount.com",
  "client_id": "102846842006987322514",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://accounts.google.com/o/oauth2/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-ph2lg%40kute-ec351.iam.gserviceaccount.com"}

	#################### Initialising FireBase ##################
	cred = credentials.Certificate(c)
	# Initialize the app with a service account, granting admin privileges
	app=firebase_admin.initialize_app(cred, {
	    'databaseURL': 'https://kute-ec351.firebaseio.com/'
	})

	# As an admin, the app has access to read and write all data, regradless of Security Rules
	#################### Create references to Firebase database ##############
	friend_ref = db.reference('Friends')
	route_ref=db.reference("Routes")
	user_ref=db.reference("Users")

	## get person name
	## We will get the person id dynamically 
	## dynamically person_id
	person=json.dumps(user_ref.order_by_key().equal_to("1582574208439481").get())
	person=json.loads(person)
	person_name=person.values()[0]["name"]
	##print person_name
	source_cordsy="28.619365,77.033534"
	destination_cordsy="28.557110,77.06130"



	user_name=person_name
	user_friends=[]

	try:
		import json
		######## Querying for the list of all friends##############
		user_friends_dict=json.dumps(friend_ref.order_by_key().equal_to(user_name).get())
		user_friends_dict=json.loads(user_friends_dict)
		user_friends=user_friends_dict[user_name].keys()
		##print user_friends
		
		################ Query For Friend routes ############
		for friend in user_friends:
			friend_routes={}
			friend_routes=route_ref.order_by_key().equal_to(friend).get()
			friend_route_list=friend_routes.values()

			for route in friend_route_list:
				route_dict=route.values()[0]
				print route_dict["name"]
				print "The route dict is ",route_dict
				if(isRouteCompatible(route_dict["source_cords"],route_dict["destination_cords"],source_cordsy,destination_cordsy)):
					print "Route Matched"+route_dict["name"]
					print friend 
					return friend

				else:
					continue

		
		
	except Exception as e:
		print "Exception in findMatchingRoute :",e
		return None

	####################### End of CheckPath #################


########################## Task to send out general notifications ####################
####################### Sending out notification to the rider for confirmation or to both owner and rider that the trip has been booked ###########
@task
def postNotifications(owner,rider,notifType):
	############### We will have two types of notification as discussed in the above comments ###########
	########## Type: confirmAwait which is sent out to the rider asking for whether he wants to travel with this person ################
	########## Type: confirmed which is  sent out to both the owner and rider declaring that the ride is confirmed ##########################

	import requests
	import firebase_admin
	from firebase_admin import credentials
	from firebase_admin import db
	import json 
	c={
  "type": "service_account",
  "project_id": "kute-ec351",
  "private_key_id": "bdb18465d7a9f2e777982fc3f5d60157ec90c283",
  "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQD0BbYeOKjWXeN+\nEB13AAlcFmlFPVM1OG0m1Z+iZzEIUrMS4KeJw3uRjq6JSkBgmkJ2y9BkbAopNmiC\nrtyB7ddPR50NCUZPglbOosO0aU1gSU2m1q/m5pGTAOBuXe482XyZtljblPeOLB5/\nX7zLm7mbePl6uswcc3lq4d/w7mw3kA/sj3T4NXXZDi9/OHtuQb1flaxt/YhoAyMW\n1jlI09x6lSH9p80+VhOdOJ9WtLToUwf/YpcLep0ng5iBOFRxdiXYmigKO+CFW70p\nFnPpdxfO7vMbu8dJUxNpHe+1GlVAogF8cWYZpOk7KZvD7+2X9HH2QdnKNbVUhIRd\nVnkhTdzPAgMBAAECggEAJsTaMdlQ+GSIoP3WHegWFJjaQcNj+w0gyQOLQ1s/Xtym\nEbuP3U3UTdm24Mi4tgBcIJFHZbpahOtdgFcKto5/NNvNAltfsysjN9zZLfUa83oX\nSbNLKr/6ueRw7mf4UpeVJGYO861VBV6nZmzjSw/4gFB129SzWq59SF4O3QbsOjQB\nkl3sV+qkG7xDnjh2cJu3ILw8tea40wQZOr1FrBN8CFlreFVGwVtU61Bfe5VkJFrs\nfJJArh+A8VFs+ecfiOqqzpfCWj/lgFd35QtDlqMyVOOfW3eOA9FIFT2w3jH0bzb4\nOk6stlkULuZPTYq2eBlCeCSP2SFLEkls2ZFBj9wg3QKBgQD+e0tDYeRwgzdxh+oA\n0Md47jj9TR7x9mimI6aczdLKFulVbwPXej1ql8BDdbgQNHXj1wmePR4u4KNAqlqS\nokcv29+NtK5pr8pTvKGzisobp7AQhupBlsllHZxX+lvZ3FxHJdI5GeRPzU4sEgQu\nehcQTE36piTenbNW5F61MI1tkwKBgQD1enEAclFlsHCH54+Z03yLRAtFW4GSKOtL\nTze/XD8fJ2ThGOGBoLjkhYGI4r94bJocFcqCxduWSauPilmopeS4XeO3loRBIqO+\nV2j80pL7NW3dMuE6vXN6O3LSV7nOrtJFQRDY8exkNTOaYYrwyDx6DmNhYTN2yqNa\nXi2YOPp5VQKBgE+EOwo9BmJZvfNNosLKeenBljEf7fFxK1XugdsxPRJEgnhdjffA\njHxIGp15pR/7JHMi+DBnrIy9SIWmNVLoPhIoQ/xFXtJLSY9Mu8IcNfbaONuRLJV+\nBkQAMqAS7KxwfK0Gll+dRYfiAPEoWAIlyBshnKQbUh31bNpT1XwMRcTdAoGBAIYT\nSTMYPVMQSnZASJOZClY6ZPmN4DhHdzRb4TP4m1VVu+iiIVEeyr2uGbD9P9zzXDzo\nvgItNSFhvX2Z8ByH92OnjF/Sqwu0csDclzA3hyYD6ay+RHxDy5XAcJdoaMj1fU1s\nG1qS0C1vTW8Nxch7ZWS5BRjD8Ur5pL0P4VFaFZw9AoGAAwyLcHFMMqtEa8alXPnz\nSoBQPy24PFww+dcgjbYXMi2sQtnTGOSjpYP/75dktK9ruX9bGYCTYts8BJmtyAWJ\nnWPawetKW0vpFDrnbLnauIeVRnCl9cxiyBf7+bc1CFaCDGnBeZ4UFc6HKO6k2cbS\nexDaVN2Q8Ovgme71MksWB+s=\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-ph2lg@kute-ec351.iam.gserviceaccount.com",
  "client_id": "102846842006987322514",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://accounts.google.com/o/oauth2/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-ph2lg%40kute-ec351.iam.gserviceaccount.com"}

	#################### Initialising FireBase ##################
	cred = credentials.Certificate(c)
	# Initialize the app with a service account, granting admin privileges
	# app=firebase_admin.initialize_app(cred, {
	# 	'databaseURL': 'https://kute-ec351.firebaseio.com/'
	# })
	
	

	head={'project_id': 'kute-ec351', 'Content-Type': 'application/json', 'Authorization': 'key=AAAAhaDytwk:APA91bGLIVdeWNocYSj_zm6dHAQ4cSmXBRR9xdolqe5ENgjbmaSRv_F2GraNpHNP-tIlvFSd5S6OuiiYqbNa0-cHfAjnEpaUjb_heDvcbW7TWrRD6kqccPALnaLsR4mkXIHHPyaOoXWe'}
	# messagestring=raw_input("Enter the message you wish to send as notification:")

	if(notifType=="confirmAwait"):
		####### Send notification to the rider ############
		token=getFCMToken(db,rider)
		d={'to': token, 
			'priority': 10,
			"data" : {
		      "Message" : "Foundride", 
		    }}
		notif_request=requests.post(url='https://fcm.googleapis.com/fcm/send',headers=head,data=json.dumps(d))
		print notif_request.text


	elif (notifType=="confirmed"):
		###### send notifications to owner and rider ############
		##### First send to owner

		token=getFCMToken(db,owner)
		d={'to': token
		, 'priority': 10,
			"data" : {
		      "Message" : "ConfirmedRide",
		      
		      
		    }}
		notif_request=requests.post(url='https://fcm.googleapis.com/fcm/send',headers=head,data=json.dumps(d))
		
		token=getFCMToken(db,rider)
		d={'to': token
		, 'priority': 10,
			"data" : {
		      "Message" : "ConfirmedRide",
		      
		      
		    }}
		notif_request=requests.post(url='https://fcm.googleapis.com/fcm/send',headers=head,data=json.dumps(d))
	
	############## End of postNotifications ###############


################ function to get the FCM Token from firebase given we have the user id ###########
def getFCMToken(firebaseDbInstance,personId):
	################ retrieve the person's token from firebase ##############
	import json
	user_ref=firebaseDbInstance.reference("Users")

	person=json.dumps(user_ref.order_by_key().equal_to(personId).get())
	person=json.loads(person)
	token=person.values()[0]["token"]
	return token
	