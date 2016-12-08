/**
 * Created by Satej on 11/14/2016.
 */

import CR.*;
import DDS.*;

import java.util.ArrayList;

//This is the Dealer class which adds functionality to the bjDealer.
public class Dealer {
    public final static int DECKS = 6;
    private static boolean gameRunning = true;
    private bjDealer dealer;
    private int credits;
    private ArrayList<CR.card> cards, dealerCards;
    private ArrayList<PlayerCards> players;
    private int playerCount = 0;

    Dealer() {
        dealer = new bjDealer();                //Creates an object dealer of bjDealer()
        dealer.uuid = UUIDGen.generate_UUID();  //Generating UUID for the object dealer
        dealer.action = bjd_action.shuffling;   //Dealer shuffles the cards
        dealer.seqno = 0;                       //Initializing the sequence number of the Dealer to 0
        credits = 500;                          //Initializing the credits to 500
        for (int i = 0; i < 21; ++i) {
            dealer.cards[i].suite = 0;
        }
        dealerCards = new ArrayList<>();        //Creating an ArrayList for dealerCards
        players = new ArrayList<>();            //Creating an ArrayList for players
        cards = new ArrayList<>();              //Creating a new ArrayList for cards 
        CardFunctions.GenerateDeck(cards);      //Generates cards
    }

    //Dealer assigning Constructor
    Dealer(bjDealer d) {
        dealer = d;
    }

    /**The main method creates an instance d of the class Dealer. Then the method calls the
    * start function to start the game.
    *
    */
    public static void main(String args[]) {
        Dealer d = new Dealer();
        d.start();
    }

    /**The DealerPrint method prints the credit the Dealer currently has. The method also has a paramter String x, 
    * that is printed along with the credits.
    *
    */
    public void DealerPrint(String x) {
        System.out.println("[Seq No = " + this.dealer.seqno + " Dealer " + this.dealer.uuid + "**Credits = " + credits + "] " + x);
    }

    /** The Wait method pauses the game for a certain amount of time. 
     * The amount of time the game is paused for depends on the parameter int ms, time in milli seconds. 
     *
     */
    public void Wait(int ms) {
        try {
            Thread.sleep(ms);
            DealerPrint("Waiting " + ms / 1000 + "s");
        } catch (Exception e) {

        }
    }
    
    /** The method start() starts a new game. Then the Dealer is asked to shuffle the cards. 
     * It then calls the wait function. The function then Subscribes with the appropriate response.
     *
     */
    public void start() {

        this.DealerPrint("New Game!!!");
        gameRunning = true;

        this.dealer.action = bjd_action.shuffling;
        card c;
        CardFunctions.GenerateDeck(this.cards);
        this.cards = CardFunctions.ShuffleCards(this.cards);
        this.dealer.seqno++;
        DealerPrint("Shuffling the Deck...");
        this.Wait(5000);
        this.dealer.seqno++;
        this.dealer.action = bjd_action.waiting;
        this.Subscribe();
    }

    public void Publish(bjd_action a) {
        DDSEntityManager mgr = new DDSEntityManager();


        mgr.createParticipant("CR"); // create Domain Participant

        // create Type
        bjDealerTypeSupport msgTS = new bjDealerTypeSupport();
        mgr.registerType(msgTS);

        // create Topic
        mgr.createTopic("Dealer");

        // create Publisher
        mgr.createPublisher();

        // create DataWriter
        mgr.createWriter();

        // Publish Events
        DataWriter dwriter = mgr.getWriter();
        bjDealerDataWriter dealerWriter = bjDealerDataWriterHelper.narrow(dwriter);
        bjDealer d = this.dealer;
        d.action = a;
        if (a == bjd_action.waiting) {
            DealerPrint("Waiting for more players.");
        }
        if (a == bjd_action.collecting) {
            DealerPrint("Collecting");
        }
        dealerWriter.register_instance(d);
        int status = dealerWriter.write(d, HANDLE_NIL.value);
        ErrorHandler.checkStatus(status, "MsgDataWriter.write");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // clean up
        mgr.getPublisher().delete_datawriter(dealerWriter);
        mgr.deletePublisher();
        mgr.deleteTopic();
        mgr.deleteParticipant();
    }

