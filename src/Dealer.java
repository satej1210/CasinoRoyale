/**
 * Created by Satej on 11/14/2016.
 */

import CR.*;
import DDS.*;

import java.util.ArrayList;

public class Dealer {
    public final static int DECKS = 6;
    private static boolean gameRunning = true;
    private bjDealer dealer;
    private int credits;
    private ArrayList<CR.card> cards, dealerCards;
    private ArrayList<PlayerCards> players;
    private int playerCount = 0;

    Dealer() {
        dealer = new bjDealer();
        dealer.uuid = UUIDGen.generate_UUID();
//        dealer.cards = new card[21];
        dealer.action = bjd_action.waiting;
        dealer.seqno = 0;
        credits = 500;
        for (int i = 0; i < 21; ++i) {
            dealer.cards[i].suite = 0;
        }
        dealerCards = new ArrayList<>();
        players = new ArrayList<>();
        cards = new ArrayList<>();
        CardFunctions.GenerateDeck(cards);
    }

    Dealer(bjDealer d) {
        dealer = d;
    }

    public static void main(String args[]) {
        Dealer d = new Dealer();
        d.start();
        //d.AskBets();
//        System.out.println(MAX_PLAYERS.value);
//        Scanner reader = new Scanner(System.in);
//
//        System.out.println("Enter a number: ");
//        int n = reader.nextInt();
//        System.out.println("number: " + n);
//        Publish("Dealer", d);
        //d.Publish(bjd_action.collecting);

    }

    public void DealerPrint(String x) {
        System.out.println("[Dealer " + this.dealer.uuid + "**Credits = " + credits + "] " + x);
    }

    public void start() {

        this.DealerPrint("New Game!!!");
        gameRunning = true;

        this.dealer.action = bjd_action.shuffling;
        card c;
        CardFunctions.GenerateDeck(this.cards);
        this.cards = CardFunctions.ShuffleCards(this.cards);
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

    }

    public void DealCards(bjPlayer p) {
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
        System.out.println("Dealing Cards ");
        this.Publish(bjd_action.dealing);
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


    }

    public void PlayerJoining(bjPlayer p) {
        boolean flag = false;
        System.out.println("=== [DealerSubscriber] Player message: joining :");
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

            this.Publish(bjd_action.waiting);
            this.Publish(bjd_action.collecting);
        }
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
            if (dealerSum == 17) {
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
                DealerPrint("Dealer has Busted!");
                this.Publish(bjd_action.paying);
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
                DealerPrint("No one wins");
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

            // main(new String[2]);
        };
        Thread t = new Thread(b);
        t.start();

    }

    @Override
    public String toString() {
        return "\tUUID : " + this.dealer.uuid + "\n\taction: " + this.dealer.action;

    }
}
