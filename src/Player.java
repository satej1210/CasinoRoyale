/**
 * Created by satejmhatre on 10/26/16.
 */

import CR.*;
import DDS.*;

import java.util.Scanner;


public class Player  {
    private bjPlayer player = null;
    private Player(){
        player = new bjPlayer();
        player.uuid = UUIDGen.generate_UUID();
        player.credits = 100;
        player.action = bjp_action.joining;
        player.seqno = 0;
        this.PublishInit("Player_init", this);
    }

    public static void main(String args[]) {
        Player p = new Player();

        System.out.println(MAX_PLAYERS.value);
        Scanner reader = new Scanner(System.in);
        System.out.println("Waiting for action:");
        p.SubscribeDealer();

        while (true) {
            p.update();
            //int h = reader.nextInt();

        }
//        System.out.println("Enter a number: ");
//        int n = reader.nextInt();
//        System.out.println("number: " + n);
        //Publish("Player", p);

    }

    public void SubscribeDealer() {
        Runnable b = () -> {
        DDSEntityManager mgr = new DDSEntityManager();
            String partitionName = "Dealer";

        // create Domain Participant
        mgr.createParticipant(partitionName);

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
            bjDealerDataReader HelloWorldReader = bjDealerDataReaderHelper.narrow(dreader);

            bjDealerSeqHolder msgSeq = new bjDealerSeqHolder();
            SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

            System.out.println("=== [DealerSubscriber] Ready ...");
            boolean terminate = false;
            int count = 0;
            while (!terminate) { // We dont want the example to run indefinitely
                HelloWorldReader.take(msgSeq, infoSeq, LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);
                for (int i = 0; i < msgSeq.value.length; i++) {
                    if (msgSeq.value[i].getClass() == bjDealer.class && msgSeq.value[i].action == bjd_action.collecting) {//.message.equals("Hello World")) {
                        System.out.println("=== [DealerSubscriber] message received from Dealer with id " + msgSeq.value[i].uuid + " :");
                        Scanner a = new Scanner(System.in);
                        System.out.println("Enter bets:");
                        int x = a.nextInt();
                        if (x <= this.player.credits) {
                            this.player.wager = this.player.credits - x;
                        }
                        Publish(bjp_action.wagering);
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
        };
        Thread t = new Thread(b);
        t.start();
    }

    public void PublishInit(String partitionName, Player p) {
        DDSEntityManager mgr = new DDSEntityManager();
        // create Domain Participant
        mgr.createParticipant(partitionName);
        // create Type
        bjPlayerTypeSupport msgTS = new bjPlayerTypeSupport();
        mgr.registerType(msgTS);
        // create Topic
        mgr.createTopic(partitionName);
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
        mgr.createParticipant("Player");
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
        this.player.action = action;
        System.out.println("=== [Player Publisher] writing a message :" + action);
        playerWriter.register_instance(this.player);
        int status = playerWriter.write(this.player, HANDLE_NIL.value);
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

    }
}
