from django.conf.urls import patterns, include, url
from django.contrib import admin

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'RouteMatcher.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r'^admin/', include(admin.site.urls)),
    url (r'^check$','MicroService.views.check',name="Check"),
    url (r'^sendNotification$','MicroService.views.sendNotifications',name="sendNotifications"),
)
