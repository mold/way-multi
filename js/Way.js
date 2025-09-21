/**
 * The main class
 * Handles the actual game
 */
class Way {
    constructor() {
        // Game constants
        Way.w = 1000;
        Way.h = 800;
        Way.gravity = 0.5;
        Way.slowDownBoxes = false;
        
        // Canvas and context
        this.canvas = document.getElementById('gameCanvas');
        this.ctx = this.canvas.getContext('2d');
        
        // Adjust canvas size to fit screen
        this.adjustCanvasSize();
        
        // Game timing
        this.sleepTime = 1000 / 50; // 1000/UPS, default 1000/50
        this.timeLimitMillis = 120 * 1000; // The time limit in milliseconds
        this.numberOfSlowDownBoxes = 0;
        
        // Game objects
        this.players = [new Player(1), new Player(2)];
        this.boxes = [];
        this.particles = [];
        this.item = new Item();
        
        // Create boxes and particles arrays
        for (let i = 0; i < Math.floor((Way.h * Way.w) / 6000); i++) {
            this.boxes.push(null); // Will be filled during init
        }
        for (let i = 0; i < 500; i++) {
            this.particles.push(null); // Will be filled during init
        }
        
        // Timing variables
        this.timeStartMillis = 0;
        this.currentTimeMillis = 0;
        this.timePauseMillis = 0;
        this.timePauseStartMillis = 0;
        this.timeTotalPauseMillis = 0;
        this.timeStopMillis = 0;
        this.timeStopStartMillis = 0;
        this.timeStopTime = 0;
        
        // Game state variables
        this.gameStateStart = true;
        this.showInstructions = false;
        this.gameStateRun = false;
        this.gameStateEnd = false;
        this.gameStatePause = false;
        
        // Visual effects
        this.explosionTimer = 0;
        this.explode = false;
        
        // Load images
        this.images = {};
        this.loadImages();
        
        // Set up event listeners
        this.setupEventListeners();
        
        // Start the game loop
        this.gameLoop();
    }
    
    adjustCanvasSize() {
        const maxWidth = window.innerWidth - 20;
        const maxHeight = window.innerHeight - 100;
        
        const scaleX = maxWidth / Way.w;
        const scaleY = maxHeight / Way.h;
        const scale = Math.min(scaleX, scaleY, 1);
        
        this.canvas.width = Way.w;
        this.canvas.height = Way.h;
        this.canvas.style.width = (Way.w * scale) + 'px';
        this.canvas.style.height = (Way.h * scale) + 'px';
    }
    
    loadImages() {
        const imageNames = [
            'playerRight', 'playerLeft', 'playerCrouchRight', 'playerCrouchLeft',
            'playerRunRight', 'playerRunLeft', 'item'
        ];
        
        let loadedCount = 0;
        const totalImages = imageNames.length;
        
        imageNames.forEach(name => {
            const img = new Image();
            img.onload = () => {
                loadedCount++;
                if (loadedCount === totalImages) {
                    this.onImagesLoaded();
                }
            };
            img.onerror = () => {
                console.warn(`Failed to load image: ${name}.gif`);
                loadedCount++;
                if (loadedCount === totalImages) {
                    this.onImagesLoaded();
                }
            };
            img.src = `img/${name}.gif`;
            this.images[name] = img;
        });
    }
    
    onImagesLoaded() {
        // Set images for players
        for (let player of this.players) {
            player.setImages(
                this.images.playerRight,
                this.images.playerLeft,
                this.images.playerCrouchRight,
                this.images.playerCrouchLeft,
                this.images.playerRunRight,
                this.images.playerRunLeft
            );
        }
        
        // Set image for item
        this.item.setImage(this.images.item);
    }
    
