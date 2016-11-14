/**
 * Created by satejmhatre on 10/26/16.
 */
import CR.*;
import DDS.*;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class GameManager {
    private static final String dealerInit = "Dealer_init";
    private static final String playerInit = "Player_init";
    public boolean playerJoined = false;
    public boolean dealerJoined = false;
    public boolean playerRunning = true;
    public boolean dealerRunning = true;
    public ArrayList<bjPlayer> players = null;
    public ArrayList<bjDealer> dealers = null;
    public ArrayList<Table> tables = null;
    UUID id;
    private boolean playerInitTh = false, dealerInitTh = false, player = false, dealer = false;
    GameManager(){
        id = UUID.randomUUID();
        players = new ArrayList<>();
        dealers = new ArrayList<>();
        tables = new ArrayList<>();
    }

    public static int Start() {
        System.out.println("Press Enter to start Casino Royale...");
        Scanner s = new Scanner(System.in);
        String f = s.nextLine();
        System.out.println("1. Start New Game");
        System.out.println("2. Exit\nEnter:");
        return s.nextInt();
    }

    public static void NewGame(GameManager n) {
        n.players = new ArrayList<>();
        n.dealers = new ArrayList<>();
        n.initializeDealer();
        n.initializePlayer();
        n.subscribe();
        while (n.playerRunning || n.dealerRunning) {
            if (n.playerRunning && n.dealerRunning) {
                n.DisplayMenu();
            }
        }
    }

    public static void main(String... args) {
        int opt = Start();
        while (true) {
            switch (opt) {
                case 1:
                    GameManager n = new GameManager();
                    NewGame(n);
                    break;
                case 2:
                    System.exit(0);
                default:
                    System.out.println("Incorrect option");
            }
        }


    }

    public void initializePlayer() {
        DDSEntityManager mgr = new DDSEntityManager();

        // create Domain Participant
        mgr.createParticipant("Player_init");

        // create Type
        bjPlayerTypeSupport msgTS = new bjPlayerTypeSupport();
        mgr.registerType(msgTS);

        // create Topic
        mgr.createTopic("Player_init");

        // create Subscriber
        mgr.createSubscriber();

        // create DataReader
        mgr.createReader();

        // Read Events

        DataReader playerReader = mgr.getReader();
        bjPlayerDataReader bjPlayerReader = bjPlayerDataReaderHelper.narrow(playerReader);


        bjPlayerSeqHolder bjPlayerSeq = new bjPlayerSeqHolder();

        SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

        System.out.println("From GM " + id + " === [Subscriber] Ready to receive player init ...");
        Runnable backGroundRunnable = () -> {

            boolean terminate = false;
            int count = 0;
            while (!playerInitTh) { // We dont want the example to run indefinitely
                bjPlayerReader.take(bjPlayerSeq, infoSeq, LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);

                for (int i = 0; i < bjPlayerSeq.value.length; i++) {
                    if (bjPlayerSeq.value[i].getClass() == bjPlayer.class && bjPlayerSeq.value[i].action.value() == bjp_action._joining){//.message.equals("Hello World")) {

                        boolean exists = false;
                        for(bjPlayer p : players){
                            if(p.uuid == bjPlayerSeq.value[i].uuid){
                                System.out.println("From GM " + id + " Player has already joined.");
                                exists = true;
                                break;
                            }
                        }
                        if(!exists){
                            System.out.println("From GM " + id + " === [PlayerSubscriber] message received from new Player:");
                            System.out.println("A Player with UUID " + bjPlayerSeq.value[i].uuid + " has joined!");
                            players.add(bjPlayerSeq.value[i]);
                        }
                        if (!dealerJoined) {
                            System.out.println("From GM " + id + " === [PlayerSubscriber] WARNING!!!:");
                            System.out.println("Dealer has not joined yet. Will wait for dealer to join.");
                        }
                        playerJoined = true;
                    }

                }

                try
                {
                    Thread.sleep(200);
                }
                catch(InterruptedException ie)
                {
                    // nothing to do
                }
                ++count;

            }

            bjPlayerReader.return_loan(bjPlayerSeq, infoSeq);

            // clean up
            mgr.getSubscriber().delete_datareader(bjPlayerReader);
            mgr.deleteSubscriber();
            mgr.deleteTopic();
            mgr.deleteParticipant();
            System.out.println("Stopping Thread");
        };
        Thread sampleThread = new Thread(backGroundRunnable);

        sampleThread.start();
    }

    public void initializeDealer() {
        DDSEntityManager mgr = new DDSEntityManager();

        // create Domain Participant
        mgr.createParticipant("Dealer_init");

        // create Type
        bjDealerTypeSupport msgTS = new bjDealerTypeSupport();
        mgr.registerType(msgTS);

        // create Topic
        mgr.createTopic("Dealer_init");

        // create Subscriber
        mgr.createSubscriber();

        // create DataReader
        mgr.createReader();

        // Read Events

        DataReader dealerReader = mgr.getReader();
        bjDealerDataReader bjDealerReader = bjDealerDataReaderHelper.narrow(dealerReader);


        bjDealerSeqHolder bjDealerSeq = new bjDealerSeqHolder();

        SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

        System.out.println("From GM " + id + " === [Subscriber] Ready to receive dealer init ...");
        Runnable backGroundRunnable = () -> {

            boolean terminate = false;
            int count = 0;
            while (!dealerInitTh) { // We dont want the example to run indefinitely
                bjDealerReader.take(bjDealerSeq, infoSeq, LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);
                for (int i = 0; i < bjDealerSeq.value.length; i++) {
                    if (bjDealerSeq.value[i].getClass() == bjDealer.class && bjDealerSeq.value[i].action.value() == bjd_action._waiting){//.message.equals("Hello World")) {
                        bjDealer rec = bjDealerSeq.value[i];
                        boolean exists = false;
                        for(bjDealer p : dealers){
                            if(p.uuid == bjDealerSeq.value[i].uuid){
                                System.out.println("From GM " + id + " Dealer has already joined.");
                                exists = true;
                                break;
                            }
                        }
                        if(!exists){
                            System.out.println("From GM " + id + " === [DealerInitSubscriber] message received from new Dealer:");
                            System.out.println("A Dealer with UUID " + bjDealerSeq.value[i].uuid + " has joined!");
                            dealers.add(bjDealerSeq.value[i]);
                            for (bjPlayer p : players) {
                                if (p.dealer_id == 0) {
                                    AssignTable(bjDealerSeq.value[i], null);
                                }
                            }
                        }
                        System.out.println("From GM " + id + " Now assigning Table to dealer: ");
                        AssignTable(bjDealerSeq.value[i], null);
                        System.out.println("From GM " + id + " === [DealerInitSubscriber] message received from new Dealer :");
                        System.out.println("Dealer is now waiting...");
                        dealerJoined = true;

                    }


                }

                try
                {
                    Thread.sleep(200);
                }
                catch(InterruptedException ie)
                {
                    // nothing to do
                }
                ++count;

            }
            bjDealerReader.return_loan(bjDealerSeq, infoSeq);


            // clean up
            mgr.getSubscriber().delete_datareader(bjDealerReader);
            mgr.deleteSubscriber();
            mgr.deleteTopic();
            mgr.deleteParticipant();
            System.out.println("Stopping Thread");
        };
        Thread sampleThread = new Thread(backGroundRunnable);

        sampleThread.start();
    }

    public void SubscribeToPlayer() {
        DDSEntityManager mgr = new DDSEntityManager();
        String partitionName = "Player";

        // create Domain Participant
        mgr.createParticipant(partitionName);

        // create Type
        bjPlayerTypeSupport msgTS = new bjPlayerTypeSupport();
        mgr.registerType(msgTS);

        // create Topic
        mgr.createTopic("Player");

        // create Subscriber
        mgr.createSubscriber();

        // create DataReader
        mgr.createReader();

        // Read Events

        DataReader dealerReader = mgr.getReader();
        bjPlayerDataReader bjPlayerReader = bjPlayerDataReaderHelper.narrow(dealerReader);

        bjPlayerSeqHolder bjPlayerSeq = new bjPlayerSeqHolder();
        SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

        System.out.println("From GM " + id + " === [Subscriber] Ready for Player ...");
        Runnable backGroundRunnable = () -> {

            boolean terminate = false;
            int count = 0;
            while (!player) { // We dont want the example to run indefinitely
                bjPlayerReader.take(bjPlayerSeq, infoSeq, LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);
                for (int i = 0; i < bjPlayerSeq.value.length; i++) {
                    if (bjPlayerSeq.value[i].getClass() == bjPlayer.class){//.message.equals("Hello World")) {
                        System.out.println("From GM " + id + " === [Subscriber] message received from Player :");
                        System.out.println("    uuid  : "
                                + bjPlayerSeq.value[i].uuid);
                        System.out.println("    Message : \""
                                + bjPlayerSeq.value[i].seqno + "\"");

                    }

                }

                try
                {
                    Thread.sleep(200);
                }
                catch(InterruptedException ie)
                {
                    // nothing to do
                }
                ++count;

            }
            bjPlayerReader.return_loan(bjPlayerSeq, infoSeq);
            System.out.println("Stopping Thread");
            // clean up
            mgr.getSubscriber().delete_datareader(bjPlayerReader);
            mgr.deleteSubscriber();
            mgr.deleteTopic();
            mgr.deleteParticipant();

        };
        Thread sampleThread = new Thread(backGroundRunnable);

        sampleThread.start();
    }

    public void AssignTable(bjDealer d, bjPlayer p) {
        if (p == null && d != null) {
            tables.add(new Table(d));
            System.out.println("Dealer with uuid " + d.uuid + " assigned to Table with uuid " + tables.get(tables.size() - 1).getUUID());
        }
        if (d == null && p != null) {
            for (Table t : tables) {
                if (t.players.size() < 6) {
                    p.dealer_id = t.dealer.uuid;
                    t.players.add(p);
                    System.out.println("From GM " + id + " === Player with uuid " + p.uuid + " added to Table with uuid " + t.getUUID() + " with Dealer with uuid " + t.dealer.uuid);

                    break;
                }
            }
        }
//        if(d!=null && p!=null){
//            for(Table table: tables) {
//                if (table.players.size() >= 6) {
//                    continue;
//                }
//                else{
//                    p.dealer_id = d.uuid;
//                    tables.add(new Table(d, p));
//                }
//            }
//
//        }
    }

    public void SubscribeToDealer() {
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

        DataReader dealerReader = mgr.getReader();
        bjDealerDataReader bjDealerReader = bjDealerDataReaderHelper.narrow(dealerReader);

        bjDealerSeqHolder bjDealerSeq = new bjDealerSeqHolder();
        SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

        System.out.println("From GM " + id + " === [Subscriber] Ready ...");
        Runnable backGroundRunnable = () -> {

            boolean terminate = false;
            int count = 0;
            while (!dealer) { // We dont want the example to run indefinitely
                bjDealerReader.take(bjDealerSeq, infoSeq, LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);
                for (int i = 0; i < bjDealerSeq.value.length; i++) {
                    if (bjDealerSeq.value[i].getClass() == bjDealer.class){//.message.equals("Hello World")) {
                        System.out.println("From GM " + id + " === [Subscriber] message received from Dealer :");
                        System.out.println("    uuid  : "
                                + bjDealerSeq.value[i].uuid);
                        System.out.println("    Message : \""
                                + bjDealerSeq.value[i].seqno + "\"");
                    }

                }
                try
                {
                    Thread.sleep(200);
                }
                catch(InterruptedException ie)
                {
                    // nothing to do
                }
                ++count;

            }
            bjDealerReader.return_loan(bjDealerSeq, infoSeq);

            // clean up
            mgr.getSubscriber().delete_datareader(bjDealerReader);
            mgr.deleteSubscriber();
            mgr.deleteTopic();
            mgr.deleteParticipant();
            System.out.println("Stopping Thread");
        };
        Thread sampleThread = new Thread(backGroundRunnable);
        sampleThread.start();
    }

    public void subscribe() {
        Runnable backGroundRunnable = () -> {
            boolean a = true;
            while(a) {
                if (playerJoined && dealerJoined) {
                    a = false;
                }
            }
        };
        Thread sampleThread = new Thread(backGroundRunnable);
        sampleThread.start();
    }

    public void DisplayMenu() {
        System.out.println("From GM: " + id + " === Player and Dealer Threads running in background.");
        Scanner s = new Scanner(System.in);

        System.out.println("1. Exit GM " + id + "\nEnter:");
        int f = s.nextInt();
        switch (f) {
            case 1:
                System.out.println("Stopping all Threads:");
        }

    }
}
