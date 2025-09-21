/**
 * This class is for all the flying boxes.
 */
class Box {
    constructor(xx = null, yy = null, dir = null) {
        if (xx !== null && yy !== null && dir !== null) {
            // Creates a box that's actually an EPIC WAVE!!!!
            this.h = Math.floor(Math.random() * 200);
            this.w = 10;
            this.x = xx;
            if (dir === 0) this.x -= 17;
            else this.x += 12;
            this.doubleX = this.x;
            this.y = yy - this.h / 2;
            
            this.xV = 8.0;
            if (dir === 1) this.xV = this.xV * (-1);
            this.xVStart = this.xV;
            
            this.wave = true;
            this.deadly = false;
            this.stopper = false;
        } else {
            // Normal box creation
            // One out of 25 boxes is deadly
            this.deadly = Math.floor(Math.random() * 25) === 1;
            
            this.w = Math.floor(Math.random() * 50) + 20;
            this.h = Math.floor(Math.random() * 30) + 10;
            
            // One out of 50 boxes is extra huge (and never deadly)
            if (Math.floor(Math.random() * 50) === 1) {
                this.w += Math.floor(Math.random() * 100) + 100;
                this.h += Math.floor(Math.random() * 50) + 50;
                this.deadly = false;
            }
            
            this.stopper = false;
            if (Math.floor(Math.random() * 25) === 1 && this.w < 100) {
                this.deadly = false;
                this.stopper = true;
            }
            
            this.y = Math.floor(Math.random() * (Way.h - 21 - this.h)); // Starting y-coordinate
            
            this.xV = 0;
            while (this.xV === 0) {
                this.xV = Math.random() * 6 - 3; // 6-3
                
                // Some boxes are REALLY FAST!!!!!!!!!!!!!!!! (so called ronnil)
                if (Math.floor(Math.random() * 30) === 1) this.xV = 30;
                
                // Gives us xV and xVStart with one decimal
                let xvint = Math.floor(this.xV * 10);
                this.xV = xvint / 10.0;
                this.xVStart = xvint / 10.0;
            }
            
            if (this.xV > 0) this.x = Way.w;
            else this.x = 0 - this.w;
            
            this.doubleX = this.x;
            this.wave = false;
        }
    }
    
    // Getters
    getXV() { return this.xV; }
    getXVStart() { return this.xVStart; }
    getX() { return Math.floor(this.doubleX); }
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
     * Draws the box to the canvas context
     */
    draw(ctx) {
        if (this.deadly) {
            ctx.fillStyle = '#808080'; // Gray
        } else if (this.stopper) {
            ctx.fillStyle = '#00ff00'; // Green
        } else if (this.wave) {
            ctx.fillStyle = '#ffffff'; // White
        } else {
            ctx.fillStyle = '#000000'; // Black
        }
        
        ctx.fillRect(this.x, this.y, this.w, this.h);
        ctx.strokeStyle = '#ffffff'; // White border
        ctx.strokeRect(this.x, this.y, this.w, this.h);
        
        // Draw movement indicator when stopped
        if (this.xV === 0 && this.xVStart > 0) {
            ctx.strokeStyle = '#ffffff';
            ctx.beginPath();
            ctx.moveTo(this.getX() - 1, this.y);
            ctx.lineTo(this.getX() - 1, this.y + this.h);
            ctx.stroke();
        }
        if (this.xV === 0 && this.xVStart < 0) {
            ctx.strokeStyle = '#ffffff';
            ctx.beginPath();
            ctx.moveTo(this.getX() + this.w + 1, this.y);
            ctx.lineTo(this.getX() + this.w + 1, this.y + this.h);
            ctx.stroke();
        }
    }
    
    /**
     * Updates the position
     */
    updatePosition() {
        this.doubleX -= this.xV;
        this.x = Math.floor(this.doubleX);
    }
    
    /**
     * Returns a rectangle bounds for collision detection
     */
    getBounds() {
        return {
            x: this.x,
            y: this.y,
            width: this.w,
            height: this.h
        };
    }
    
    /**
     * Checks collision with given bounds
     */
    checkHit(bounds) {
        const boxBounds = this.getBounds();
        return this.intersects(boxBounds, bounds);
    }
    
    /**
     * Helper method to check if two rectangles intersect
     */
    intersects(rect1, rect2) {
        return !(rect1.x + rect1.width < rect2.x ||
                rect2.x + rect2.width < rect1.x ||
                rect1.y + rect1.height < rect2.y ||
                rect2.y + rect2.height < rect1.y);
    }
    
    /**
     * Returns true if the box is deadly
     */
    isDeadly() { return this.deadly; }
    
    /**
     * Returns true if the box is a stopper
     */
    isStopper() { return this.stopper; }
}