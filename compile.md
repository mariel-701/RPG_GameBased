# RPG Battle System — Compile & Run

## Prerequisites

- Java 17+ installed (`javac` and `java` available in PATH)
- Run all commands from the **project root directory**

## Compile & Run (One Liner)

```bash
javac -d build src/**/*.java && java -cp build main.Main
```

## Step-by-Step

### 1. Compile

```bash
javac -d build src/characters/Character.java \
  src/characters/Warrior.java \
  src/characters/Mage.java \
  src/characters/Archer.java \
  src/items/Item.java \
  src/items/EmptyInventoryException.java \
  src/enemies/Enemy.java \
  src/enemies/Goblin.java \
  src/enemies/Orc.java \
  src/enemies/Skeleton.java \
  src/enemies/DarkKnight.java \
  src/main/SaveManager.java \
  src/main/GameOverPanel.java \
  src/main/ShopPanel.java \
  src/main/CharacterSelect.java \
  src/main/GamePanel.java \
  src/main/Main.java
```

### 2. Run

```bash
java -cp build main.Main
```

## Using a Shell Script

Create `run.sh` in the project root:

```bash
#!/bin/bash
javac -d build src/**/*.java && java -cp build main.Main
```

Then:

```bash
chmod +x run.sh
./run.sh
```

## Important Notes

- Run from the **project root** (`/home/tiredshyt/help/game`) so the game can find image assets in the `assets/` folder.
- The `build/` directory is created automatically during compilation.
- If you get a "file not found" for images, the game will still run — it just won't show the background or character sprites.

## Game Flow

1. **Character Select** — Click 2 heroes to add to your party, name them
2. **Battle** — Fight through 4 waves of enemies (Attack / Skill / Item / Flee)
3. **Shop** — Between waves, spend gold on items
4. **Victory/Defeat** — Score screen shows at the end
5. **Save** — Click SAVE anytime, or auto-saves between waves. Load from the main menu.
