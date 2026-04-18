# Main Quest

`Main Quest` is a small Java Swing RPG project.

## Features
- Start menu with `Play` and `Exit`
- Class selection: `Warrior`, `Mage`, `Archer`, `Tank`
- Turn-based combat with speed-based turn order
- Explore scenes and enemy encounters
- Inventory with item slots and item images
- EXP, HP, and SP progression

## Main Classes
- `GameFrame`: main Swing UI and gameplay controller
- `Character`: abstract base class for all combat units
- `Warrior`, `Mage`, `Archer`, `Tank`: player classes
- `Enemy`, `Goblin`, `WizardEnemy`, `Ogre`: enemy hierarchy
- `Inventory`, `Item`, `Usable`: inventory system
- `LevelUpInfo`, `EnemyTurnResult`: helper/data classes

## Character Skills
### Warrior
- `Slash`: Deals x2 Damage to Enemies
- `Guard`: Take ½ Damage from Enemies next Attack
- `Counter`: Risk 1 turn to Prepare to counter the Enemies next Attack

### Mage
- `Doom`: Deal x2 Damage to Enemies
- `Heal`: Heals ½ of the Mages Max HP
- `Bolt`: Does ½ Damage but always goes First regardless of Enemies speed

### Archer
- `Quick Shot`: Deal Damage and always goes First regardless of Enemies speed
- `Heavy Arrow`: Does x3 Damage but always goes Last regardless of Enemies speed
- `Evade`: Dodge the Enemies next Attack

### Tank
- `Fortify`: Takes ½ Damage from Enemies next Attack and heal 20% of the Tank's HP
- `Protect`: Does not take any Damage the next turn
- `Bash`: Deal x2 Damage and a 20% Chance to Stun Enemies

## Enemy Skills
### Goblin
- `Nimble`: Attacks and takes another Turn after | Requires 1 SP
- `Double Stab`: Deal x2 Damage to the Player | Requires 2 SP

### Wizard
- `Field`: Casts a shield that absorbs Damage | Requires 1 SP
- `Concuss`: Stuns the Player the next turn | Requires 2 SP

### Ogre
- `Smash`: Deal x2 Damage to the Player and a 20% chance to Stun | Requires 1 SP
- `Earthquake`: Deal Damage to the Player and a 70% chance to Stun | Requires 2 SP

## Run
Compile:

```powershell
javac -d build\classes src\main\*.java
```

Run:
```powershell
java -cp build\classes main.Main
```
