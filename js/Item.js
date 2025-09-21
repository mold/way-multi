/**
 * The class for all the "items", the things that the player is supposed to catch.
 * (actually there will only be one item object created)
 */
class Item {
    constructor() {
        this.w = 15; // width
        this.h = 15; // height
        this.x = Way.w / 2;
        this.y = Way.h / 2;
        this.sprite = null; // Will be set later
    }
    
    // Getters
    getX() { return this.x; }
    getY() { return this.y; }
    getWidth() { return this.w; }
    getHeight() { return this.h; }
    
    // Setters
    setX(xx) { this.x = xx; }
    setY(yy) { this.y = yy; }
    setImage(itemSprite) { this.sprite = itemSprite; }
    
    /**
     * Draws the item
     */
    draw(ctx) {
        if (this.sprite) {
            ctx.drawImage(this.sprite, this.x, this.y);
        } else {
            // Fallback if image not loaded
            ctx.fillStyle = '#ff0000';
            ctx.fillRect(this.x, this.y, this.w, this.h);
        }
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
     * Moves the item to a new location
     */
    updatePosition(boxes) {
        let repeat = true;
        
        // Places the item at a random position
        // If it's placed on/behind a box, it's replaced
        while (repeat) {
            this.x = Math.floor(Math.random() * (Way.w - 11));
            this.y = Math.floor(Math.random() * (Way.h - 31)) + 20;
            
            repeat = false;
            for (let box of boxes) {
                if (box.checkHit(this.getBounds())) {
                    repeat = true;
                    break;
                }
            }
        }
    }
    
    /**
     * Checks collision with given bounds
     */
    checkHit(bounds) {
        const itemBounds = this.getBounds();
        return this.intersects(itemBounds, bounds);
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
}