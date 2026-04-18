package main;

import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import javax.swing.*;

//Main UI
public class GameFrame extends JFrame {
    private static final String CARD_START = "start";
    private static final String CARD_CLASS = "class";
    private static final String CARD_GAME = "game";
    private static final String ACTION_EXPLORE = "explore";
    private static final String ACTION_COMBAT = "combat";
    private static final int PRIORITY_FIRST = 1;
    private static final int PRIORITY_NORMAL = 0;
    private static final int PRIORITY_LAST = -1;
    private static final Color BG_DARK = new Color(22, 28, 39);
    private static final Color PANEL_DARK = new Color(33, 42, 58);
    private static final Color PANEL_MID = new Color(54, 63, 79);
    private static final Color BORDER = new Color(104, 116, 139);
    private static final Color TEXT_LIGHT = new Color(230, 235, 245);
    private static final Color TEXT_MUTED = new Color(180, 190, 205);
    private static final Color SLOT_EMPTY = new Color(64, 72, 87);
    private static final Color TEXT_DARK = new Color(22, 28, 39);
    private static final Color BUTTON_LIGHT = new Color(225, 230, 238);

    private final Random random = new Random();
    private final CardLayout rootLayout = new CardLayout();
    private final JPanel rootPanel = new JPanel(rootLayout);
    private final CardLayout actionLayout = new CardLayout();
    private final JPanel actionPanel = new JPanel(actionLayout);

    private Character player;
    private Enemy enemy;
    private Inventory inventory;
    private SceneState currentScene = new SceneState("forest", "Forest", "You are standing in the forest.");
    private int shortRestsUsedInArea = 0;

    private final JLabel titleLabel = new JLabel("Main Quest");
    private final JLabel subtitleLabel = new JLabel("Explore, fight, rest, and manage your build.");
    private final JLabel playerStatsLabel = new JLabel();
    private final JLabel enemyStatsLabel = new JLabel();
    private final JLabel skillLabel = new JLabel();
    private final JProgressBar playerHpBar = new JProgressBar();
    private final JProgressBar playerSpBar = new JProgressBar();
    private final JProgressBar playerExpBar = new JProgressBar();
    private final JProgressBar enemyHpBar = new JProgressBar();
    private final JProgressBar enemySpBar = new JProgressBar();
    private final JPanel playerImageHolder = new JPanel(new BorderLayout());
    private final JPanel encounterImageHolder = new JPanel(new BorderLayout());
    private final JPanel inventoryGrid = new JPanel(new GridLayout(0, 2, 12, 12));
    private final JTextArea logArea = new JTextArea(12, 28);
    private final JButton exploreButton = new JButton("Explore");
    private final JButton shortRestButton = new JButton("Short Rest");
    private final JButton longRestButton = new JButton("Long Rest");
    private final JButton restartButton = new JButton("Restart");
    private final JButton attackButton = new JButton("Attack");
    private final JButton skill1Button = new JButton("Skill 1");
    private final JButton skill2Button = new JButton("Skill 2");
    private final JButton skill3Button = new JButton("Skill 3");
    private final JButton runButton = new JButton("Run");

    public GameFrame() {
        super("Main Quest");
        configureFrame();
        rootPanel.add(buildStartPanel(), CARD_START);
        rootPanel.add(buildClassSelectPanel(), CARD_CLASS);
        rootPanel.add(buildGamePanel(), CARD_GAME);
        setContentPane(rootPanel);
        rootLayout.show(rootPanel, CARD_START);
    }

