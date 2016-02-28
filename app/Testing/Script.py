'''
# Imports the monkeyrunner modules used by this program
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

# Connects to the current device, returning a MonkeyDevice object
device = MonkeyRunner.waitForConnection()

# Installs the Android package. Notice that this method returns a boolean, so you can test
# to see if the installation worked.
#device.installPackage('myproject/bin/MyApplication.apk')

# sets a variable with the package's internal name
package = 'com.festivent.hardeep.festivent'

# sets a variable with the name of an Activity in the package
activity = 'com.festivent.hardeep.festivent.Welcome'

# sets the name of the component to start
runComponent = package + '/' + activity

# Runs the component
device.startActivity(component=runComponent)

# Presses the Menu button
device.press('KEYCODE_MENU', MonkeyDevice.DOWN_AND_UP)

#start process 
MonkeyRunner.sleep(4)
device.touch(280,740, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(1)
device.type('Ranchoc')
MonkeyRunner.sleep(2)
device.touch(580, 925, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(1)
device.touch(1320, 865, MonkeyDevice.DOWN_AND_UP)
#MonkeyRunner.sleep(1)
#dialog list or map view
#device.touch(920, 1490, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(5)
result = device.takeSnapshot()
result.writeToFile('/home/hardeep/Desktop/listviewresult.png','png')

#Filter Eventbrite
device.touch(1265, 2205, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(1)
device.touch(280, 1125, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(1)
device.touch(1160, 1535, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(3)
result = device.takeSnapshot()
result.writeToFile('/home/hardeep/Desktop/EventbriteResult.png','png')

#Go Back
device.touch(350, 2500, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(2)

#Open side menu
device.drag((65,1960), (1261,1960), 0.5, 5)
MonkeyRunner.sleep(2)
device.touch(400, 780, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(7)
result = device.takeSnapshot()
result.writeToFile('/home/hardeep/Desktop/MapResult.png','png')

#Filer Eventful
device.touch(180, 2160, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(1)
device.touch(260, 1320, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(1)
device.touch(1160, 1550, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(2)
result = device.takeSnapshot()
result.writeToFile('/home/hardeep/Desktop/EventfulResult.png','png')

#Go Back
device.touch(350, 2500, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(2)

#Open side menu again go to settings
device.drag((65,1960), (1261,1960), 0.5, 5)
MonkeyRunner.sleep(1)
device.touch(500, 1160, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(1)
device.touch(860, 1115, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(1)
device.touch(860, 1315, MonkeyDevice.DOWN_AND_UP)
result = device.takeSnapshot()
result.writeToFile('/home/hardeep/Desktop/DistanceSet.png','png')

#Go Back
device.touch(350, 2500, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(2)

#Clear Events in Settings
device.drag((65,1960), (1261,1960), 0.5, 5)
MonkeyRunner.sleep(1)
device.touch(440, 1165, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(1)
device.touch(465, 1430, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(1)
result = device.takeSnapshot()
result.writeToFile('/home/hardeep/Desktop/clearevents.png','png')
device.touch(1084, 1340, MonkeyDevice.DOWN_AND_UP)


#Go Back and Back again
device.touch(350, 2500, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(2)
device.touch(350, 2500, MonkeyDevice.DOWN_AND_UP)
MonkeyRunner.sleep(2)
'''

