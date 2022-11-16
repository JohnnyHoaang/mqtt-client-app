import RPi.GPIO as GPIO

ledPin = 12 # define ledPin
sensorPin = 13 # define sensorPin

def setup():
    GPIO.setmode(GPIO.BOARD) # use PHYSICAL GPIO Numbering
    GPIO.setup(ledPin, GPIO.OUT) # set ledPin to OUTPUT mode
    GPIO.setup(sensorPin, GPIO.IN) # set sensorPin to INPUT mode

def loop():
    if GPIO.input(sensorPin)==GPIO.HIGH:
        print ('motion detected >>>')
    else :
        print ('no motion detected <<<')

def destroy():
    GPIO.cleanup() # Release GPIO resourc

    if __name__ == '__main__': # Program entrance
        print ('Program is starting...')
        setup()
        try:
            loop()
        except KeyboardInterrupt: # Press ctrl-c to end the program.
            destroy()
