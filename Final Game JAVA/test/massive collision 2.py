from pygame import *
from math import *
from random import *

# distance formula
def hit(p1,p2):
    return (p1[0]-p2[0])**2 +(p1[1]-p2[1])**2 < 100 


screen = display.set_mode((900,700))

badies = [[randint(0,100),randint(0,350)] for i in range(100)]
shots = []
heat = 5
myClock = time.Clock()
running = True
direct = 1
dCount = 0
while running:
    for e in event.get():
        if e.type == QUIT:
            running = False

    mb = mouse.get_pressed()
    mx,my = mouse.get_pos()

    dCount += 1
    if dCount % 30 == 0:
        direct *= -1
        
    if mb[0]==1:
        shots.append([mx,my])
        
    for shot in shots:
        shot[1]-=5
    for bad in badies:
        bigx = 500-bad[0]
        bigy = 400-bad[1]
        dist = max(1,hypot(bigx,bigy))
        dx = int(5 * bigx/dist * direct)
        dy = int(5 * bigy/dist * direct)
        bad[0]+=5
        if bad[0] > 900:
            bad[0]*=-1
        #bad[1]+=dy
    
##    shotGrid = {}
##    for shot in shots:
##        x,y = shot[0]//10, shot[1]//10
##        if (x,y) in shotGrid:
##            shotGrid[(x,y)].append(shot)
##        else:
##            shotGrid[(x,y)] = [shot]
    badGrid = {}
    for bad in badies:
        x,y = bad[0]//10, bad[1]//10
        if (x,y) in badGrid:
            badGrid[(x,y)].append(bad)
        else:
            badGrid[(x,y)] = [bad]
    #print (badGrid)
    for i in range(len(shots)-1,-1,-1):
        x,y = shots[i][0]//10, shots[i][1]//10
        for gx in range(x-1,x+2): 
            for gy in range(y-1,y+2):
                if (gx,gy) in badGrid:
                    guys = badGrid[(gx,gy)]
                    for j in range(len(guys)-1,-1,-1):
                        if hit(shots[i],guys[j]):
                            del shots[i]
                            badies.remove(guys[j])
                            del guys[j]
                            break

    screen.fill((110,110,110))
    for shot in shots:
        draw.circle(screen,(255,0,0),shot,5)
    for bad in badies:
        draw.circle(screen,(0,255,0),bad,5)
    display.flip()
    myClock.tick(50)

quit()
