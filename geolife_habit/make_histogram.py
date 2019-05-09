import numpy as np
import matplotlib.pyplot as plt
import os
import json
def is_number(s):
    try:
        float(s)
        return True
    except ValueError:
        return False

def divide(l,divisor):
    for i in range(len(l)):
        if is_number(l[i]):
            l[i] = float(l[i]) / divisor
    return l

period=[]
reliability=[]
occurence=[]
std=[]
arrival=[]
first_location = []
last_location = []
first_date = []
last_date = []
habit_found = 0
probable_true_habit = 0
habits = []
users = {}
for dirname, dirnames, filenames in os.walk('.'):
    username = dirname.split("\\")
    habits = {}
    if len(username) == 2:
        users[username[1]] = []
        curent_user = username[1]
    for filename in filenames:
        with open(os.path.join(dirname, filename)) as f:
            
            line = f.readline()
            while(line):
                if "Period" in line:
                    for s in line.split():
                        try:
                            period.append(float(s))
                        except ValueError:
                            line = line
                if "Reliability" in line:
                    for s in line.split():
                        try:
                            reliability.append(float(s))
                        except ValueError:
                            line = line
                if "Number of occurrences:" in line:
                    for s in line.split():
                        try:
                            occurence.append(int(s))
                        except ValueError:
                            line = line
                if "Standard Deviation:" in line:
                    for s in line.split():
                        try:
                            std.append(float(s))
                        except ValueError:
                            line=line

                if "Offset: " in line:
                    first_date.append(line.split("Offset: ")[1])

                if "arrival time:" in line :
                    last_date.append(line.split("arrival time:")[1])

                if "First Location: " in line:
                    first_location.append(line.split(":")[1])

                if "Last Location:" in line:
                    last_location.append(line.split(":")[1])

                if "arrival time:" in line:
                    for s in line.split():
                        try:
                            arrival.append(float(s))
                        except ValueError:
                            line=line

                if "====" in line and "if" not in line:
                    if period[-1] %7 == 0 and std[-1] < 60*4 and occurence[-1] > 4:
                        probable_true_habit += 1
                    habit_found += 1
                    habit = {}
                    #parse first location
                    tmp = first_location[-1].split(";")
                    lat = tmp[0].split("[")[1]
                    long = tmp[1].split("]")[0]
                    habit["departureLat"] = float(lat)
                    habit["departureLong"] = float(long)
                    #parse last location
                    tmp = last_location[-1].split(";")
                    lat = tmp[0].split("[")[1]
                    long = tmp[1].split("]")[0]
                    habit["ArrivalLat"] = float(lat)
                    habit["ArrivalLong"] = float(long)
                    #parse departure time
                    tmp = first_date[-1].split()
                    habit["departureTime"] = tmp[3]
                    habit["firstDate"] = first_date[-1]
                    #parse arrival time
                    tmp = last_date[-1].split()
                    habit["arrivalTime"] = tmp[3]
                    #parse frequency
                    habit["frequency"] = 1/period[-1]
                    habit["period"] = period[-1]
                    users[curent_user].append(habit)
                line = f.readline()    

#dump json
with open('user_habit.json', 'w') as json_file:
    json.dump(users, json_file)

reliability = np.asarray(reliability)
period = np.asarray(period)
occurence = np.asarray(occurence)
std= np.asarray(std)

print("number of habit: " + str(habit_found))
plt.hist(reliability,bins=np.arange(50,100,5))
plt.title("Reliability on geolife")
plt.xlabel("Reliability in %")
plt.savefig('reliability.eps')
plt.show()

plt.hist(period,bins=np.arange(1,32,1))

plt.title("Period on geolife")
plt.xlabel("Period in day")
plt.savefig('period.eps')
plt.show()

plt.hist(std,bins=np.arange(0,1440,10),range=[0,1440])
plt.title("Habit standard deviation on geolife")
plt.xlabel("std in minute")
plt.savefig('std_minute.eps')
plt.show()

std_day = divide(std,1440)
plt.hist(std_day,bins=np.arange(0,4,0.1))
plt.title("Habit standard deviation on geolife")
plt.xlabel("std in day")
plt.savefig('std_day.eps')
plt.show()

plt.hist(occurence,bins=np.arange(0,40,1))
plt.title("Habit occurence on geolife")
plt.xlabel("number of occurence of the habit")
plt.savefig('occ.eps')
plt.show()

print("good habit: " + str(probable_true_habit))