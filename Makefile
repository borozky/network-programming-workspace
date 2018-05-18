singleplayer-server: compile-singleplayer
	cd SimpleGuessingGameSinglePlayer && java -cp bin server.SinglePlayerServer

singleplayer-client: compile-singleplayer
	cd SimpleGuessingGameSinglePlayer && java -cp bin client.Client

multiplayer-server: compile-multiplayer
	cd SimpleGuessingGameMultiplayer && java -cp bin server.MultiPlayerServer

multiplayer-client: compile-multiplayer
	cd SimpleGuessingGameMultiplayer && java -cp bin client.Client

compile: compile-singleplayer compile-multiplayer

compile-singleplayer: 
	javac -d SimpleGuessingGameSinglePlayer/bin SimpleGuessingGameSinglePlayer/src/*/*.java

compile-multiplayer:
	javac -d SimpleGuessingGameMultiplayer/bin SimpleGuessingGameMultiplayer/src/*/*.java
