
import numpy as np
import os
import matplotlib.pyplot as plt



filename="/Users/yxtwkk/Desktop/Profile/TPC-H-q14/AppInfo/Apps.txt"
AppName=[]
AppDuration0=[]
AppDuration4=[]
f = open(filename,'r')
lines = f.readlines()
for line in lines:
    print(line)
    n_tmp, D_tmp = [line.split()[0],line.split()[1]]
    #print(n_tmp)
    #np.append(AppName,n_tmp)
    #np.append(AppDuration,D_tmp)
    AppName.append(n_tmp)
    appName = n_tmp
    appName = appName[0:10] + appName[len(appName) - 4:len(appName)]
    if("Skew0" in n_tmp):
        AppDuration0.append(float(D_tmp)/1000)
    else:
        AppDuration4.append(float(D_tmp)/1000)

for appDuration in AppDuration4:
    appDuration=appDuration

names = [1,2,4,8]
x = range(len(names))
plt.plot(x,AppDuration0,marker='o',mec='r',mfc='w',label='Skew0')
plt.plot(x,AppDuration4,marker='*',ms=10,label='Skew4')
plt.legend()
plt.xticks(x,names,rotation=0)
plt.margins(0)
plt.subplots_adjust(bottom=0.15)
plt.ylabel('App.Duration(s)')
plt.xlabel('Executor')
plt.title(appName)
plt.savefig("/Users/yxtwkk/Desktop/Profile/TPC-H-q14/Image/AppDuration.jpg")
#plt.show()

#AppName=[1,2,4,8]
#plt.figure()
#plt.plot(AppName,AppDuration)
#plt.savefig("easyplot.jpg")