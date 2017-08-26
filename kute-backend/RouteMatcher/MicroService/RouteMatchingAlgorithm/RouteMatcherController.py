####### Importing Basic Packages ############
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import json 

########### Importing the Matching Algorithm #########
from RouteAlgo import isRouteCompatible

#################### Initialising FireBase ##################
cred = credentials.Certificate('service-account.json')
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
person=json.dumps(user_ref.order_by_key().equal_to("10203398330196489").get())
person=json.loads(person)
person_name=person.values()[0]["name"]
print person_name
source_cordsy="28.619365,77.033534"
destination_cordsy="28.557110,77.06130"


######## End Of Firebase Initialisation



def findMatchingRoute(person_name,source_cords,destination_cords):
	user_name=person_name
	user_friends=[]

	try:
		######## Querying for the list of all friends##############
		user_friends_dict=json.dumps(friend_ref.order_by_key().equal_to(user_name).get())
		user_friends_dict=json.loads(user_friends_dict)
		user_friends=user_friends_dict[user_name].keys()
		print user_friends
		
		################ Query For Friend routes ############
		for friend in user_friends:
			friend_routes={}
			friend_routes=route_ref.order_by_key().equal_to(friend).get()
			friend_route_list=friend_routes.values()

			for route in friend_route_list:
				route_dict=route.values()[0]
				print route_dict["name"]
				print "The route dict is ",route_dict
				if(isRouteCompatible(route_dict["source_cords"],route_dict["destination_cords"],source_cords,destination_cords)):
					print "Route Matched"+route_dict["name"]
					print friend 
					return friend

				else:
					continue

		
		
	except Exception as e:
		print "Exception in findMatchingRoute :",e
		return None


findMatchingRoute("Vishrut Kohli",source_cordsy,destination_cordsy)



