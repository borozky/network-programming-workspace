## Network Programming Week 4-5 Lab

### Requirements
- Java 1.8

### Run Task 1
- To run task 1 solution using this command 
<pre>javac -d bin src/networkinterface/ListNIFs.java && java -cp bin networkinterface.ListNIFs</pre>

### Run EchoServer socket app in local machine
- You have to run the server first before running the client
- Run the server using this command
<pre>javac -d bin src/echoserver/Server.java && java -cp bin echoserver.Server</pre>
- Open another terminal, run the client using this command 
<pre>javac -d bin src/echoserver/Client.java && java -cp bin echoserver.Client</pre>