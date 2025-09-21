# Way - Multiplayer Game

A 2-player multiplayer action game ported from Java to JavaScript for browser play.

## Play Online

🎮 **[Play the game here](https://mold.github.io/way-multi/)** (GitHub Pages)

## Local Development

1. Clone this repository
2. Open `index.html` in a web browser or serve with a local HTTP server:
   ```bash
   python3 -m http.server 8080
   ```
3. Navigate to `http://localhost:8080`

## Testing

Run the unit tests by opening `test.html` in a browser or navigating to `http://localhost:8080/test.html`

## Game Controls

### Player 1
- **Arrow Keys**: Move
- **Enter**: Epic Wave attack

### Player 2  
- **WASD**: Move
- **1**: Epic Wave attack

### General Controls
- **S**: Start Game
- **I**: Show/Hide Instructions  
- **ESC**: Pause
- **Q**: Quit

## Gameplay

- Catch 10 items before time runs out
- Avoid deadly gray boxes
- Use green stopper boxes to slow down obstacles
- Epic Wave attacks can clear obstacles but cost points
- Don't get crushed between boxes!

## Deployment to GitHub Pages

This repository is configured for automatic deployment to GitHub Pages via GitHub Actions. The deployment happens automatically when changes are pushed to the main branch.

### Manual Deployment Setup

1. Go to your repository Settings
2. Navigate to Pages section
3. Set Source to "GitHub Actions"
4. The workflow in `.github/workflows/deploy.yml` will handle the deployment

## Technical Details

The game is a JavaScript port of the original Java version with:
- HTML5 Canvas rendering
- Object-oriented class structure
- Physics-based movement and collision detection
- Local multiplayer support
- Responsive design

## Files Structure

- `index.html` - Main game page
- `test.html` - Unit tests page
- `js/` - JavaScript game classes
  - `Way.js` - Main game engine
  - `Player.js` - Player character logic
  - `Box.js` - Moving obstacles
  - `Item.js` - Collectible items
  - `Particle.js` - Background effects
- `img/` - Game sprites and images
- `.github/workflows/deploy.yml` - GitHub Pages deployment
