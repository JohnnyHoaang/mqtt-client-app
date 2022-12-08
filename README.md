# How to use MQTT-Client-App

## Run order

**NOTE: Teachers MQTT credentials found at the bottom od the doc**

1. Run the GUI application App first 
2. Run the ConsoleApp application on the pi afterwards

## Instructions for compiling and running   
The code can be compile and run from an IDE or via command line.   
   
#### Compile and run using an IDE   
_To compile_: click the "Clean and Build Project" button.   
_To run_: click the "Run Project" button.   
   
### Compile and run using the command line
#### ConsoleApp
_To compile_: mvn compile
_To run_: mvn exec:java -e -Dexec.mainClass=com.mycompany.mqtt.client.app.ConsoleApp

#### App
_To compile_: mvn compile
_To run_: mvn exec:java -e -Dexec.mainClass=com.mycompany.mqtt.client.app.App

## Use - App

On startup you will need to follow the prompts in the console
- Enter your keystore path then password
  - Will be used to get and store certificates used in verification
- Enter your MQTT credentials to connect to the server
- When all is valid the GUI will launch and wait for data

**NOTE: When logged in with teacher credentials top row "Johnny" tiles will update**

## Use - ConsoleApp

On startup you will be presented with a menu
1. Load a Keystore - Enter path and password for keystore and it will be loaded and saved for later use
2. Extract keys from keystore - Enter keystore password again to obtain keys from the keystore, they will be saved for later use
3. Connect to MQTT and Start all sensors 
    - Enter topic username to be used:
       - johnny
       - alexandre
       - katharina
       - carlton <-- to be used by teacher
    - Enter username and password of the MQTT client
    - All sensors will start and send data to the MQTT server
      - Can stop all sensors by entering f
4. Exit - will exit the app

### Teachers user credentials

Topic username: carlton

MQTT

username: carlton
pass: teacher420-540