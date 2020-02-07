// 
// Decompiled by Procyon v0.5.36
// 

class FixedBullet extends Bullet
{
    private double velX;
    private double velY;
    private double px;
    private double py;
    private double pr;
    
    public FixedBullet(final int n, final int n2, final double velX, final double velY) {
        super(n, n2);
        this.velX = velX;
        this.velY = velY;
        this.px = n;
        this.py = n2;
    }
    
    public void shoot(final boolean b) {
        this.px += this.velX;
        this.py += this.velY;
        this.x = (int)this.px;
        this.y = (int)this.py;
    }
}