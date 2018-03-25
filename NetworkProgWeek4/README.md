# RMIT Network Programming Week 4-5 Lab

### Task 1
- Copy the code from https://docs.oracle.com/javase/tutorial/networking/nifs/retrieving.html
- Extend the code to print MAC address in hex form, host names and host addresses

Run <code>networkinterface/ListNIFs.java</code> using this command
<pre>javac -d bin networkinterface/ListNIFs.java & java -cp bin networkinterface.ListNIFs</pre>

### Task 2 
- Create simple client-server program which client and server communicates via sockets
##### Client
- Client should read lines from console, then sends them to the server.
- The client should print server replies.
- After the client receives a single uppercase 'X', the client should exit.

##### Server
- Should print client's host address
- Server should receive client messages. 
- Those client messages should be echoed back to client as upper case letters.
- Client messages should be put in a log file

### Task 3
##### Client
- Should calculate the checksum of line input
- Calculated checksum should be sent to a separate socket
##### Server
- Should calculate the checksum of client messages
- Should receive checksum sent by client from the separate socket
- Checksums should be equal


### How to run EchoServer
#### Server
- Run the server first using <pre>> javac -d bin echoserver/Server.java<br/>> java -cp bin echoserver.Server</pre>
- You may pass 2 optional params to the command above. 
- The first parameter is the main port number. 
- The second parameter is the port number that will be used for receiving checksums
- Example with parameters:<pre>> javac -d bin echoserver/Server.java<br/>> java -cp bin echoserver.Server 15376 25376</pre>
- Server will run forever unless there's an error or you terminate it.

#### Client
- Run the client using command
<pre>> javac -d bin echoserver/Client.java<br/>> java -cp bin echoserver.Client</pre>
- You may pass 3 parameters to the command above
- The first parameter is the server host address
- The second paramater is the server's main port (ie. 15376)
- The third paramter is the server's port dedicated for receiving checksums (ie. 25376)
- Example with parameters: <pre>> javac -d bin echoserver/Client.java</br>> java -cp bin echoserver.Client m1-c12n1.csit.rmit.edu.au 15376 25376
</pre>