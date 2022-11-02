from threading import Timer
from flask import Flask, render_template
import time
import json
import MBTAApiClient

# ------------------
#    BUS LOCATION  
# ------------------

# Initialize buses list by doing an API call to the MBTA database below
buses = MBTAApiClient.callMBTAApi()

#Update the function below
def update_data():
    buses = MBTAApiClient.callMBTAApi()

def status():
    for bus in buses:
        print(bus)

def timeloop():
    print(f'--- ' + time.ctime() + ' ---')
    # status()
    update_data()
    Timer(10, timeloop).start()
timeloop()

# ----------------
#    WEB SERVER  
# ----------------

# create application instance
app = Flask(__name__)

# root route - landing page
@app.route('/')
def root():
    return render_template('index.html')

# root route - landing page
@app.route('/location')
def location():
    return (json.dumps(buses))


# start server - note the port is 3000
if __name__ == '__main__':
    app.run(port=3000)