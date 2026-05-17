package main;

import items.Item;
import items.Item.EffectType;
import items.EmptyInventoryException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Shop panel that appears between waves. Player can spend gold on items.
 */
public class ShopPanel extends JDialog {

    private final List<Item> shopItems;
    private final List<Item> playerInventory;
    private int gold;
    private boolean purchased = false;

    private JLabel goldLabel;
    private JLabel statusLabel;
    private DefaultListModel<Item> inventoryModel;
    private JPanel shopGrid;

    private static final Color DARK_BG = new Color(25, 25, 40);
    private static final Color ACCENT = new Color(100, 180, 255);
    private static final Color GOLD_COLOR = new Color(255, 215, 0);

    public ShopPanel(JFrame parent, List<Item> inventory, int gold) {
        super(parent, "Item Shop", true);
        this.playerInventory = inventory;
        this.gold = gold;

        setSize(800, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Shop inventory
        shopItems = new ArrayList<>();
        shopItems.add(new Item("Health Potion", "Restores 50 HP", EffectType.HEAL_HP, 50));
        shopItems.add(new Item("Mana Elixir", "Restores 40 MP", EffectType.RESTORE_MP, 40));
        shopItems.add(new Item("Revive Scroll", "Revives a fallen ally with 50% HP", EffectType.REVIVE, 80));

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(DARK_BG);

        // ========== TOP: Title + Gold ==========
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(DARK_BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        JLabel title = new JLabel("🏪 SHOP");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(ACCENT);
        topPanel.add(title, BorderLayout.WEST);

        goldLabel = new JLabel("💰 Gold: " + gold);
        goldLabel.setFont(new Font("Arial", Font.BOLD, 22));
        goldLabel.setForeground(GOLD_COLOR);
        topPanel.add(goldLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ========== CENTER: Shop Items Grid ==========
        shopGrid = new JPanel(new GridLayout(1, 3, 15, 15));
        shopGrid.setBackground(DARK_BG);
        shopGrid.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        for (Item item : shopItems) {
            shopGrid.add(createItemCard(item));
        }
        add(shopGrid, BorderLayout.CENTER);

        // ========== BOTTOM: Inventory + Continue ==========
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(DARK_BG);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 15, 20));

        // Inventory list
        JPanel invPanel = new JPanel(new BorderLayout());
        invPanel.setBackground(DARK_BG);
        JLabel invLabel = new JLabel("Your Inventory:");
        invLabel.setFont(new Font("Arial", Font.BOLD, 16));
        invLabel.setForeground(Color.LIGHT_GRAY);
        invPanel.add(invLabel, BorderLayout.NORTH);

        inventoryModel = new DefaultListModel<>();
        refreshInventoryModel();
        JList<Item> invList = new JList<>(inventoryModel);
        invList.setFont(new Font("Arial", Font.PLAIN, 14));
        invList.setBackground(new Color(35, 35, 50));
        invList.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(invList);
        scrollPane.setPreferredSize(new Dimension(400, 80));
        invPanel.add(scrollPane, BorderLayout.CENTER);
        bottomPanel.add(invPanel, BorderLayout.CENTER);

        // Status + Continue button
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(DARK_BG);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(255, 200, 100));
        rightPanel.add(statusLabel, BorderLayout.NORTH);

        JButton continueBtn = new JButton("CONTINUE TO BATTLE");
        continueBtn.setFont(new Font("Arial", Font.BOLD, 18));
        continueBtn.setBackground(new Color(50, 150, 50));
        continueBtn.setForeground(Color.WHITE);
        continueBtn.setFocusPainted(false);
        continueBtn.addActionListener(e -> {
            purchased = true;
            dispose();
        });
        rightPanel.add(continueBtn, BorderLayout.SOUTH);

        bottomPanel.add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createItemCard(Item item) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(new Color(35, 35, 55));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 120), 1),
            BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));

        // Item name
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(nameLabel, BorderLayout.NORTH);

        // Description
        JLabel descLabel = new JLabel("<html><center>" + item.getDescription() + "</center></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descLabel.setForeground(Color.LIGHT_GRAY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(descLabel, BorderLayout.CENTER);

        // Price + Buy button
        JPanel buyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buyPanel.setBackground(new Color(35, 35, 55));

        JLabel priceLabel = new JLabel("💰 " + item.getValue() + "g");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(GOLD_COLOR);
        buyPanel.add(priceLabel);

        JButton buyBtn = new JButton("BUY");
        buyBtn.setFont(new Font("Arial", Font.BOLD, 14));
        buyBtn.setBackground(new Color(60, 120, 200));
        buyBtn.setForeground(Color.WHITE);
        buyBtn.setFocusPainted(false);
        buyBtn.addActionListener(e -> purchaseItem(item));
        buyPanel.add(buyBtn);

        card.add(buyPanel, BorderLayout.SOUTH);

        return card;
    }

    private void purchaseItem(Item item) {
        if (gold < item.getValue()) {
            statusLabel.setText("❌ Not enough gold! Need " + item.getValue() + "g");
            return;
        }

        gold -= item.getValue();
        playerInventory.add(new Item(item.getName(), item.getDescription(), item.getEffect(), item.getValue()));

        goldLabel.setText("💰 Gold: " + gold);
        refreshInventoryModel();
        statusLabel.setText("✅ Purchased " + item.getName() + "!");
    }

    private void refreshInventoryModel() {
        inventoryModel.clear();
        for (Item item : playerInventory) {
            inventoryModel.addElement(item);
        }
    }

    public boolean hasPurchased() {
        return purchased;
    }

    public int getRemainingGold() {
        return gold;
    }
}
