from __future__ import absolute_import
import os
from celery import Celery
from django.conf import settings

project = Celery('RouteMatcher')
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'RouteMatcher.settings')
project.config_from_object('django.conf:settings')
project.autodiscover_tasks(lambda: settings.INSTALLED_APPS)


project.conf.update(
     CELERY_ACCEPT_CONTENT = ['json'],
     CELERY_TASK_SERIALIZER = 'json',
     CELERY_RESULT_SERIALIZER = 'json',
)