import polyline,requests,json

from geopy.distance import vincenty
############## Sending first sample request to directions api ##########

def isRouteCompatible(source_owner,destination_owner,source_rider,destination_rider):
	###### Get coordinates for Owner route ##########
	url1="https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=AIzaSyAacNLiHHRaHxA___8h9cvTgtrqLfHEOUQ"%(source_owner,destination_owner)
	re_1=requests.get(url1)
	route_1_polyline=json.loads(re_1.text)["routes"][0]["overview_polyline"]["points"]
	re_1_list=polyline.decode(route_1_polyline)


	###### Get coordinates for Passenger route ##########
	url2="https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=AIzaSyAacNLiHHRaHxA___8h9cvTgtrqLfHEOUQ"%(source_rider,destination_rider)
	re_2=requests.get(url2)
	route_2_polyline=json.loads(re_2.text)["routes"][0]["overview_polyline"]["points"]
	re_2_list=polyline.decode(route_2_polyline)


	################## Start Matching in O(MxN) ########################

	matched_points=[]
	prev=None
	dist=0

	for cord in re_1_list:
		for cord2 in re_2_list:
			if cord==cord2:
				matched_points.append(cord)
				#print cord
				if(prev!=None):
					dist +=  vincenty(prev,cord).miles * 1.6
				prev=cord


	####### Calculating the offset from the first and last point #############
	########### initial offset ###############
	initial_offset=vincenty(re_2_list[0],matched_points[0]).miles * 1.6

	final_offset=vincenty(re_2_list[len(re_2_list)-1],matched_points[len(matched_points)-1]).miles * 1.6

	print re_2_list[0]
	print matched_points[0]
	print re_2_list[len(re_2_list)-1]
	print matched_points[len(matched_points)-1]
	
	print "The initial distance is ",initial_offset
	print "The Final Offset is ",final_offset
	print len(matched_points)
	print "The total common path distance is :",dist,"km"

	if(final_offset<0.5 and initial_offset<0.5):
		return True 
	else:
		return False
	##get the total distance of both the sides and get a particular proportion above which we can declare a match
	## get the first and last matched co_ordinate and find how far the two points are from the given co_ordinate