/**
 * The player class
 * 
 * Here are all player specific values stored: position, size, speed, various constants and variables, current
 * status and so on and so on. Things that haven't got to do with the actual running of the game.
 * See below for commented blocks of variables.
 * 
 * Use setX() instead of "x =" when changing x, so that doubleX is changed as well
 */
class Player {
    constructor(n) {
        // Player constants
        this.w = 15; // width (default 15)
        this.h = 20; // height (default 20)
        this.x = 50;
        this.y = Way.h - 20;
        this.doubleX = this.x; // exact (with decimals) x-coordinate
        this.xV = 0; // current speed in x-axis
        this.yV = 0; // current speed in y-axis
        this.xSpeed = 5; // speed constant (default 5)
        this.jumpSpeed = 10; // speed upwards at jump (default 10)
        this.playerNo = n; // player number
        
        // Variables for moving or to determine movement
        this.crouching = false;
        this.jumping = false;
        this.falling = false;
        this.keyLeft = false;
        this.keyRight = false;
        this.dirRight = false; // direction player is facing
        this.dirLeft = false;
        this.keyDown = false;
        this.keyUp = false;
        this.jumpFallTimer = 0;
        
        // Variables for player status
        this.alive = true;
        this.deathBySquishFlag = false;
        this.deathByTimeFlag = false;
        this.deathByDeadlyBoxFlag = false;
        this.caughtAllItems = false;
        this.itemsLeft = 10;
        this.wave = 0; // waves launched (goes to ten and resets)
        
        // Variables for collisions
        this.collRight = false;
        this.collLeft = false;
        this.collHead = false;
        this.collFeet = false;
        this.collGround = false;
        
        this.points = 0;
        
        // Player images
        this.playerRight = null;
        this.playerLeft = null;
        this.playerCrouchRight = null;
        this.playerCrouchLeft = null;
        this.playerRunRight = null;
        this.playerRunLeft = null;
        
        // Set initial direction
        if (n === 2) this.dirRight = true;
        if (n === 1) this.dirLeft = true;
    }
    
    // Getters
    getXV() { return this.xV; }
    getYV() { return this.yV; }
    getXSpeed() { return this.xSpeed; }
    getX() { return this.x; }
    getY() { return this.y; }
    getWidth() { return this.w; }
    getHeight() { return this.h; }
    getJumpSpeed() { return this.jumpSpeed; }
    getItemsLeft() { return this.itemsLeft; }
    getPoints() { return this.points; }
    getPlayerNo() { return this.playerNo; }
    
    getBounds() {
        return {
            x: this.x,
            y: this.y,
            width: this.w,
            height: this.h
        };
    }
    
    // Setters
    setX(xx) {
        this.x = xx;
        this.doubleX = xx;
    }
    
    setItemsLeft(i) { this.itemsLeft = i; }
    setAlive(b) { this.alive = b; }
    setY(yy) { this.y = yy; }
    setXV(xv) { this.xV = xv; }
    setYV(yv) { this.yV = yv; }
    setPoints(p) { this.points = p; }
    
    // Key input setters
    keyDown(b) { this.keyDown = b; }
    keyUp(b) { this.keyUp = b; }
    keyRight(b) { this.keyRight = b; }
    keyLeft(b) { this.keyLeft = b; }
    
    dirRight() {
        this.dirRight = true;
        this.dirLeft = false;
    }
    
    dirLeft() {
        this.dirLeft = true;
        this.dirRight = false;
    }
    
    setImages(pR, pL, pCR, pCL, pRR, pRL) {
        this.playerRight = pR;
        this.playerLeft = pL;
        this.playerCrouchRight = pCR;
        this.playerCrouchLeft = pCL;
        this.playerRunRight = pRR;
        this.playerRunLeft = pRL;
    }
    
    // Status methods
    hasCaughtAllItems() { return this.caughtAllItems; }
    deathBySquish() { return this.deathBySquishFlag; }
    deathByTime() { return this.deathByTimeFlag; }
    deathByDeadlyBox() { return this.deathByDeadlyBoxFlag; }
    isAlive() { return this.alive; }
    
