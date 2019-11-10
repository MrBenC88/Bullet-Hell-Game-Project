from pygame import *

screen = display.set_mode((900,700))
running = True

while running:
    for e in event.get():
        if e.type == QUIT:
            running = False
    screen.fill((110,110,110))
    draw.rect(screen,(255,0,0),(49,35,28,30))
    draw.rect(screen,(255,0,0),(505,340,6,10))
    display.flip()
quit()
