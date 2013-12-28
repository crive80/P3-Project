# Makefile da inserire nella root del progetto, si occupa di compilare il progetto, di farlo partire con un comportamento di default, di pulire il progetto dai file inutili e di
# terminare l'applicazione

progetto : ClientGUI.class ServerGUI.class ResourceInterface.class Resource.class ServerInterface.class Server.class ClientInterface.class Client.class

ClientGUI.class : ClientGUI.java
	javac ClientGUI.java -d ./

ServerGUI.class : ServerGUI.java
	javac ServerGUI.java -d ./

ResourceInterface.class : ResourceInterface.java
	javac ResourceInterface.java -d ./

Resource.class : Resource.java
	javac Resource.java -d ./

ServerInterface.class : ServerInterface.java
	javac ServerInterface.java -d ./

Server.class : Server.java
	javac Server.java -d ./

ClientInterface.class : ClientInterface.java
	javac ClientInterface.java -d ./

Client.class : Client.java
	javac Client.java -d ./

clean: 
	rm -f *.class

start: 
	rmiregistry &
	sleep 2
	java Server.Server server1 &
	java Server.Server server2 &
	sleep 2
	java Client.Client client1 server1 5 r1 5 r2 7 r3 8 &
	java Client.Client client3 server1 6 g1 4 g2 3 &
	java Client.Client client2 server2 5 r4 2 r5 5 r6 1 &

stop:
	killall -q rmiregistry &