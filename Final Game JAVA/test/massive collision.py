from pygame import *
from math import *
from random import *

def hit(p1,p2):
    return (p1[0]-p2[0])**2 +(p1[1]-p2[1])**2 < 100 


screen = display.set_mode((1000,800))

badies = [[randint(0,1000),randint(0,400)] for i in range(1000)]
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
    if randint(0,2)==0:
        badies.append([randint(0,1000),randint(0,400)])
    for bad in badies:
        bigx = 500-bad[0]
        bigy = 400-bad[1]
        dist = max(1,hypot(bigx,bigy))
        dx = int(5 * bigx/dist * direct)
        dy = int(5 * bigy/dist * direct)
        bad[0]+=dx
        bad[1]+=dy
    #shotGrid = {}
    #bad
    for i in range(len(shots)-1,-1,-1):
        for j in range(len(badies)-1,-1,-1):
            if hit(shots[i],badies[j]):
                del shots[i]
                del badies[j]
                break
    screen.fill((110,110,110))
    for shot in shots:
        draw.circle(screen,(255,0,0),shot,5)
    for bad in badies:
        draw.circle(screen,(0,255,0),bad,5)
    display.flip()
    myClock.tick(50)

quit()
