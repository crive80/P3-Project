# Makefile da inserire nella root del progetto, si occupa di compilare il progetto, di farlo partire con un comportamento di default, di pulire il progetto dai file inutili e di
# terminare l'applicazione

progetto : ClientGUI.class ServerGUI.class ServerInterface.class Server.class Client.class

ClientGUI.class : ClientGUI.java
	javac ClientGUI.java -d ./

ServerGUI.class : ServerGUI.java
	javac ServerGUI.java -d ./

ServerInterface.class : ServerInterface.java
	javac ServerInterface.java -d ./

Server.class : Server.java
	javac Server.java -d ./

Client.class : Client.java
	javac Client.java -d ./

clean: 
	rm -f *.class

start: 
	rmiregistry &
	sleep 2 &
	java Server.Server server1 &
	java Server.Server server2 &
	sleep 2 &
	java Client.Client client1 &
	java Client.Client client2