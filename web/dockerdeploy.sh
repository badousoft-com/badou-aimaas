docker -H tcp://192.168.1.242:2375 rm -f init .
docker -H tcp://192.168.1.242:2375 rmi badousoft/init
docker -H tcp://192.168.1.242:2375 build -t badousoft/init .
docker -H tcp://192.168.1.242:2375 run -d --name init -p 8093:8080/tcp badousoft/init
