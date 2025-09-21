/**
 * This class is for all the flying particles.
 */
class Particle {
    constructor() {
        this.w = Math.floor(Math.random() * 5) + 1; // width
        this.h = this.w; // height
        
        this.x = Math.floor(Math.random() * Way.w);
        this.y = Math.floor(Math.random() * Way.h);
        
        this.xV = Math.random() * 2 + 3; // velocity
        this.doubleX = this.x; // precise x coordinate
    }
    
    // Getters
    getXV() { return this.xV; }
    getX() { return this.x; }
    getY() { return this.y; }
    getWidth() { return this.w; }
    getHeight() { return this.h; }
    
    // Setters
    setX(xx) {
        this.x = xx;
        this.doubleX = xx;
    }
    
    setY(yy) { this.y = yy; }
    setXV(xv) { this.xV = xv; }
    
    /**
     * Draws the particle to the canvas context
     */
    draw(ctx) {
        ctx.fillStyle = '#444444'; // Dark gray
        ctx.fillRect(this.x, this.y, this.w, this.h);
    }
    
    /**
     * Updates the position
     */
    updatePosition() {
        this.doubleX -= this.xV;
        this.x = Math.floor(this.doubleX);
    }
}