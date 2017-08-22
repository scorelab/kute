from django.shortcuts import render
from MicroService.tasks import checkPath,postNotifications
from django.http import HttpResponse 

# Create your views here.
## test function 
def check(request):
	checkPath.delay()
	return HttpResponse("Yes this works")

## Function To send notifications to clients 
def sendNotifications(request):
	## retrieve the get parameters
	owner=request.GET.get("Owner")
	rider=request.GET.get("Rider")
	notifType=request.GET.get("notifType")
	
	############# invoke celery task to send out notifications ##########
	postNotifications.delay(owner,rider,notifType)

	return HttpResponse("{Status:OK}")


