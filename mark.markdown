# Server

## server-Instance
  * #### Log System
    * Join
    * Quit
    * Request
    * Serve
    * Traffic
  * #### Control System
    * Kick
  * #### Trafic System
    * Requests
    * Serves
  * #### h3. server System
    * Start
    * Close
    * Bussy
  * #### h3. client System
    * OnJoin
      * OnLeave
  * #### h3. Lobby System
      * creation
      * Join
      * Leave
      * OnJoin
      * OnLeave

## client-Instance
  * #### Control System
    * Join
      * Leave
  * #### Trafic System
    * Requests
      * Servers
  * #### client System
    * OnJoin
    * OnLeave

# Importace

#### Everythin should be dynamic and customizable from user to user! 
#### Lobbies should not interfear with other ones!

2 different classes: ClientTransfer and ServerTransfer. Client/Server
for Requests and Responses from it. So Clients store ServerTransfer.
Each Transfer stores a handle function. Client Transfer gets passed
the Clienthandler. Server transfer gets the Client passed.

To handle transfer we do certain: We have one function to send Request
Packages. Also one seperate thread which reads in all Packages and
runs the handle function.
