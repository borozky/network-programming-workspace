singleplayer-server: compile-singleplayer
	cd SimpleGuessingGameSinglePlayer & java -cp bin server.SinglePlayerServer

singleplayer-client: compile-singleplayer
	cd SimpleGuessingGameSinglePlayer & java -cp bin client.Client

multiplayer-server: compile-multiplayer
	cd SimpleGuessingGameMultiPlayer & java -cp bin server.MultiPlayerServer

multiplayer-client: compile-multiplayer
	cd SimpleGuessingGameMultiPlayer & java -cp bin client.Client

compile: compile-singleplayer compile-multiplayer

compile-singleplayer: 
	cd SimpleGuessingGameSinglePlayer & javac -d bin src/core/*.java src/client/*.java src/server/*.java

compile-multiplayer:
	cd SimpleGuessingGameMultiPlayer & javac -d bin src/core/*.java src/client/*.java src/server/*.java