    /**
     * Kills the player
     */
    die() {
        this.alive = false;
        this.keyLeft = false;
        this.keyRight = false;
    }
    
    /**
     * Removes one from itemsLeft
     */
    itemCaught() {
        this.itemsLeft--;
        if (this.itemsLeft === 0) {
            this.caughtAllItems = true;
        }
    }
    
    /**
     * Draws Player on the canvas context
     */
    draw(ctx) {
        let image = null;
        
        if (this.jumping && !this.crouching) {
            if (this.dirRight) image = this.playerRight;
            if (this.dirLeft) image = this.playerLeft;
        } else {
            if (this.dirRight && !this.crouching && !this.keyRight) image = this.playerRight;
            if (this.dirRight && !this.crouching && this.keyRight) image = this.playerRunRight;
            if (this.dirRight && this.crouching) image = this.playerCrouchRight;
            if (this.dirLeft && !this.crouching && !this.keyLeft) image = this.playerLeft;
            if (this.dirLeft && !this.crouching && this.keyLeft) image = this.playerRunLeft;
            if (this.dirLeft && this.crouching) image = this.playerCrouchLeft;
        }
        
        if (image) {
            ctx.drawImage(image, this.x, this.y);
        } else {
            // Fallback if images not loaded
            ctx.fillStyle = this.playerNo === 1 ? '#ff0000' : '#0000ff';
            ctx.fillRect(this.x, this.y, this.w, this.h);
        }
        
        // Draw player number indicator
        ctx.fillStyle = '#ffffff';
        for (let i = 0; i < this.playerNo; i++) {
            ctx.fillRect(this.x + 17, this.y + 4 * i, 2, 2);
        }
    }
    
    /**
     * Fire an EPIC WAVE
     */
    epicWave(boxes) {
        if (this.wave > 10) this.wave = 0;
        
        if (this.itemsLeft < 10) {
            let dir = this.dirLeft ? 0 : 1;
            
            boxes[this.wave + (this.playerNo - 1) * 10] = new Box(
                this.x + Math.floor(this.w / 2), 
                this.y + Math.floor(this.h / 2), 
                dir
            );
        }
        
        this.wave++;
    }
    
    /**
     * Move player in x-axis
     */
    moveX() {
        // Moves the player according to the player's current xV
        this.updatePosition();
        
        // Moves the player XSpeed pixels to the left
        if (this.keyLeft) this.walkLeft();
        
        // Moves the player XSpeed pixels to the right
        if (this.keyRight) this.walkRight();
    }
    
