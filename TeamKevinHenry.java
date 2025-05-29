package uno;

import uno.UnoPlayer.Color;
import uno.UnoPlayer.Rank;

import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.Card;

public class TeamKevinHenry {
    // API Endpoints
    private Color colorToCall = Color.RED;

    public int play(List<Card> hand, Card xC, Color xCol, GameState state) {
        this.state = state;

        // Stat Models
        deck = new ModelDeck();
        deck.removeCards(hand);
        deck.removeCards(discarded);

        hand = new Hands(hand, state);
        
        double[] R = new double[cards.size()];

        // Constants
        double aveDraws = aveDrawsTillColorChange();

        for (LCard lc : cards) {
            // Consideration 1
            double oppRVal = oppR(lc, lc.c.color);
            double r = -1 * oppR(xC, xCol) * win_frac[1];
            for (int i = 2; i < 4; i++) {
                r -= -1 * oppRVal * win_frac[i];
            }
            R[lc.j] += r;

            // Consideration 2
            R[lc.j]
        }

        

        
    }

    public Color callColor(List<Card> hand) {
        return colorToCall;
    }

    // Internal Code
    private GameState state;
    private int plusTwoVal = 20,  plusFourVal = 25, wildVal = 16, skipVal = 14, reverseVal = 12;
    private int deckP = 0; private int deckCnt = 0;
    
    private ModelDeck deck;
    private Hand hand;

    private double[] win_frac = new double[4];
    private int handP = 0; private int handCnt = 0;

    private double p(Card c) {
        double score = 0.0;

        if (c.getRank().equals(Rank.DRAW_TWO)) {score += plusTwoVal;}
        else if(c.getRank().equals(Rank.SKIP)) {score += skipVal;} 
        else if (c.getRank().equals(Rank.REVERSE)) {score += reverseVal;}
        else if (c.getRank().equals(Rank.WILD_D4)) {score += plusFourVal;}
        else if (c.getRank().equals(Rank.WILD)) {score += wildVal;}
        else {score += c.getNumber();}

        return score;
    }

    private double oppR(Card c, Color col) {
        double r = 0.0;

        // Fraction of cards that can be played
        double canPlayCnt = 0.0; double canPlayP = 0.0;
        for (Card nextC : deck.remainingCards()) {
            if (nextC.canPlayOn(c, col)) {
                canPlayCnt++;
                canPlayP += p(nextC);
            }
        }
        canPlayP /= canPlayCnt;

        // Percentage of hands that can play
        double canPlayFrac = canPlayCnt / deck.drawsRemaining;
        double handCanPlay = pow(canPlayFrac, handSize[1]);

        // Weighted reward of hands that play
        r += canPlayP * handCanPlay; // If hand can play, how many win points for opp lose
        r -= deck.aveDrawPoints() * (1 - handCanPlay); // If hand can't play, how many win points opp gain

        return r;
    }

    private static class LCard {
        public Card c;
        public int i;
        public int j;

        public LCard(Card c, int i, int j) {
            this.c = c;
            this.i = i;
            this.j = j;
        }
    }

    private static class Hands {
        public GameState state;

        public List<Card> hand;
        public ArrayList<LCard> lhand;

        public double[] winFrac = new double[4];

        public Hands(List<Card> hand, GameState state) {
            this.state = state;
            this.hand = hand;

            // Legal hand
            for (int i = 0; i < hand.size(); i++) {
                Card c = hand.get(i);
                if (c.canPlayOn(xC, xCol)) {
                    lhand.add(new LCard(c, i, lhand.size()));
                }
            }

            // Win fraction
            int[] handSizes = state.getNumCardsInHandsOfUpcomingPlayers();
            double total = 0;
            for (int handSize : handSize) { total += handSize; }

            for (int i = 0; i < 4; i++) {
                int I = (i + 1) % 4;
                winFrac[I] = handSizes[i] / total;
            }
        }
    }
    
    private static class ModelDeck {
        public static final int NUMBER_OF_DUP_REGULAR_CARDS = 2;
        public static final int NUMBER_OF_DUP_ZERO_CARDS = 1;
        public static final int NUMBER_OF_DUP_SPECIAL_CARDS = 2;
        public static final int NUMBER_OF_WILD_CARDS = 4;
        public static final int NUMBER_OF_WILD_D4_CARDS = 4;

        private int plusTwoVal = 20, plusFourVal = 25, wildVal = 16, skipVal = 14, reverseVal = 12;

        public ArrayList<Card> draws = new ArrayList<>();
        public int drawP = 0;
        public int drawsRemaining = 0;
        public int colorChngRemaining = 0;

        public ModelDeck() {
            fillDeck();

            drawsRemaining += NUMBER_OF_DUP_REGULAR_CARDS + NUMBER_OF_DUP_SPECIAL_CARDS + NUMBER_OF_DUP_ZERO_CARDS + NUMBER_OF_WILD_CARDS + NUMBER_OF_WILD_D4_CARDS;
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
                // Updaing actual draws
                for (int i = 0; i < draws.size(); i++) {
                    Card deckC = draws.get(i);
                    
                    if (deckC.equals(c)) {
                        // Keeping track of cards
                        draws.remove(i);

                        // Keeping track of draw stats
                        drawP -= p(c);
                        drawsRemaining--;
                        if (c.getColor().equals(UnoPlayer.Color.NONE)) {
                            colorChngRemaining--;
                        }
                        break;
                    }
                }
            }
        }

        public double aveDrawPoints() {
            return drawP / drawsRemaining;
        }

        public aveDrawsTillColorChange() {
            return 1 / (colorChngRemaining / drawsRemaining);
        }
    }
}
