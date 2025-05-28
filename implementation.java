package uno;

import uno.UnoPlayer.Color;
import uno.UnoPlayer.Rank;

import java.util.ArrayList;
import java.util.List;

public class TeamKevinHenry {
    // API Endpoints
    private Color colorToCall = Color.RED;

    public int play(List<Card> hand, Card xC, Color xCol, GameState state) {
        this.state = state;
        calc_win_frac();
        calc_handP(hand);
        handSizes = state.getNumCardsInHandsOfUpcomingPlayers();

        ArrayList<LCard> cards = new ArrayList<>();
        for (int i = 0; i < hand.size(); i++) {
            Card c = hand.get(i);
            if (c.canPlayOn(xC, xCol)) {
                cards.add(new LCard(c, 0.0, i));
            }
        }

        // Work in progress
        for (LCard lc : cards) {
            double r_card = -1 * r_V1(c) * win_frac[1];

        }

        for card in hand:
        double r_card = -1 * r_V1(card) * win%(V_1)
        for V_i in villans:
        r_card -= -r_V1(card) * win%(V_i)
                r[card] += r_card
        // Work in progress

        int maxI = -1;
        double maxR = Double.MIN_VALUE;
        for (int i = 0; i < r.size(); i++) {
            if (r.get(i) > maxR) {
                maxR = r.get(i);
                maxI = indexes.get(i);
            }
        }

        return maxI;
    }

    public Color callColor(List<Card> hand) {
        return colorToCall;
    }

    // Internal Code
    private GameState state;
    private int plusTwoVal = 20,  plusFourVal = 25, wildVal = 16, skipVal = 14, reverseVal = 12;
    private int deckP = 0;
    private int deckCnt = 0;
    
    private ModelDeck deck;
    private int[] handSizes;

    private double[] win_frac = new double[4];
    private int handP = 0;
    private int handCnt = 0;
    
    private void calcDeck(List<Card> hand, List<Card> discarded) {
        deck = new ModelDeck();
        deck.removeCards(hand);
        deck.removeCards(discarded);
    }

    private double p(Card c) {
        double score = 0.0;

        if (c.getRank().equals(Rank.DRAW_TWO)) {
            score += plusTwoVal;
        } else if(c.getRank().equals(Rank.SKIP)){
            score += skipVal;
        } else if (c.getRank().equals(Rank.REVERSE)) {
            score += reverseVal;
        } else if (c.getRank().equals(Rank.WILD_D4)) {
            score += plusFourVal;
        } else if (c.getRank().equals(Rank.WILD)) {
            score += wildVal;
        } else {
            score += c.getNumber();
        }

        return score;
    }

    private double[] calc_win_frac() {
        int[] player_cards = state.getNumCardsInHandsOfUpcomingPlayers(); // [0] is next or (hero + 1) index
        double total = 0;

        for (int hand : player_cards) {
            total += hand;
        }

        for (int i = 0; i < 4; i++) {
            int I = (i + 1) % 4;

            win_frac[I] = player_cards[i] / total;
        }
    }

    private double calc_handP(List<Card> cards) {
        for (Card c : cards) {
            handP += p(c);
        }

        return handP;
    }

    private double r_V1(Card c) {
        double x = cardsThatCanPlay(); // frac of drawCards that play on c

        double r_V1 = 0.0;
        r_V1 -= pow(x, handSizes[1]) * deck.aveDrawPoints();
        r_V1 += (1 - pow(x, handSizes[1])) * deck.aveDrawPoints();

    }

    private double calc_aveDrawP() {
        double drawP = deckP - handP;

        for (Card c : state.getPlayedCards()) {
            drawP -= p(c);
        }

        return drawP / (deckCnt - state.getPlayedCards().size() - handCnt);
    }

    private static class LCard {
        public Card c;
        public double r;
        public int i;

        public LCard(Card c, double r, int i) {
            this.c = c;
            this.r = r;
            this.i = i;
        }
    }
    
    private static class ModelDeck {
        public static final int NUMBER_OF_DUP_REGULAR_CARDS = 2;
        public static final int NUMBER_OF_DUP_ZERO_CARDS = 1;
        public static final int NUMBER_OF_DUP_SPECIAL_CARDS = 2;
        public static final int NUMBER_OF_WILD_CARDS = 4;
        public static final int NUMBER_OF_WILD_D4_CARDS = 4;

        public ArrayList<Card> draws = new ArrayList<>();
        public int drawP = 0;
        public int cardRemaining = 0;
        public int colorChngRemaining = 0;

