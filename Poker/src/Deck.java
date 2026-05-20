import java.util.*;

public class Deck {

    List<Card> cards = new ArrayList<>();

    String[] suits = {"♥", "♦", "♣", "♠"};

    String[] ranks = {
            "2","3","4","5","6","7",
            "8","9","10","J","Q","K","A"
    };

    public Deck() {

        for (String suit : suits) {

            for (int i = 0; i < ranks.length; i++) {

                cards.add(
                        new Card(
                                suit,
                                ranks[i],
                                i + 2
                        )
                );
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card dealCard() {
        return cards.remove(0);
    }
}