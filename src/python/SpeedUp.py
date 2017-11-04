
import numpy as np
import os
import matplotlib.pyplot as plt



filename="/Users/yxtwkk/Desktop/Profile/TPC-H-q14/AppInfo/Apps.txt"
AppDuration0=[]
AppDuration4=[]
f = open(filename,'r')
lines = f.readlines()
for line in lines:
    #print(line)
    n_tmp, D_tmp = [line.split()[0],line.split()[1]]
    #print(n_tmp)
    #np.append(AppName,n_tmp)
    #np.append(AppDuration,D_tmp)
    appName = n_tmp
    appName = appName[0:10] + appName[len(appName) - 4:len(appName)]
    if("Skew0" in n_tmp):
        AppDuration0.append(float(D_tmp)/1000)
    else:
        AppDuration4.append(float(D_tmp)/1000)

for appDuration in AppDuration0:
        print(appDuration)
for appDuration in AppDuration4:
        print(appDuration)
#for appDuration in AppDuration4:
 #  appDuration=appDuration

print(AppDuration0[0])
print(AppDuration0[3])
i=0
length=4
pivot0 = AppDuration0[0]
pivot4=  AppDuration4[0]

while i <length:
    #print(i)
    AppDuration0[i] = pivot0/AppDuration0[i]
    #print(tmp)
    AppDuration4[i] = pivot4/AppDuration4[i]
    i=i+1

for appDuration in AppDuration0:
   print(appDuration)
for appDuration in AppDuration4:
  print(appDuration)
names = [1,2,4,8]
x = range(len(names))
plt.plot(x,AppDuration0,marker='o',mec='r',mfc='w',label='Skew0')
plt.plot(x,AppDuration4,marker='*',ms=10,label='Skew4')
plt.legend()
plt.xticks(x,names,rotation=0)
plt.margins(0)
plt.subplots_adjust(bottom=0.15)
plt.ylabel('SpeedUp')
plt.xlabel('Executor')
plt.title(appName +"-SpeedUp")
plt.savefig("/Users/yxtwkk/Desktop/Profile/TPC-H-q14/Image/SpeedUp.jpg")
#plt.show()
plt.show()

#AppName=[1,2,4,8]
#plt.figure()
#plt.plot(AppName,AppDuration)
#plt.savefig("easyplot.jpg")