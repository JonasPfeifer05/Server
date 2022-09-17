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

Thread to accept new Users. When requesting something from users, first send
a ping package. Wait a bit, if nothing comes back, close connection!
Also sometimes send some ping packages to generally check if user is still here.