    private void configureFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1260, 840);
        setLocationRelativeTo(null);
        rootPanel.setBackground(BG_DARK);
        UIManager.put("TitledBorder.titleColor", TEXT_LIGHT);
    }

    private JPanel buildStartPanel() {
        //Start screen with Play and Exit buttons
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(80, 80, 80, 80));
        panel.setBackground(BG_DARK);
        JLabel heading = new JLabel("Main Quest", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 44));
        heading.setForeground(TEXT_LIGHT);
        JLabel caption = new JLabel("A Little RPG Game", SwingConstants.CENTER);
        caption.setFont(new Font("SansSerif", Font.PLAIN, 18));
        caption.setForeground(TEXT_MUTED);
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG_DARK);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        caption.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(Box.createVerticalGlue());
        center.add(heading);
        center.add(Box.createRigidArea(new Dimension(0, 10)));
        center.add(caption);
        center.add(Box.createRigidArea(new Dimension(0, 30)));
        JButton playButton = new JButton("Play");
        JButton exitButton = new JButton("Exit");
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.setMaximumSize(new Dimension(300, 64));
        exitButton.setMaximumSize(new Dimension(300, 64));
        playButton.setPreferredSize(new Dimension(300, 64));
        exitButton.setPreferredSize(new Dimension(300, 64));
        playButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        styleMenuButton(playButton);
        styleMenuButton(exitButton);
        playButton.addActionListener(e -> rootLayout.show(rootPanel, CARD_CLASS));
        exitButton.addActionListener(e -> System.exit(0));
        center.add(playButton);
        center.add(Box.createRigidArea(new Dimension(0, 12)));
        center.add(exitButton);
        center.add(Box.createVerticalGlue());
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildClassSelectPanel() {
        //Lets the player choose one of the four classes
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(BG_DARK);
        JLabel title = new JLabel("Choose Your Character", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT_LIGHT);
        panel.add(title, BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setBackground(BG_DARK);
        grid.add(createClassCard("Warrior", "warrior", "HP 50 | ATK 55 | SPD 45"));
        grid.add(createClassCard("Mage", "mage", "HP 30 | ATK 80 | SPD 40"));
        grid.add(createClassCard("Archer", "archer", "HP 35 | ATK 45 | SPD 70"));
        grid.add(createClassCard("Tank", "tank", "HP 80 | ATK 50 | SPD 20"));
        panel.add(grid, BorderLayout.CENTER);
        JButton backButton = new JButton("Back");
        styleMenuButton(backButton);
        backButton.addActionListener(e -> rootLayout.show(rootPanel, CARD_START));
        panel.add(backButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createClassCard(String className, String imageKey, String statsText) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL_DARK);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER), BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JComponent visual = createVisualComponent(imageKey, className, 220, 220, PANEL_MID);
        visual.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel nameLabel = new JLabel(className);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        nameLabel.setForeground(TEXT_LIGHT);
        JLabel statsLabel = new JLabel(statsText);
        statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsLabel.setForeground(TEXT_MUTED);
        JButton chooseButton = new JButton("Choose " + className);
        chooseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleMenuButton(chooseButton);
        chooseButton.setMaximumSize(new Dimension(220, 42));
        chooseButton.setPreferredSize(new Dimension(220, 42));
        chooseButton.setMinimumSize(new Dimension(220, 42));
        chooseButton.addActionListener(e -> startGame(className));
        card.add(visual);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(statsLabel);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(chooseButton);
        return card;
    }

    private JPanel buildGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.setBackground(BG_DARK);
        panel.add(buildTopStrip(), BorderLayout.NORTH);
        panel.add(buildCenterPanel(), BorderLayout.CENTER);
        panel.add(buildBottomPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildTopStrip() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(PANEL_DARK);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER), BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        titleLabel.setForeground(TEXT_LIGHT);
        subtitleLabel.setForeground(TEXT_MUTED);
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(PANEL_DARK);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        actionPanel.add(buildExploreActionPanel(), ACTION_EXPLORE);
        actionPanel.add(buildCombatActionPanel(), ACTION_COMBAT);
        panel.add(textPanel, BorderLayout.WEST);
        panel.add(actionPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildExploreActionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 8, 8));
        panel.setBackground(PANEL_DARK);
        exploreButton.addActionListener(e -> handleExplore());
        shortRestButton.addActionListener(e -> handleShortRest());
        longRestButton.addActionListener(e -> handleLongRest());
        restartButton.addActionListener(e -> rootLayout.show(rootPanel, CARD_START));
        styleActionButton(exploreButton, 170, 54);
        styleActionButton(shortRestButton, 170, 54);
        styleActionButton(longRestButton, 170, 54);
        styleActionButton(restartButton, 170, 54);
        panel.add(exploreButton);
        panel.add(shortRestButton);
        panel.add(longRestButton);
        panel.add(restartButton);
        return panel;
    }

    private JPanel buildCombatActionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 8, 8));
        panel.setBackground(PANEL_DARK);
        attackButton.addActionListener(e -> handleAttack());
        skill1Button.addActionListener(e -> handleAbility(0));
        skill2Button.addActionListener(e -> handleAbility(1));
        skill3Button.addActionListener(e -> handleAbility(2));
        runButton.addActionListener(e -> handleRun());
        styleActionButton(attackButton, 150, 54);
        styleActionButton(skill1Button, 150, 54);
        styleActionButton(skill2Button, 150, 54);
        styleActionButton(skill3Button, 150, 54);
        styleActionButton(runButton, 150, 54);
        panel.add(attackButton);
        panel.add(skill1Button);
        panel.add(skill2Button);
        panel.add(skill3Button);
        panel.add(runButton);
        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 16, 16));
        panel.setBackground(BG_DARK);
        panel.add(buildPlayerPanel());
        panel.add(buildEncounterPanel());
        panel.add(buildInventoryPanel());
        return panel;
    }

    private JPanel buildPlayerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(PANEL_DARK);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER), "Player"));
        JPanel statsPanel = new JPanel(new GridLayout(5, 1, 8, 8));
        statsPanel.setBackground(PANEL_DARK);
        playerHpBar.setStringPainted(true);
        playerSpBar.setStringPainted(true);
        playerExpBar.setStringPainted(true);
        playerStatsLabel.setForeground(TEXT_LIGHT);
        skillLabel.setForeground(TEXT_LIGHT);
        statsPanel.add(playerStatsLabel);
        statsPanel.add(playerHpBar);
        statsPanel.add(playerSpBar);
        statsPanel.add(playerExpBar);
        statsPanel.add(skillLabel);
        playerImageHolder.setBackground(PANEL_DARK);
        playerImageHolder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER), "Portrait"));
        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(playerImageHolder, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildEncounterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(PANEL_DARK);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER), "Encounter"));
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 8, 8));
        statsPanel.setBackground(PANEL_DARK);
        enemyHpBar.setStringPainted(true);
        enemySpBar.setStringPainted(true);
        enemyStatsLabel.setForeground(TEXT_LIGHT);
        statsPanel.add(enemyStatsLabel);
        statsPanel.add(enemyHpBar);
        statsPanel.add(enemySpBar);
        encounterImageHolder.setBackground(PANEL_DARK);
        encounterImageHolder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER), "Scene"));
        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(encounterImageHolder, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(PANEL_DARK);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER), "Inventory"));
        inventoryGrid.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        inventoryGrid.setBackground(PANEL_DARK);
        JScrollPane scrollPane = new JScrollPane(inventoryGrid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PANEL_DARK);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_DARK);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER), "Battle Log"));
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBackground(BG_DARK);
        logArea.setForeground(TEXT_LIGHT);
        logArea.setCaretColor(TEXT_LIGHT);
        panel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        return panel;
    }

    private void startGame(String className) {
        //Creates the chosen player class and resets the run state
        String enteredName = JOptionPane.showInputDialog(this, "Enter your name:", className);
        String name = enteredName == null || enteredName.isBlank() ? className : enteredName.trim();
        if ("Warrior".equalsIgnoreCase(className)) player = new Warrior(name);
        else if ("Mage".equalsIgnoreCase(className)) player = new Mage(name);
        else if ("Archer".equalsIgnoreCase(className)) player = new Archer(name);
        else player = new Tank(name);
        inventory = new Inventory();
        inventory.addItem(Item.createLesserHpPotion());
        enemy = null;
        currentScene = new SceneState("forest", "Forest", "You are standing in the forest.");
        shortRestsUsedInArea = 0;
        playerImageHolder.removeAll();
        playerImageHolder.add(createVisualComponent(className.toLowerCase(), className, 300, 300, PANEL_MID), BorderLayout.CENTER);
        playerImageHolder.revalidate();
        playerImageHolder.repaint();
        logArea.setText("");
        appendLog(player.getName() + " enters the world as a " + className + ".");
        setScene(currentScene);
        refreshGameView();
        rootLayout.show(rootPanel, CARD_GAME);
    }

    private record SceneState(String imageKey, String title, String summary) {}
    private record PlayerActionOutcome(String message, boolean escaped) {}
    @FunctionalInterface private interface CombatAction { PlayerActionOutcome execute(); }

    private void handleExplore() {
        //Exploring either changes the area or creates a random encounter
        if (player == null || !player.isAlive()) return;
        if (isBattleActive()) {
            appendLog("You cannot explore while an enemy is in front of you.");
            return;
        }
        if (random.nextInt(100) < 68) {
            spawnEnemy(createRandomEnemy());
            return;
        }
        int sceneRoll = random.nextInt(3);
        if (sceneRoll == 0) setScene(new SceneState("forest", "Forest", "You wander through a forest path. It stays quiet."));
        else if (sceneRoll == 1) setScene(new SceneState("ocean", "Ocean", "You reach the ocean. The waves are calm and nothing attacks."));
        else setScene(new SceneState("lake", "Lake", "You find a cluster of lakes reflecting the sky. No enemies appear."));
        appendLog(currentScene.summary());
        refreshGameView();
    }

    private Enemy createRandomEnemy() {
        //Goblins are most common, then wizards, then ogres
        int roll = random.nextInt(100);
        if (roll < 60) return new Goblin(random, player);
        if (roll < 85) return new WizardEnemy(random, player);
        return new Ogre(random, player);
    }

    private void spawnEnemy(Enemy spawnedEnemy) {
        enemy = spawnedEnemy;
        appendLog("A " + enemy.getName() + " appears.");
        showEncounterVisual(enemy.getImageKey(), enemy.getName(), new Color(151, 93, 93));
        refreshGameView();
    }

    private void handleAttack() {
        resolveCombatAction(PRIORITY_NORMAL, () -> {
            int damage = enemy.applyDamage(player.getAttack());
            return new PlayerActionOutcome(player.getName() + " attacks " + enemy.getName() + " for " + damage + " damage.", false);
        });
    }

    private void handleRun() {
        if (!isBattleActive()) {
            appendLog("There is nothing to run from.");
            return;
        }
        if (player.getSpeed() <= enemy.getSpeed()) {
            appendLog("You are not fast enough to run from " + enemy.getName() + ".");
            return;
        }
        resolveCombatAction(PRIORITY_FIRST, () -> new PlayerActionOutcome(player.getName() + " runs away safely.", true));
    }

    private void handleAbility(int index) {
        //Each class maps the three combat buttons to different abilities
        if (!isBattleActive()) {
            appendLog("There is no enemy to target.");
            return;
        }
        if (!player.spendSkillPoint()) {
            appendLog("You do not have enough skill points.");
            refreshGameView();
            return;
        }
        int priority = PRIORITY_NORMAL;
        if (player instanceof Mage && index == 2) priority = PRIORITY_FIRST;
        else if (player instanceof Archer && index == 0) priority = PRIORITY_FIRST;
        else if (player instanceof Archer && index == 1) priority = PRIORITY_LAST;
        final int abilityIndex = index;
        resolveCombatAction(priority, () -> executePlayerAbility(abilityIndex));
    }

    private PlayerActionOutcome executePlayerAbility(int index) {
        //Ability logic is dispatched by player class and button index
        if (player instanceof Warrior) {
            if (index == 0) {
                int damage = enemy.applyDamage(player.getAttack() * 2);
                return new PlayerActionOutcome("Warrior uses Slash for " + damage + " damage.", false);
            }
            if (index == 1) {
                player.prepareHalfDamage();
                return new PlayerActionOutcome("Warrior uses Guard and halves the next damage taken.", false);
            }
            player.prepareBlockNextDamage();
            player.prepareCounter(1.0);
            return new PlayerActionOutcome("Warrior prepares Counter and will reflect the next hit.", false);
        }
        if (player instanceof Mage) {
            if (index == 0) {
                int damage = enemy.applyDamage(player.getAttack() * 2);
                return new PlayerActionOutcome("Mage casts Doom for " + damage + " damage.", false);
            }
            if (index == 1) {
                player.restoreHealth(player.getMaxHealth() / 2);
                return new PlayerActionOutcome("Mage casts Heal and restores health.", false);
            }
            int damage = enemy.applyDamage(Math.max(1, player.getAttack() / 2));
            return new PlayerActionOutcome("Mage casts Bolt for " + damage + " damage and acts first.", false);
        }
        if (player instanceof Archer) {
            if (index == 0) {
                int damage = enemy.applyDamage(player.getAttack());
                return new PlayerActionOutcome("Archer uses Quick Shot for " + damage + " damage.", false);
            }
            if (index == 1) {
                int damage = enemy.applyDamage(player.getAttack() * 3);
                return new PlayerActionOutcome("Archer fires Heavy Arrow for " + damage + " damage.", false);
            }
            player.prepareBlockNextDamage();
            return new PlayerActionOutcome("Archer uses Evade and dodges the next attack.", false);
        }
        if (index == 0) {
            player.prepareHalfDamage();
            player.restoreHealthPercent(0.20);
            return new PlayerActionOutcome("Tank uses Fortify, heals, and halves the next damage taken.", false);
        }
        if (index == 1) {
            player.prepareBlockNextDamage();
            return new PlayerActionOutcome("Tank uses Protect and ignores the next damage.", false);
        }
        int damage = enemy.applyDamage(player.getAttack() * 2);
        boolean stunned = random.nextInt(100) < 20;
        if (stunned) enemy.stun();
        return new PlayerActionOutcome("Tank uses Bash for " + damage + " damage" + (stunned ? " and stuns the enemy." : "."), false);
    }

    private void resolveCombatAction(int priority, CombatAction action) {
        if (!isBattleActive()) {
            appendLog("Combat is not active.");
            return;
        }

        //Turn order depends on ability priority first, then speed
        boolean playerFirst = shouldPlayerActFirst(priority);
        if (playerFirst) {
            if (!performPlayerTurn(action)) { refreshGameView(); return; }
            if (!isBattleActive()) { refreshGameView(); return; }
            performEnemyTurn();
        } else {
            performEnemyTurn();
            if (!isBattleActive()) { refreshGameView(); return; }
            performPlayerTurn(action);
        }
        refreshGameView();
    }

    private boolean shouldPlayerActFirst(int priority) {
        if (priority == PRIORITY_FIRST) return true;
        if (priority == PRIORITY_LAST) return false;
        return player.getSpeed() >= enemy.getSpeed();
    }

    private boolean performPlayerTurn(CombatAction action) {
        if (!isBattleActive()) return false;
        if (player.consumeStun()) {
            appendLog(player.getName() + " is stunned and loses the turn.");
            return true;
        }
        PlayerActionOutcome outcome = action.execute();
        appendLog(outcome.message());
        if (outcome.escaped()) {
            enemy = null;
            setScene(currentScene);
            return false;
        }
        if (enemy != null && !enemy.isAlive()) {
            handleVictory();
            return false;
        }
        return true;
    }

    private void performEnemyTurn() {
        //Enemy actions can also trigger counter damage or extra turns
        if (!isBattleActive()) return;
        if (enemy.consumeStun()) {
            appendLog(enemy.getName() + " is stunned and cannot act.");
            pauseBetweenTurns();
            return;
        }
        EnemyTurnResult result = enemy.takeTurn(player, random);
        appendLog(result.getMessage());
        pauseBetweenTurns();
        if (result.attackedTarget() && player.hasCounterReady() && enemy.isAlive()) {
            int reflectedDamage = Math.max(1, result.getAttemptedDamage());
            player.consumeCounterDamage();
            int counterDamage = enemy.applyDamage(reflectedDamage);
            appendLog(player.getName() + " counters for " + counterDamage + " damage.");
            pauseBetweenTurns();
            if (!enemy.isAlive()) {
                handleVictory();
                return;
            }
        }
        if (!player.isAlive()) {
            appendLog(player.getName() + " has fallen.");
            handlePlayerDefeat();
            return;
        }
        if (result.takesExtraTurn() && enemy.isAlive()) performEnemyTurn();
    }

    private void handleVictory() {
        //Winning grants EXP, may drop loot, and returns to the current area
        appendLog("You defeated the " + enemy.getName() + ".");
        List<LevelUpInfo> levelUps = player.gainExp(enemy.getExpReward());
        appendLog("You gained " + enemy.getExpReward() + " EXP.");
        Item drop = enemy.getDrop(random);
        if (drop != null) {
            inventory.addItem(drop);
            appendLog(enemy.getName() + " dropped a " + drop.getName() + ".");
        }
        enemy = null;
        setScene(currentScene);
        refreshGameView();
        for (LevelUpInfo levelUpInfo : levelUps) showLevelUpPopup(levelUpInfo);
    }

    private void handleShortRest() {
        if (!canRest()) return;
        int maxShortRests = getMaxShortRestsForArea();
        if (shortRestsUsedInArea >= maxShortRests) {
            appendLog("You have reached the short rest limit for this area (" + maxShortRests + ").");
            return;
        }
        player.restoreHealth(30);
        player.restoreSkillPoints(1);
        shortRestsUsedInArea++;
        appendLog("Short rest: restored 30 HP and 1 SP.");
        refreshGameView();
    }

    private void handleLongRest() {
        if (!canRest()) return;
        player.restoreFullHealth();
        player.restoreAllSkillPoints();
        appendLog("Long rest: restored all HP and SP.");
        if (random.nextBoolean()) {
            int lostExp = player.loseExp(30);
            appendLog("Long rest penalty: you lost " + lostExp + " EXP.");
        }
        refreshGameView();
    }

    private boolean canRest() {
        if (player == null || !player.isAlive()) return false;
        if (isBattleActive()) {
            appendLog("You cannot rest during combat.");
            return false;
        }
        return true;
    }

    private void showLevelUpPopup(LevelUpInfo levelUpInfo) {
        StringBuilder message = new StringBuilder();
        message.append(player.getName()).append(" reached Level ").append(levelUpInfo.getNewLevel()).append("!\n");
        message.append("+").append(levelUpInfo.getHealthGain()).append(" HP\n");
        message.append("+").append(levelUpInfo.getAttackGain()).append(" ATK\n");
        message.append("+").append(levelUpInfo.getSpeedGain()).append(" SPD");
        if (levelUpInfo.getSkillPointCapGain() > 0) {
            message.append("\n+").append(levelUpInfo.getSkillPointCapGain()).append(" Max SP (now ").append(levelUpInfo.getMaxSkillPoints()).append(")");
        }
        JOptionPane.showMessageDialog(this, message.toString(), "Level Up", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshGameView() {
        //Rebuilds text, bars, buttons, and inventory to match current state
        if (player == null) return;
        titleLabel.setText("Main Quest - " + player.getName());
        subtitleLabel.setText("Current location: " + currentScene.title() + " | Short Rests Left: " + getRemainingShortRestsInArea() + "/" + getMaxShortRestsForArea());
        playerStatsLabel.setText(player.getClass().getSimpleName() + " | LVL " + player.getLevel() + " | ATK " + player.getAttack() + " | SPD " + player.getSpeed());
        skillLabel.setText("Abilities: " + String.join(" | ", getAbilityNames()));
        updateBar(playerHpBar, "HP", player.getHealth(), player.getMaxHealth(), new Color(114, 132, 160));
        updateBar(playerSpBar, "SP", player.getSkillPoints(), player.getMaxSkillPoints(), new Color(60, 92, 140));
        updateBar(playerExpBar, "EXP", player.getExp(), player.getExpToNextLevel(), new Color(92, 112, 145));
        if (enemy == null) {
            enemyStatsLabel.setText(currentScene.title() + " | " + currentScene.summary());
            updateBar(enemyHpBar, "Encounter HP", 0, 1, new Color(88, 100, 124));
            updateBar(enemySpBar, "Encounter SP", 0, 1, new Color(76, 88, 112));
            actionLayout.show(actionPanel, ACTION_EXPLORE);
        } else {
            enemyStatsLabel.setText(enemy.getName() + " | ATK " + enemy.getAttack() + " | SPD " + enemy.getSpeed());
            updateBar(enemyHpBar, enemy.getName() + " HP", enemy.getHealth(), enemy.getMaxHealth(), new Color(88, 100, 124));
            updateBar(enemySpBar, enemy.getName() + " SP", enemy.getSkillPoints(), enemy.getMaxSkillPoints(), new Color(76, 88, 112));
            actionLayout.show(actionPanel, ACTION_COMBAT);
        }
        String[] abilities = getAbilityNames();
        skill1Button.setText(abilities[0]);
        skill2Button.setText(abilities[1]);
        skill3Button.setText(abilities[2]);
        runButton.setEnabled(isBattleActive() && player.getSpeed() > enemy.getSpeed());
        refreshInventoryGrid();
    }

    private void handlePlayerDefeat() {
        JOptionPane.showMessageDialog(this, player.getName() + " was defeated.", "Defeat", JOptionPane.WARNING_MESSAGE);
        player = null;
        enemy = null;
        inventory = null;
        currentScene = new SceneState("forest", "Forest", "You are standing in the forest.");
        playerImageHolder.removeAll();
        playerImageHolder.revalidate();
        playerImageHolder.repaint();
        encounterImageHolder.removeAll();
        encounterImageHolder.revalidate();
        encounterImageHolder.repaint();
        inventoryGrid.removeAll();
        inventoryGrid.revalidate();
        inventoryGrid.repaint();
        logArea.setText("");
        rootLayout.show(rootPanel, CARD_START);
    }

    private void styleActionButton(JButton button, int width, int height) {
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
    }

    private String[] getAbilityNames() {
        if (player instanceof Warrior) return new String[]{"Slash", "Guard", "Counter"};
        if (player instanceof Mage) return new String[]{"Doom", "Heal", "Bolt"};
        if (player instanceof Archer) return new String[]{"Quick Shot", "Heavy Arrow", "Evade"};
        return new String[]{"Fortify", "Protect", "Bash"};
    }

    private void refreshInventoryGrid() {
        inventoryGrid.removeAll();
        int totalSlots = Math.max(6, inventory == null ? 0 : inventory.size());
        for (int i = 0; i < totalSlots; i++) {
            if (inventory != null && i < inventory.size()) inventoryGrid.add(createInventorySlot(inventory.getItem(i), i));
            else inventoryGrid.add(createEmptyInventorySlot());
        }
        inventoryGrid.revalidate();
        inventoryGrid.repaint();
    }

    private JComponent createInventorySlot(Item item, int index) {
        JButton button = new JButton("<html><center>" + item.getName() + "</center></html>");
        button.setPreferredSize(new Dimension(180, 120));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setBackground(BUTTON_LIGHT);
        button.setForeground(TEXT_DARK);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 2), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        button.setToolTipText(item.getDescription());
        button.setIcon(loadInventoryIcon(item, 48, 48));
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.addActionListener(e -> handleItemUse(index));
        return button;
    }

    private JComponent createEmptyInventorySlot() {
        JPanel slot = new JPanel(new BorderLayout());
        slot.setPreferredSize(new Dimension(180, 120));
        slot.setBackground(SLOT_EMPTY);
        slot.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 2), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        JLabel label = new JLabel("Empty Slot", SwingConstants.CENTER);
        label.setForeground(TEXT_MUTED);
        slot.add(label, BorderLayout.CENTER);
        return slot;
    }

    private void handleItemUse(int index) {
        if (inventory == null || index < 0 || index >= inventory.size()) return;
        Item item = inventory.getItem(index);
        inventory.removeItem(index);
        item.use(player);
        appendLog("You used " + item.getName() + ".");
        if (isBattleActive()) performEnemyTurn();
        refreshGameView();
    }

    private ImageIcon loadInventoryIcon(Item item, int width, int height) {
        String imageKey;
        if ("Lesser HP Potion".equals(item.getName())) imageKey = "LHP";
        else if ("HP Potion".equals(item.getName())) imageKey = "HP";
        else if ("SP Potion".equals(item.getName())) imageKey = "SP";
        else imageKey = item.getName();
        Path imagePath = findImage(imageKey);
        if (imagePath == null) return null;
        ImageIcon icon = new ImageIcon(imagePath.toString());
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void updateBar(JProgressBar bar, String label, int value, int max, Color color) {
        bar.setMaximum(Math.max(1, max));
        bar.setValue(Math.max(0, value));
        bar.setForeground(color);
        bar.setBackground(BG_DARK);
        bar.setBorder(BorderFactory.createLineBorder(BORDER));
        bar.setFont(new Font("SansSerif", Font.BOLD, 12));
        bar.setStringPainted(true);
        bar.setString(label + ": " + value + "/" + Math.max(1, max));
    }

    private void setScene(SceneState scene) {
        boolean sameScene = currentScene != null
                && currentScene.imageKey().equals(scene.imageKey())
                && currentScene.title().equals(scene.title())
                && currentScene.summary().equals(scene.summary());
        currentScene = scene;
        //Short rests only refresh when the player actually discovers a new area
        if (!sameScene) {
            shortRestsUsedInArea = 0;
        }
        showEncounterVisual(scene.imageKey(), scene.title(), PANEL_MID);
    }

    private void showEncounterVisual(String imageKey, String label, Color fallbackColor) {
        encounterImageHolder.removeAll();
        encounterImageHolder.add(createVisualComponent(imageKey, label, 300, 300, fallbackColor), BorderLayout.CENTER);
        encounterImageHolder.revalidate();
        encounterImageHolder.repaint();
    }

    private boolean isBattleActive() {
        return player != null && enemy != null && player.isAlive() && enemy.isAlive();
    }

    private void appendLog(String text) {
        logArea.append(text + System.lineSeparator());
        logArea.setCaretPosition(logArea.getDocument().getLength());
        logArea.paintImmediately(logArea.getVisibleRect());
        if (getRootPane() != null) {
            getRootPane().paintImmediately(getRootPane().getBounds());
        }
    }

    private void pauseBetweenTurns() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }

    private JComponent createVisualComponent(String imageKey, String label, int width, int height, Color fallbackColor) {
        Path imagePath = findImage(imageKey);
        if (imagePath != null) {
            ImageIcon icon = new ImageIcon(imagePath.toString());
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaled));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setPreferredSize(new Dimension(width, height));
            return imageLabel;
        }
        return new PlaceholderCard(label, fallbackColor, width, height);
    }

    private Path findImage(String imageKey) {
        String[] extensions = {"png", "jpg", "jpeg", "gif", "bmp"};
        String[] directories = {"", "images", "assets", "src\\main\\assets", "src\\assets", "resources"};
        for (String directory : directories) {
            for (String extension : extensions) {
                Path candidate = directory.isEmpty() ? Path.of(imageKey + "." + extension) : Path.of(directory, imageKey + "." + extension);
                if (Files.exists(candidate)) return candidate;
                Path titleCaseCandidate = directory.isEmpty() ? Path.of(capitalize(imageKey) + "." + extension) : Path.of(directory, capitalize(imageKey) + "." + extension);
                if (Files.exists(titleCaseCandidate)) return titleCaseCandidate;
            }
        }
        try (Stream<Path> pathStream = Files.walk(Path.of("."))) {
            return pathStream.filter(Files::isRegularFile)
                    .filter(path -> hasImageExtension(path, extensions))
                    .filter(path -> path.getFileName().toString().toLowerCase().contains(imageKey.toLowerCase()))
                    .sorted(Comparator.comparing(Path::toString))
                    .findFirst().orElse(null);
        } catch (IOException ignored) {
            return null;
        }
    }

    private boolean hasImageExtension(Path path, String[] extensions) {
        String filename = path.getFileName().toString().toLowerCase();
        for (String extension : extensions) if (filename.endsWith("." + extension)) return true;
        return false;
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return java.lang.Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase();
    }

    private int getMaxShortRestsForArea() {
        if (player == null) return 2;
        return 2 + (player.getLevel() / 10);
    }

    private int getRemainingShortRestsInArea() {
        return Math.max(0, getMaxShortRestsForArea() - shortRestsUsedInArea);
    }

    private void styleMenuButton(JButton button) {
        button.setBackground(BUTTON_LIGHT);
        button.setForeground(TEXT_DARK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 2), BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
    }

    private void styleLightButton(JButton button) {
        button.setForeground(TEXT_DARK);
    }

    private static class PlaceholderCard extends JPanel {
        private final String label;
        private final Color color;
        private final int width;
        private final int height;

        private PlaceholderCard(String label, Color color, int width, int height) {
            this.label = label;
            this.color = color;
            this.width = width;
            this.height = height;
            setPreferredSize(new Dimension(width, height));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(245, 245, 245));
            g.fillRect(0, 0, width, height);
            g.setColor(color);
            g.fillRoundRect(20, 20, width - 40, height - 40, 20, 20);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 24));
            int textWidth = g.getFontMetrics().stringWidth(label);
            g.drawString(label, (width - textWidth) / 2, height / 2);
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            String subtitle = "Image not found";
            int subtitleWidth = g.getFontMetrics().stringWidth(subtitle);
            g.drawString(subtitle, (width - subtitleWidth) / 2, (height / 2) + 28);
        }
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new GameFrame().setVisible(true);
        });
    }
}
