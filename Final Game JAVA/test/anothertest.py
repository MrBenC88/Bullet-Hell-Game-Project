shotGrid ={}
pts = [[255,353],[252,357],[644,421],[453,431]]
print(shotGrid)
c = 0
x,y = pts[c][0]//10, pts[c][1]//10
if (x,y) in shotGrid:
    shotGrid[(x,y)].append(pts[c])
    print (pts[0])
else:
    print ([pts[c]])
    shotGrid[(x,y)] = [pts[c]]

bad = shotGrid[(x,y)]
print (bad)
