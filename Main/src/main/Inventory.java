package main;

import java.util.ArrayList;
import java.util.List;

//Stores the player's items
public class Inventory {

    private final ArrayList<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        items.add(item);
    }

    public void showItems() {
        //To help debug
        if (items.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).getName());
        }
    }

    public Item getItem(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

    public void removeItem(int index) {
        items.remove(index);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int size() {
        return items.size();
    }

    public List<Item> getItems() {
        //Return a copy so callers cannot modify the inventory directly
        return new ArrayList<>(items);
    }
}