        public ModelDeck() {
            fillDeck();

            cardRemaining += NUMBER_OF_DUP_REGULAR_CARDS + NUMBER_OF_DUP_SPECIAL_CARDS + NUMBER_OF_DUP_ZERO_CARDS + NUMBER_OF_WILD_CARDS + NUMBER_OF_WILD_D4_CARDS;
            colorChngRemaining += NUMBER_OF_WILD_CARDS + NUMBER_OF_WILD_D4_CARDS;
            drawP += 45 * NUMBER_OF_DUP_REGULAR_CARDS;
            drawP += 4 * skipVal * NUMBER_OF_DUP_SPECIAL_CARDS;
            drawP += 4 * reverseVal * NUMBER_OF_DUP_SPECIAL_CARDS;
            drawP += 4 * plusTwoVal * NUMBER_OF_DUP_SPECIAL_CARDS;
            drawP += wildVal * NUMBER_OF_WILD_CARDS;
            drawP += plusFourVal * NUMBER_OF_WILD_D4_CARDS;
        }

        private void fillDeck() {
            for (int i = 1; i <= 9; i++) {
                for (int j = 0; j < NUMBER_OF_DUP_REGULAR_CARDS; j++) {
                    draws.add(new Card(UnoPlayer.Color.RED, i));
                    draws.add(new Card(UnoPlayer.Color.YELLOW, i));
                    draws.add(new Card(UnoPlayer.Color.BLUE, i));
                    draws.add(new Card(UnoPlayer.Color.GREEN, i));
                }
            }
            for (int j = 0; j < NUMBER_OF_DUP_ZERO_CARDS; j++) {
                draws.add(new Card(UnoPlayer.Color.RED, 0));
                draws.add(new Card(UnoPlayer.Color.YELLOW, 0));
                draws.add(new Card(UnoPlayer.Color.BLUE, 0));
                draws.add(new Card(UnoPlayer.Color.GREEN, 0));
            }
            for (int j = 0; j < NUMBER_OF_DUP_SPECIAL_CARDS; j++) {
                draws.add(new Card(UnoPlayer.Color.RED, UnoPlayer.Rank.SKIP));
                draws.add(new Card(UnoPlayer.Color.YELLOW, UnoPlayer.Rank.SKIP));
                draws.add(new Card(UnoPlayer.Color.GREEN, UnoPlayer.Rank.SKIP));
                draws.add(new Card(UnoPlayer.Color.BLUE, UnoPlayer.Rank.SKIP));
                draws.add(new Card(UnoPlayer.Color.RED, UnoPlayer.Rank.REVERSE));
                draws.add(new Card(UnoPlayer.Color.YELLOW, UnoPlayer.Rank.REVERSE));
                draws.add(new Card(UnoPlayer.Color.GREEN, UnoPlayer.Rank.REVERSE));
                draws.add(new Card(UnoPlayer.Color.BLUE, UnoPlayer.Rank.REVERSE));
                draws.add(new Card(UnoPlayer.Color.RED, UnoPlayer.Rank.DRAW_TWO));
                draws.add(new Card(UnoPlayer.Color.YELLOW, UnoPlayer.Rank.DRAW_TWO));
                draws.add(new Card(UnoPlayer.Color.GREEN, UnoPlayer.Rank.DRAW_TWO));
                draws.add(new Card(UnoPlayer.Color.BLUE, UnoPlayer.Rank.DRAW_TWO));
            }
            for (int i = 0; i < NUMBER_OF_WILD_CARDS; i++) {
                draws.add(new Card(UnoPlayer.Color.NONE, UnoPlayer.Rank.WILD));
            }
            for (int i = 0; i < NUMBER_OF_WILD_D4_CARDS; i++) {
                draws.add(new Card(UnoPlayer.Color.NONE, UnoPlayer.Rank.WILD_D4));
            }
        }
        
        public void removeCards(List<Card> cards) {
            for (Card c : cards) {
                // Keeping track of draw stats
                drawP -= p(c);
                cardsRemaining--;
                if (c.getColor().equals(UnoPlayer.Color.NONE)) {
                    colorChngRemaining--;
                }
                
                // Updaing actual draws
                for (int i = 0; i < draws.size(); i++) {
                    Card deckC = draws.get(i);
                    
                    if (deckC.equals(c)) {
                        draws.remove(i);
                        break;
                    }
                }
            }
        }
    }
}
