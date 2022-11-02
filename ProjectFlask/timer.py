from threading import Timer
import time

# sample 10 second timer
def timeloop():
    print(f'--- ' + time.ctime() + ' ---')
    Timer(10, timeloop).start()
timeloop()