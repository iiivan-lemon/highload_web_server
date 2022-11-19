FROM nginx

WORKDIR /etc/nginx

COPY nginx.conf /etc/nginx/nginx.conf
COPY  var/www/html/httptest /etc/nginx/static

