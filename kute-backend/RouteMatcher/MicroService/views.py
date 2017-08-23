from django.shortcuts import render
from MicroService.tasks import checkPath
from django.http import HttpResponse 

# Create your views here.
## test function 
def check(request):
	checkPath.delay()
	return HttpResponse("Yes this works")