    setupEventListeners() {
        document.addEventListener('keydown', (e) => this.keyPressed(e));
        document.addEventListener('keyup', (e) => this.keyReleased(e));
        
        // Prevent default behavior for game keys
        document.addEventListener('keydown', (e) => {
            if (['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'Space', 'Enter', 'Escape'].includes(e.code) ||
                ['KeyW', 'KeyA', 'KeyS', 'KeyD', 'Digit1', 'KeyI', 'KeyQ', 'KeyC', 'KeyR', 'KeyH', 'KeyG'].includes(e.code)) {
                e.preventDefault();
            }
        });
        
        // Handle window resize
        window.addEventListener('resize', () => this.adjustCanvasSize());
    }
    
    init() {
        this.gameStateEnd = false;
        this.gameStatePause = false;
        this.gameStateStart = false;
        
        // Create all the boxes
        for (let i = 0; i < this.boxes.length; i++) {
            this.boxes[i] = new Box();
        }
        
        // Create all particles
        for (let i = 0; i < this.particles.length; i++) {
            this.particles[i] = new Particle();
        }
        
        if (!this.showInstructions) {
            this.item.setX(Way.w / 2);
            this.item.setY(Way.h / 2);
        }
        
        this.showInstructions = false;
        
        // Start the timer
        this.timeStartMillis = Date.now();
        
        this.explosionTimer = 0;
        this.explode = true;
        
        Way.slowDownBoxes = false;
        this.numberOfSlowDownBoxes = 0;
        
        // Reset players
        for (let player of this.players) {
            player.reset();
        }
        
        this.gameStateRun = true;
    }
    
    gameLoop() {
        if (this.gameStateRun) {
            this.updateGameRun();
        } else if (this.gameStateStart) {
            this.updateGameStart();
        } else if (this.gameStatePause) {
            this.updateGamePause();
        } else if (this.gameStateEnd) {
            this.updateGameEnd();
        }
        
        this.render();
        
        setTimeout(() => this.gameLoop(), this.sleepTime);
    }
    
    updateGameRun() {
        // Move particles
        for (let particle of this.particles) {
            if (particle) {
                particle.updatePosition();
                
                if (particle.getX() + particle.getWidth() < 0) particle.setX(Way.w);
                if (particle.getX() > Way.w) particle.setX(0);
            }
        }
        
        // Player movement and stance
        for (let player of this.players) {
            player.setStance(); // Make the player crouch or stand up
            player.checkCollisionsCrouch(this.boxes); // Check for collisions when the player has stopped crouching
            player.moveX(); // Move player in x-axis
        }
        
        // Box speed management
        if (Way.slowDownBoxes) {
            for (let box of this.boxes) {
                if (box && box.getXV() !== 0) {
                    let XVint = Math.floor(box.getXV() * 10);
                    
                    if (XVint > 0) XVint -= 1;
                    else XVint += 1;
                    
                    box.setXV(XVint / 10.0);
                }
            }
            
            if (this.timeStopTime === 0) {
                this.timeStopTime = Math.floor(Math.random() * 15) + 5;
                this.timeStopStartMillis = this.currentTimeMillis;
            }
            
            this.timeStopMillis = this.currentTimeMillis - this.timeStopStartMillis;
            
            if (this.timeStopMillis > this.timeStopTime * 1000) {
                Way.slowDownBoxes = false;
            }
        }
        
        // Accelerate boxes back to normal speed
        if (!Way.slowDownBoxes) {
            for (let box of this.boxes) {
                if (box && box.getXV() !== box.getXVStart()) {
                    let XVint = Math.floor(box.getXV() * 10);
                    
                    if (box.getXVStart() > 0) XVint += 1;
                    else XVint -= 1;
                    
                    box.setXV(XVint / 10.0);
                }
            }
            
            this.timeStopTime = 0;
        }
        
        // Move all boxes
        for (let i = 0; i < this.boxes.length; i++) {
            if (this.boxes[i]) {
                this.boxes[i].updatePosition();
                
                if (this.boxes[i].getX() + this.boxes[i].getWidth() < 0) this.boxes[i] = new Box();
                if (this.boxes[i].getX() > Way.w) this.boxes[i] = new Box();
            }
        }
        
        // Player physics and collisions
        for (let player of this.players) {
            player.keepInside(this.boxes); // Keep the player inside the game area
            player.checkCollisionsX(this.boxes); // Check for collisions in x-axis
            
            if (player.isAlive()) {
                player.moveY(); // move player in y-axis
                player.checkCollisionsY(this.boxes); // Check for collisions in y-axis
            }
            
            player.checkItemCatch(this.item, this.boxes); // Check if player has caught an item
            player.resetCollisions(); // Reset all collision booleans
        }
        
        this.currentTimeMillis = Date.now() - this.timeStartMillis - this.timeTotalPauseMillis;
        
        // Handle player death and respawn
        for (let player of this.players) {
            if (!player.isAlive()) {
                if (player.getItemsLeft() < 10) {
                    player.setItemsLeft(player.getItemsLeft() + 1);
                    if (player.getPlayerNo() === 1) player.setX(Way.w - 50 - player.getWidth());
                    if (player.getPlayerNo() === 2) player.setX(50);
                    player.setY(Way.h - 20);
                    player.keyLeft(false);
                    player.keyRight(false);
                    player.keyUp(false);
                    player.keyDown(false);
                    
                    if (player.getPlayerNo() === 1) player.dirLeft();
                    if (player.getPlayerNo() === 2) player.dirRight();
                    
                    player.setAlive(true);
                } else {
                    this.gameStateRun = false;
                    this.gameStateEnd = true;
                    player.keyLeft(false);
                    player.keyRight(false);
                    player.keyDown(false);
                }
            }
            
            // End game if player has caught all items
            if (player.hasCaughtAllItems()) {
                this.gameStateRun = false;
                this.gameStateEnd = true;
            }
        }
    }
    
    updateGameStart() {
        // Just wait for input
    }
    
    updateGamePause() {
        this.timePauseMillis = Date.now() - this.timePauseStartMillis;
    }
    
    updateGameEnd() {
        // Just wait for input
    }
    
    render() {
        // Clear the canvas
        this.ctx.fillStyle = 'black';
        this.ctx.fillRect(0, 0, Way.w, Way.h);
        
        if (!this.gameStateStart) {
            // Draw particles
            for (let particle of this.particles) {
                if (particle) particle.draw(this.ctx);
            }
            
            // Draw point boxes for both players
            this.drawPointBoxes();
            
            // Draw the item
            this.item.draw(this.ctx);
            
            // Draw the boxes
            for (let box of this.boxes) {
                if (box) box.draw(this.ctx);
            }
            
            // Draw players
            for (let player of this.players) {
                player.draw(this.ctx);
            }
        }
        
        if (this.gameStateStart) {
            this.drawStartScreen();
        }
        
        if (this.gameStateEnd) {
            this.drawEndScreen();
        }
        
        if (this.gameStatePause) {
            this.drawPauseScreen();
        }
    }
    
    drawPointBoxes() {
        // Player 1 (right side)
        for (let i = 0; i < 10; i++) {
            this.ctx.strokeStyle = '#ff0000';
            this.ctx.strokeRect(Way.w - 40, 30 + 40 * i, 30, 30);
        }
        
        for (let i = 0; i < 10 - this.players[0].getItemsLeft(); i++) {
            this.ctx.fillStyle = '#ff0000';
            this.ctx.fillRect(Way.w - 40, 30 + 40 * i, 30, 30);
        }
        
        // Player 2 (left side)
        for (let i = 0; i < 10; i++) {
            this.ctx.strokeStyle = '#ff0000';
            this.ctx.strokeRect(10, 30 + 40 * i, 30, 30);
        }
        
        for (let i = 0; i < 10 - this.players[1].getItemsLeft(); i++) {
            this.ctx.fillStyle = '#ff0000';
            this.ctx.fillRect(10, 30 + 40 * i, 30, 30);
        }
    }
    
    drawStartScreen() {
        this.ctx.strokeStyle = '#ffffff';
        this.ctx.strokeRect(Math.floor(Way.w / 4 * 1.5), Math.floor(Way.h / 5), Math.floor(Way.w / 4), Math.floor(Way.h / 5 * 3));
        
        this.ctx.fillStyle = '#ffffff';
        this.ctx.font = '16px monospace';
        this.ctx.fillText('Way 2009', Math.floor(Way.w / 4 * 1.5) + 30, Math.floor(Way.h / 5) + 50);
        this.ctx.fillText('by dkd (JS port)', Math.floor(Way.w / 4 * 1.5) + 30, Math.floor(Way.h / 5) + 70);
        
        this.ctx.fillText('S: Start game', Math.floor(Way.w / 4 * 1.5) + 30, Math.floor(Way.h / 5) + 150);
        this.ctx.fillText('I: Show/hide instructions', Math.floor(Way.w / 4 * 1.5) + 30, Math.floor(Way.h / 5) + 170);
        this.ctx.fillText('Q: Exit', Math.floor(Way.w / 4 * 1.5) + 30, Math.floor(Way.h / 5) + 190);
        
        if (this.showInstructions) {
            this.drawInstructions();
        }
    }
    
    drawInstructions() {
        this.ctx.fillStyle = '#ff0000';
        
        // Draw point boxes
        for (let i = 0; i < 8; i++) {
            this.ctx.fillRect(Way.w - 40, 30 + 40 * i, 30, 30);
        }
        
        // Draw time boxes
        for (let i = 0; i < 20; i++) {
            this.ctx.fillRect(Way.w - 65, 30 + 13 * i, 15, 9);
        }
        
        this.item.setX(Way.w - 33);
        this.item.setY(30 + 40 * 10);
        this.item.draw(this.ctx);
        
        this.ctx.fillStyle = '#ffffff';
        this.ctx.font = '14px monospace';
        
        const instructionTexts = [
            '* Control your character',
            '   with the arrow keys (P1) or WASD (P2)',
            '* Catch 10 items before',
            '   the time runs out.',
            '* Stop the boxes with special attacks.',
            '   This may or may not help you,',
            '   but it will cost points.',
            '* If you get crushed between',
            '   two boxes, you die.',
            '* If you hit a gray box, you die.',
            '* May the force be with you.'
        ];
        
        for (let i = 0; i < instructionTexts.length; i++) {
            this.ctx.fillText(instructionTexts[i], Math.floor(Way.w / 4 * 2.5) + 20, Math.floor(Way.h / 5) + 14 + i * 16);
        }
        
        this.ctx.fillText('Time left', Way.w - 120, 25 + 13 * 20);
        this.ctx.fillText('Items left', Way.w - 96, 19 + 40 * 8);
        this.ctx.fillText('Item -', Way.w - 70, 42 + 40 * 10);
    }
    
    drawEndScreen() {
        for (let player of this.players) {
            if (!player.isAlive()) {
                // Explosion
                this.ctx.fillStyle = '#ff0000';
                this.ctx.beginPath();
                this.ctx.arc(
                    player.getX() + Math.floor(player.getWidth() / 2),
                    player.getY() + Math.floor(player.getHeight() / 2),
                    this.explosionTimer,
                    0,
                    2 * Math.PI
                );
                this.ctx.fill();
                
                if (this.explosionTimer > 20) this.explode = false;
                if (this.explode) this.explosionTimer++;
                
                player.draw(this.ctx);
                
                this.drawEndDialog(player);
            }
            
            if (player.hasCaughtAllItems()) {
                // Victory explosion
                this.ctx.fillStyle = '#00ff00';
                this.ctx.beginPath();
                this.ctx.arc(
                    player.getX() + Math.floor(player.getWidth() / 2),
                    player.getY() + Math.floor(player.getHeight() / 2),
                    this.explosionTimer,
                    0,
                    2 * Math.PI
                );
                this.ctx.fill();
                
                if (this.explosionTimer > 50) this.explode = false;
                if (this.explode) this.explosionTimer++;
                
                player.draw(this.ctx);
                
                this.drawVictoryDialog(player);
            }
        }
    }
    
    drawEndDialog(player) {
        const x = Math.floor(Way.w / 4 * 1.5);
        const y = Math.floor(1.5 * Way.h / 5);
        const w = Math.floor(Way.w / 4);
        const h = Math.floor(2 * Way.h / 5);
        
        this.ctx.fillStyle = '#000000';
        this.ctx.fillRect(x, y, w, h);
        this.ctx.strokeStyle = '#ffffff';
        this.ctx.strokeRect(x, y, w, h);
        
        this.ctx.fillStyle = '#ffffff';
        this.ctx.font = '14px monospace';
        
        if (player.deathBySquish()) {
            this.ctx.fillText(`Player ${player.getPlayerNo()} got squished and`, x + 30, y + 40);
            this.ctx.fillText('DIED DIED DIED DIED', x + 30, y + 54);
        }
        
        if (player.deathByDeadlyBox()) {
            this.ctx.fillText(`Player ${player.getPlayerNo()} got hit by a`, x + 30, y + 40);
            this.ctx.fillText('deadly box and', x + 30, y + 54);
            this.ctx.fillText('DIED DIED DIED DIED', x + 30, y + 68);
        }
        
        this.ctx.fillText('R: Restart game', x + 30, y + 124);
        this.ctx.fillText('Q: Quit', x + 30, y + 138);
    }
    
    drawVictoryDialog(player) {
        const x = Math.floor(Way.w / 4 * 1.5);
        const y = Math.floor(1.5 * Way.h / 5);
        const w = Math.floor(Way.w / 4);
        const h = Math.floor(2 * Way.h / 5);
        
        this.ctx.fillStyle = '#000000';
        this.ctx.fillRect(x, y, w, h);
        this.ctx.strokeStyle = '#ffffff';
        this.ctx.strokeRect(x, y, w, h);
        
        this.ctx.fillStyle = '#ffffff';
        this.ctx.font = '14px monospace';
        
        this.ctx.fillText(`Player ${player.getPlayerNo()} won! Congratulations!`, x + 30, y + 40);
        
        this.ctx.fillText('Score', x + 30, y + 68);
        const score1 = Math.floor((12 - this.players[0].getItemsLeft()) * 54.31 - ((this.currentTimeMillis + 500) / 10000) * 3.14);
        const score2 = Math.floor((12 - this.players[1].getItemsLeft()) * 54.31 - ((this.currentTimeMillis + 500) / 10000) * 3.14);
        this.ctx.fillText(`Player 1: ${score1}`, x + 30, y + 82);
        this.ctx.fillText(`Player 2: ${score2}`, x + 30, y + 96);
        
        this.ctx.fillText(`Time: ${Math.floor((this.currentTimeMillis + 500) / 1000)}s`, x + 30, y + 124);
        
        this.ctx.fillText('R: Restart game', x + 30, y + 152);
        this.ctx.fillText('Q: Quit', x + 30, y + 166);
    }
    
    drawPauseScreen() {
        const x = Math.floor(Way.w / 4 * 1.5);
        const y = Math.floor(1.5 * Way.h / 5);
        const w = Math.floor(Way.w / 4);
        const h = Math.floor(2 * Way.h / 5);
        
        this.ctx.fillStyle = '#000000';
        this.ctx.fillRect(x, y, w, h);
        this.ctx.strokeStyle = '#ffffff';
        this.ctx.strokeRect(x, y, w, h);
        
        this.ctx.fillStyle = '#ffffff';
        this.ctx.font = '16px monospace';
        this.ctx.fillText('GAME PAUSED', x + 30, y + 40);
        
        this.ctx.font = '14px monospace';
        this.ctx.fillText('C: Continue game', x + 30, y + 124);
        this.ctx.fillText('R: Restart game', x + 30, y + 138);
        this.ctx.fillText('Q: Quit', x + 30, y + 152);
    }
    
    keyPressed(e) {
        if (this.gameStateRun) {
            // Player 1 - Arrow keys + Enter
            if (e.code === 'ArrowLeft') {
                this.players[0].keyLeft(true);
                this.players[0].dirLeft();
            }
            if (e.code === 'ArrowRight') {
                this.players[0].keyRight(true);
                this.players[0].dirRight();
            }
            if (e.code === 'ArrowUp') {
                this.players[0].keyUp(true);
            }
            if (e.code === 'ArrowDown') {
                this.players[0].keyDown(true);
            }
            if (e.code === 'Enter') {
                this.players[0].epicWave(this.boxes);
            }
            
            // Player 2 - WASD + 1
            if (e.code === 'KeyA') {
                this.players[1].keyLeft(true);
                this.players[1].dirLeft();
            }
            if (e.code === 'KeyD') {
                this.players[1].keyRight(true);
                this.players[1].dirRight();
            }
            if (e.code === 'KeyW') {
                this.players[1].keyUp(true);
            }
            if (e.code === 'KeyS') {
                this.players[1].keyDown(true);
            }
            if (e.code === 'Digit1') {
                this.players[1].epicWave(this.boxes);
            }
            
            if (e.code === 'Escape') {
                this.gameStatePause = true;
                this.gameStateRun = false;
                for (let player of this.players) {
                    player.keyLeft(false);
                    player.keyRight(false);
                }
                this.timePauseStartMillis = Date.now();
            }
            
            // Developer cheat keys
            if (e.code === 'KeyH') {
                Way.slowDownBoxes = !Way.slowDownBoxes;
                if (!Way.slowDownBoxes) this.numberOfSlowDownBoxes++;
            }
            if (e.code === 'KeyG') {
                this.players[0].itemCaught();
                this.players[1].itemCaught();
            }
        }
        
        if (this.gameStatePause) {
            if (e.code === 'KeyC') {
                this.gameStateRun = true;
                this.gameStatePause = false;
                this.timeTotalPauseMillis += this.timePauseMillis;
            }
            if (e.code === 'KeyQ') {
                window.close();
            }
            if (e.code === 'KeyR') {
                this.init();
            }
        }
        
        if (this.gameStateStart) {
            if (e.code === 'KeyS') {
                this.init();
            }
            if (e.code === 'KeyI') {
                this.showInstructions = !this.showInstructions;
            }
            if (e.code === 'KeyQ') {
                window.close();
            }
        }
        
        if (this.gameStateEnd) {
            if (e.code === 'KeyR') {
                this.init();
            }
            if (e.code === 'KeyQ') {
                window.close();
            }
        }
    }
    
    keyReleased(e) {
        if (this.gameStateRun) {
            // Player 1 - Arrow keys
            if (e.code === 'ArrowLeft') {
                this.players[0].keyLeft(false);
                this.players[0].setXV(0);
            }
            if (e.code === 'ArrowRight') {
                this.players[0].keyRight(false);
                this.players[0].setXV(0);
            }
            if (e.code === 'ArrowDown') {
                this.players[0].keyDown(false);
            }
            if (e.code === 'ArrowUp') {
                this.players[0].keyUp(false);
            }
            
            // Player 2 - WASD
            if (e.code === 'KeyA') {
                this.players[1].keyLeft(false);
                this.players[1].setXV(0);
            }
            if (e.code === 'KeyD') {
                this.players[1].keyRight(false);
                this.players[1].setXV(0);
            }
            if (e.code === 'KeyS') {
                this.players[1].keyDown(false);
            }
            if (e.code === 'KeyW') {
                this.players[1].keyUp(false);
            }
        }
    }
}

// Start the game when the page loads
window.addEventListener('load', () => {
    new Way();
});