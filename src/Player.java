/**
 * Created by satejmhatre on 10/26/16.
 */

import CR.*;
import DDS.*;

import java.util.ArrayList;
import java.util.Scanner;


public class Player {
    private bjPlayer player = null;
    private ArrayList<CR.card> playerCards;

    private Player() {
        player = new bjPlayer();
        player.uuid = UUIDGen.generate_UUID();
        player.dealer_id = 0;
        player.wager = 0;
        player.credits = 100;
        player.action = bjp_action.none;
        player.seqno = 0;
        playerCards = new ArrayList<>();
        this.Publish(bjp_action.joining);
    }

    public static void main(String args[]) {
        Player p = new Player();

        System.out.println(MAX_PLAYERS.value);
        Scanner reader = new Scanner(System.in);
        System.out.println("Waiting for action:");
        p.SubscribeDealer();


//        System.out.println("Enter a number: ");
//        int n = reader.nextInt();
//        System.out.println("number: " + n);
        //Publish("Player", p);

    }

    public void PlayerPrint(String x) {
        System.out.println("[Player " + this.player.uuid + " !! SeqNo: " + this.player.seqno + "] " + x);
    }

    public void DealerCollecting(bjDealer d) {
        PlayerPrint("Message received from Dealer with id " + d.uuid + " :");
        Scanner a = new Scanner(System.in);
        PlayerPrint("Enter bets:");
        //int x = a.nextInt();
        int x = 10;
        if (x < this.player.credits) {
            this.player.credits = this.player.credits - x;
            this.player.wager = x;
        } else {
            PlayerPrint("You don't have enough credits!!!");
        }
        this.Publish(bjp_action.wagering);
    }

    public void PlayCards(bjDealer d) {
        int sum = 0;
        playerCards.add(d.cards[0]);
        CardFunctions.PrintCards(playerCards);

        for (int i = 0; i < playerCards.size(); ++i) {
            if (d.cards[i].base_value != 0)
                sum += CardFunctions.GetValue(d.cards[i].base_value);
        }
        if (sum > 21) {
            PlayerPrint("Sum = " + sum + "\nBUSTED!");
            this.Publish(bjp_action.exiting);
            return;
        }
        Scanner a = new Scanner(System.in);
        PlayerPrint("Sum = " + sum + "\nHit(1) or Stay(2)?:");
        int x = a.nextInt();
        if (x == 1) {
            this.Publish(bjp_action.requesting_a_card);
        } else {
            this.Publish(bjp_action.exiting);
        }
    }

    public void SubscribeDealer() {
        Runnable b = () -> {
            DDSEntityManager mgr = new DDSEntityManager();

            // create Domain Participant
            mgr.createParticipant("CR");

            // create Type
            bjDealerTypeSupport msgTS = new bjDealerTypeSupport();
            mgr.registerType(msgTS);

            // create Topic
            mgr.createTopic("Dealer");

            // create Subscriber
            mgr.createSubscriber();

            // create DataReader
            mgr.createReader();

            // Read Events

            DataReader dreader = mgr.getReader();
            bjDealerDataReader dealerReader = bjDealerDataReaderHelper.narrow(dreader);

            bjDealerSeqHolder msgSeq = new bjDealerSeqHolder();
            SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

            PlayerPrint("Dealer Subscriber Ready...");
            boolean terminate = false;
            int count = 0;

            while (!terminate) { // We dont want the example to run indefinitely
                dealerReader.take(msgSeq, infoSeq, LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);

                {
                    for (int i = 0; i < msgSeq.value.length; i++) {
                        bjDealer d = msgSeq.value[i];
                        if (d.getClass() == bjDealer.class) {
                            if (this.player.dealer_id == 0) {
                                if (d.action == bjd_action.waiting) {
                                    PlayerPrint("Dealer has Entered!");
                                    this.player.dealer_id = d.uuid;
                                    this.Publish(bjp_action.joining);
                                } else {
                                    continue;
                                }
                            }
                            if (d.target_uuid == this.player.uuid) {
                                if (d.action == bjd_action.waiting) {
                                    PlayerPrint("Dealer has already Entered!");
                                    //this.player.dealer_id = d.uuid;
                                    //this.Publish(bjp_action.joining);
                                }
                                if (d.action == bjd_action.collecting) {
                                    PlayerPrint("Dealer collecting!");
                                    this.DealerCollecting(d);
                                    //this.player.action = bjp_action.requesting_a_card;

                                    ++count;
                                    continue;
                                }
                                if (d.action == bjd_action.dealing) {
                                    PlayerPrint("Cards Recieved");
                                    this.PlayCards(d);
                                }
                            }
                            this.player.seqno++;
                        }

                    }

                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    // nothing to do
                }
            }
            dealerReader.return_loan(msgSeq, infoSeq);

            // clean up
            mgr.getSubscriber().delete_datareader(dealerReader);
            mgr.deleteSubscriber();
            mgr.deleteTopic();
            mgr.deleteParticipant();
        };
        Thread t = new Thread(b);
        t.start();
    }

