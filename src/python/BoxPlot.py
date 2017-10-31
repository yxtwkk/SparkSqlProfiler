
import numpy as np
import matplotlib.pyplot as plt

fig, axes = plt.subplots(ncols=2, sharey=True)
fig.subplots_adjust(wspace=0)

filename="/Users/yxtwkk/Desktop/Profile/TPC-H-q22/StageInfo/Stage.txt"
AppName=[]
l1=[]
l2=[]
l3=[]
l4=[]
l5=[]
l6=[]
l7=[]
l8=[]

i=0
j=0
f = open(filename,'r')
lines = f.readlines()
for line in lines:
    if("TPC" in line):
        i=1
        j=j+1
        appName = line
        print:line
    else:
        if(0<i<=472):
            n_tmp, D_tmp = [line.split()[0],line.split()[1]]
            i=i+1
            locals()['l'+str(j)].append((float(D_tmp))/1000)
            #print(D_tmp)

label=[1,2,4,8]
l=[l1,l2,l3,l4]
m=[l5,l6,l7,l8]
axes[0].boxplot(l, sym='r*',whis='range',showfliers=False)
#axes[0].bxp(list,showfliers=False, showcaps=False, meanline=False, showmeans=True)

axes[0].set_xlabel('Executor')
axes[0].set_ylabel('Tasks.duration(s)')
axes[0].set_title('Skew0')

#plt.ylabel('Task.Duration(s)')
#plt.xlabel('Executor')

axes[1].boxplot(m, sym='r*',whis='range',showfliers=False)
axes[1].set_xlabel('Executor')
axes[1].set_title('Skew4')
appName = appName[0:10]+appName[len(appName)-5:len(appName)-1]
fig.suptitle(appName+'-TasksTime')
#plt.ylabel('Task.Duration(s)')
#plt.xlabel('Executor')
#plt.show()
plt.savefig("/Users/yxtwkk/Desktop/Profile/TPC-H-q22/TaskInfo/Task.jpg")
#plt2.boxplot(m, sym='r*',whis='range',showfliers=False)

#gs1 = gs.GripSpec(1,2)
#gs1.update(hspace=0, wspace=0, top=0.90, bottom=0.10)
#sp1 = plt.subplot(gs1[0:0,0:1])
#sp2=plt.subplpt(gs1[0:0,1:2])