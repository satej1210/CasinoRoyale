/**
 * Created by satejmhatre on 10/26/16.
 */
import CR.*;
import DDS.*;

import java.util.ArrayList;
import java.util.Scanner;

public class GameManager {
    public static boolean playerJoined = false;
    public static boolean dealerJoined = false;
    public static boolean playerRunning = true;
    public static boolean dealerRunning = true;
    private static final String dealerInit = "Dealer_init";
    private static final String playerInit = "Player_init";


    public static ArrayList<bjPlayer> players = null;
    public static ArrayList<bjDealer> dealers = null;
    GameManager(){
        players = new ArrayList<>();
        dealers = new ArrayList<>();
    }
    public static void initializePlayer(){
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

        System.out.println ("=== [Subscriber] Ready to receive player init ...");
        Runnable backGroundRunnable = () -> {

            boolean terminate = false;
            int count = 0;
            while (!terminate) { // We dont want the example to run indefinitely
                bjPlayerReader.take(bjPlayerSeq, infoSeq, LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);

                for (int i = 0; i < bjPlayerSeq.value.length; i++) {
                    if (bjPlayerSeq.value[i].getClass() == bjPlayer.class && bjPlayerSeq.value[i].action.value() == bjp_action._joining){//.message.equals("Hello World")) {

                        boolean exists = false;
                        for(bjPlayer p : players){
                            if(p.uuid == bjPlayerSeq.value[i].uuid){
                                System.out.println("Player has already joined.");
                                exists = true;
                                break;
                            }
                        }
                        if(!exists){
                            System.out.println("=== [Subscriber] message received from new Player:");
                            System.out.println("A Player with UUID " + bjPlayerSeq.value[i].uuid + " has joined!");
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

        };
        Thread sampleThread = new Thread(backGroundRunnable);
        sampleThread.start();
    }
    public static void initializeDealer(){
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

        System.out.println ("=== [Subscriber] Ready to receive dealer init ...");
        Runnable backGroundRunnable = () -> {

            boolean terminate = false;
            int count = 0;
            while (!terminate) { // We dont want the example to run indefinitely
                bjDealerReader.take(bjDealerSeq, infoSeq, LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);
                for (int i = 0; i < bjDealerSeq.value.length; i++) {
                    if (bjDealerSeq.value[i].getClass() == bjDealer.class && bjDealerSeq.value[i].action.value() == bjd_action._waiting){//.message.equals("Hello World")) {
                        bjDealer rec = bjDealerSeq.value[i];
                        boolean exists = false;
                        for(bjDealer p : dealers){
                            if(p.uuid == bjDealerSeq.value[i].uuid){
                                System.out.println("Dealer has already joined.");
                                exists = true;
                                break;
                            }
                        }
                        if(!exists){
                            System.out.println("=== [Subscriber] message received from new Dealer:");
                            System.out.println("A Dealer with UUID " + bjDealerSeq.value[i].uuid + " has joined!");
                        }
                        System.out.println("=== [Subscriber] message received from new Dealer :");
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

        };
        Thread sampleThread = new Thread(backGroundRunnable);
        sampleThread.start();
    }
    public static void SubscribeToPlayer(){
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

        System.out.println ("=== [Subscriber] Ready ...");
        Runnable backGroundRunnable = () -> {

            boolean terminate = false;
            int count = 0;
            while (!terminate) { // We dont want the example to run indefinitely
                bjPlayerReader.take(bjPlayerSeq, infoSeq, LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);
                for (int i = 0; i < bjPlayerSeq.value.length; i++) {
                    if (bjPlayerSeq.value[i].getClass() == bjPlayer.class){//.message.equals("Hello World")) {
                        System.out.println("=== [Subscriber] message received from Player :");
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

            // clean up
            mgr.getSubscriber().delete_datareader(bjPlayerReader);
            mgr.deleteSubscriber();
            mgr.deleteTopic();
            mgr.deleteParticipant();

        };
        Thread sampleThread = new Thread(backGroundRunnable);
        sampleThread.start();
    }
    public static void SubscribeToDealer(){
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

        System.out.println ("=== [Subscriber] Ready ...");
        Runnable backGroundRunnable = () -> {

            boolean terminate = false;
            int count = 0;
            while (!terminate) { // We dont want the example to run indefinitely
                bjDealerReader.take(bjDealerSeq, infoSeq, LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);
                for (int i = 0; i < bjDealerSeq.value.length; i++) {
                    if (bjDealerSeq.value[i].getClass() == bjDealer.class){//.message.equals("Hello World")) {
                        System.out.println("=== [Subscriber] message received from Dealer :");
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

        };
        Thread sampleThread = new Thread(backGroundRunnable);
        sampleThread.start();
    }
    public static void Start(){
//        System.out.println("Press Enter to start Casino Royale...");
//        Scanner s = new Scanner(System.in);
//        String f = s.nextLine();
    }
    public static void subscribe(){
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
    public static void main(String... args) {
        players = new ArrayList<>();
        dealers = new ArrayList<>();
        Start();
        initializeDealer();
        initializePlayer();
        subscribe();
        while(playerRunning || dealerRunning);
    }
}