    public void PublishInit(String partitionName, Player p) {
        DDSEntityManager mgr = new DDSEntityManager();
        // create Domain Participant
        mgr.createParticipant("CR");
        // create Type
        bjPlayerTypeSupport msgTS = new bjPlayerTypeSupport();
        mgr.registerType(msgTS);
        // create Topic
        mgr.createTopic("Player");
        // create Publisher
        mgr.createPublisher();
        // create DataWriter
        mgr.createWriter();
        // Publish Events
        DataWriter dwriter = mgr.getWriter();
        bjPlayerDataWriter playerWriter = bjPlayerDataWriterHelper.narrow(dwriter);
//            bjPlayer msgInstance = new bjPlayer();
        System.out.println("=== [Player Publisher] writing a message containing :");
        System.out.println("    uuid  : " + p.player.uuid);
        System.out.println("    seqno : \"" + p.player.seqno + "\"");
        playerWriter.register_instance(p.player);
        int status = playerWriter.write(p.player, HANDLE_NIL.value);
        ErrorHandler.checkStatus(status, "MsgDataWriter.write");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // clean up
        mgr.getPublisher().delete_datawriter(playerWriter);
        mgr.deletePublisher();

        mgr.deleteTopic();
        mgr.deleteParticipant();
        boolean recievedDealerID = false;
        while (!recievedDealerID) {
            partitionName = "Dealer";

            // create Domain Participant
            mgr.createParticipant(partitionName);

            // create Type
            bjDealerTypeSupport msgTSs = new bjDealerTypeSupport();
            mgr.registerType(msgTSs);

            // create Topic
            mgr.createTopic("Dealer");

            // create Subscriber
            mgr.createSubscriber();

            // create DataReader
            mgr.createReader();

            // Read Events

            DataReader dreader = mgr.getReader();
            bjDealerDataReader HelloWorldReader = bjDealerDataReaderHelper.narrow(dreader);

            bjDealerSeqHolder msgSeq = new bjDealerSeqHolder();
            SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

            System.out.println("=== [DealerSubscriber] Waiting for dealer: ...");
            boolean terminate = false;
            int count = 0;
            while (!terminate) { // We dont want the example to run indefinitely
                HelloWorldReader.take(msgSeq, infoSeq, LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);
                for (int i = 0; i < msgSeq.value.length; i++) {
                    if (msgSeq.value[i].getClass() == bjDealer.class && msgSeq.value[i].action == bjd_action.waiting) {//.message.equals("Hello World")) {
                        System.out.println("=== [DealerSubscriber] message received from Dealer with id " + msgSeq.value[i].uuid + " :");
                        this.player.dealer_id = msgSeq.value[i].uuid;
                        terminate = true;
                    }
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    // nothing to do
                }
                ++count;

            }
            HelloWorldReader.return_loan(msgSeq, infoSeq);

            // clean up
            mgr.getSubscriber().delete_datareader(HelloWorldReader);
            mgr.deleteSubscriber();
            mgr.deleteTopic();
            mgr.deleteParticipant();

        }

    }

    public void update() {

        if (GameManager.tables != null)
            for (Table t : GameManager.tables) {
                for (bjPlayer p : t.players) {
                    if (p.uuid == this.player.uuid) {
                        if (this.player.dealer_id == 0) {
                            this.player.dealer_id = p.dealer_id;
                            System.out.println("Updated Dealer ID");
                        }
                    }
                }
            }

    }

    public void Publish(bjp_action action) {
        DDSEntityManager mgr = new DDSEntityManager();
        // create Domain Participant
        mgr.createParticipant("CR");
        // create Type
        bjPlayerTypeSupport msgTS = new bjPlayerTypeSupport();
        mgr.registerType(msgTS);
        // create Topic
        mgr.createTopic("Player");
        // create Publisher
        mgr.createPublisher();
        // create DataWriter
        mgr.createWriter();
        // Publish Events
        DataWriter dwriter = mgr.getWriter();
        bjPlayerDataWriter playerWriter = bjPlayerDataWriterHelper.narrow(dwriter);
        playerWriter.register_instance(this.player);

        this.player.action = action;
        if (action == bjp_action.joining) {
//            SubscribeDealer();
        } else if (action == bjp_action.wagering) {
            PlayerPrint("Player is wagering");
        }
        int status = playerWriter.write(this.player, HANDLE_NIL.value);
        ErrorHandler.checkStatus(status, "MsgDataWriter.write");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ie) {
            // nothing to do
        }
        this.player.seqno++;
        // clean up
        mgr.getPublisher().delete_datawriter(playerWriter);
        mgr.deletePublisher();
        mgr.deleteTopic();
        mgr.deleteParticipant();

    }
}