    /* In the method DealToSelf, the Dealer deals cards to himself or herself. The function
     * then calculates and prints the total sum of the Dealer's cards by running a for loop through the
     * Dealer's cards and calling the DealerPrint function to print the sum. The function then calls the Wait 
     * function to pause the game for 5000 milliseconds.
     */
    public void DealToSelf() {
        CardAndDeck cd = new CardAndDeck(cards);
        card c = new card();

        CardFunctions.PickCard(cd);

        CardFunctions.PrintCard(cd.card);
        c.visible = cd.card.visible;
        c.suite = cd.card.suite;
        c.base_value = cd.card.base_value;
        dealerCards.add(c);
        int sum = 0;
        for (card d : dealerCards) {
            sum += CardFunctions.GetValue(d.base_value);
        }
        DealerPrint("Dealer Sum = " + sum);
        Wait(5000);
    }

    
    public void DealCards(bjPlayer p) {
        DealerPrint("Dealing Cards ");
        PlayerCards l = null;
        for (PlayerCards c : players) {
            if (c.uuid == p.uuid) {
                l = c;
            }
        }
        if (l == null) {
            l = new PlayerCards(p.uuid);
            players.add(l);
        }
        CardAndDeck cd = new CardAndDeck(cards);
        CardFunctions.PickCard(cd);
        cards = cd.c;
        this.dealer.cards[0] = cd.card;
        this.dealer.cards[0].visible = false;
        l.playerCards.add(this.dealer.cards[0]);

        this.Publish(bjd_action.dealing);
        Wait(5000);
    }

    public void PlayerWagering(bjPlayer p) {
        DealerPrint("Player set wager");
        for (player_status t :
                this.dealer.players) {
            if (t.uuid == p.uuid) {
                t.wager = p.wager;
                System.out.println("Player has set wager of " + p.wager);
            }
        }
        Wait(5000);

    }

    public void PlayerJoining(bjPlayer p) {
        boolean flag = false;
        DealerPrint("Player With ID " + p.uuid + " is joining...");
        for (player_status a : this.dealer.players) {
            if (a.uuid == p.uuid) {
                System.out.println("Player exists");
                flag = true;
                break;
            }
        }
        CheckSize();
        if (!flag && playerCount + 1 < MAX_PLAYERS.value) {
            this.dealer.players[playerCount].uuid = p.uuid;
            this.dealer.players[playerCount++].wager = p.wager;
            
            this.Publish(bjd_action.collecting);
        }
        Wait(5000);
    }

    public void PlayerStay(bjPlayer p) {
        DealerPrint("Player is Staying.");
        PlayerCards t = null;
        for (PlayerCards e : players) {
            if (p.uuid == e.uuid) {
                t = e;
            }
        }
        if (t == null) {
            DealerPrint("Unknown Player Staying");
        } else {
            int sum = 0;
            int aceExists = 0;
            for (card c : t.playerCards) {
                if (c.base_value == 'A') {
                    aceExists++;
                }
                sum += CardFunctions.GetValue(c.base_value);
            }
            if (sum > 21) {
                while (aceExists != 0) {
                    sum -= 10;
                    aceExists--;
                    if (sum == 21) {
                        DealerPrint("Player BLACKJACK!");
                        break;
                    }
                }
                if (sum > 21) {
                    DealerPrint("Player has busted!");
                    t.playerCards = null;
                }
            }
            if (sum == 21) {
                DealerPrint("Player BLACKJACK!");
            }

            int dealerSum = 0;
            int dealerAceExists = 0;
            for (card c : dealerCards) {
                if (c.base_value == 'A') {
                    dealerAceExists++;
                }
                dealerSum += CardFunctions.GetValue(c.base_value);
            }
            DealerPrint("Sum = " + dealerSum);
            if (dealerSum >= 17) {
                DealerPrint("Dealer is Staying");
            }
            while (dealerSum < 17) {
                DealerPrint("Dealer is hitting");
                this.DealToSelf();
                for (card c : dealerCards) {
                    if (c.base_value == 'A') {
                        dealerAceExists++;
                    }
                    dealerSum += CardFunctions.GetValue(c.base_value);
                }
            }
            if (dealerSum == 17) {
                DealerPrint("Dealer is Staying");
            }
            if (dealerSum > 21) {
                while (dealerAceExists > 0) {
                    dealerSum -= 10;
                    dealerAceExists--;
                }
                if (dealerSum > 21) {
                    DealerPrint("Dealer has Busted!");
                    this.Publish(bjd_action.paying);
                } else {
                    DealerPrint("Ace Exists! Sum = " + dealerSum);
                }
            }
            if (dealerSum < sum) {
                DealerPrint("Player Wins!");
                player_status m = null;
                for (player_status ps : this.dealer.players) {
                    if (ps.uuid == p.uuid) {
                        m = ps;
                    }
                }
                if (m != null) {
                    float pay = (float) 3 / 2 * (float) m.wager;
                    this.credits -= pay;
                    this.Publish(bjd_action.paying);
                }

            }
            if (dealerSum == sum) {
                DealerPrint("It's a Tie!");
                player_status m = null;
                for (player_status ps : this.dealer.players) {
                    if (ps.uuid == p.uuid) {
                        m = ps;
                    }
                }
                m.wager = 0;
                this.Publish(bjd_action.paying);
            }
        }
        Wait(5000);
    }

