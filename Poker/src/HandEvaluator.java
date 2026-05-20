import java.util.*;

public class HandEvaluator {

    public static int evaluate(List<Card> hand) {

        Map<Integer,Integer> count = new HashMap<>();

        for (Card c : hand) {

            count.put(
                    c.value,
                    count.getOrDefault(c.value,0)+1
            );
        }

        Collection<Integer> values = count.values();

        if (values.contains(4))
            return 7;

        if (values.contains(3) && values.contains(2))
            return 6;

        if (values.contains(3))
            return 3;

        int pair = 0;

        for (int v : values) {

            if (v == 2)
                pair++;
        }

        if (pair == 2)
            return 2;

        if (pair == 1)
            return 1;

        return 0;
    }

    public static String getHandName(int score) {

        switch (score) {

            case 7:
                return "Four of a Kind";

            case 6:
                return "Full House";

            case 3:
                return "Three of a Kind";

            case 2:
                return "Two Pair";

            case 1:
                return "One Pair";

            default:
                return "High Card";
        }
    }
}