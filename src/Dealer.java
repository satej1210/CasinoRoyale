/**
 * Created by satejmhatre on 10/26/16.
 */
import CR.*;
import DDS.*;

import java.util.Scanner;

public class Dealer {

    public static int NUMBER_OF_DECKS = 6;

    public static bjDealer dealer;
    Dealer(){
        dealer = new bjDealer();
        dealer.uuid = UUIDGen.generate_UUID();
        dealer.seqno = 0;
        dealer.action = bjd_action.waiting;
        dealer.cards = new CR.card[Dealer.NUMBER_OF_DECKS*52];
    }

    public static void Subscribe(){
        DDSEntityManager mgr = new DDSEntityManager();
        String partitionName = "HelloWorld example";

        // create Domain Participant
        mgr.createParticipant(partitionName);

        // create Type
        bjDealerTypeSupport msgTS = new bjDealerTypeSupport();
        mgr.registerType(msgTS);

        // create Topic
        mgr.createTopic("HelloWorldData_Msg");

        // create Subscriber
        mgr.createSubscriber();

        // create DataReader
        mgr.createReader();

        // Read Events

        DataReader dreader = mgr.getReader();
        bjDealerDataReader HelloWorldReader = bjDealerDataReaderHelper.narrow(dreader);

        bjDealerSeqHolder msgSeq = new bjDealerSeqHolder();
        SampleInfoSeqHolder infoSeq = new SampleInfoSeqHolder();

        System.out.println ("=== [Subscriber] Ready ...");
        Runnable backGroundRunnable = () -> {

            boolean terminate = false;
            int count = 0;
            while (!terminate) { // We dont want the example to run indefinitely
                HelloWorldReader.take(msgSeq, infoSeq, LENGTH_UNLIMITED.value,
                        ANY_SAMPLE_STATE.value, ANY_VIEW_STATE.value,
                        ANY_INSTANCE_STATE.value);
                for (int i = 0; i < msgSeq.value.length; i++) {
                    if (msgSeq.value[i].getClass() == bjDealer.class){//.message.equals("Hello World")) {
                        System.out.println("=== [Subscriber] message received :");
                        System.out.println("    uuid  : "
                                + msgSeq.value[i].uuid);
                        System.out.println("    Message : \""
                                + msgSeq.value[i].seqno + "\"");
                        terminate = true;
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
            HelloWorldReader.return_loan(msgSeq, infoSeq);

            // clean up
            mgr.getSubscriber().delete_datareader(HelloWorldReader);
            mgr.deleteSubscriber();
            mgr.deleteTopic();
            mgr.deleteParticipant();
        };
        Thread sampleThread = new Thread(backGroundRunnable);
        sampleThread.start();
    }

    public static void Publish(){
        DDSEntityManager mgr = new DDSEntityManager();
        String partitionName = "Dealer";
        // create Domain Participant
        mgr.createParticipant(partitionName);
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
        bjDealerDataWriter HelloWorldWriter = bjDealerDataWriterHelper.narrow(dwriter);
        bjDealer msgInstance = new bjDealer();
        msgInstance.action = bjd_action.waiting;
        System.out.println("=== [Publisher] writing a message containing :");
        System.out.println("    uuid  : " + msgInstance.uuid);
        System.out.println("    seqno : \"" + msgInstance.seqno + "\"");
        HelloWorldWriter.register_instance(msgInstance);
        int status = HelloWorldWriter.write(msgInstance, HANDLE_NIL.value);
        ErrorHandler.checkStatus(status, "MsgDataWriter.write");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // clean up
        mgr.getPublisher().delete_datawriter(HelloWorldWriter);
        mgr.deletePublisher();
        mgr.deleteTopic();
        mgr.deleteParticipant();

    }

    public static void initialize(){

    }
    public static void main(String args[]){
        System.out.println(MAX_PLAYERS.value);
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        //Subscribe();
        Dealer d = new Dealer();

        CardFunctions.GenerateDeck(d.dealer.cards);
        CardFunctions.ShuffleCards(d.dealer.cards);
        CardFunctions.PrintDeck(d.dealer.cards);
        System.out.println(d.dealer.cards.length);
        CardAndDeck cd;
        cd = CardFunctions.PickCard(d.dealer.cards);
        d.dealer.cards = cd.c.clone();
        System.out.println(d.dealer.cards.length);
        System.out.println(cd.card);
        Publish();

    }

}
