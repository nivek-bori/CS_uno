


package uno;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeamWeights1_UnoPlayer implements UnoPlayer {

    public int plusTwoVal = 20,  plusFourVal = 25, wildVal = 16, skipVal = 14, reverseVal = 12, harmNextPlayerThreshold = 3;
    public double plusTwoMult = 1.7, plusFourMult = 2, wildMult = 1.6, skipMult = 1.6, reverseMult = 1.2;
    private GameState state;


    
    public int play(List<Card> hand, Card upCard, Color calledColor,
                    GameState state) {
        List<Card> legalCards = new ArrayList<>();
        List<Card> powerCards = new ArrayList<>();
        this.state = state;

        //all legal moves
        for (Card card : hand) {
            if (card.canPlayOn(upCard, calledColor)) {
                legalCards.add(card);

                if (isPowerCard(card)) {
                    powerCards.add(card);
                }
            }
        }

        Card powerCard = new Card(Color.RED, 9);
        //its better to harm the next player
        if (harmNextPlayer(state)) {
            // +4, +2, skip / wild, reverse
            for (Card card : powerCards) {
                if (getScoreAdaptive(powerCard) < getScoreAdaptive(card)) {
                    powerCard = card;
                }
            }
            if (powerCards.size() != 0) {
                return hand.indexOf(powerCard);
            }
        }
        //not better to harm the next player
        //just to avoid returning null
        int minScore = 9999;
        Card bestCard = new Card(Color.RED, 9);
        for (Card c : legalCards) {
            if (getScoreAdaptive(c) < minScore) {
                minScore = getScoreAdaptive(c);
                bestCard = c;
            }
        }

        return hand.indexOf(bestCard);
    }

    private boolean harmNextPlayer(GameState state) {
        //needs work
        //evaluating if player, or player after is better
        int[] playerHands = state.getNumCardsInHandsOfUpcomingPlayers();
        //checking if next next player has uno
        if (playerHands[1] - playerHands[0] < 3 || (playerHands[1] == 1 && playerHands[0] != 1)) {
            return false;
        }
        if (playerHands[0] < harmNextPlayerThreshold) {
            return true;
        }
        return false;
    }

    private boolean isPowerCard(Card card) {
        return card.getRank() != Rank.NUMBER;
    }

    private int getScoreAdaptive(Card card) {
        int score = 0;
        //tweak values
        if (card.getRank().equals(Rank.DRAW_TWO)) {
            score += plusTwoVal;
        } else if(card.getRank().equals(Rank.SKIP)){
            score += skipVal;
        } else if (card.getRank().equals(Rank.REVERSE)) {
            score += reverseVal;
        } else if (card.getRank().equals(Rank.WILD_D4)) {
            score += plusFourVal;
        } else if (card.getRank().equals(Rank.WILD)) {
            score += wildVal;
        } else {
            //scales the numbers inverse
            score += 10 - card.getNumber();
        }

        //late game bonus for power cards to incentive playing them?
        if (Arrays.stream(state.getNumCardsInHandsOfUpcomingPlayers()).sum() / (1.0 * state.getNumCardsInHandsOfUpcomingPlayers().length) > 4) {
            if (card.getRank().equals(Rank.DRAW_TWO)) {
                score *= plusTwoMult;
            } else if(card.getRank().equals(Rank.SKIP)){
                score *= skipMult;
            } else if (card.getRank().equals(Rank.REVERSE)) {
                score *= reverseMult;
            } else if (card.getRank().equals(Rank.WILD_D4)) {
                score *= plusFourMult;
            } else if (card.getRank().equals(Rank.WILD)) {
                score *= wildMult;
            }
        }

        //huge bonus to +2 and +4 if next player has uno
        if (state.getNumCardsInHandsOfUpcomingPlayers()[0] == 1) {
            if (card.getRank().equals(Rank.DRAW_TWO)) {
                score *= 2;
            }
            else if (card.getRank().equals(Rank.WILD_D4)) {
                score *= 4;
            }
        }

        return score;
    }

    private int getScore(Card card) {
        int score = 0;
        //tweak values
        if (card.getRank().equals(Rank.DRAW_TWO)) {
            score += plusTwoVal;
        } else if(card.getRank().equals(Rank.SKIP)){
            score += skipVal;
        } else if (card.getRank().equals(Rank.REVERSE)) {
            score += reverseVal;
        } else if (card.getRank().equals(Rank.WILD_D4)) {
            score += plusFourVal;
        } else if (card.getRank().equals(Rank.WILD)) {
            score += wildVal;
        } else {
            //scales the numbers inverse
            score += 10 - card.getNumber();
        }
        return score;
    }

    private int getColorKey(Card c) {
        if (c.getColor().equals(Color.RED)) return 0;
        if (c.getColor().equals(Color.GREEN)) return 1;
        if (c.getColor().equals(Color.BLUE)) return 2;
        if (c.getColor().equals(Color.YELLOW)) return 3;
        return -1;
    }

    private Color getIndexColor(int i) {
        if (i == 0) return Color.RED;
        if (i == 1) return Color.GREEN;
        if (i == 2) return Color.BLUE;
        if (i == 3) return Color.YELLOW;
        //should never have to do this
        return Color.RED;
    }

    private int[] scoreColors (List<Card> hand) {
        int[] score = new int[]{0, 0, 0, 0};
        for (Card card : hand) {
            int val = getScoreAdaptive(card);
            if (isPowerCard(card)) {
                //multi-color cards add to the scores of all hands, not sure if this is smart or not
                //could use to check the relative power of colors and if similar can choose to screwing over people
                score[0] += val;
                score[1] += val;
                score[2] += val;
                score[3] += val;
            } else {
                score[getColorKey(card)] += val;
            }
        }
        return score;
    }

    private Color getBestColorSelf(List<Card> hand) {
        int[] score = scoreColors(hand);
        int max = Arrays.stream(score).max().getAsInt();
        for (int i = 0; i < score.length; i++) {
            if (score[i] == max) {
                return getIndexColor(i);
            }
        }
        //should never have to do this
        return Color.RED;
    }

    public Color callColor(List<Card> hand) {
        //just looks at the values of our hand and takes the "best" oneQ
        return getBestColorSelf(hand);
    }

}




