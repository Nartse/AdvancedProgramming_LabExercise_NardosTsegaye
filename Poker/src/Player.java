import java.util.*;

public class Player {

    String name;

    List<Card> hand = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public void replaceCard(int index, Card newCard) {
        hand.set(index, newCard);
    }

    public void clearHand() {
        hand.clear();
    }
}