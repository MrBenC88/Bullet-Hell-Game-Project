// 
// Decompiled by Procyon v0.5.36
// 

class RotateBullet extends Bullet
{
    private double velY;
    private double velG;
    private double velR;
    private double px;
    private double py;
    private double pr;
    private double maxR;
    private double rotate;
    
    public RotateBullet(final int n, final int n2, final double velY, final double velG, final double maxR, final double rotate, final double velR) {
        super(n, n2);
        this.velY = velY;
        this.velG = velG;
        this.velR = velR;
        this.px = n;
        this.py = n2;
        this.rotate = rotate;
        this.maxR = maxR;
    }
    
    public void shoot(final boolean b) {
        if (this.pr < this.maxR) {
            this.pr += this.velG;
        }
        this.py += this.velY;
        this.rotate += this.velR;
        this.x = (int)(Math.cos(this.rotate) * this.pr + this.px);
        this.y = (int)(Math.sin(this.rotate) * this.pr + this.py);
    }
}