    /**
     * Moves the player in y-axis
     */
    moveY() {
        // The player jumps if the up key is being pressed
        // and he's not falling or jumping
        if (this.keyUp) {
            if (!this.falling && !this.jumping) {
                this.jumping = true;
                this.jumpFallTimer = 0;
            }
        }
        
        // Update player y-speed if he's falling
        if (this.falling) {
            this.yV = Math.floor(-(-Way.gravity * this.jumpFallTimer));
            this.y += this.yV;
            this.jumpFallTimer++;
        }
        
        // If the player is in a jump
        if (this.jumping) {
            this.yV = Math.floor(-(this.jumpSpeed - Way.gravity * this.jumpFallTimer));
            this.y += this.yV;
            this.jumpFallTimer++;
            
            if (this.yV === 0) {
                this.jumping = false;
                this.falling = true;
                this.jumpFallTimer = 1;
            }
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
     * Moves the player left
     */
    walkLeft() {
        this.doubleX -= this.xSpeed;
        this.x = Math.floor(this.doubleX);
    }
    
    /**
     * Moves the player right
     */
    walkRight() {
        this.doubleX += this.xSpeed;
        this.x = Math.floor(this.doubleX);
    }
    
    /**
     * Makes the player crouch if the sent boolean is true
     * Makes the player stand up if it's false
     */
    crouch(c) {
        this.crouching = c;
        
        if (c) {
            this.h = 10;
            this.y += 10;
            this.xSpeed = 3;
        }
        
        if (!c) {
            this.h = 20;
            this.y -= 10;
            this.xSpeed = 5;
        }
    }
    
    /**
     * Makes the player crouch if the down key is being pressed
     * Makes the player uncrouch if the down key is not being pressed
     */
    setStance() {
        // Makes the player crouch if he isn't already crouching
        if (this.keyDown && !this.crouching) {
            this.crouch(true);
        }
        
        // Makes the player stand. If there's something
        // in the way, he keeps crouching (even if the down key
        // is still being pressed)
        if (!this.keyDown && this.crouching) {
            this.crouch(false);
        }
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
     * Checks for collisions with all boxes (after player has changed stance)
     */
    checkCollisionsCrouch(boxes) {
        for (let box of boxes) {
            if (box.checkHit(this.getBounds())) {
                // Kills the player if he collided with a deadly box
                if (box.isDeadly()) {
                    this.die();
                    this.deathByDeadlyBoxFlag = true;
                } else {
                    // Player keeps crouching if there's a box above him
                    this.crouch(true);
                }
            }
        }
    }
    
    /**
     * Checks for collisions with all boxes (after player and boxes have moved in x-axis)
     */
    checkCollisionsX(boxes) {
        // Check collisions with boxes
        for (let box of boxes) {
            if (box.checkHit(this.getBounds())) {
                // Kills the player if the box he collided with was deadly
                if (box.isDeadly()) {
                    this.die();
                    this.deathByDeadlyBoxFlag = true;
                }
                
                if (box.isStopper()) {
                    box.setX(-100);
                    Way.slowDownBoxes = true;
                } else {
                    // If player collided with the box on his left side
                    if (this.getX() > box.getX()) {
                        // Places the player next to the box
                        this.setX(box.getX() + box.getWidth());
                        this.collLeft = true;
                        
                        // Check if player was placed on another box to the right
                        for (let box2 of boxes) {
                            if (box2.checkHit(this.getBounds())) {
                                if (this.x < box.getX()) {
                                    if (box2.isDeadly()) {
                                        this.die();
                                        this.deathByDeadlyBoxFlag = true;
                                    } else {
                                        this.collRight = true;
                                    }
                                }
                            }
                        }
                    }
                    
                    // If player collided with the box on his right side
                    if (this.x < box.getX()) {
                        // Places the player next to the box
                        this.setX(box.getX() - this.w);
                        this.collRight = true;
                        
                        // Check if player was placed on another box to the left
                        for (let box2 of boxes) {
                            if (box2.checkHit(this.getBounds())) {
                                if (this.getX() > box.getX()) {
                                    if (box2.isDeadly()) {
                                        this.die();
                                        this.deathByDeadlyBoxFlag = true;
                                    } else {
                                        this.collLeft = true;
                                    }
                                }
                            }
                        }
                    }
                    
                    this.xV = 0;
                }
            }
        }
        
        // Kills player if he's collided with something on both sides
        if (this.collRight && this.collLeft) {
            this.die();
            this.jumping = false;
            this.falling = false;
            this.deathBySquishFlag = true;
        }
    }
    
    /**
     * Checks for collisions with all boxes in y-axis
     */
    checkCollisionsY(boxes) {
        this.collFeet = true;
        this.collHead = true;
        
        // Runs until the player isn't colliding with any box anymore
        while (this.collFeet || this.collHead) {
            this.collFeet = false;
            this.collHead = false;
            
            for (let box of boxes) {
                if (box.checkHit(this.getBounds())) {
                    if (box.isDeadly()) {
                        this.die();
                        this.deathByDeadlyBoxFlag = true;
                    }
                    
                    if (box.isStopper()) {
                        box.setX(-100);
                        Way.slowDownBoxes = true;
                    } else {
                        // If player is falling he is put ON TOP OF the box he collided with
                        if (this.falling) {
                            this.collFeet = true;
                            this.xV = box.getXV();
                            this.y = box.getY() - this.h;
                        }
                        
                        // If player is jumping he is put UNDERNEATH the box he collided with
                        if (this.jumping) {
                            this.collHead = true;
                            this.xV = box.getXV();
                            this.y = box.getY() + box.getHeight();
                        }
                    }
                }
            }
            
            // If player has landed on a box
            if (this.collFeet) {
                this.yV = 0;
                this.jumpFallTimer = 0;
                this.falling = false;
                this.jumping = false;
            }
            
            // If player jumped into a box from underneath
            if (this.collHead) {
                this.yV = 0;
                this.jumping = false;
                this.falling = true;
                this.jumpFallTimer = 1;
            }
        }
        
        // Check collision with ground
        if (this.y + this.h > Way.h) {
            this.y = Way.h - this.h;
            this.falling = false;
            this.jumping = false;
            this.yV = 0;
            this.xV = 0;
            this.jumpFallTimer = 0;
            this.collGround = true;
        }
        
        // Makes the player fall if there's no box underneath him
        if (!this.falling && !this.jumping) {
            this.falling = true;
            this.y += 1;
            
            for (let box of boxes) {
                if (box.checkHit(this.getBounds())) {
                    this.falling = false;
                    this.xV = box.getXV();
                }
            }
            
            if (this.y + this.h > Way.h) {
                this.falling = false;
                this.xV = 0;
            }
            
            this.y -= 1;
            this.jumpFallTimer = 0;
        }
    }
    
    /**
     * Keeps player inside the game area
     * Player appears on the other side if he leaves the area
     */
    keepInside(boxes) {
        // Keep inside left
        if (this.x + this.w < 0) {
            this.setX(Way.w);
            
            for (let box of boxes) {
                if (box.checkHit(this.getBounds())) {
                    this.setX(0 - this.w);
                    if (box.isDeadly()) {
                        this.deathByDeadlyBoxFlag = true;
                        this.die();
                    }
                }
            }
        }
        
        // Keep inside right
        if (this.x > Way.w) {
            this.setX(0 - this.w);
            
            for (let box of boxes) {
                if (box.checkHit(this.getBounds())) {
                    this.setX(Way.w);
                    if (box.isDeadly()) {
                        this.deathByDeadlyBoxFlag = true;
                        this.die();
                    }
                }
            }
        }
    }
    
    /**
     * Checks if player has collided with an item and thus caught it
     */
    checkItemCatch(item, boxes) {
        // Check if player has caught the Item
        if (item.checkHit(this.getBounds())) {
            this.itemCaught();
            
            // Ends the game if player has caught all items
            if (this.caughtAllItems) {
                item.setX(Way.w);
            } else {
                item.updatePosition(boxes);
            }
        }
    }
    
    /**
     * Resets the collisions (at the end of every loop)
     */
    resetCollisions() {
        this.collLeft = false;
        this.collRight = false;
        this.collHead = false;
        this.collFeet = false;
        this.collGround = false;
    }
    
    /**
     * Resets everything at the start of a game
     */
    reset() {
        if (this.playerNo === 2) this.x = 50;
        if (this.playerNo === 1) this.x = Way.w - 50 - this.w;
        this.y = Way.h - 20;
        this.xV = 0;
        this.yV = 0;
        this.doubleX = this.x;
        
        this.keyDown = false;
        this.keyUp = false;
        this.keyLeft = false;
        if (this.playerNo === 1) this.dirLeft = true;
        this.keyRight = false;
        this.jumping = false;
        this.falling = false;
        
        this.jumpFallTimer = 0;
        
        this.itemsLeft = 10;
        this.points = 0;
        
        this.deathBySquishFlag = false;
        this.deathByTimeFlag = false;
        this.deathByDeadlyBoxFlag = false;
        this.alive = true;
        this.caughtAllItems = false;
    }
}