    public void PlayerExiting(bjPlayer p) {
        PlayerCards t = null;
        for (PlayerCards e : players) {
            if (p.uuid == e.uuid) {
                t = e;
            }
        }
        if (t == null) {
            DealerPrint("Unknown Player Exiting");
        } else {
            int sum = 0;
            int aceExists = 0;
            for (card c : t.playerCards) {
                if (c.base_value == 'A') {
                    aceExists++;
                }
                sum += CardFunctions.GetValue(c.base_value);
            }
            if (sum > 21) {
                while (aceExists != 0) {
                    sum -= 10;
                    aceExists--;
                    if (sum == 21) {
                        DealerPrint("BLACKJACK!");
                        break;
                    }
                }
                if (sum > 21) {
                    DealerPrint("Player has busted!");
                    t.playerCards = null;
                }
            }
            if (sum == 21) {
                DealerPrint("BLACKJACK!");
            }
        }
    }

    /** The CheckSize() method cchecks the number of the players on the table by comparing
     * the number of players to 6 (MAX_PLAYERS.value). If the number of players is greater than
     * or equal to 6, then it prints "Players full".
     */
    public void CheckSize() {
        if (players.size() >= MAX_PLAYERS.value) {
            System.out.println("Players full");
        }
    }

    public void Subscribe() {
        Runnable b = () -> {
            DDSEntityManager mgr = new DDSEntityManager();
            String partitionName = "CR";
            mgr.createParticipant(partitionName);
            bjPlayerTypeSupport msgTS = new bjPlayerTypeSupport();
            mgr.registerType(msgTS);
            mgr.createTopic("Player");
            mgr.createSubscriber();
            mgr.createReader();
            DataReader dreader = mgr.getReader();
            bjPlayerDataReader HelloWorldReader = bjPlayerDataReaderHelper.narrow(dreader);
            bjPlayerSeqHolder msgSeq = new bjPlayerSeqHolder();
            SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

            System.out.println("=== [Subscriber] Ready ...");
            boolean terminate = false;
            while (!terminate) { // We dont want the example to run indefinitely
                HelloWorldReader.take(msgSeq, infoSeq, LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);
                for (int i = 0; i < msgSeq.value.length; i++) {
                    bjPlayer p = msgSeq.value[i];
                    if (p.getClass() == bjPlayer.class) {
                        this.dealer.target_uuid = p.uuid;
                        if (p.action == bjp_action.joining) {
                            this.PlayerJoining(p);
                        }
                        if (p.action == bjp_action.wagering) {
                            this.PlayerWagering(p);
                            this.DealCards(p);
                            this.DealToSelf();
                        }
                        if (p.action == bjp_action.requesting_a_card) {
                            this.DealCards(p);
                            this.DealToSelf();
                        }
                        if (p.action == bjp_action.exiting) {
                            this.PlayerExiting(p);
                        }
                        if (p.action == bjp_action.none) {
                            this.PlayerStay(p);
                            terminate = true;
                        }
                        this.dealer.seqno++;
                    }


                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    // nothing to do
                }
            }
            HelloWorldReader.return_loan(msgSeq, infoSeq);

            // clean up
            mgr.getSubscriber().delete_datareader(HelloWorldReader);
            mgr.deleteSubscriber();
            mgr.deleteTopic();
            mgr.deleteParticipant();
            DealerPrint("Subscriber Closing.");
        };
        Thread t = new Thread(b);
        t.start();

    }

    @Override
    public String toString() {
        return "\tUUID : " + this.dealer.uuid + "\n\taction: " + this.dealer.action;

    }
}
